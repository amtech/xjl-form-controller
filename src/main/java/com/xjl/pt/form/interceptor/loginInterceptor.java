package com.xjl.pt.form.interceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import com.xjl.pt.core.domain.User;
import com.xjl.pt.core.domain.UserLog;
import com.xjl.pt.core.domain.XJLDomain;
import com.xjl.pt.core.service.UserLogService;
import com.xjl.pt.core.service.UserService;
import com.xjl.pt.form.controller.SystemConstant;
import com.xjl.pt.form.controller.locationController;
/**
 * 登录认证拦截器
 * @author guan.zheyuan
 */
public class loginInterceptor implements HandlerInterceptor{
	
	@Autowired  
	private UserLogService userLogService;
	@Autowired  
	private UserService userService;
	
	private static Log log = LogFactory.getLog(loginInterceptor.class);
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
	}
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	}
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
				//判断url区分开需要登录和不需要登录的功能
				//得到请求ip地址
			 String requestUri = request.getRequestURI();
			 String contextPath = request.getContextPath();
			 String url = requestUri.substring(contextPath.length());
			 log.debug("url:" + url);
			 String  userId ="";
			 String userName = "";
			 //得到访问真实IP地址
			 //这个地方应该用异步方法，要不然每次都等待ip138返回结果会很耗时间，并且这个地址经常访问超时，我先注释了
			 //String addIp = locationController.getWebIP("http://www.ip138.com/ip2city.asp");
			 String addIp  = locationController.getIpAddr(request);
			 log.debug("addIp:" + addIp);
			 //通过ip地址定位城市
			 String city = new locationController().getAddresses(addIp,"utf-8");
			 log.debug("city:" + city);
			 //数据入库
			 User user = (User) request.getSession().getAttribute(SystemConstant.SESSION_USER);
			 //得到用户信息，如果未登录访问则暂时不存用户信息
			 if (null != user) {
				 userId = String.valueOf(user.getUserId());
				 userName = String.valueOf(user.getUserName());
			}
			 //日志入库
			 addLog(addIp,city,url,userId,userName);
			if(url.indexOf("/login") != -1 && null == user){
				return true;
			}
//			UserInfo userInfo = (UserInfo) request.getSession().getAttribute(SystemConstant.SESSION_USER);
//			if (null == userInfo) {
//				response.sendRedirect("");
//			}
			return true;
	}
	/**
	 * 日志插入
	 * @param ip ip地址
	 * @param city 城市
	 * @param url 请求路径
	 * @param userId 用户编号
	 * @param userName 用户姓名
	 */
	public  void addLog(String ip,String city,String url,String userId,String userName){
		//添加用户信息
		User userDefault = userService.queryById("9fcfdb3e-3bdb-4234-a0c4-f91d023c308e");
		UserLog userLog = new UserLog();
		userLog.setIp(ip);
		userLog.setCity(city);
		userLog.setUrl(url);
		userLog.setUserId(userId);
		userLog.setUserName(userName);
		userLog.setState(XJLDomain.StateType.A.name());
		userLogService.add(userLog,userDefault);
	}
}
