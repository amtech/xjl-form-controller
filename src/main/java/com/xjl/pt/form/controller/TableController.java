package com.xjl.pt.form.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xjl.pt.core.domain.User;
import com.xjl.pt.core.service.UserService;
import com.xjl.pt.form.domain.Table;
import com.xjl.pt.form.service.TableFieldService;
/**
 * 表的控制器
 * @author lilisheng
 *
 */
import com.xjl.pt.form.service.TableService;
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
		List<Table> list = this.tableService.query(page, pageSize);
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
}
