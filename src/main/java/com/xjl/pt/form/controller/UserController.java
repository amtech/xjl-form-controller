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
 * �û���Ϣ������
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
	 * ִ���û����
	 * @throws IOException 
	 */
	@SuppressWarnings("static-access")
	@ResponseBody
	@RequestMapping(value="/add",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse add(@RequestBody Map<String, Object> models,HttpServletRequest request,HttpServletResponse response) throws IOException{
		//����û���Ϣ
		User userDefault = this.userService.queryById("9fcfdb3e-3bdb-4234-a0c4-f91d023c308e");
		String cardNo =  String.valueOf(models.get("cardno"));
		String phoneNo = String.valueOf(models.get("phone"));
		//��֤���֤�Ƿ�Ψһ
		XJLResponse xjlResponse = null;
		if(null != this.userInfoService.queryByCardNo(cardNo)){
			xjlResponse = new XJLResponse();
			xjlResponse.setErrorMsg("����������֤�Ѿ�����");
			xjlResponse.setSuccess(false);
			 return xjlResponse;
		}
		//��֤�ֻ������Ƿ�Ψһ
		if(null != this.userInfoService.queryByPhoneNo(phoneNo)){
			xjlResponse = new XJLResponse();
			xjlResponse.setErrorMsg("��������ֻ����Ѿ�����");
			xjlResponse.setSuccess(false);
			return xjlResponse;
		}
		String userId = UUID.randomUUID().toString();
		String master= UUID.randomUUID().toString();
		//ִ���û�����
		 User user = new User();
		 user.setUserId(userId);
		 user.setUserName(models.get("userName").toString());
		 user.setOrg("91984dde-4f8d-43ac-bed1-cd8eaac9aea3");
		 user.setMaster(master);
		 this.userService.add(user,userDefault);
		 //ִ���û��������
		 UserPwd userPwd = new UserPwd();
		 userPwd.setUserId(userId);
		 Coder coder = new Coder();
		 String password = coder.password(models.get("cardno").toString()+models.get("password").toString(), models.get("password").toString());
		 userPwd.setPassword(password);
		 userPwd.setOrg("91984dde-4f8d-43ac-bed1-cd8eaac9aea3");
		 userPwd.setMaster(master);
		 this.userPwdService.add(userPwd, userDefault);
		 //ִ���û���Ϣ����
		 UserInfo userInfo = new UserInfo();
		 userInfo.setUserId(userId);
		 userInfo.setUserName(models.get("userName").toString());
		 userInfo.setCardNo(cardNo);
		 userInfo.setPhoneNo(models.get("phone").toString());
		 userInfo.setOrg("91984dde-4f8d-43ac-bed1-cd8eaac9aea3");
		 userInfo.setMaster(master);
		 this.userInfoService.add(userInfo, userDefault);
		 return XJLResponse.successInstance();
	} 
	
	/**
	 * �û�ʵ����֤
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
	 * �޸��û�����
	 */
	@SuppressWarnings("static-access")
	@ResponseBody
	@RequestMapping(value="/updatePwd",method=RequestMethod.POST,consumes = "application/json")
	public void  updatePwdByMaster(@RequestBody Map<String, Object> models){
		String phoneNo = String.valueOf(models.get("phoneNo"));
		UserInfo  userInfo = this.userInfoService.queryByPhoneNo(phoneNo);
		if(null != userInfo){
			userInfo.setUserId(UUID.randomUUID().toString());
			this.userPwdService._resetNewId(userInfo);
			//����û���Ϣ
			User userDefault = this.userService.queryById("9fcfdb3e-3bdb-4234-a0c4-f91d023c308e");
			 UserPwd userPwd = new UserPwd();
			 userPwd.setUserId(UUID.randomUUID().toString());
			 Coder coder = new Coder();
			 String password = coder.password(userInfo.getCardNo()+models.get("password").toString(), models.get("password").toString());
			 userPwd.setPassword(password);
			 userPwd.setOrg("91984dde-4f8d-43ac-bed1-cd8eaac9aea3");
			 userPwd.setMaster(userInfo.getMaster());
			 this.userPwdService.add(userPwd, userDefault);
		}
	}
	
	/**
	 * ���ö��ŷ����Ͷ���
	 * @throws IOException 
	 */
	@ResponseBody
	@RequestMapping(value="/sendMsg",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse  sendMessage(HttpServletRequest request,HttpServletResponse response) throws IOException{
		String phone =  request.getParameter("phoneno");
		String content = this.verifyCode.generate(phone, 1) ;
		XJLResponse xjlResponse = new XJLResponse();
		if(this.sms.sendVerifyCode(phone, content)){
			xjlResponse.setSuccess(true);
			xjlResponse.setShowMsg(content);
		}else{
			xjlResponse.setSuccess(false);
			xjlResponse.setShowMsg(content);
		}
		return xjlResponse;
	}
}
