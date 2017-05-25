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
	 * 登录
	 */
	@SuppressWarnings("static-access")
	@ResponseBody
	@RequestMapping(value="/login",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse doLogin(@RequestBody Map<String, Object> models,HttpServletRequest request,HttpServletResponse response){
		String loginId = String.valueOf(models.get("loginId"));
		String password = String.valueOf(models.get("loginPwd"));
		//首先验证登录账号
		UserInfo userInfo = this.userInfoService.queryByCardNo(loginId);
		XJLResponse xjlResponse = null;
		//判断登录账号是否为身份证
		if (null == userInfo) {
			//判断登录账号是否为手机号
			userInfo = this.userInfoService.queryByPhoneNo(loginId);
			if (null == userInfo) {
				xjlResponse = new XJLResponse();
				xjlResponse.setErrorMsg("您输入的账号错误!");
				xjlResponse.setSuccess(false);
				 return xjlResponse;
			}
		}
		//得到加密密码
		Coder coder = new Coder();
		String pwd = coder.password(userInfo.getCardNo()+password, password);
		//数据库取密码相匹配
		UserPwd userPwd = this.userPwdService.queryByUserId(userInfo);
		if(null != userPwd){
			if(!pwd.equals(userPwd.getPassword())){
				xjlResponse = new XJLResponse();
				xjlResponse.setErrorMsg("您输入的密码有误!");
				xjlResponse.setSuccess(false);
				 return xjlResponse;
			}else{
				//存入session
				 request.getSession().setAttribute(SystemConstant.SESSION_USER, userInfo);//登录成功，将用户数据放入到Session
				 xjlResponse = new XJLResponse();
				 xjlResponse.setSuccess(true);
			}
		}
		return xjlResponse;
	}
	/**
	 * 退出登录
	 */
	public void loginOut(HttpServletRequest request,HttpServletResponse response){
		HttpSession session = request.getSession(false);//防止创建Session
		if (null == session) {}
		session.removeAttribute(SystemConstant.SESSION_USER);
	}
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
		String master= UUID.randomUUID().toString();
		//执行用户插入
		 User user = new User();
		 user.setUserId(userId);
		 user.setUserName(models.get("userName").toString());
		 user.setOrg("91984dde-4f8d-43ac-bed1-cd8eaac9aea3");
		 user.setMaster(master);
		 this.userService.add(user,userDefault);
		 //执行用户密码插入
		 UserPwd userPwd = new UserPwd();
		 userPwd.setUserId(userId);
		 Coder coder = new Coder();
		 String password = coder.password(models.get("cardno").toString()+models.get("password").toString(), models.get("password").toString());
		 userPwd.setPassword(password);
		 userPwd.setOrg("91984dde-4f8d-43ac-bed1-cd8eaac9aea3");
		 userPwd.setMaster(master);
		 this.userPwdService.add(userPwd, userDefault);
		 //执行用户信息插入
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
	 * 修改用户密码
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
			//添加用户信息
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
	 * 调用短信服务发送短信
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
