package com.xjl.pt.form.controller;

import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.xjl.pt.form.controller.SessionTools;
import com.xjl.pt.form.controller.XJLResponse;
import com.xjl.pt.form.controller.BootstrapGridTable;
import com.xjl.pt.core.domain.ZnOrganization;
import com.xjl.pt.core.domain.Dept;
import com.xjl.pt.core.domain.DictItem;
import com.xjl.pt.core.domain.User;
import com.xjl.pt.core.service.DeptService;
import com.xjl.pt.core.service.DictItemService;
import com.xjl.pt.core.service.ZnItemService;
import com.xjl.pt.core.tools.DictItemTools;
import com.xjl.pt.core.service.ZnOrganizationService;
/**
 * 办事指南实施机构控制器类
 * @author ControllerCoderTools Arthur
 *
 */
@Controller
@RequestMapping("/znOrganization")
public class ZnOrganizationController {
	@Autowired
	private SessionTools sessionTools;
	@Autowired
	private DictItemService dictItemService;
	@Autowired
	private DeptService deptService;
	@Autowired
	private ZnOrganizationService znOrganizationService;
	@Autowired
	private ZnItemService znItemService;
	@ResponseBody
	@RequestMapping(value="/query/{page}/{rows}",method=RequestMethod.GET,consumes = "application/json")
	public BootstrapGridTable query(HttpServletRequest request, @PathVariable Integer page,@PathVariable Integer rows){
		String search = StringUtils.trimToNull(request.getParameter("search"));
		List<ZnOrganization> list = this.znOrganizationService.query(search, page, rows);
		Dept dept = null;
		for (ZnOrganization znOrganization : list) {
			//处理实施机构
			dept = this.deptService.queryById(znOrganization.getImplOrg());
			if (null != dept) {
				znOrganization.setImplOrg$name(dept.getDeptName());
			}
			//得到单位下办事指南个数
			znOrganization.setZnOrganizationItemCount(this.znItemService.queryZnItemCount(znOrganization.getOrganizationId()));
		}
		//处理字典
		//处理外键
		return BootstrapGridTable.getInstance(list);
	}

	@ResponseBody
	@RequestMapping(value="/query/{id}",method=RequestMethod.GET,consumes = "application/json")
	public ZnOrganization queryById(HttpServletRequest request, @PathVariable String id){
		ZnOrganization znOrganization = this.znOrganizationService.queryById(id);
		if (znOrganization != null){
			//处理字典
			//处理外键
		}
		return znOrganization;
	}

	@ResponseBody
	@RequestMapping(value="/add",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse add(HttpServletRequest request, @RequestBody ZnOrganization znOrganization){
		User user = this.sessionTools.getUser(request);
		znOrganization.setOrganizationId(UUID.randomUUID().toString());
		this.znOrganizationService.add(znOrganization, user);
		return XJLResponse.successInstance();
	}

	@ResponseBody
	@RequestMapping(value="/modify",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse modify(HttpServletRequest request, @RequestBody ZnOrganization znOrganization){
		User user = this.sessionTools.getUser(request);
		this.znOrganizationService.modify(znOrganization, user);
		return XJLResponse.successInstance();
	}

	@ResponseBody
	@RequestMapping(value="/delete",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse delete(HttpServletRequest request, @RequestBody List<ZnOrganization> list){
		User user = this.sessionTools.getUser(request);
		for (ZnOrganization znOrganization : list) {
			this.znOrganizationService.delete(znOrganization, user);
		}
		return XJLResponse.successInstance();
	}

}

