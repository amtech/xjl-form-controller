package com.xjl.pt.form.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xjl.pt.core.domain.BjHistoryPiece;
import com.xjl.pt.core.domain.BjPieceLicence;
import com.xjl.pt.core.domain.DictItem;
import com.xjl.pt.core.domain.Licence;
import com.xjl.pt.core.domain.User;
import com.xjl.pt.core.domain.UserInfo;
import com.xjl.pt.core.service.BjHistoryPieceService;
import com.xjl.pt.core.service.BjPieceLicenceService;
import com.xjl.pt.core.service.DictItemService;
import com.xjl.pt.core.service.LicenceService;
import com.xjl.pt.core.service.UserInfoService;
import com.xjl.pt.core.service.UserService;
import com.xjl.pt.core.tools.DictItemTools;


@Controller
@RequestMapping(value="/historypiece")
public class HistoryPieceController {
	
	@Autowired
	private UserInfoService userInfoService;
	@Autowired
	private UserService userService;
	@Autowired
	private BjPieceLicenceService bjPieceLicenceService;
	@Autowired
	private BjHistoryPieceService bjHistoryPieceService;
	@Autowired
	private LicenceService licenceService;
	@Autowired
	private DictItemService dictItemService;
	private static final Log log = LogFactory.getLog(HistoryPieceController.class);
	SimpleDateFormat format=new SimpleDateFormat(SystemConstant.FOMATEDATE_SECOND);
	
	@ResponseBody
	@RequestMapping(value="/query/{page}/{rows}",method=RequestMethod.GET,consumes="application/json")
	public BootstrapGridTable query(HttpServletRequest request,@PathVariable Integer page,@PathVariable Integer rows){
		String search = StringUtils.trimToNull(request.getParameter("search"));
		List<BjHistoryPiece> list=this.bjHistoryPieceService.query(search, page, rows);
		List<DictItem> managementStatuslist=dictItemService.queryByDictId("a7d936ef-fb32-4343-9854-e836ec8c35f1", 1, 1000);
		for(BjHistoryPiece bjHistoryPiece:list){
			bjHistoryPiece.setManagementStatus$name(DictItemTools.getDictItemNames(bjHistoryPiece.getManagementStatus(),managementStatuslist));
		}
		return BootstrapGridTable.getInstance(list);
	}
	
	@ResponseBody
	@RequestMapping(value="/queryForDetail",method=RequestMethod.POST,consumes="application/json")
	public BjHistoryPiece queryForDetail(@RequestBody Map<String,Object> model,HttpServletRequest request){
		String pieceId=String.valueOf(model.get("pieceId"));
		BjHistoryPiece bjHistoryPiece=this.bjHistoryPieceService.queryByPieceId(pieceId);
		List<DictItem> managementStatuslist=dictItemService.queryByDictId("a7d936ef-fb32-4343-9854-e836ec8c35f1", 1, 1000);
		//服务对象字典
		//List<DictItem> ownerTypeList=dictItemService.queryByDictId("", 1, 1000);
		bjHistoryPiece.setManagementStatus$name(DictItemTools.getDictItemNames(bjHistoryPiece.getManagementStatus(),managementStatuslist));
		return bjHistoryPiece;
	}
	
	@ResponseBody
	@RequestMapping(value="/showPieceLicence",method=RequestMethod.POST,consumes="application/json")
	public List<BjPieceLicence> showPieceLicence(@RequestBody Map<String,Object> model,HttpServletRequest request){
		String pieceId=String.valueOf(model.get("pieceId"));
		List<BjPieceLicence> bjPieceLicenceList = this.bjPieceLicenceService.queryByPieceId(pieceId);
		for(BjPieceLicence bjPieceLicence : bjPieceLicenceList){
			Licence licence=this.licenceService.queryByLicenceId(bjPieceLicence.getLicenceId());
			bjPieceLicence.setLicenceId$name(licence.getLicenceName());
		}
		return bjPieceLicenceList;
	}
	
	/**
	 *  补交上传材料
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
		String pieceId=String.valueOf(models.get("pieceId"));
		log.debug("完成参数组装");
		//将上传的证照信息存入证照表中
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
		//将上传的证照存入与历史办件相关联的历史办件证照表中
		BjPieceLicence bjPieceLicence=new BjPieceLicence();
		bjPieceLicence.setLicenceId(licence.getLicenceId());
		bjPieceLicence.setPieceId(pieceId);
		bjPieceLicence.setPid(UUID.randomUUID().toString());
		bjPieceLicence.setState("A");
		bjPieceLicence.setUseable("01");//补交材料后  办件材料状态置为待处理
		this.bjPieceLicenceService.add(bjPieceLicence, userDefault);
		return XJLResponse.successInstance();
	}
	
	
}
