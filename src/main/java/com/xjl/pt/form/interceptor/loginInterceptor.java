package com.xjl.pt.form.interceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.xjl.pt.core.domain.UserInfo;
import com.xjl.pt.form.controller.SystemConstant;
import com.xjl.pt.form.controller.UserLogController;
import com.xjl.pt.form.controller.locationController;
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
				//判断url区分开需要登录和不需要登录的功能
				//得到请求ip地址
			 String requestUri = request.getRequestURI();
			 String contextPath = request.getContextPath();
			 String url = requestUri.substring(contextPath.length());
			 String  userId ="";
			 String userName = "";
			 //得到访问真实IP地址
			 String addIp = locationController.getWebIP("http://www.ip138.com/ip2city.asp");
			 //通过ip地址定位城市
			 String city = new locationController().getAddresses(addIp,"utf-8");
			 //数据入库
			 UserLogController userLogController = new UserLogController();
			 UserInfo userInfo = (UserInfo) request.getSession().getAttribute(SystemConstant.SESSION_USER);
			 //得到用户信息，如果未登录访问则暂时不存用户信息
			 if (null != userInfo) {
				 userId = String.valueOf(userInfo.getUserId());
				 userName = String.valueOf(userInfo.getUserName());
			}
			 //userLogController.add(addIp, city, requestUri,userId,userName);
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