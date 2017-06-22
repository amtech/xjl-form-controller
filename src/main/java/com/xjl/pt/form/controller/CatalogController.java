package com.xjl.pt.form.controller;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xjl.pt.core.domain.DictItem;
import com.xjl.pt.core.domain.Licence;
import com.xjl.pt.core.domain.User;
import com.xjl.pt.core.domain.ZzCatalog;
import com.xjl.pt.core.domain.ZzCatalogLicence;
import com.xjl.pt.core.service.DictItemService;
import com.xjl.pt.core.service.LicenceService;
import com.xjl.pt.core.service.UserService;
import com.xjl.pt.core.service.ZzCatalogLicenceService;
import com.xjl.pt.core.service.ZzCatalogService;
import com.xjl.pt.core.tools.DictItemTools;


@Controller
@RequestMapping("/catalog")
public class CatalogController {
	
	@Autowired
	private UserService userService;
	@Autowired
	private LicenceService licenceService;
	@Autowired
	private ZzCatalogService zzCatalogService;
	@Autowired
	private ZzCatalogLicenceService zzCatalogLicenceService;
	@Autowired
	private DictItemService dictItemService;
	private static final Log log = LogFactory.getLog(CatalogController.class);
	SimpleDateFormat format=new SimpleDateFormat(SystemConstant.FOMATDATE_DAY);
	
	@ResponseBody
	@RequestMapping(value = "/showZZcatalog")
	public List<Map<String,Object>>  showzzCatalog(){
		//获取全部有效证照
		List<Licence> list=licenceService.queryUrlByOwnid("320423199102108613");
		//通过用户编号过滤目录文件信息
		List<ZzCatalog> zzCatalogList=this.zzCatalogService.queryByUserId("73f94e44-bb52-4041-8a18-f0b193a970ea");
		//声明存储目录文件下条目集合
		List<ZzCatalogLicence> zzCatalogLicenceList = null;
		for (ZzCatalog zzCatalog : zzCatalogList) {
			 zzCatalogLicenceList = this.zzCatalogLicenceService.queryByCatalogId(zzCatalog.getCatalogId());
		}
		list = this.removeRep(list, zzCatalogLicenceList);
		List<Map<String,Object>> allList = new ArrayList<>();
		Map<String,Object> _tempMap = new HashMap<>();
		_tempMap.put("licence", list);
		_tempMap.put("catalog", zzCatalogList);
		allList.add(_tempMap);
		return allList;
	}
	
	public List<Licence>  removeRep(List<Licence> list1,List<ZzCatalogLicence> list2){
		if(null != list1 && list1.size() > 0 && (null != list2 && list2.size() > 0)){
			for(int i= 0;i<list1.size();i++){
				for(int j = 0;j<list2.size();j++){
					if(list2.get(j).getLicenceId().equals(list1.get(i).getLicenceId())){
						list1.remove(i);
					}
				}
			} 	
		}
		return list1;
	}
	
	@ResponseBody
	@RequestMapping(value = "/createCatalog")
	public Map<String, String>  createCatalog(HttpServletRequest request,HttpServletResponse response){
		Map<String, String> model=new HashMap<String, String>();
		String catalogName=StringUtils.trim(request.getParameter("catalogName"));
		User userDefault = this.userService.queryById("73f94e44-bb52-4041-8a18-f0b193a970ea");
		ZzCatalog zzCatalog=new ZzCatalog();
		String catalogId=UUID.randomUUID().toString();
		zzCatalog.setCatalogId(catalogId);
		zzCatalog.setCatalogName(catalogName);
		zzCatalog.setUserId("73f94e44-bb52-4041-8a18-f0b193a970ea");//预留功能，从session中获取userid
		log.debug("新建证照目录:"+catalogName);
		this.zzCatalogService.add(zzCatalog,userDefault);
		model.put("catalogId", catalogId);
		model.put("catalogName", catalogName);
		return model;
		
	}
	
	
	@ResponseBody
	@RequestMapping(value = "/changeCatalogName")
	public XJLResponse  changeCatalogName(HttpServletRequest request,HttpServletResponse response){
		XJLResponse xjlResponse = new XJLResponse();
		String catalogId=StringUtils.trim(request.getParameter("catalogId"));
		System.out.println(catalogId);
		String newName=StringUtils.trim(request.getParameter("newName"));
		ZzCatalog zzCatalog=new ZzCatalog();
		zzCatalog.setCatalogId(catalogId);
		zzCatalog.setCatalogName(newName);
		zzCatalog.setUserId("73f94e44-bb52-4041-8a18-f0b193a970ea");
		log.debug("目录名称修改");
		this.zzCatalogService.modifyCatalogName(zzCatalog);
		xjlResponse.setSuccess(true);
		xjlResponse.setShowMsg("目录名称修改成功");
		return xjlResponse;
	}
	
	
	@ResponseBody
	@RequestMapping(value = "/showcataloglicence")
	public List<Map<String, Object>>  showcataloglicence(HttpServletRequest request,HttpServletResponse response){
		List<Map<String, Object>> returnList=new ArrayList<Map<String, Object>>();
		Map<String, Object> model=new HashMap<String, Object>();
		String catalogId=StringUtils.trim(request.getParameter("catalogId"));
		ZzCatalog zzCatalog=new ZzCatalog();
		List<ZzCatalogLicence>  list=null;
		log.debug("目录展开");
		zzCatalog=this.zzCatalogService.queryById(catalogId);
		list=this.zzCatalogLicenceService.queryByCatalogId(catalogId);
		model.put("catalog", zzCatalog);
		model.put("licenceOfCata", list);
		returnList.add(model);
		return returnList;
	}
	
	@ResponseBody
	@RequestMapping(value="/query")
	public List<Map<String, Licence>> queryById(HttpServletRequest request,HttpServletResponse response){
		String licenceId=StringUtils.trim(request.getParameter("licenceId"));
		List<Map<String, Licence>> list=new ArrayList<Map<String, Licence>>();
		List<DictItem> itemTypeLicenceItems = this.dictItemService.queryByDictId("9b48ca78-6366-4598-8509-64bf26ca3832", 1, 1000);
		List<DictItem> itemTypeSourceItems = this.dictItemService.queryByDictId("8c438833-0803-41eb-abcb-63d06902cf66", 1, 1000);
		Map<String, Licence> model=new HashMap<String, Licence>();
		Licence licence=this.licenceService.queryByLicenceId(licenceId);
		//解析证照状态
		licence.setLicenceStatus$name(DictItemTools.getDictItemNames(licence.getLicenceStatus(),itemTypeLicenceItems));
		//解析证照数据来源
		licence.setLicenceSourceType$name(DictItemTools.getDictItemNames(licence.getLicenceSourceType(),itemTypeSourceItems));
		//解析时间
		licence.setIssuingDateStr(format.format(licence.getIssuingDate()));
		licence.setExirationDateStr(format.format(licence.getExpirationDate()));
		model.put("licence",licence);
		list.add(model);
		return list;
	}
	
	
	
}
