package com.jyl.springmvc;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HelloWorldController {
	
	@RequestMapping("/hello")
	public String hello(@RequestParam(value="name", required=false, defaultValue="World") String name, Model model) {
		System.out.println("xxxxxxxxxx");
		model.addAttribute("name", name);
		return "helloworld";
	}
}
