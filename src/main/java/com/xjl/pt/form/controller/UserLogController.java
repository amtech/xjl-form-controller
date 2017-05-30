package com.xjl.pt.form.controller;

import org.springframework.beans.factory.annotation.Autowired;

import com.xjl.pt.core.domain.User;
import com.xjl.pt.core.domain.UserLog;
import com.xjl.pt.core.domain.XJLDomain;
import com.xjl.pt.core.service.UserLogService;

/**
 * 系统日志控制器
 * @author guan.zheyuan
 */
public class UserLogController  {
	@Autowired  
	private UserLogService userLogService;
	@Autowired
	private SessionTools sessionTools;
	
	/**
	 * 日志插入
	 * @param ip ip地址
	 * @param city 城市
	 * @param url 请求路径
	 * @param userId 用户编号
	 * @param userName 用户姓名
	 */
	public  void add(String ip,String city,String url,String userId,String userName){
		userLogService.print();
		UserLog userLog = new UserLog();
		userLog.setIp(ip);
		userLog.setCity(city);
		userLog.setUrl(url);
		userLog.setUserId(userId);
		userLog.setUserName(userName);
		userLog.setState(XJLDomain.StateType.A.name());
		User user = null;
		//用户id应当从session中获取
		//user = sessionTools.getUser(request);
		userLogService.add(userLog, user);
	}
}
