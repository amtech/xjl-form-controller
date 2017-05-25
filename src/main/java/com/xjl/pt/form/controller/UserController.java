package com.xjl.pt.form.controller;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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
	 * ��¼
	 */
	@SuppressWarnings("static-access")
	@ResponseBody
	@RequestMapping(value="/login",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse doLogin(@RequestBody Map<String, Object> models,HttpServletRequest request,HttpServletResponse response){
		String loginId = String.valueOf(models.get("loginId"));
		String password = String.valueOf(models.get("loginPwd"));
		//������֤��¼�˺�
		UserInfo userInfo = this.userInfoService.queryByCardNo(loginId);
		XJLResponse xjlResponse = null;
		//�жϵ�¼�˺��Ƿ�Ϊ���֤
		if (null == userInfo) {
			//�жϵ�¼�˺��Ƿ�Ϊ�ֻ���
			userInfo = this.userInfoService.queryByPhoneNo(loginId);
			if (null == userInfo) {
				xjlResponse = new XJLResponse();
				xjlResponse.setErrorMsg("��������˺Ŵ���!");
				xjlResponse.setSuccess(false);
				 return xjlResponse;
			}
		}
		//�õ���������
		Coder coder = new Coder();
		String pwd = coder.password(userInfo.getCardNo()+password, password);
		//���ݿ�ȡ������ƥ��
		UserPwd userPwd = this.userPwdService.queryByUserId(userInfo);
		if(null != userPwd){
			if(!pwd.equals(userPwd.getPassword())){
				xjlResponse = new XJLResponse();
				xjlResponse.setErrorMsg("���������������!");
				xjlResponse.setSuccess(false);
				 return xjlResponse;
			}else{
				//����session
				 request.getSession().setAttribute(SystemConstant.SESSION_USER, userInfo);//��¼�ɹ������û����ݷ��뵽Session
				 xjlResponse = new XJLResponse();
				 xjlResponse.setSuccess(true);
			}
		}
		return xjlResponse;
	}
	/**
	 * �˳���¼
	 */
	public void loginOut(HttpServletRequest request,HttpServletResponse response){
		HttpSession session = request.getSession(false);//��ֹ����Session
		if (null == session) {}
		session.removeAttribute(SystemConstant.SESSION_USER);
	}
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
	public XJLResponse  updatePwdByMaster(@RequestBody Map<String, Object> models){
		String phoneNo = String.valueOf(models.get("phoneNo"));
		UserInfo  userInfo = this.userInfoService.queryByPhoneNo(phoneNo);
		String userId = userInfo.getUserId();
		if(null != userInfo){
			userInfo.setUserId(UUID.randomUUID().toString());
			this.userPwdService._resetNewId(userInfo);
			//����û���Ϣ
			User userDefault = this.userService.queryById("9fcfdb3e-3bdb-4234-a0c4-f91d023c308e");
			UserPwd userPwd = new UserPwd();
			userPwd.setUserId(userId);
			Coder coder = new Coder();
			String password = coder.password(userInfo.getCardNo()+models.get("password").toString(), models.get("password").toString());
			userPwd.setPassword(password);
			userPwd.setOrg("91984dde-4f8d-43ac-bed1-cd8eaac9aea3");
			userPwd.setMaster(userInfo.getMaster());
			this.userPwdService.add(userPwd, userDefault);
		}
		return XJLResponse.successInstance();
	}
	
	/**
	 * ���ö��ŷ����Ͷ���
	 * @throws IOException 
	 */
	@ResponseBody
	@RequestMapping(value="/sendMsg",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse  sendMessage(HttpServletRequest request,HttpServletResponse response,@RequestBody Map<String, Object> models) throws IOException{
		String phone =  String.valueOf(models.get("phoneno"));
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
