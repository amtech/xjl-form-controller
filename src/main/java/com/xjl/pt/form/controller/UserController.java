package com.xjl.pt.form.controller;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.xjl.notification.sms.SMS;
import com.xjl.notification.verifyCode.VerifyCode;
import com.xjl.pt.core.domain.User;
import com.xjl.pt.core.domain.UserInfo;
import com.xjl.pt.core.domain.UserPwd;
import com.xjl.pt.core.service.UserInfoService;
import com.xjl.pt.core.service.UserPwdService;
import com.xjl.pt.core.service.UserService;
import com.xjl.pt.security.Coder;

/**
 * 用户信息控制类
 * @author guan.zheyuan
 */
@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserService userService;
	@Autowired
	private UserPwdService userPwdService;
	@Autowired
	private UserInfoService userInfoService;
	@Autowired
	private VerifyCode verifyCode;
	@Autowired
	private SMS sms;
	/**
	 * 执行用户添加
	 * @param user
	 * @return
	 */
	@SuppressWarnings("static-access")
	@ResponseBody
	@RequestMapping(value="/add",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse add(@RequestBody Map<String, Object> models){
		//添加用户信息
		User userDefault = this.userService.queryById("9fcfdb3e-3bdb-4234-a0c4-f91d023c308e");
		String userId = UUID.randomUUID().toString();
		//执行用户插入
		 User user = new User();
		 user.setUserId(userId);
		 user.setUserName(models.get("userName").toString());
		 user.setOrg("91984dde-4f8d-43ac-bed1-cd8eaac9aea3");
		 this.userService.add(user,userDefault);
		 //执行用户密码插入
		 UserPwd userPwd = new UserPwd();
		 userPwd.setUserId(userId);
		 Coder coder = new Coder();
		 String password = coder.password(models.get("cardno").toString()+models.get("password").toString(), models.get("password").toString());
		 userPwd.setPassword(password);
		 userPwd.setOrg("91984dde-4f8d-43ac-bed1-cd8eaac9aea3");
		 this.userPwdService.add(userPwd, userDefault);
		 //执行用户信息插入
		 UserInfo userInfo = new UserInfo();
		 userInfo.setUserId(userId);
		 userInfo.setUserName(models.get("userName").toString());
		 userInfo.setCardNo(models.get("cardno").toString());
		 userInfo.setPhoneNo(models.get("phone").toString());
		 userInfo.setOrg("91984dde-4f8d-43ac-bed1-cd8eaac9aea3");
		 this.userInfoService.add(userInfo, userDefault);
		 return XJLResponse.successInstance();
	}
	
	/**
	 * 调用短信服务发送短信
	 */
	@RequestMapping(value="/sendMsg",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse  sendMessage(HttpServletRequest request){
		String phone =  request.getParameter("phoneno");
		String content = this.verifyCode.generate(phone, 1) ;
		this.sms.sendVerifyCode(phone, content);
		return XJLResponse.successInstance();
	}
}
