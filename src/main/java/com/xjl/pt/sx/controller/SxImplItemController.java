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
import org.springframework.web.bind.annotation.ResponseBody;
import com.xjl.pt.form.controller.SessionTools;
import com.xjl.pt.form.controller.XJLResponse;
import com.xjl.pt.form.controller.BootstrapGridTable;
import com.xjl.pt.sx.domain.SxImplItem;
import com.xjl.pt.core.domain.DictItem;
import com.xjl.pt.core.domain.User;
import com.xjl.pt.core.service.DictItemService;
import com.xjl.pt.core.tools.DictItemTools;
import com.xjl.pt.sx.service.SxImplItemService;
/**
 * 实施清单控制器类
 * @author ControllerCoderTools lilisheng
 *
 */
@Controller
@RequestMapping("/sxImplItem")
public class SxImplItemController {
	@Autowired
	private SessionTools sessionTools;
	@Autowired
	private DictItemService dictItemService;
	@Autowired
	private SxImplItemService sxImplItemService;
	@ResponseBody
	@RequestMapping(value="/query/{page}/{rows}",method=RequestMethod.GET,consumes = "application/json")
	public BootstrapGridTable query(HttpServletRequest request, @PathVariable Integer page,@PathVariable Integer rows){
		String search = StringUtils.trimToNull(request.getParameter("search"));
		List<SxImplItem> list = this.sxImplItemService.query(search, page, rows);
		//处理字典
		List<DictItem> itemTypeDictItems = this.dictItemService.queryByDictId("ee9c8363-e344-4fde-93cb-5a4e1811fe44", 1, 1000);
		List<DictItem> implLevelDictItems = this.dictItemService.queryByDictId("56b9b4e8-f49b-4fdd-86a3-32c48513a2d1", 1, 1000);
		List<DictItem> implOrgDictItems = this.dictItemService.queryByDictId("26816a50-0ba2-434e-85c1-d81d7eac6491", 1, 1000);
		List<DictItem> implOrgTypeDictItems = this.dictItemService.queryByDictId("45e8ea1e-6f10-4c3e-a3f3-59871ec9cfa7", 1, 1000);
		List<DictItem> joinOrgDictItems = this.dictItemService.queryByDictId("26816a50-0ba2-434e-85c1-d81d7eac6491", 1, 1000);
		List<DictItem> serviceObjectDictItems = this.dictItemService.queryByDictId("bd3952a7-3a45-4587-8421-76018fa58866", 1, 1000);
		List<DictItem> officeTypeDictItems = this.dictItemService.queryByDictId("70bca30e-3229-4c14-8e61-c854d29c12f2", 1, 1000);
		List<DictItem> openScopeDictItems = this.dictItemService.queryByDictId("7de53fbc-0500-43fa-979e-95cdd492ac4b", 1, 1000);
		List<DictItem> serviceTypeDictItems = this.dictItemService.queryByDictId("135ff746-284b-45cb-b671-28cf04b0b8ba", 1, 1000);
		List<DictItem> runSystemLevelDictItems = this.dictItemService.queryByDictId("803ce77b-9a31-4715-b8dd-6d754edb4b31", 1, 1000);
		for (SxImplItem sxImplItem : list) {
			sxImplItem.setItemType$name(DictItemTools.getDictItemNames(sxImplItem.getItemType(), itemTypeDictItems));
			sxImplItem.setImplLevel$name(DictItemTools.getDictItemNames(sxImplItem.getImplLevel(), implLevelDictItems));
			sxImplItem.setImplOrg$name(DictItemTools.getDictItemNames(sxImplItem.getImplOrg(), implOrgDictItems));
			sxImplItem.setImplOrgType$name(DictItemTools.getDictItemNames(sxImplItem.getImplOrgType(), implOrgTypeDictItems));
			sxImplItem.setJoinOrg$name(DictItemTools.getDictItemNames(sxImplItem.getJoinOrg(), joinOrgDictItems));
			sxImplItem.setServiceObject$name(DictItemTools.getDictItemNames(sxImplItem.getServiceObject(), serviceObjectDictItems));
			sxImplItem.setOfficeType$name(DictItemTools.getDictItemNames(sxImplItem.getOfficeType(), officeTypeDictItems));
			sxImplItem.setOpenScope$name(DictItemTools.getDictItemNames(sxImplItem.getOpenScope(), openScopeDictItems));
			sxImplItem.setServiceType$name(DictItemTools.getDictItemNames(sxImplItem.getServiceType(), serviceTypeDictItems));
			sxImplItem.setRunSystemLevel$name(DictItemTools.getDictItemNames(sxImplItem.getRunSystemLevel(), runSystemLevelDictItems));
		}
		//处理外键
		return BootstrapGridTable.getInstance(list);
	}

	@ResponseBody
	@RequestMapping(value="/query/{id}",method=RequestMethod.GET,consumes = "application/json")
	public SxImplItem queryById(HttpServletRequest request, @PathVariable String id){
		SxImplItem sxImplItem = this.sxImplItemService.queryById(id);
		//处理字典
		List<DictItem> itemTypeDictItems = this.dictItemService.queryByDictId("ee9c8363-e344-4fde-93cb-5a4e1811fe44", 1, 1000);
		List<DictItem> implLevelDictItems = this.dictItemService.queryByDictId("56b9b4e8-f49b-4fdd-86a3-32c48513a2d1", 1, 1000);
		List<DictItem> implOrgDictItems = this.dictItemService.queryByDictId("26816a50-0ba2-434e-85c1-d81d7eac6491", 1, 1000);
		List<DictItem> implOrgTypeDictItems = this.dictItemService.queryByDictId("45e8ea1e-6f10-4c3e-a3f3-59871ec9cfa7", 1, 1000);
		List<DictItem> joinOrgDictItems = this.dictItemService.queryByDictId("26816a50-0ba2-434e-85c1-d81d7eac6491", 1, 1000);
		List<DictItem> serviceObjectDictItems = this.dictItemService.queryByDictId("bd3952a7-3a45-4587-8421-76018fa58866", 1, 1000);
		List<DictItem> officeTypeDictItems = this.dictItemService.queryByDictId("70bca30e-3229-4c14-8e61-c854d29c12f2", 1, 1000);
		List<DictItem> openScopeDictItems = this.dictItemService.queryByDictId("7de53fbc-0500-43fa-979e-95cdd492ac4b", 1, 1000);
		List<DictItem> serviceTypeDictItems = this.dictItemService.queryByDictId("135ff746-284b-45cb-b671-28cf04b0b8ba", 1, 1000);
		List<DictItem> runSystemLevelDictItems = this.dictItemService.queryByDictId("803ce77b-9a31-4715-b8dd-6d754edb4b31", 1, 1000);
		sxImplItem.setItemType$name(DictItemTools.getDictItemNames(sxImplItem.getItemType(), itemTypeDictItems));
		sxImplItem.setImplLevel$name(DictItemTools.getDictItemNames(sxImplItem.getImplLevel(), implLevelDictItems));
		sxImplItem.setImplOrg$name(DictItemTools.getDictItemNames(sxImplItem.getImplOrg(), implOrgDictItems));
		sxImplItem.setImplOrgType$name(DictItemTools.getDictItemNames(sxImplItem.getImplOrgType(), implOrgTypeDictItems));
		sxImplItem.setJoinOrg$name(DictItemTools.getDictItemNames(sxImplItem.getJoinOrg(), joinOrgDictItems));
		sxImplItem.setServiceObject$name(DictItemTools.getDictItemNames(sxImplItem.getServiceObject(), serviceObjectDictItems));
		sxImplItem.setOfficeType$name(DictItemTools.getDictItemNames(sxImplItem.getOfficeType(), officeTypeDictItems));
		sxImplItem.setOpenScope$name(DictItemTools.getDictItemNames(sxImplItem.getOpenScope(), openScopeDictItems));
		sxImplItem.setServiceType$name(DictItemTools.getDictItemNames(sxImplItem.getServiceType(), serviceTypeDictItems));
		sxImplItem.setRunSystemLevel$name(DictItemTools.getDictItemNames(sxImplItem.getRunSystemLevel(), runSystemLevelDictItems));
		//处理外键
		return sxImplItem;
	}

	@ResponseBody
	@RequestMapping(value="/add",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse add(HttpServletRequest request, @RequestBody SxImplItem sxImplItem){
		User user = this.sessionTools.getUser(request);
		this.sxImplItemService.add(sxImplItem, user);
		return XJLResponse.successInstance();
	}

	@ResponseBody
	@RequestMapping(value="/modify",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse modify(HttpServletRequest request, @RequestBody SxImplItem sxImplItem){
		User user = this.sessionTools.getUser(request);
		this.sxImplItemService.modify(sxImplItem, user);
		return XJLResponse.successInstance();
	}

	@ResponseBody
	@RequestMapping(value="/delete",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse delete(HttpServletRequest request, @RequestBody List<SxImplItem> list){
		User user = this.sessionTools.getUser(request);
		for (SxImplItem sxImplItem : list) {
			this.sxImplItemService.delete(sxImplItem, user);
		}
		return XJLResponse.successInstance();
	}

}

