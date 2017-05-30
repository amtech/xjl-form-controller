package com.xjl.pt.form.controller;

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

import com.xjl.pt.core.domain.Dict;
import com.xjl.pt.core.domain.User;
import com.xjl.pt.core.service.DictService;
import com.xjl.pt.core.service.DictItemService;
/**
 * 字典控制器类
 * @author li.lisheng
 *
 */
@Controller
@RequestMapping("/dict")
public class DictController {
	@Autowired
	private DictService dictService;
	@Autowired
	private DictItemService dictItemService;
	@Autowired
	private SessionTools sessionTools;
	@ResponseBody
	@RequestMapping(value="/query/{page}/{rows}",method=RequestMethod.GET,consumes = "application/json")
	public BootstrapGridTable query(HttpServletRequest request, @PathVariable Integer page,@PathVariable Integer rows){
		String search = StringUtils.trimToNull(request.getParameter("search"));
		List<Dict> list = this.dictService.queryByName(search, page, rows);
		for (Dict dict : list) {
			dict.setDictItemCount(this.dictItemService.countByDictId(dict.getDictId()));
		}
		return BootstrapGridTable.getInstance(list);
	}
	@ResponseBody
	@RequestMapping(value="/add",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse add(HttpServletRequest request, @RequestBody Dict dict){
		User user = this.sessionTools.getUser(request);
		this.dictService.add(dict, user);
		return XJLResponse.successInstance();
		
	}
	@ResponseBody
	@RequestMapping(value="/modify",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse modify(HttpServletRequest request, @RequestBody Dict dict){
		User user = this.sessionTools.getUser(request);
		this.dictService.modify(dict, user);
		return XJLResponse.successInstance();
	}
	@ResponseBody
	@RequestMapping(value="/delete",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse delete(HttpServletRequest request, @RequestBody List<Dict> list){
		User user = this.sessionTools.getUser(request);
		for (Dict dict : list) {
			System.out.println(dict.getDictId() + ":" + dict.getDictName());
			this.dictService.delete(dict, user);
		}
		return XJLResponse.successInstance();
	}
}
