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

import com.xjl.cdc.cloud.domain.CdcProject;
import com.xjl.cdc.cloud.domain.CdcProjectModule;
import com.xjl.cdc.cloud.service.CdcProjectModuleService;
import com.xjl.cdc.cloud.service.CdcProjectService;
import com.xjl.pt.core.domain.User;
import com.xjl.pt.form.controller.BootstrapGridTable;
import com.xjl.pt.form.controller.SessionTools;
import com.xjl.pt.form.controller.XJLResponse;

@Controller
@RequestMapping("/cdcProject")
public class CdcProjectController {
	@Autowired
	private SessionTools sessionTools;
	@Autowired
	private CdcProjectService cdcProjectService;
	@Autowired
	private CdcProjectModuleService cdcProjectModuleService;
	
	@ResponseBody
	@RequestMapping(value="/query",method=RequestMethod.GET,consumes = "application/json")
	public BootstrapGridTable query(HttpServletRequest request){
		String search = StringUtils.trimToNull(request.getParameter("search"));
		List<CdcProject> list = this.cdcProjectService.query(search);
		
		return BootstrapGridTable.getInstance(list);
	}
	
	@ResponseBody
	@RequestMapping(value="/query/{page}/{rows}",method=RequestMethod.GET,consumes = "application/json")
	public BootstrapGridTable query(HttpServletRequest request, @PathVariable Integer page,@PathVariable Integer rows){
		String search = StringUtils.trimToNull(request.getParameter("search"));
		List<CdcProject> list = this.cdcProjectService.query(search, page, rows);
		
		return BootstrapGridTable.getInstance(list);
	}

	@ResponseBody
	@RequestMapping(value="/query/{id}",method=RequestMethod.GET,consumes = "application/json")
	public CdcProject queryById(HttpServletRequest request, @PathVariable String id){
		CdcProject cdcProject = this.cdcProjectService.queryById(id);
		
		return cdcProject;
	}

	@ResponseBody
	@RequestMapping(value="/add",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse add(HttpServletRequest request, @RequestBody CdcProject cdcProject){
		User user = this.sessionTools.getUser(request);
		this.cdcProjectService.add(cdcProject, user);
		return XJLResponse.successInstance();
	}

	@ResponseBody
	@RequestMapping(value="/modify",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse modify(HttpServletRequest request, @RequestBody CdcProject cdcProject){
		User user = this.sessionTools.getUser(request);
		this.cdcProjectService.modify(cdcProject, user);
		return XJLResponse.successInstance();
	}

	@ResponseBody
	@RequestMapping(value="/delete",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse delete(HttpServletRequest request, @RequestBody List<CdcProject> list){
		User user = this.sessionTools.getUser(request);
		for (CdcProject cdcProject : list) {
			String projectId = cdcProject.getProjectId();
			CdcProjectModule cdcProjectModule = new CdcProjectModule();
			cdcProjectModule.setProjectId(projectId);
			this.cdcProjectModuleService.deleteByProjectId(cdcProjectModule, user);
			this.cdcProjectService.delete(cdcProject, user);
		}
		return XJLResponse.successInstance();
	}
}
