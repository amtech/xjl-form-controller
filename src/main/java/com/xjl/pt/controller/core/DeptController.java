package com.xjl.pt.controller.core;

import java.util.List;

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
import com.xjl.pt.core.domain.Dept;
import com.xjl.pt.core.domain.User;
import com.xjl.pt.core.service.DeptService;
/**
 * 字典控制器类
 * @author ControllerCoderTools
 *
 */
@Controller
@RequestMapping("/dept")
public class DeptController {
	@Autowired
	private SessionTools sessionTools;
	@Autowired
	private DeptService deptservice;
	@ResponseBody
	@RequestMapping(value="/query/{page}/{rows}",method=RequestMethod.GET,consumes = "application/json")
	public BootstrapGridTable query(HttpServletRequest request, @PathVariable Integer page,@PathVariable Integer rows){
		String search = StringUtils.trimToNull(request.getParameter("search"));
		List<Dept> list = this.deptservice.query(search, page, rows);
		return BootstrapGridTable.getInstance(list);
	}

	@ResponseBody
	@RequestMapping(value="/query/{id}",method=RequestMethod.GET,consumes = "application/json")
	public Dept queryById(HttpServletRequest request, @PathVariable String id){
		Dept dept = this.deptservice.queryById(id);
		return dept;
	}

	@ResponseBody
	@RequestMapping(value="/add",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse add(HttpServletRequest request, @RequestBody Dept dept){
		User user = this.sessionTools.getUser(request);
		this.deptservice.add(dept, user);
		return XJLResponse.successInstance();
	}

	@ResponseBody
	@RequestMapping(value="/modify",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse modify(HttpServletRequest request, @RequestBody Dept dept){
		User user = this.sessionTools.getUser(request);
		this.deptservice.modify(dept, user);
		return XJLResponse.successInstance();
	}

	@ResponseBody
	@RequestMapping(value="/delete",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse delete(HttpServletRequest request, @RequestBody List<Dept> list){
		User user = this.sessionTools.getUser(request);
		for (Dept dept : list) {
			this.deptservice.delete(dept, user);
		}
		return XJLResponse.successInstance();
	}

}

