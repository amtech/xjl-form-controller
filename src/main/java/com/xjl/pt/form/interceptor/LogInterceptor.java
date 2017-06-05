package com.xjl.pt.form.interceptor;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
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
 * 创建日志
 * @author guan.zheyuan
 */
public class LogInterceptor implements HandlerInterceptor{
	private static Log log = LogFactory.getLog(LogInterceptor.class);
	@Autowired  
	private UserLogService userLogService;
	@Autowired  
	private UserService userService;
	
	public static List<UserLog> userLogList = new ArrayList<>();
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
	}

	@Override
	public void afterCompletion( HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		final  User user = (User) request.getSession().getAttribute(SystemConstant.SESSION_USER);
		 //得到访问真实IP地址
  		 String requestUri = request.getRequestURI();
  		 String contextPath = request.getContextPath();
  		 final String url = requestUri.substring(contextPath.length());
		//添加用户信息
		final User userDefault = userService.queryById("9fcfdb3e-3bdb-4234-a0c4-f91d023c308e");
		//异步调用
		new Thread(){
            public void run() {
            		 String city ="";
                	 String  userId ="";
                	 String addIp ="";
           		 String userName = "";
           		 log.debug("url:" + url);
           		 log.debug("addIp:" + addIp);
           		 //通过ip地址定位城市
				try {
					addIp = locationController.getWebIP(SystemConstant.LOG_GETIP);
					city = new locationController().getAddresses(addIp,"utf-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
           		 log.debug("city:" + city);
           		 //得到用户信息，如果未登录访问则暂时不存用户信息
           		 if (null != user) {
           			 userId = String.valueOf(user.getUserId());
           			 userName = String.valueOf(user.getUserName());
           		}
           		//组装参数
				UserLog userLog = new UserLog();
				userLog.setIp(addIp);
				userLog.setCity(city);
				userLog.setUrl(url);
				userLog.setUserId(userId);
				userLog.setUserName(userName);
				userLog.setState(XJLDomain.StateType.A.name());
				userLogService.add(userLog,userDefault);
            }
        }.start();
	}
	
 
	
}
