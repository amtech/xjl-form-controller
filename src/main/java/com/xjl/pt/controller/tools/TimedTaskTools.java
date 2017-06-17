package com.xjl.pt.controller.tools;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xjl.pt.core.domain.UserLog;
import com.xjl.pt.core.service.UserLogService;
import com.xjl.pt.core.service.UserService;
import com.xjl.pt.form.controller.locationController;
import com.xjl.pt.form.interceptor.LogInterceptor;
/**
 * 定时任务工具
 * @author guan.zheyuan
 */
@Component
public class TimedTaskTools {

	private static Log log = LogFactory.getLog(LogInterceptor.class);
	@Autowired  
	private UserLogService userLogService;
	
	/**
	 * 定时执行修改城市名称
	 */
	public void locationCity(){
		List<UserLog> list = this.userLogService.queryUserLogForCityNull();
		log.debug("得到日志列表:"+list.size());
		if (null != list) {
			String city = "";
			UserLog userLog = null;
			for (UserLog tempuserLog : list) {
				try {
					city = new locationController().getAddresses(tempuserLog.getIp().toString(),"utf-8");
				} catch (UnsupportedEncodingException e) {
					log.debug(e);
				}
				log.debug("master:"+tempuserLog.getMaster());
				userLog = new UserLog();
				userLog.setCity(city=="0"?null:city);
				userLog.setMaster(tempuserLog.getMaster());
				this.userLogService.addCity(userLog);
			}
		}
	}
	
}
