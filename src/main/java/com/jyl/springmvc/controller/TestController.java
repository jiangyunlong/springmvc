package com.jyl.springmvc.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jyl.springmvc.annotation.JAutowired;
import com.jyl.springmvc.annotation.JController;
import com.jyl.springmvc.annotation.JRequestMapping;
import com.jyl.springmvc.service.TestService;

/**
 * @TODO
 * @author Long
 * @date 2018年7月19日下午5:15:29
 */
@JController
@JRequestMapping("/test")
public class TestController {

	@JAutowired("TestServiceImpl")
	private TestService testService;
	
	@JRequestMapping("/xxx")
	public void xxx(HttpServletRequest req, HttpServletResponse resp){
		
		String result = testService.query("kobe");
		
		PrintWriter pw;
		try {
			pw = resp.getWriter();
			pw.write(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
