package com.xjl.pt.sx.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.xjl.pt.form.controller.SessionTools;
import com.xjl.pt.form.controller.XJLResponse;
import com.xjl.pt.form.controller.BootstrapGridTable;
import com.xjl.pt.sx.domain.SxDirItem;
import com.xjl.pt.sx.domain.SxImplItem;
import com.xjl.pt.core.domain.DictItem;
import com.xjl.pt.core.domain.User;
import com.xjl.pt.core.service.DictItemService;
import com.xjl.pt.core.tools.DictItemTools;
import com.xjl.pt.sx.service.SxDirItemService;
import com.xjl.pt.sx.service.SxImplItemService;
/**
 * 事项目录清单控制器类
 * @author ControllerCoderTools lilisheng
 *
 */
@Controller
@RequestMapping("/sxDirItem")
public class SxDirItemController {
	@Autowired
	private SessionTools sessionTools;
	@Autowired
	private DictItemService dictItemService;
	@Autowired
	private SxDirItemService sxDirItemService;
	@Autowired
	private SxImplItemService sxImplItemService;
	@ResponseBody
	@RequestMapping(value="/query/{page}/{rows}",method=RequestMethod.GET,consumes = "application/json")
	public BootstrapGridTable query(HttpServletRequest request, @PathVariable Integer page,@PathVariable Integer rows){
		String search = StringUtils.trimToNull(request.getParameter("search"));
		List<SxDirItem> list = this.sxDirItemService.query(search, page, rows);
		//处理字典
		List<DictItem> itemTypeDictItems = this.dictItemService.queryByDictId("ee9c8363-e344-4fde-93cb-5a4e1811fe44", 1, 1000);
		for (SxDirItem sxDirItem : list) {
			sxDirItem.setItemType$name(DictItemTools.getDictItemNames(sxDirItem.getItemType(), itemTypeDictItems));
		}
		//处理外键
		return BootstrapGridTable.getInstance(list);
	}

	@ResponseBody
	@RequestMapping(value="/query/{id}",method=RequestMethod.GET,consumes = "application/json")
	public SxDirItem queryById(HttpServletRequest request, @PathVariable String id){
		SxDirItem sxDirItem = this.sxDirItemService.queryById(id);
		//处理字典
		List<DictItem> itemTypeDictItems = this.dictItemService.queryByDictId("ee9c8363-e344-4fde-93cb-5a4e1811fe44", 1, 1000);
		sxDirItem.setItemType$name(DictItemTools.getDictItemNames(sxDirItem.getItemType(), itemTypeDictItems));
		//处理外键
		return sxDirItem;
	}

	@ResponseBody
	@RequestMapping(value="/add",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse add(HttpServletRequest request, @RequestBody SxDirItem sxDirItem){
		User user = this.sessionTools.getUser(request);
		this.sxDirItemService.add(sxDirItem, user);
		return XJLResponse.successInstance();
	}

	@ResponseBody
	@RequestMapping(value="/modify",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse modify(HttpServletRequest request, @RequestBody SxDirItem sxDirItem){
		User user = this.sessionTools.getUser(request);
		this.sxDirItemService.modify(sxDirItem, user);
		return XJLResponse.successInstance();
	}
	@ResponseBody
	@RequestMapping(value="/deploy/{itemId}",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse deploy(HttpServletRequest request, @PathVariable String itemId){
		User user = this.sessionTools.getUser(request);
		SxDirItem dirItem = this.sxDirItemService.queryById(itemId);
		this.sxDirItemService.deploy(itemId, user);
		SxImplItem implItem = new SxImplItem();
		implItem.setItemId(itemId);
		implItem.setBaseCode(dirItem.getBaseCode());
		implItem.setAccording(dirItem.getAccording());
		implItem.setItemName(dirItem.getItemName());
		implItem.setItemState("");
		implItem.setItemType(dirItem.getItemType());
		this.sxImplItemService.add(implItem, user);
		return XJLResponse.successInstance();
	}
	@ResponseBody
	@RequestMapping(value="/delete",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse delete(HttpServletRequest request, @RequestBody List<SxDirItem> list){
		User user = this.sessionTools.getUser(request);
		for (SxDirItem sxDirItem : list) {
			this.sxDirItemService.delete(sxDirItem, user);
		}
		return XJLResponse.successInstance();
	}

}

