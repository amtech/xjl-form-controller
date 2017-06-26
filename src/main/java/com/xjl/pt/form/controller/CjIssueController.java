package com.xjl.pt.form.controller;

import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
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
import com.xjl.pt.core.domain.CjIssue;
import com.xjl.pt.core.domain.User;
import com.xjl.pt.core.service.DictItemService;
import com.xjl.pt.core.service.CjIssueService;
/**
 * 常见问题控制器类
 * @author ControllerCoderTools Arthur
 *
 */
@Controller
@RequestMapping("/cjIssue")
public class CjIssueController {
	@Autowired
	private SessionTools sessionTools;
	@Autowired
	private DictItemService dictItemService;
	@Autowired
	private CjIssueService cjIssueService;
	@ResponseBody
	@RequestMapping(value="/query/{page}/{rows}",method=RequestMethod.GET,consumes = "application/json")
	public BootstrapGridTable query(HttpServletRequest request, @PathVariable Integer page,@PathVariable Integer rows){
		String search = StringUtils.trimToNull(request.getParameter("search"));
		List<CjIssue> list = this.cjIssueService.query(search, page, rows);
		//处理字典
		//处理外键
		return BootstrapGridTable.getInstance(list);
	}

	@ResponseBody
	@RequestMapping(value="/query/{id}",method=RequestMethod.GET,consumes = "application/json")
	public CjIssue queryById(HttpServletRequest request, @PathVariable String id){
		CjIssue cjIssue = this.cjIssueService.queryById(id);
		if (cjIssue != null){
			//处理字典
			//处理外键
		}
		return cjIssue;
	}

	@ResponseBody
	@RequestMapping(value="/add",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse add(HttpServletRequest request, @RequestBody CjIssue cjIssue){
		User user = this.sessionTools.getUser(request);
		cjIssue.setIssueId(UUID.randomUUID().toString());
		this.cjIssueService.add(cjIssue, user);
		return XJLResponse.successInstance();
	}

	@ResponseBody
	@RequestMapping(value="/modify",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse modify(HttpServletRequest request, @RequestBody CjIssue cjIssue){
		User user = this.sessionTools.getUser(request);
		this.cjIssueService.modify(cjIssue, user);
		return XJLResponse.successInstance();
	}

	@ResponseBody
	@RequestMapping(value="/delete",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse delete(HttpServletRequest request, @RequestBody List<CjIssue> list){
		User user = this.sessionTools.getUser(request);
		for (CjIssue cjIssue : list) {
			this.cjIssueService.delete(cjIssue, user);
		}
		return XJLResponse.successInstance();
	}

}

