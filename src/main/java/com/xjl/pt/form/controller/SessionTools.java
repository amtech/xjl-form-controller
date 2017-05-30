package com.xjl.pt.form.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xjl.pt.core.domain.User;
import com.xjl.pt.core.domain.UserInfo;
import com.xjl.pt.core.service.UserInfoService;
import com.xjl.pt.core.service.UserService;
/**
 * session工具，用户存和取session中的内容
 * @author lilisheng
 *
 */
@Component
public class SessionTools {
	private static Log log = LogFactory.getLog(SessionTools.class);
	@Autowired
	private UserService userService;
	public void setUser(HttpServletRequest request, User user){
		request.getSession().setAttribute(SystemConstant.SESSION_USER, user);
	}
	public User getUser(HttpServletRequest request){
		Object obj = request.getSession().getAttribute("SystemConstant.SESSION_USER");
		if (obj == null){
			log.debug("用户还没有登录，从session中没有获取到值，从数据库中临时获取一个，便于测试");
			log.debug("在用户登录功能完成之后，需要把这个临时用户去除");
			User user = this.userService.queryFixUser();
			return user;
		} else {
			return (User)obj;
		}
	}
}
