package com.xjl.pt.form.controller;
import java.io.IOException;
import java.util.Calendar;
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
import com.xjl.pt.core.domain.UserLog;
import com.xjl.pt.core.domain.UserPwd;
import com.xjl.pt.core.service.UserInfoService;
import com.xjl.pt.core.service.UserLogService;
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
	private UserLogService userLogService;
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
		String password = String.valueOf(models.get("loginPwd"));
		String loginId = String.valueOf(models.get("loginId"));
		//首先验证登录账号
		UserInfo userInfo = this.userInfoService.queryByCardNo(loginId);
		if(null == userInfo){
			//判断登录账号是否为手机号
			userInfo = this.userInfoService.queryByPhoneNo(loginId);
		}
		XJLResponse xjlResponse = null;
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
				//判断登录所在地是否与上一次一样
				UserLog userLog = this.userLogService.queryUserLogForMax(userInfo.getUserId());
				if (null != userLog) {
					//String addIp = locationController.getWebIP(SystemConstant.LOG_GETIP);
					//String address = locationController.getProvinceName(addIp);
					//判断验证城市是否相同
//					if (null  != address  ||( null != userLog.getCity() && !address.equals(userLog.getCity()))) {
//						xjlResponse = new XJLResponse();
//						xjlResponse.setErrorMsg(SystemConstant.SIGN_CITY_VERIFY);
//						xjlResponse.setSuccess(false);
//						 return xjlResponse;
//					}
//					//判断验证ip是否相同
//					if(!addIp.equals(userLog.getIp())){
//						xjlResponse = new XJLResponse();
//						xjlResponse.setErrorMsg(SystemConstant.SIGN_IP_VERIFY);
//						xjlResponse.setSuccess(false);
//						 return xjlResponse;
//					}
				}
				//存入session,把user存入session
				 User user = this.userService.queryById(userInfo.getUserId());
				 request.getSession().setAttribute(SystemConstant.SESSION_USER, user);//登录成功，将用户数据放入到Session
				 xjlResponse = new XJLResponse();
				 xjlResponse.setSuccess(true);
			}
		}else{
			xjlResponse = new XJLResponse();
			xjlResponse.setErrorMsg("您输入的密码有误!");
			xjlResponse.setSuccess(false);
			 return xjlResponse;
		}
		return XJLResponse.successInstance();
	}
	/**
	 * 退出登录
	 */
	@ResponseBody
	@RequestMapping(value="/loginOut",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse loginOut(HttpServletRequest request,HttpServletResponse response){
		HttpSession session = request.getSession(false);//防止创建Session
		session.removeAttribute(SystemConstant.SESSION_USER);
		return XJLResponse.successInstance();
	}
	
	/**
	 * 展示用户信息
	 */
	@ResponseBody
	@RequestMapping(value="/showUser",method=RequestMethod.POST,consumes = "application/json")
	public User  getSessionUser(HttpServletRequest request,HttpServletResponse response){
		User user = (User) request.getSession().getAttribute(SystemConstant.SESSION_USER);
		return user;
	}
	
	/**
	 * 得到用户信息
	 */
	@ResponseBody
	@RequestMapping(value="/queryUserInfo",method=RequestMethod.POST,consumes = "application/json")
	public UserInfo getUserinfo(@RequestBody Map<String, Object> models){
		String userId = String.valueOf(models.get("userId"));
		return this.userInfoService.queryByUserId(userId);
	}
	
	/**
	 * 个人信息维护
	 */
	@ResponseBody
	@RequestMapping(value="/modifyUserInfo",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse modifyUserInfo(@RequestBody Map<String, Object> models){
		String iphoneNo = String.valueOf(models.get("iphoneNo"));
		String username = String.valueOf(models.get("userName"));
		String cardNo = String.valueOf(models.get("cardNo"));
		String userId = String.valueOf(models.get("userId"));
		String email = String.valueOf(models.get("email"));
		String address = String.valueOf(models.get("address"));
		UserInfo userInfo = this.userInfoService.queryByUserId(userId);
		XJLResponse xjlResponse = null;
		if(null != userInfo){
			//删除
			this.userInfoService.modifyUserInfo(userInfo);
			//添加用户信息
			User userDefault = this.userService.queryById("9fcfdb3e-3bdb-4234-a0c4-f91d023c308e");
			if(!"".equals(iphoneNo)){
				userInfo.setPhoneNo(iphoneNo);
			}
			if("".equals(userInfo.getCardName())){
				userInfo.setCardName(username);
			}
			if("".equals(userInfo.getCardNo())){
				userInfo.setCardNo(cardNo);
			}
			userInfo.setEmail(email);
			userInfo.setAddress(address);
			this.userInfoService.add(userInfo, userDefault);
		}else{
			xjlResponse = new XJLResponse();
			xjlResponse.setErrorMsg("修改信息失败");
			xjlResponse.setSuccess(false);
			return xjlResponse;
		}
		return XJLResponse.successInstance();
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
		 //添加用户信息
		 User userDefault = this.userService.queryById("9fcfdb3e-3bdb-4234-a0c4-f91d023c308e");
		 //执行用户插入
		 User user = new User();
		 user.setUserName(models.get("userName").toString());
		 this.userService.add(user,userDefault);
		 //执行用户密码插入
		 UserPwd userPwd = new UserPwd();
		 userPwd.setUserId(user.getUserId());
		 Coder coder = new Coder();
		 String password = coder.password(models.get("cardno").toString()+models.get("password").toString(), models.get("password").toString());
		 userPwd.setPassword(password);
		 this.userPwdService.add(userPwd, userDefault);
		 //执行用户信息插入
		 UserInfo userInfo = new UserInfo();
		 userInfo.setUserId(user.getUserId());
		 userInfo.setCardName(models.get("userName").toString());
		 userInfo.setCardNo(cardNo);
		 userInfo.setPhoneNo(models.get("phone").toString());
		 userInfo.setUserType("1");
		 this.userInfoService.add(userInfo, userDefault);
		 return XJLResponse.successInstance();
	} 
	
	/**
	 * 用户实名认证
	 */
	@SuppressWarnings("deprecation")
	@ResponseBody
	@RequestMapping(value="/realNameCertification",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse  realNameCertification(@RequestBody Map<String, Object> models){
		String handShot = String.valueOf(models.get("handShot"));
		String cardPhoto = String.valueOf(models.get("cardPhoto"));
		String cardBackPhoto = String.valueOf(models.get("cardBackPhoto"));
		String cardNo = String.valueOf(models.get("cardNo"));
		UserInfo userInfo = new UserInfo();
		userInfo.setHandShotPhotoUrl(handShot);
		userInfo.setHandCardPhotoUrl(cardPhoto);
		userInfo.setCardBackPhotoUrl(cardBackPhoto);
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
			this.userPwdService.modifyUserPwd(userInfo);
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
	
	
	/**
	 * 验证 验证码是否正确
	 * @throws IOException 
	 */
	@ResponseBody
	@RequestMapping(value="/check",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse  check(HttpServletRequest request,HttpServletResponse response,@RequestBody Map<String, Object> models) throws IOException{
		String phone = String.valueOf(models.get("phoneno"));
		String verify = String.valueOf(models.get("verify"));
		boolean flag = this.verifyCode.check(phone, verify);
		XJLResponse xjlResponse = new XJLResponse();
		if(flag == true){
			xjlResponse.setSuccess(true);
		}else{
			xjlResponse.setSuccess(false);
		}
		return xjlResponse;
	}
	
	/**
	 * 将用户照片上传入库  .
	 * @throws IOException 
	 */
	@ResponseBody
	@RequestMapping(value="/checkFace",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse  checkFace(HttpServletRequest request,HttpServletResponse response,@RequestBody Map<String, Object> models) throws IOException{
		String base64Face = String.valueOf(models.get("base64Face"));
		String userid = UUID.randomUUID().toString();
		try{
			//添加用户信息
			User userDefault = this.userService.queryById("9fcfdb3e-3bdb-4234-a0c4-f91d023c308e");
			String path=FileController.uploadFtp(base64Face,userid+SystemConstant.SIGN_HAND_VALUE+SystemConstant.IMAGE_SUFFIX_JPG);//将base64解码成图片后上传FTP
			UserInfo userinfo = new UserInfo();
			userinfo.setOrg(userDefault.getOrg());//预留部分，等签字确认机传来地区数据
			userinfo.setMaster(userDefault.getMaster());//预留部分，master用于存储逻辑删除数据中间的管理，随机数
			userinfo.setCreateUserId(userDefault.getUserId());
			userinfo.setCreateDate(Calendar.getInstance().getTime());
			userinfo.setState(userDefault.getState());
			userinfo.setUserId(userid);
			userinfo.setHandCardPhotoUrl(path);
			String cardNo = "341124199406230030";
			userinfo.setCardNo(cardNo);//预留部分，等签字确认机传来用户数据
//			userinfo.setOrg("1dbcef5f-66bb-4738-8295-15445ed76d5d");//预留功能，org代表城市地址，通过定位功能
//			userinfo.setMaster("9fcfdb3e-3bdb-4234-a0c4-f91d023c308e");//预留部分，master是随机数，关联逻辑删除数据
			this.userInfoService._addHandCardPhotoUrl(userinfo);
			User user=new User();
			user.setUserId(userid);
			user.setUserName("陶杰");//预留部分，刷身份证获取姓名入库
			user.setOrg(userDefault.getOrg());
			user.setMaster(userDefault.getMaster());
			user.setCreateDate(Calendar.getInstance().getTime());
			user.setCreateUserId(userDefault.getUserId());
			user.setState(userDefault.getState());
			this.userService._add(user);
		}catch(Exception e){
			e.printStackTrace();
		}
		XJLResponse xjlResponse = new XJLResponse();
		xjlResponse.setSuccess(true);
		xjlResponse.setShowMsg(userid);
		return xjlResponse;
	}
	
	/**
	 * 签字确认机输入密码加密入库.
	 * @throws IOException 
	 */
	@SuppressWarnings("static-access")
	@ResponseBody
	@RequestMapping(value="/savePassword",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse  savePassword(HttpServletRequest request,HttpServletResponse response,@RequestBody Map<String, Object> models) throws IOException{
		String userid = String.valueOf(models.get("userid"));
		UserPwd userPwd = new UserPwd();
		userPwd.setUserId(userid);
		Coder coder = new Coder();
		String cardNo = "341124199406230030";//用户身份证号码，预留功能，等签字确认机获取全部信息后补全
		String password = coder.password(cardNo+models.get("password").toString(), models.get("password").toString());
		userPwd.setPassword(password);
//		userPwd.setOrg("1dbcef5f-66bb-4738-8295-15445ed76d5d");
//		userPwd.setMaster("9fcfdb3e-3bdb-4234-a0c4-f91d023c308e");
		//添加用户信息
		User userDefault = this.userService.queryById("9fcfdb3e-3bdb-4234-a0c4-f91d023c308e");
		XJLResponse xjlResponse = new XJLResponse();
		try{
			this.userPwdService.add(userPwd,userDefault);
			xjlResponse.setSuccess(true);
		}catch(Exception e){
			xjlResponse.setSuccess(false);
		}
		return xjlResponse;
	}
	
	
	
	
}