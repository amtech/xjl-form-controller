package com.xjl.pt.form.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.xjl.pt.core.domain.UserInfo;
/**
 * 登录认证拦截器
 * @author guan.zheyuan
 */
public class loginInterceptor implements HandlerInterceptor{

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		
	}
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		
	}
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
			 String requestUri = request.getRequestURI();
			 String contextPath = request.getContextPath();
			 String url = requestUri.substring(contextPath.length());
			if(url.indexOf("/login") != -1){
				return true;
			}
//			UserInfo userInfo = (UserInfo) request.getSession().getAttribute(SystemConstant.SESSION_USER);
//			if (null == userInfo) {
//				response.sendRedirect("");
//			}
			return true;
	}

}
