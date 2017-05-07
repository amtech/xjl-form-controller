package com.xjl.pt.form.controller;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.xjl.pt.web.controller.BaseControllerTest;


public class FormControllerTest extends BaseControllerTest{
	@Autowired
	private FormController formController;
	@Override
	public Object getController() {
		return this.formController;
	}
	@Test
	public void add(){
		String uri = "/form/add";
		String json = "{\"formName\":\"abc\"}";
		String resp = BaseControllerTest.mockPost(uri, json);
		System.out.println("add:" + resp);
	}
	@Test
	public void query(){
		String uri = "/form/all/1/10";
		String resp = BaseControllerTest.mockGet(uri, null);
		System.out.println("all:" + resp);
	}

}
