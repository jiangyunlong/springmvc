package com.jyl.springmvc.service.impl;

import com.jyl.springmvc.annotation.JService;
import com.jyl.springmvc.service.TestService;

/**
 * @TODO
 * @author Long
 * @date 2018年7月19日下午5:19:56
 */
@JService("TestServiceImpl")
public class TestServiceImpl implements TestService{

	public String query(String name) {
		
		return "name==="+name;
	}

}
