package com.xjl.pt.form.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;


public class XJLHandlerInterceptor extends HandlerInterceptorAdapter {
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		//可以做一些权限等方面的处理，后续可以考虑用spring的安全部分来代替
		System.out.println("pre1");
		System.out.println(handler);
		return super.preHandle(request, response, handler);
	}
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		//modelAndView是空的，无法判断返回值是否为空
		System.out.println("post1");
		System.out.println(handler);
		System.out.println(modelAndView);
		super.postHandle(request, response, handler, modelAndView);
	}
	 public void afterCompletion(HttpServletRequest request,  
	            HttpServletResponse response, Object handler, Exception ex)  
	            throws Exception {
		 System.out.println("after1");
	        System.out.println(handler);
	        System.out.println(ex);
		 super.afterCompletion(request, response, handler, ex);
	  
	 }  
}
