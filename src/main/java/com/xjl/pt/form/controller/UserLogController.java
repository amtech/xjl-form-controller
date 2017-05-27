package com.xjl.pt.form.controller;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;

import com.xjl.pt.core.domain.User;
import com.xjl.pt.core.domain.UserLog;
import com.xjl.pt.core.domain.XJLDomain;
import com.xjl.pt.core.service.UserLogService;
import com.xjl.pt.core.service.UserService;

/**
 * 系统日志控制器
 * @author guan.zheyuan
 */
public class UserLogController  {
	@Autowired  
	private UserLogService userLogService;
	@Autowired  
	private UserService userService;
	
	/**
	 * 日志插入
	 * @param ip ip地址
	 * @param city 城市
	 * @param url 请求路径
	 * @param userId 用户编号
	 * @param userName 用户姓名
	 */
	public  void add(String ip,String city,String url,String userId,String userName){
		ClassPathXmlApplicationContext xmlBeanFactory  = new ClassPathXmlApplicationContext("ApplicationContext-example.xml");
		userLogService = (UserLogService) xmlBeanFactory.getBean("userLogService");
		userLogService.print();
		//添加用户信息
		User userDefault = userService.queryById("9fcfdb3e-3bdb-4234-a0c4-f91d023c308e");
		UserLog userLog = new UserLog();
		userLog.setIp(ip);
		userLog.setCity(city);
		userLog.setUrl(url);
		userLog.setUserId(userId);
		userLog.setUserName(userName);
		userLog.setState(XJLDomain.StateType.A.name());
		userLogService.add(userDefault, userLog);
	}
 
}
