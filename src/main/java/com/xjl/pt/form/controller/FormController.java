package com.xjl.pt.form.controller;


import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xjl.pt.form.domain.Form;
import com.xjl.pt.form.service.FormService;

/**
 * 表单控制器，用于显示表单内容和接受表单参数
 * @author leasonlive
 *
 */
@Controller
@RequestMapping("/form")
public class FormController {
	@Autowired
	private FormService formService;
	private static final Logger logger = LoggerFactory.getLogger(FormController.class);
	@ResponseBody
	@RequestMapping(value="/add",method=RequestMethod.POST,consumes = "application/json")
	public void add(@RequestBody Form form){
		this.formService.insert(form);
	}
	@ResponseBody
	@RequestMapping(value="/all/{page}/{rows}",method=RequestMethod.GET,consumes = "application/json")
	public List<Form> all(HttpServletRequest request,@PathVariable Integer page,@PathVariable Integer rows){
		return this.formService.find(page, rows);
	}
}
