package com.xjl.pt.form.interceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.xjl.pt.core.domain.User;
import com.xjl.pt.form.controller.SystemConstant;
/**
 * 登录认证拦截器
 * @author guan.zheyuan
 */
public class LoginInterceptor implements HandlerInterceptor{
	
	
	
	private static Log log = LogFactory.getLog(LoginInterceptor.class);
	
	/** 
     * 在系统启动时执行 
     */  
    public void afterPropertiesSet() throws Exception {  
        log.debug("=======初始化CommonInterceptor拦截器=========");  
    } 
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		log.debug("==========postHandle=========");  
	}
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		log.debug("=====afterCompletion===="); 
	}
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
			log.debug("=====preHandle====");  
			 request.setCharacterEncoding("UTF-8");  
		     response.setCharacterEncoding("UTF-8");   
		     response.setContentType("text/html;charset=UTF-8");  
		     //业务逻辑  
			 String requestUri = request.getRequestURI();
			 String contextPath = request.getContextPath();
			 String url = requestUri.substring(contextPath.length());
			 log.debug("url:" + url);
		     //过滤登录、退出访问  
		     String[] noFilters = new String[]{"/loginVerify","/login"};
		     boolean beFilter = true;
		     for (String s : noFilters) {  
		            if (url.indexOf(s) != -1) {  
		                beFilter = false;  
		                break;  
		            }  
		        }
		     if(!beFilter){
		    	 	//判断权限判断用户	
		    	 	User user = (User) request.getSession().getAttribute(SystemConstant.SESSION_USER);
		    	 	if (null == user) {
						//用户未登录
				}
		     }
			return true;
	}
}
