package com.xjl.cdc.cloud.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xjl.cdc.cloud.domain.CdcTerminal;
import com.xjl.cdc.cloud.domain.CdcTerminalLog;
import com.xjl.cdc.cloud.service.CdcTerminalLogService;
import com.xjl.cdc.cloud.service.CdcTerminalService;
import com.xjl.pt.core.domain.User;
import com.xjl.pt.form.controller.BootstrapGridTable;
import com.xjl.pt.form.controller.SessionTools;
import com.xjl.pt.form.controller.XJLResponse;

/**
 * 云检测中心终端日志记录控制器类
 * @author you.guess
 *
 */
@Controller
@RequestMapping("/cdcTerminalLog")
public class CdcTerminalLogController {
	@Autowired
	private SessionTools sessionTools;
	@Autowired
	private CdcTerminalService cdcTerminalService;
	@Autowired
	private CdcTerminalLogService cdcTerminalLogService;
	@ResponseBody
	@RequestMapping(value="/query/{page}/{rows}",method=RequestMethod.GET,consumes = "application/json")
	public BootstrapGridTable query(HttpServletRequest request, @PathVariable Integer page,@PathVariable Integer rows){
		String search = StringUtils.trimToNull(request.getParameter("search"));
		List<CdcTerminalLog> list = this.cdcTerminalLogService.query(search, page, rows);
		
		return BootstrapGridTable.getInstance(list);
	}
	@ResponseBody
	@RequestMapping(value="/query/{id}",method=RequestMethod.GET,consumes = "application/json")
	public CdcTerminalLog queryById(HttpServletRequest request, @PathVariable String id){
		CdcTerminalLog cdcTerminalLog = this.cdcTerminalLogService.queryById(id);
		return cdcTerminalLog;
	}
	@ResponseBody
	@RequestMapping(value="/query/guid/{guid}",method=RequestMethod.GET,consumes = "application/json")
	public BootstrapGridTable queryByGUID(HttpServletRequest request, @PathVariable String guid){
		List<CdcTerminalLog> list = this.cdcTerminalLogService.queryByGUID(guid);
		return BootstrapGridTable.getInstance(list);
	}
	@ResponseBody
	@RequestMapping(value="/query/tmid/{tmid}",method=RequestMethod.GET,consumes = "application/json")
	public BootstrapGridTable queryByTMID(HttpServletRequest request, @PathVariable String tmid){
		List<CdcTerminalLog> list = this.cdcTerminalLogService.queryByTMId(tmid);
		return BootstrapGridTable.getInstance(list);
	}
	@ResponseBody
	@RequestMapping(value="/queryByParams",method=RequestMethod.POST,consumes = "application/json")
	public BootstrapGridTable queryByOperTime(HttpServletRequest request, @RequestBody CdcTerminalLog cdcTerminalLog){
		String terminalGUID = cdcTerminalLog.getTerminalGuid();
		String beginTime = cdcTerminalLog.getBeginTime();
		String endTime = cdcTerminalLog.getEndTime();
		List<CdcTerminalLog> list = this.cdcTerminalLogService.queryByParams(terminalGUID, beginTime, endTime);
		return BootstrapGridTable.getInstance(list);
	}
	@ResponseBody
	@RequestMapping(value="/queryByParamsTj",method=RequestMethod.POST,consumes = "application/json")
	public BootstrapGridTable queryByOperTimeTj(HttpServletRequest request, @RequestBody CdcTerminalLog cdcTerminalLog){
		String beginTime = cdcTerminalLog.getBeginTime();
		String endTime = cdcTerminalLog.getEndTime();
		List<CdcTerminalLog> list = this.cdcTerminalLogService.queryByParamsTj(beginTime, endTime);
		return BootstrapGridTable.getInstance(list);
	}
	@ResponseBody
	@RequestMapping(value="/add",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse add(HttpServletRequest request, @RequestBody CdcTerminalLog cdcTerminalLog){
		User user = this.sessionTools.getUser(request);
		// 根据终端设备唯一编码GUID查找系统记录的TerminalId
		CdcTerminal cdcTerminal = this.cdcTerminalService.queryByGUID(cdcTerminalLog.getTerminalGuid());
		if(cdcTerminal!=null) {
			String terminalId = cdcTerminal.getTerminalId();
			cdcTerminalLog.setTerminalId(terminalId);
		}
		cdcTerminalLog.setOperateDate(Calendar.getInstance().getTime());
		String operateType = cdcTerminalLog.getOperateType();
		if("1".equals(operateType)) {
			cdcTerminalLog.setOperateDesc("出厂检测");
		}else if("2".equals(operateType)) {
			cdcTerminalLog.setOperateDesc("主页设置");
		}else {
			cdcTerminalLog.setOperateDesc("正常运行");
		}
		this.cdcTerminalLogService.add(cdcTerminalLog, user);
		return XJLResponse.successInstance();
	}
}
