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

import com.xjl.pt.core.domain.Table;
import com.xjl.pt.core.domain.User;
import com.xjl.pt.core.service.TableFieldService;
import com.xjl.pt.core.service.TableService;
import com.xjl.pt.core.service.UserService;
@Controller
@RequestMapping("/table")
public class TableController {
	@Autowired
	private UserService userService;
	@Autowired
	private TableService tableService;
	@Autowired
	private TableFieldService tableFieldService;
	@ResponseBody
	@RequestMapping(value="/query/{page}/{pageSize}",method=RequestMethod.GET,consumes = "application/json")
	public BootstrapGridTable query(HttpServletRequest request, @PathVariable Integer page,@PathVariable Integer pageSize){
		String search = request.getParameter("search");
		List<Table> list = null;
		if (StringUtils.isEmpty(search)){
			list = this.tableService.query(page, pageSize);
		} else {
			list = this.tableService.queryBySearch(search, page, pageSize);
		}
		for (Table table : list) {
			table.setTableFieldCount(this.tableFieldService.queryCountByTableId(table.getTableId()));
		}
		return BootstrapGridTable.getInstance(list);
	}
	@ResponseBody
	@RequestMapping(value="/add",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse add(@RequestBody Table table){
		User user = this.userService.queryById("9fcfdb3e-3bdb-4234-a0c4-f91d023c308e");
		this.tableService.add(table, user);
		return XJLResponse.successInstance();
	}
	@ResponseBody
	@RequestMapping(value="/delete",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse delete(@RequestBody List<Table> list){
		User user = this.userService.queryFixUser();
		for (Table table : list) {
			this.tableService.delete(table, user);
		}
		return XJLResponse.successInstance();
		
	}
	@ResponseBody
	@RequestMapping(value="/modify",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse modify(@RequestBody Table table){
		User user = this.userService.queryFixUser();
		this.tableService.modify(table, user);
		return XJLResponse.successInstance();
	}
}
