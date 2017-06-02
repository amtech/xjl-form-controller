package com.xjl.pt.form.controller;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
	 * 登录验证
	 */
	@ResponseBody
	@RequestMapping(value="/loginVerify",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse doLoginVerify(@RequestBody Map<String, Object> models,HttpServletRequest request,HttpServletResponse response){
		String loginId = String.valueOf(models.get("loginId"));
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
		//判断登录所在地是否与上一次一样
		UserLog userLog = this.userLogService.queryUserLogForMax(userInfo.getUserId());
		if (null != userLog) {
			//String addIp = locationController.getIpAddr(request);
			String addIp = locationController.getWebIP("http://www.ip138.com/ip2city.asp");
			String address = locationController.getProvinceName(addIp);
			//判断验证城市是否相同
			if (null == address || !address.equals(userLog.getCity())) {
				xjlResponse = new XJLResponse();
				xjlResponse.setErrorMsg(SystemConstant.SIGN_CITY_VERIFY);
				xjlResponse.setSuccess(false);
				 return xjlResponse;
			}
			//判断验证ip是否相同
			if(!addIp.equals(userLog.getIp())){
				xjlResponse = new XJLResponse();
				xjlResponse.setErrorMsg(SystemConstant.SIGN_IP_VERIFY);
				xjlResponse.setSuccess(false);
				 return xjlResponse;
			}
		}
		return XJLResponse.successInstance();
	}
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
				//存入session,把user存入session
				User user = this.userService.queryById(userInfo.getUserId());
				 request.getSession().setAttribute(SystemConstant.SESSION_USER, user);//登录成功，将用户数据放入到Session
				 xjlResponse = new XJLResponse();
				 xjlResponse.setSuccess(true);
			}
		}
		return XJLResponse.successInstance();
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
		//执行用户插入
		 User user = new User();
		 user.setUserName(models.get("userName").toString());
		 this.userService.add(user,null);
		 //执行用户密码插入
		 UserPwd userPwd = new UserPwd();
		 userPwd.setUserId(user.getUserId());
		 Coder coder = new Coder();
		 String password = coder.password(models.get("cardno").toString()+models.get("password").toString(), models.get("password").toString());
		 userPwd.setPassword(password);
		 this.userPwdService.add(userPwd, user);
		 //执行用户信息插入
		 UserInfo userInfo = new UserInfo();
		 userInfo.setUserId(user.getUserId());
		 userInfo.setUserName(models.get("userName").toString());
		 userInfo.setCardNo(cardNo);
		 userInfo.setPhoneNo(models.get("phone").toString());
		 this.userInfoService.add(userInfo, user);
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
	
	/**
	 * 调用短信服务发送验证码
	 * @throws IOException 
	 */
	@ResponseBody
	@RequestMapping(value="/sendverify",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse  sendverify(HttpServletRequest request,HttpServletResponse response,@RequestBody Map<String, Object> models) throws IOException{
		String phone =  String.valueOf(models.get("phoneno"));
		String content = this.verifyCode.generate(phone, 1) ;
		XJLResponse xjlResponse = null;
		UserInfo  userInfo = this.userInfoService.queryByPhoneNo(phone);
		if(null != userInfo){
			xjlResponse = new XJLResponse();
			xjlResponse.setSuccess(false);
			xjlResponse.setShowMsg("您输入的手机号已经存在");
			return xjlResponse;
		}
		if(this.sms.sendVerifyCode(phone, content)){
			xjlResponse = new XJLResponse();
			xjlResponse.setSuccess(true);
			xjlResponse.setShowMsg(content);
		}else{
			xjlResponse = new XJLResponse();
			xjlResponse.setSuccess(false);
			xjlResponse.setShowMsg(content);
		}
		return XJLResponse.successInstance();
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
		String userid = String.valueOf(models.get("userid"));
		boolean flag = this.verifyCode.check(phone, verify);
		XJLResponse xjlResponse = new XJLResponse();
		if(flag == true){
			xjlResponse.setSuccess(true);
			UserInfo userInfo = new UserInfo();
			userInfo.setUserId(userid);
			userInfo.setPhoneNo(phone);
			this.userInfoService.updatePhone(userInfo);
		}else{
			xjlResponse.setSuccess(false);
		}
		return xjlResponse;
	}
	
	/**
	 * 将用户照片上传入库
	 * @throws IOException 
	 */
	@ResponseBody
	@RequestMapping(value="/checkFace",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse  checkFace(HttpServletRequest request,HttpServletResponse response,@RequestBody Map<String, Object> models) throws IOException{
		String base64Face = String.valueOf(models.get("base64Face"));
		String userid = UUID.randomUUID().toString();
		boolean flag = GenerateImage(base64Face,userid);
		if(flag == true){
			try{
				FileController fileController=new FileController();
				File file = new File("d://"+userid+".jpg");
				fileController.uploadFtp(file);
				String path = SystemConstant.FTP_PATH+"/"+userid+".jpg";
				UserInfo userinfo = new UserInfo();
				userinfo.setOrg("");
				userinfo.setUserId(userid);
				userinfo.setHandCardPhotoUrl(path);
				String cardNo = "341124199406230030";
				userinfo.setCardNo(cardNo);//预留部分，等签字确认机传来用户数据
				this.userInfoService.insertHandCardPhotoUrl(userinfo);
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		XJLResponse xjlResponse = new XJLResponse();
		xjlResponse.setSuccess(true);
		xjlResponse.setShowMsg(userid);
		return xjlResponse;
	}
	
	/**
	 * 签字确认机输入密码加密入库
	 * @throws IOException 
	 */
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
		XJLResponse xjlResponse = new XJLResponse();
		
		try{
			this.userPwdService._add(userPwd);
			xjlResponse.setSuccess(true);
		}catch(Exception e){
			xjlResponse.setSuccess(false);
		}
		return xjlResponse;
	}
	
	public static boolean GenerateImage(String imgStr,String userid)  
    {   //对字节数组字符串进行Base64解码并生成图片  
        if (imgStr == null) //图像数据为空  
            return false;  
        @SuppressWarnings("restriction")
		sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();  
    try   
        {  
            //Base64解码  
            byte[] b = decoder.decodeBuffer(imgStr);  
            for(int i=0;i<b.length;++i)  
            {  
                if(b[i]<0)  
                {//调整异常数据  
                    b[i]+=256;  
                }  
            }  
            //生成jpeg图片  
            String imgFilePath = "d://"+userid+".jpg";//新生成的图片  
            OutputStream out = new FileOutputStream(imgFilePath);      
            out.write(b);  
            out.flush();  
            out.close();  
            return true;  
        }   
        catch (Exception e)   
        {  
            return false;  
        }  
    } 
	
	
}