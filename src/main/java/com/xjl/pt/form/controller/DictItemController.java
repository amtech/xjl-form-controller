package com.xjl.pt.form.controller;

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

import com.xjl.pt.core.domain.DictItem;
import com.xjl.pt.core.domain.User;
import com.xjl.pt.core.service.DictItemService;
import com.xjl.pt.core.service.UserService;
/**
 * 
 * @author lilisheng
 *
 */
@Controller
@RequestMapping("/dictItem")
public class DictItemController {
	@Autowired
	private DictItemService dictItemService;
	@Autowired
	private UserService userService;
	@ResponseBody
	@RequestMapping(value="/query/{page}/{rows}",method=RequestMethod.GET,consumes = "application/json")
	public BootstrapGridTable query(HttpServletRequest request, @PathVariable Integer page,@PathVariable Integer rows){
		String dictId = request.getParameter("dictId");
		String search = request.getParameter("search");
		List<DictItem> list = null;
		if (StringUtils.isEmpty(search)){
			list = this.dictItemService.queryByDictId(dictId,page,rows);
		} else {
			list = this.dictItemService.queryByDictId(dictId, search, page, rows);
		}
		return BootstrapGridTable.getInstance(list);
	}
	@ResponseBody
	@RequestMapping(value="/add",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse add(@RequestBody DictItem dictItem){
		User user = this.userService.queryById("9fcfdb3e-3bdb-4234-a0c4-f91d023c308e");
		
		this.dictItemService.add(dictItem, user);
		return XJLResponse.successInstance();
	}
	@ResponseBody
	@RequestMapping(value="/modify",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse modify(@RequestBody DictItem dictItem){
		User user = this.userService.queryById("9fcfdb3e-3bdb-4234-a0c4-f91d023c308e");
		
		this.dictItemService.modify(dictItem, user);
		return XJLResponse.successInstance();
	}
}
