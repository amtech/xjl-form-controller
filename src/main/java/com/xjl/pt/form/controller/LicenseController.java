package com.xjl.pt.form.controller;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import com.xjl.pt.core.domain.Licence;
import com.xjl.pt.core.domain.User;
import com.xjl.pt.core.domain.UserInfo;
import com.xjl.pt.core.service.LicenceService;
import com.xjl.pt.core.service.UserInfoService;
import com.xjl.pt.core.service.UserService;
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
	private static final Log log = LogFactory.getLog(LicenseController.class);

	/**
	 *  分页
	 */
	@ResponseBody
	@RequestMapping(value="/query/{page}/{rows}",method=RequestMethod.GET,consumes = "application/json")
	public BootstrapGridTable query(HttpServletRequest request, @PathVariable Integer page,@PathVariable Integer rows){
		String search = StringUtils.trimToNull(request.getParameter("search"));
		List<Licence> list = licenceService.query(search, page, rows);
		for (Licence licence : list) {
			if(null != licence && null != licence.getLicenceId()){
				licence.setLicenceItemCount(this.licenceService.countByLicense(licence.getLicenceId()));
			}
		}
		return BootstrapGridTable.getInstance(list);
	}
	
	/**
	 *  提交上传材料
	 * @param models
	 * @param request
	 * @throws ParseException
	 */
	@ResponseBody
	@RequestMapping(value="/add",method=RequestMethod.POST,consumes = "application/json")
	public void add(@RequestBody Map<String, Object> models,HttpServletRequest request) throws ParseException{
		String licenceName= String.valueOf(models.get("licencename"));
		String startDate = String.valueOf(models.get("startDate"));
		String endDate = String.valueOf(models.get("endDate"));
		String ftpURL = String.valueOf(models.get("ftpURL"));
		String fileName = String.valueOf(models.get("fileName"));
		log.debug("完成参数组装");
		SimpleDateFormat format = new SimpleDateFormat(SystemConstant.FOMATDATE_DAY);
		//添加用户信息
		User user = (User) request.getSession().getAttribute(SystemConstant.SESSION_USER);
		UserInfo userInfo = this.userInfoService.queryByUserId(user.getUserId());
		Licence licence = new Licence();
		licence.setLicenceId(UUID.randomUUID().toString());
		licence.setLicenceName(StringUtils.isBlank(licenceName)?fileName:licenceName);
		licence.setLicenceFileUrl(ftpURL);
		licence.setIssuingDate(format.parse(startDate));
		licence.setExpirationDate(format.parse(endDate));
		licence.setLicenceFileUrl(ftpURL);
		licence.setOwnerOn(userInfo.getCardNo());
		licence.setOwnerType(userInfo.getUserType());
		this.licenceService.add(licence,null);
	}
	/**
	 * 上传
	 */
	@ResponseBody
	@RequestMapping(value="/upload")
	public Map<String, Object> uploadLicence(HttpServletRequest request,HttpServletResponse response){
		return new FileController().uploadFTPForController(request, response, SystemConstant.FTP_PATH_LICENCE);
	}
}
