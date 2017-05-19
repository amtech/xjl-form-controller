package com.xjl.pt.form.controller;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
	 * @throws IOException 
	 */
	@SuppressWarnings("static-access")
	@ResponseBody
	@RequestMapping(value="/add",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse add(@RequestBody Map<String, Object> models,HttpServletRequest request,HttpServletResponse response) throws IOException{
		//添加用户信息
		User userDefault = this.userService.queryById("9fcfdb3e-3bdb-4234-a0c4-f91d023c308e");
		String cardNo =  String.valueOf(models.get("cardno"));
		String phoneNo = String.valueOf(models.get("phone"));
		//验证身份证是否唯一
		XJLResponse xjlResponse = null;
		if(null != this.userInfoService.queryByCardNo(cardNo)){
			xjlResponse = new XJLResponse();
			xjlResponse.setErrorMsg("您输入的身份证已经存在");
			xjlResponse.setSuccess(false);
			 return xjlResponse;
		}
		//验证手机号码是否唯一
		if(null != this.userInfoService.queryByPhoneNo(phoneNo)){
			xjlResponse = new XJLResponse();
			xjlResponse.setErrorMsg("您输入的手机号已经存在");
			xjlResponse.setSuccess(false);
			return xjlResponse;
		}
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
		 userInfo.setCardNo(cardNo);
		 userInfo.setPhoneNo(models.get("phone").toString());
		 userInfo.setOrg("91984dde-4f8d-43ac-bed1-cd8eaac9aea3");
		 this.userInfoService.add(userInfo, userDefault);
		 return XJLResponse.successInstance();
	} 
	
	/**
	 * 用户实名认证
	 */
	@ResponseBody
	@RequestMapping(value="/realNameCertification",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse  realNameCertification(@RequestBody Map<String, Object> models){
		String handShot = String.valueOf(models.get("handShot"));
		String cardPhoto = String.valueOf(models.get("cardPhoto"));
		String cardBackPhoto = String.valueOf(models.get("cardBackPhoto"));
		String cardNo = String.valueOf(models.get("cardNo"));
		UserInfo userInfo = new UserInfo();
		userInfo.setHandShot(handShot);
		userInfo.setCardPhoto(cardPhoto);
		userInfo.setCardBackPhoto(cardBackPhoto);
		userInfo.setCardNo(cardNo);
		this.userInfoService.modify(userInfo);
		return XJLResponse.successInstance();
	}
	
	/**
	 * 调用短信服务发送短信
	 * @throws IOException 
	 */
	@ResponseBody
	@RequestMapping(value="/sendMsg",method=RequestMethod.POST,consumes = "application/json")
	public boolean  sendMessage(HttpServletRequest request,HttpServletResponse response) throws IOException{
		String phone =  request.getParameter("phoneno");
		String content = this.verifyCode.generate(phone, 1) ;
		return this.sms.sendVerifyCode(phone, content);
	}
}
