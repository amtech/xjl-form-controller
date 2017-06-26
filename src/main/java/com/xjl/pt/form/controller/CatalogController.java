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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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
/**
 * 证照目录控制类
 * @author 陶杰
 *
 */
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
	
	/**
	 * 证照、目录展示
	 * @return taojie
	 */
	
	@ResponseBody
	@RequestMapping(value = "/showZZcatalog")
	public List<Map<String,Object>>  showzzCatalog(){
		//获取全部有效证照
		List<Licence> list=licenceService.queryUrlByOwnid("320423199102108613");
		//通过用户编号过滤目录文件信息
		List<ZzCatalog> zzCatalogList=this.zzCatalogService.queryByUserId("73f94e44-bb52-4041-8a18-f0b193a970ea");
		//声明存储目录文件下条目集合
		List<ZzCatalogLicence> zzCatalogLicenceList = new ArrayList<ZzCatalogLicence>();
		for(int i=0;i<zzCatalogList.size();i++){
			List<ZzCatalogLicence> onelist=this.zzCatalogLicenceService.queryByCatalogId(zzCatalogList.get(i).getCatalogId());
			for(int j=0;j<onelist.size();j++){
				zzCatalogLicenceList.add(onelist.get(j));
			}
		}
		list = this.removeRep1(list, zzCatalogLicenceList);
		List<Map<String,Object>> allList = new ArrayList<>();
		Map<String,Object> _tempMap = new HashMap<>();
		_tempMap.put("licence", list);
		_tempMap.put("catalog", zzCatalogList);
		allList.add(_tempMap);
		return allList;
	}
	
	/**
	 * 去重复方法
	 * @param list1
	 * @param list2
	 * @return
	 */
	public List<Licence>  removeRep(List<Licence> list1,List<ZzCatalogLicence> list2){
		int k=0;
		if(null != list1 && list1.size() > 0 && (null != list2 && list2.size() > 0)){
			for(int i= 0;i<list1.size();i++){
				for(int j = 0;j<list2.size();j++){
					if(list2.get(j).getLicenceId().equals(list1.get(i).getLicenceId())){
						list1.remove(i-k);
						k++;
					}
				}
			} 	
		}
		return list1;
	}
	/**
	 * 去重复方法,改进
	 * @param list1
	 * @param list2
	 * @return
	 */
	public List<Licence>  removeRep1(List<Licence> list1,List<ZzCatalogLicence> list2){
		List<String> idlist1=new ArrayList<String>();
		List<String> idlist2=new ArrayList<String>();
		for(int i=0;i<list1.size();i++){
			idlist1.add(list1.get(i).getLicenceId());
		}for(int i=0;i<list2.size();i++){
			idlist2.add(list2.get(i).getLicenceId());
		}
		int k=0;
		if(null != list1 && list1.size() > 0 && (null != list2 && list2.size() > 0)){
			for(int i= 0;i<idlist1.size();i++){
				for(int j = 0;j<idlist2.size();j++){
					if(idlist2.get(j).equals(idlist1.get(i))){
						list1.remove(i-k);
						k++;
					}
				}
			} 	
		}
		return list1;
	}
	
	/**
	 * 创建目录
	 * @param request
	 * @param response
	 * @return
	 */
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
	
	/**
	 * 修改目录名字
	 * @param request
	 * @param response
	 * @return
	 */
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
	
	/**
	 * 展示目录下证照
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/showcataloglicence")
	public List<Map<String, Object>>  showcataloglicence(HttpServletRequest request,HttpServletResponse response){
		List<Map<String, Object>> returnList=new ArrayList<Map<String, Object>>();
		Map<String, Object> model=new HashMap<String, Object>();
		String catalogId=StringUtils.trim(request.getParameter("catalogId"));
		ZzCatalog zzCatalog=new ZzCatalog();
		log.debug("目录展开");
		zzCatalog=this.zzCatalogService.queryById(catalogId);//通过catalogid查到该条目录的信息
		List<ZzCatalogLicence>  licenceOfCatalist=this.zzCatalogLicenceService.queryByCatalogId(catalogId);//通过目录id查询该目录下的所有证照
		List<Licence> list=new ArrayList<Licence>();
		for(int i=0;i<licenceOfCatalist.size();i++){
			list.add(this.licenceService.queryByLicenceId(licenceOfCatalist.get(i).getLicenceId()));
		}
		model.put("catalog", zzCatalog);
		model.put("licenceOfCata", list);
		returnList.add(model);
		return returnList;
	}
	
	/**
	 * 展示证照信息，相当于预览效果
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/query")
	public List<Map<String, Licence>> queryById(HttpServletRequest request,HttpServletResponse response){
		String licenceId=StringUtils.trim(request.getParameter("licenceId"));
		List<Map<String, Licence>> list=new ArrayList<Map<String, Licence>>();
		List<DictItem> itemStatusLicenceItems = this.dictItemService.queryByDictId("9b48ca78-6366-4598-8509-64bf26ca3832", 1, 1000);
		List<DictItem> itemTypeSourceItems = this.dictItemService.queryByDictId("8c438833-0803-41eb-abcb-63d06902cf66", 1, 1000);
		List<DictItem> itemTypeFileItems = this.dictItemService.queryByDictId("e16933ed-6cbe-4464-9420-60bdfae85ca2",1,1000);
		List<DictItem> itemCategoryLicenceItems = this.dictItemService.queryByDictId("eeea77c6-9eb6-4705-92fb-3516ba6ae6c5",1,1000);
		Map<String, Licence> model=new HashMap<String, Licence>();
		Licence licence=this.licenceService.queryByLicenceId(licenceId);
		//解析证照状态
		licence.setLicenceStatus$name(DictItemTools.getDictItemNames(licence.getLicenceStatus(),itemStatusLicenceItems));
		//解析证照数据来源
		licence.setLicenceSourceType$name(DictItemTools.getDictItemNames(licence.getLicenceSourceType(),itemTypeSourceItems));
		//解析证照文件类型
		licence.setLicenceFileType$name(DictItemTools.getDictItemNames(licence.getLicenceFileType(), itemTypeFileItems));
		//解析证照类型
		licence.setLicenceCategory$name((DictItemTools.getDictItemNames(licence.getLicenceCategory(), itemCategoryLicenceItems)));
		//解析时间
		licence.setIssuingDateStr(format.format(licence.getIssuingDate()));
		licence.setExirationDateStr(format.format(licence.getExpirationDate()));
		model.put("licence",licence);
		list.add(model);
		return list;
	}
	/**
	 * 删除目录
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/deleteCatalog")
	public XJLResponse deleteCatalog(HttpServletRequest request,HttpServletResponse response){
		String id=StringUtils.trim(request.getParameter("id"));
		ZzCatalog zzCatalog=this.zzCatalogService.queryById(id);
		User userDefault = this.userService.queryById("73f94e44-bb52-4041-8a18-f0b193a970ea");
		this.zzCatalogService.delete(zzCatalog,userDefault);
		List<ZzCatalogLicence> list=this.zzCatalogLicenceService.queryByCatalogId(id);
		for(int i=0;i<list.size();i++){
			this.zzCatalogLicenceService.delete(list.get(i), userDefault);
		}
		return XJLResponse.successInstance();
	}
	/**
	 * 删除单个证照
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/deleteLicence")
	public XJLResponse deleteLicence(HttpServletRequest request,HttpServletResponse response){
		XJLResponse xjlResponse = new XJLResponse();
		String id=StringUtils.trim(request.getParameter("id"));
		Licence licence=this.licenceService.queryByLicenceId(id);
		User userDefault = this.userService.queryById("73f94e44-bb52-4041-8a18-f0b193a970ea");
		this.licenceService.delete(licence, userDefault);
		xjlResponse.setSuccess(true);
		xjlResponse.setShowMsg("该执照删除成功");
		return xjlResponse;
	}
	
	/**
	 * 将证照拖进目录的保存
	 * */
	@ResponseBody
	@RequestMapping(value="/saveToCatalog")
	public XJLResponse saveToCatalog(HttpServletRequest request,HttpServletResponse response){
		String licenceId=StringUtils.trim(request.getParameter("licenceId"));
		String catalogId=StringUtils.trim(request.getParameter("catalogId"));
		User userDefault = this.userService.queryById("73f94e44-bb52-4041-8a18-f0b193a970ea");
		ZzCatalogLicence zzCatalogLicence=new ZzCatalogLicence();
		zzCatalogLicence.setCatalogId(catalogId);
		zzCatalogLicence.setLicenceId(licenceId);
		this.zzCatalogLicenceService.add(zzCatalogLicence, userDefault);
		return XJLResponse.successInstance();
	}
	
	/**
	 * 将目录中的证照从目录中移除的方法
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/removeOutOfCata")
	public XJLResponse removeOutOfCata(HttpServletRequest request,HttpServletResponse response){
		XJLResponse xjlResponse = new XJLResponse();
	//	String catalogId=request.getParameter("catalogId");
		String licenceId=request.getParameter("id");
		List<ZzCatalogLicence> zzCatalogLicencelist=this.zzCatalogLicenceService.queryByLicenceId(licenceId);
		ZzCatalogLicence zzCatalogLicence=zzCatalogLicencelist.get(0);
		Licence licence=this.licenceService.queryByLicenceId(licenceId);//此处id即licenceid
		String s=StringUtils.trim(licence.getLicenceSourceType());
		User userDefault = this.userService.queryById("73f94e44-bb52-4041-8a18-f0b193a970ea");
		this.zzCatalogLicenceService.delete(zzCatalogLicence, userDefault);
		xjlResponse.setSuccess(true);
		xjlResponse.setShowMsg("该证照已移除当前文件夹");
		return xjlResponse;
	}
}
