package com.xjl.cdc.cloud.controller;

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

import com.xjl.cdc.cloud.domain.CdcProjectModule;
import com.xjl.cdc.cloud.service.CdcProjectModuleService;
import com.xjl.cdc.cloud.service.CdcTerminalService;
import com.xjl.pt.core.domain.User;
import com.xjl.pt.form.controller.BootstrapGridTable;
import com.xjl.pt.form.controller.SessionTools;
import com.xjl.pt.form.controller.XJLResponse;

@Controller
@RequestMapping("/cdcProjectModule")
public class CdcProjectModuleController {
	@Autowired
	private SessionTools sessionTools;
	@Autowired
	private CdcProjectModuleService cdcProjectModuleService;
	@Autowired
	private CdcTerminalService cdcTerminalService;
	@ResponseBody
	@RequestMapping(value="/query/{page}/{rows}",method=RequestMethod.GET,consumes = "application/json")
	public BootstrapGridTable query(HttpServletRequest request, @PathVariable Integer page,@PathVariable Integer rows){
		String search = StringUtils.trimToNull(request.getParameter("search"));
		List<CdcProjectModule> list = this.cdcProjectModuleService.query(search, page, rows);
		return BootstrapGridTable.getInstance(list);
	}

	@ResponseBody
	@RequestMapping(value="/query/{id}",method=RequestMethod.GET,consumes = "application/json")
	public CdcProjectModule queryById(HttpServletRequest request, @PathVariable String id){
		CdcProjectModule cdcProjectModule = this.cdcProjectModuleService.queryById(id);
		return cdcProjectModule;
	}
	
	@ResponseBody
	@RequestMapping(value="/queryModule/{id}",method=RequestMethod.GET,consumes = "application/json")
	public BootstrapGridTable queryProjectModuleByProjectId(HttpServletRequest request, @PathVariable String id){
		List<CdcProjectModule> list = this.cdcProjectModuleService.queryProjectModuleByProjectId(id);
		return BootstrapGridTable.getInstance(list);
	}
	
	@ResponseBody
	@RequestMapping(value="/add",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse add(HttpServletRequest request, @RequestBody CdcProjectModule cdcProjectModule){
		User user = this.sessionTools.getUser(request);
		
		this.cdcProjectModuleService.add(cdcProjectModule, user);
		return XJLResponse.successInstance();
	}

	@ResponseBody
	@RequestMapping(value="/modify",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse modify(HttpServletRequest request, @RequestBody CdcProjectModule cdcProjectModule){
		User user = this.sessionTools.getUser(request);
		CdcProjectModule old = this.cdcProjectModuleService.queryById(cdcProjectModule.getModuleId());
		
		this.cdcProjectModuleService.modify(cdcProjectModule, user);
		//判断是不是更新了url，如果是更新了url则自动更新所有已经使用该module的url地址
		if (!StringUtils.equals(cdcProjectModule.getModuleUrl(), old.getModuleUrl())){
			this.cdcTerminalService.updateUrlByModuleId(cdcProjectModule.getModuleUrl(), cdcProjectModule.getModuleId());
		}
		return XJLResponse.successInstance();
	}

	@ResponseBody
	@RequestMapping(value="/delete",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse delete(HttpServletRequest request, @RequestBody List<CdcProjectModule> list){
		User user = this.sessionTools.getUser(request);
		for (CdcProjectModule cdcProjectModule : list) {
			this.cdcProjectModuleService.delete(cdcProjectModule, user);
		}
		return XJLResponse.successInstance();
	}
}
