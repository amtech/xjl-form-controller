package com.xjl.pt.form.controller;
import java.text.ParseException;
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
import com.xjl.pt.core.domain.UserInfo;
import com.xjl.pt.core.domain.ZzCatalog;
import com.xjl.pt.core.domain.ZzCatalogLicence;
import com.xjl.pt.core.service.DictItemService;
import com.xjl.pt.core.service.LicenceService;
import com.xjl.pt.core.service.UserInfoService;
import com.xjl.pt.core.service.UserService;
import com.xjl.pt.core.service.ZzCatalogLicenceService;
import com.xjl.pt.core.service.ZzCatalogService;
import com.xjl.pt.core.tools.DictItemTools;
/**
 * 证照控制类
 * @author guan.zheyuan
 */
@Controller
@RequestMapping("/licence")
public class LicenseController {
	
	@Autowired
	private LicenceService licenceService;
	@Autowired
	private UserService userService;
	@Autowired
	private UserInfoService userInfoService;
	@Autowired
	private DictItemService dictItemService;
	@Autowired
	private ZzCatalogService zzCatalogService;
	@Autowired
	private ZzCatalogLicenceService zzCatalogLicenceService;
	private static final Log log = LogFactory.getLog(LicenseController.class);

	/**
	 *  分页
	 */
	@ResponseBody
	@RequestMapping(value="/query/{page}/{rows}",method=RequestMethod.GET,consumes = "application/json")
	public BootstrapGridTable query(HttpServletRequest request, @PathVariable Integer page,@PathVariable Integer rows){
		String search = StringUtils.trimToNull(request.getParameter("search"));
		List<Licence> list = licenceService.query(search, page, rows);
		List<DictItem> itemTypeLicenceItems = this.dictItemService.queryByDictId("9b48ca78-6366-4598-8509-64bf26ca3832", 1, 1000);
		List<DictItem> itemTypeSourceItems = this.dictItemService.queryByDictId("8c438833-0803-41eb-abcb-63d06902cf66", 1, 1000);
		SimpleDateFormat format = new SimpleDateFormat(SystemConstant.FOMATDATE_DAY);
		for (Licence licence : list) {
			if(null != licence && null != licence.getLicenceId()){
				licence.setLicenceItemCount(this.licenceService.countByLicense(licence.getLicenceId()));
			}
			//解析证照状态
			licence.setLicenceStatus$name(DictItemTools.getDictItemNames(licence.getLicenceStatus(),itemTypeLicenceItems));
			//解析证照数据来源
			licence.setLicenceSourceType$name(DictItemTools.getDictItemNames(licence.getLicenceSourceType(),itemTypeSourceItems));
			//解析时间
			licence.setIssuingDateStr(format.format(licence.getIssuingDate()));
			licence.setExirationDateStr(format.format(licence.getExpirationDate()));
		}
		return BootstrapGridTable.getInstance(list);
	}
	
	/**
	 * 通过编号得到证照信息
	 */
	@ResponseBody
	@RequestMapping(value="/query/{id}",method=RequestMethod.GET,consumes = "application/json")
	public Licence queryById(HttpServletRequest request, @PathVariable String licenceId){
		return this.licenceService.queryByLicenceId(licenceId);
	}
	
	/**
	 *  提交上传材料
	 * @param models
	 * @param request
	 * @throws ParseException
	 */
	@ResponseBody
	@RequestMapping(value="/add",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse add(@RequestBody Map<String, Object> models,HttpServletRequest request) throws ParseException{
		String licenceName= String.valueOf(models.get("licencename"));
		String startDate = String.valueOf(models.get("startDate"));
		String endDate = String.valueOf(models.get("endDate"));
		String ftpURL = String.valueOf(models.get("ftpURL"));
		String fileName = String.valueOf(models.get("fileName"));
		log.debug("完成参数组装");
		SimpleDateFormat format = new SimpleDateFormat(SystemConstant.FOMATDATE_DAY);
		//添加用户信息
		//User user = (User) request.getSession().getAttribute(SystemConstant.SESSION_USER);
		User userDefault = this.userService.queryById("73f94e44-bb52-4041-8a18-f0b193a970ea");
		UserInfo userInfo = this.userInfoService.queryByUserId(userDefault.getUserId());
		log.debug("得到用户信息："+userInfo);
		Licence licence = new Licence();
		licence.setLicenceId(UUID.randomUUID().toString());
		licence.setLicenceName(StringUtils.isBlank(licenceName)?fileName:licenceName);
		licence.setLicenceFileUrl(ftpURL);
		licence.setIssuingDate(format.parse(startDate));
		licence.setExpirationDate(format.parse(endDate));
		licence.setLicenceFileUrl(ftpURL);
		licence.setOwnerOn(StringUtils.isBlank(userInfo.getCardNo())?"":userInfo.getCardNo());
		licence.setOwnerType(StringUtils.isBlank(userInfo.getUserType())?"":userInfo.getUserType());
		licence.setLicenceTrustLevel("D");
		licence.setLicenceStatus("01");
		licence.setLicenceSourceType("02");
		log.debug("执行证照上传");
		this.licenceService.add(licence,userDefault);
		return XJLResponse.successInstance();
	}
	/**
	 * 执行证照修改
	 * @param licence
	 * @param request
	 * @return
	 * @throws ParseException 
	 */
	@ResponseBody
	@RequestMapping(value="/modify",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse modify(@RequestBody Map<String, Object> models,HttpServletRequest request) throws ParseException{
		String licenceId = String.valueOf(models.get("licenceId"));
		String licenceName = String.valueOf(models.get("licencename"));
		String startDate = String.valueOf(models.get("startDate"));
		String endDate = String.valueOf(models.get("endDate"));
		String ftpURL = String.valueOf(models.get("ftpURL"));
		String fileName = String.valueOf(models.get("fileName"));
		log.debug("完成参数组装");
		SimpleDateFormat format = new SimpleDateFormat(SystemConstant.FOMATDATE_DAY);
		Licence licence = new Licence();
		licence.setLicenceId(licenceId);
		licence.setLicenceName(StringUtils.isBlank(licenceName)?fileName:licenceName);
		licence.setLicenceFileUrl(ftpURL == ""?this.licenceService.queryByLicenceId(licenceId).getLicenceFileUrl():ftpURL);
		licence.setIssuingDate(format.parse(startDate));
		licence.setExpirationDate(format.parse(endDate));
		this.licenceService._modify(licence);
		return XJLResponse.successInstance();
	}
	
	/**
	 * 删除证照信息
	 */
	@ResponseBody
	@RequestMapping(value="/delete",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse Delete(@RequestBody Map<String, Object> models){
		String licenceId = String.valueOf(models.get("licenceId"));
		Licence licence = new Licence();
		licence.setLicenceId(licenceId);
		User userDefault = this.userService.queryById("9fcfdb3e-3bdb-4234-a0c4-f91d023c308e");
		this.licenceService.delete(licence, userDefault);
		return XJLResponse.successInstance();
	}
	/**
	 * 上传
	 */
	@ResponseBody
	@RequestMapping(value="/upload")
	public Map<String, Object> uploadLicence(HttpServletRequest request,HttpServletResponse response){
		return new FileController().uploadFTPForController(request, response, SystemConstant.FTP_PATH_LICENCE,SystemConstant.FTP_READPATH_LICENCE);
	}
	
	@ResponseBody
	@RequestMapping(value = "/showZZcatalog", method = RequestMethod.POST)
	public Map<String, List<String>> showzzcatalog(HttpServletRequest request,HttpServletResponse reponse){
//		String userid= (String) request.getSession().getAttribute(SystemConstant.SESSION_USER);
//		UserInfo userInfo=this.userInfoService.queryByUserId(userid);
//		List<Licence> list=licenceService.queryUrlByOwnid(userInfo.getCardNo());
		String userid="73f94e44-bb52-4041-8a18-f0b193a970ea";
		List<Licence> list=licenceService.queryUrlByOwnid("320423199102108613");
		Map<String, List<String>> model=new HashMap<String, List<String>>();
		List<String> alist=new ArrayList<String>();//用于存放全部证照的id
		List<String> Urllist=new ArrayList<String>();//用于存放证照地址
		List<String> CataNamelist=new ArrayList<String>();//用于存放目录名称的集合
		List<String> CataIdlist=new ArrayList<String>();//用于存放目录id的集合
		//List<String> blist=new ArrayList<String>();//用于存放证照目录的catalog_id
		for(int i=0;i<list.size();i++){//此处4位list.size()，等测试数据录入后再完善
			alist.add(list.get(i).getLicenceId());
		}
		List<ZzCatalog> zzCatalogList=this.zzCatalogService.queryByUserId(userid);
		for(int i=0;i<zzCatalogList.size();i++){
			CataNamelist.add(zzCatalogList.get(i).getCatalogName());
			CataIdlist.add(zzCatalogList.get(i).getCatalogId());
		}
		List<String> licenceOfCata =new ArrayList<String>();//在目录下的证照集合
		for(int i=0;i<CataIdlist.size();i++){
			List<ZzCatalogLicence>  l=this.zzCatalogLicenceService.queryByCatalogId(CataIdlist.get(i));//根据每个目录id获取该目录下的证照
			for(int j=0;j<l.size();j++){
				licenceOfCata.add(l.get(i).getLicenceId());//遍历插入集合licenceOfCata所有的包含在目录中的证照id
			}
		}
		alist.removeAll(licenceOfCata);//取出所有证照和目录下证照重合的部分,剩下的是单独呈现的证照id
		for(int i=0;i<alist.size();i++){
			Licence licence=this.licenceService.queryUrlByLicenceId(alist.get(i));
			Urllist.add(licence.getLicenceFileUrl());
		}
		model.put("licence", Urllist);
		model.put("catalogid",CataIdlist);
		model.put("catalogname",CataNamelist);
		return model;
	}
	
	
}
