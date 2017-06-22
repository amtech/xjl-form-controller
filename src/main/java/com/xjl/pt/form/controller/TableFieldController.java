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

import com.xjl.pt.core.domain.Table;
import com.xjl.pt.core.domain.TableField;
import com.xjl.pt.core.domain.User;
import com.xjl.pt.core.service.TableFieldService;
import com.xjl.pt.core.service.TableService;
import com.xjl.pt.core.service.UserService;
import com.xjl.pt.core.service.XJLMapperTest;
/**
 * 字段控制器
 * @author lilisheng
 *
 */
@Controller
@RequestMapping("/tableField")
public class TableFieldController {
	@Autowired
	private UserService userService;
	@Autowired
	private TableService tableService;
	@Autowired
	private TableFieldService tableFieldService;
	@ResponseBody
	@RequestMapping(value="/query/{page}/{pageSize}",method=RequestMethod.GET,consumes = "application/json")
	public BootstrapGridTable query(HttpServletRequest request, @PathVariable Integer page,@PathVariable Integer pageSize){
		String tableId = request.getParameter("tableId");
		List<TableField> list = this.tableFieldService.queryByTableId(tableId, page, pageSize);
		for (TableField field : list) {
			this.tableFieldService.initDictId$name(field);
			this.tableFieldService.initFieldType$name(field);
			this.tableFieldService.initForeignTableId$name(field);
			this.tableFieldService.initTableId$name(field);
		}
		return BootstrapGridTable.getInstance(list);
	}
	@ResponseBody
	@RequestMapping(value="/queryAll",method=RequestMethod.GET,consumes = "application/json")
	public BootstrapGridTable queryAll(HttpServletRequest request){
		String tableId = request.getParameter("tableId");
		List<TableField> list = this.tableFieldService.queryByTableId(tableId);
		return BootstrapGridTable.getInstance(list);
	}
	@ResponseBody
	@RequestMapping(value="/add",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse add(@RequestBody TableField tableField){
		User user = this.userService.queryFixUser();
		this.tableFieldService.add(tableField, user);
		return XJLResponse.successInstance();
	}
	@ResponseBody
	@RequestMapping(value="/delete",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse delete(@RequestBody List<TableField> list){
		User user = this.userService.queryFixUser();
		for (TableField tableField : list) {
			this.tableFieldService.delete(tableField, user);
		}
		return XJLResponse.successInstance();
		
	}
	@ResponseBody
	@RequestMapping(value="/modify",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse modify(@RequestBody TableField tableField){
		User user = this.userService.queryFixUser();
		this.tableFieldService.modify(tableField, user);
		return XJLResponse.successInstance();
	}
}
