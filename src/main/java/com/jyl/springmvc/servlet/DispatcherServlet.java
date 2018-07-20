package com.jyl.springmvc.servlet;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jyl.springmvc.annotation.JAutowired;
import com.jyl.springmvc.annotation.JController;
import com.jyl.springmvc.annotation.JRequestMapping;
import com.jyl.springmvc.annotation.JService;

/**
 * @TODO
 * @author Long
 * @date 2018年7月19日下午5:26:01
 */
public class DispatcherServlet extends HttpServlet{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<String> classNames = new ArrayList<String>();
	
	private Map<String,Object> beans = new HashMap<String,Object>(16);
	
	private Map<String,Object> urlMapping = new HashMap<String,Object>(16);
	
	public void init(ServletConfig config){
		
		//scan
		scanPackage("com.jyl.springmvc");
		
		//instance
		doInstance();
		
		for(String key : beans.keySet()){
			System.out.println("key:"+key+",value:"+beans.get(key));
		}
		
		doAutowired();
		
		urlMapping();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String uri = req.getRequestURI();
		String context = req.getContextPath();
		
		String path = uri.replace(context, "");
		Method method = (Method)urlMapping.get(path);
		
		Object instance = beans.get("/"+path.split("/")[1]);
		
		try {
			method.invoke(instance, new Object[]{req, resp});
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		URL url = DispatcherServlet.class.getClassLoader().getResource("com/jyl/springmvc/");
		System.out.println(url);
	}

	public void scanPackage(String basePackage){
		
		String basePath = basePackage.replaceAll("\\.", "/")+"/";
		URL url = this.getClass().getClassLoader().getResource(basePath);
		String fileStr = url.getFile();
		File file = new File(fileStr);
		String[] fileArr = file.list();
		
		for(String path : fileArr){
			File filePath = new File(fileStr+path);
			if(filePath.isDirectory()){
				scanPackage(basePackage+"."+path);
			}else{
				classNames.add(basePackage+"."+filePath.getName());
			}
		}
	}
	
	public void doInstance(){
		if(classNames.size() <= 0){
			System.out.println("no class");
			return;
		}
		
		for(String className : classNames){
			
			className = className.replace(".class", "");
			try {
				Class<?> clazz  = Class.forName(className);
				if(clazz.isAnnotationPresent(JController.class)){
					
					Object instance = clazz.newInstance();
					
					JRequestMapping requestMapping = clazz.getAnnotation(JRequestMapping.class);
					String key = requestMapping.value();
					beans.put(key, instance);
				}else if(clazz.isAnnotationPresent(JService.class)){
					
					Object instance = clazz.newInstance();
					
					JService service = clazz.getAnnotation(JService.class);
					String key = service.value();
					beans.put(key, instance);
				}else{
					System.out.println("...");
					continue;
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void doAutowired(){
		
		if(beans.entrySet().size() <= 0){
			System.out.println("no instance.");
			return;
		}
		
		for(Map.Entry<String, Object> map : beans.entrySet()){
			
			Object instance = map.getValue();
			Class<?> clazz = instance.getClass();
			
			if(clazz.isAnnotationPresent(JController.class)){
				
				Field[] fields = clazz.getDeclaredFields();
				for(Field field : fields){
					if(field.isAnnotationPresent(JAutowired.class)){
						JAutowired autowired = field.getAnnotation(JAutowired.class);
						String key = autowired.value();
						field.setAccessible(true);
						
						try {
							field.set(instance, beans.get(key));
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else{
						continue;
					}
				}
			}else{
				continue;
			}
		}
	}
	
	public void urlMapping(){
		
		if(beans.entrySet().size() <= 0){
			System.out.println("no instance.");
			return;
		}
		
		for(Map.Entry<String, Object> map : beans.entrySet()){
			
			Object instance = map.getValue();
			Class<?> clazz = instance.getClass();
			
			if(clazz.isAnnotationPresent(JController.class)){
			
				JRequestMapping requestMapping = clazz.getAnnotation(JRequestMapping.class);
				String classPath = requestMapping.value();
				
				Method[] methods = clazz.getDeclaredMethods();
				for(Method method : methods){
					if(method.isAnnotationPresent(JRequestMapping.class)){
						JRequestMapping methodRequestMapping = method.getAnnotation(JRequestMapping.class);
						String methodPath = methodRequestMapping.value();
						urlMapping.put(classPath+methodPath, method);
					}else{
						continue;
					}
				}
			}else{
				continue;
			}
		}
	}
	
}
