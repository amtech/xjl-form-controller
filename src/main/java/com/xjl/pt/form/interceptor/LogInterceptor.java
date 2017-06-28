package com.xjl.pt.form.interceptor;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
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
	public SimpleDateFormat sdf = new SimpleDateFormat(SystemConstant.FOMATDATE_MINUTE);
	
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
           		 addIp = locationController.getWebIP(SystemConstant.LOG_GETIP);
           		 //隐藏定位城市，重新设置定时任务每晚执行
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
				userLog.setCreateDate(new Date());
				userLogList.add(userLog);
				//因为数据库磁盘不足，一直报错，暂时注释日志记录
//				if (userLogList.size()>5) {
//					List<UserLog> list = removeDuplicate(userLogList);
//					 for (UserLog userLog2 : list) {
//						 userLogService.add(userLog2,userDefault);
//					}
//					 userLogList.clear();
//				}
            }
        }.start();
	}
	public ArrayList<UserLog> removeDuplicate(List<UserLog> userLogs){
		  Set<UserLog> set = new TreeSet<UserLog>(new Comparator<UserLog>() {
				@Override
				public int compare(UserLog o1, UserLog o2) {
					//字符串,则按照asicc码升序排列
					int result = 0;
					if(o1.getIp().equals(o2.getIp())){
						result = sdf.format(o1.getCreateDate()).compareTo(sdf.format(o2.getCreateDate()));
					} 
	                return result;
				}
		  });
		  set.addAll(userLogs);
          return new ArrayList<UserLog>(set);
	}
}
