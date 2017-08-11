package com.xjl.rcu.monitor.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xjl.pt.core.domain.DictItem;
import com.xjl.pt.core.domain.User;
import com.xjl.pt.core.service.DictItemService;
import com.xjl.pt.core.tools.DictItemTools;
import com.xjl.pt.form.controller.BootstrapGridTable;
import com.xjl.pt.form.controller.SessionTools;
import com.xjl.pt.form.controller.XJLResponse;
import com.xjl.rcu.monitor.domain.RcuErrorLog;
import com.xjl.rcu.monitor.service.RcuErrorLogService;
/**
 * rcu错误日志表控制器类
 * @author ControllerCoderTools lilisheng
 *
 */
@Controller
@RequestMapping("/rcuErrorLog")
public class RcuErrorLogController {
	@Autowired
	private SessionTools sessionTools;
	@Autowired
	private DictItemService dictItemService;
	@Autowired
	private RcuErrorLogService rcuErrorLogService;
	@ResponseBody
	@RequestMapping(value="/query/{page}/{rows}",method=RequestMethod.GET,consumes = "application/json")
	public BootstrapGridTable query(HttpServletRequest request, @PathVariable Integer page,@PathVariable Integer rows){
		String search = StringUtils.trimToNull(request.getParameter("search"));
		List<RcuErrorLog> list = this.rcuErrorLogService.query(search, page, rows);
		//处理字典
		List<DictItem> deviceCodeDictItems = this.dictItemService.queryByDictId("5741ffd0-7fd1-409f-bb35-157099b74bcb", 1, 1000);
		for (RcuErrorLog rcuErrorLog : list) {
			rcuErrorLog.setDeviceCode$name(DictItemTools.getDictItemNames(rcuErrorLog.getDeviceCode(), deviceCodeDictItems));
		}
		//处理外键
		return BootstrapGridTable.getInstance(list);
	}

	@ResponseBody
	@RequestMapping(value="/query/{id}",method=RequestMethod.GET,consumes = "application/json")
	public RcuErrorLog queryById(HttpServletRequest request, @PathVariable String id){
		RcuErrorLog rcuErrorLog = this.rcuErrorLogService.queryById(id);
		if (rcuErrorLog != null){
			//处理字典
			List<DictItem> deviceCodeDictItems = this.dictItemService.queryByDictId("5741ffd0-7fd1-409f-bb35-157099b74bcb", 1, 1000);
			rcuErrorLog.setDeviceCode$name(DictItemTools.getDictItemNames(rcuErrorLog.getDeviceCode(), deviceCodeDictItems));
			//处理外键
		}
		return rcuErrorLog;
	}

	@ResponseBody
	@RequestMapping(value="/add",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse add(HttpServletRequest request, @RequestBody RcuErrorLog rcuErrorLog){
		User user = this.sessionTools.getUser(request);
		this.rcuErrorLogService.add(rcuErrorLog, user);
		return XJLResponse.successInstance();
	}

	@ResponseBody
	@RequestMapping(value="/modify",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse modify(HttpServletRequest request, @RequestBody RcuErrorLog rcuErrorLog){
		User user = this.sessionTools.getUser(request);
		this.rcuErrorLogService.modify(rcuErrorLog, user);
		return XJLResponse.successInstance();
	}

	@ResponseBody
	@RequestMapping(value="/delete",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse delete(HttpServletRequest request, @RequestBody List<RcuErrorLog> list){
		User user = this.sessionTools.getUser(request);
		for (RcuErrorLog rcuErrorLog : list) {
			this.rcuErrorLogService.delete(rcuErrorLog, user);
		}
		return XJLResponse.successInstance();
	}
	
	@ResponseBody
	@RequestMapping(value="/queryByErrorTime",method=RequestMethod.GET,consumes = "application/json")
	public BootstrapGridTable queryByErrorTime(HttpServletRequest request, @RequestParam(value="beginTime", required=false) String beginTime,@RequestParam(value="endTime", required=false) String endTime){
		List<RcuErrorLog> list = this.rcuErrorLogService.queryByErrorTime(beginTime, endTime);
		return BootstrapGridTable.getInstance(list);
	}
	@ResponseBody
	@RequestMapping(value="/queryByErrorTimeTj",method=RequestMethod.GET,consumes = "application/json")
	public BootstrapGridTable queryByErrorTimeTj(HttpServletRequest request, @RequestParam(value="beginTime", required=false) String beginTime,@RequestParam(value="endTime", required=false) String endTime){
		List<RcuErrorLog> list = this.rcuErrorLogService.queryByErrorTimeTj(beginTime, endTime);
		return BootstrapGridTable.getInstance(list);
	}
}

