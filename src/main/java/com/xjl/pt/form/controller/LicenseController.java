package com.xjl.pt.form.controller;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xjl.pt.core.domain.Licence;
import com.xjl.pt.core.domain.User;
import com.xjl.pt.core.service.LicenceService;
import com.xjl.pt.core.service.UserService;

/**
 * 证照控制类
 * @author guan.zheyuan
 */
@Controller
@RequestMapping("/license")
public class LicenseController {
	
	@Autowired
	private LicenceService licenceService;
	@Autowired
	private UserService userService;
	
	/**
	 *  分页
	 */
	@ResponseBody
	@RequestMapping(value="/query/{page}/{rows}",method=RequestMethod.GET,consumes = "application/json")
	public BootstrapGridTable query(HttpServletRequest request, @PathVariable Integer page,@PathVariable Integer rows){
		String search = StringUtils.trimToNull(request.getParameter("search"));
		List<Licence> list = licenceService.query(search, page, rows);
		for (Licence license : list) {
			if(null != license && null != license.getLicenceId()){
				license.setLicenceItemCount(this.licenceService.countByLicense(license.getLicenceId()));
			}
		}
		return BootstrapGridTable.getInstance(list);
	}
	
	/**
	 * 上传
	 */
	@ResponseBody
	@RequestMapping(value="/upload")
	@SuppressWarnings({ "unchecked", "unused", "rawtypes"})
	public void uploadLicence(HttpServletRequest request,HttpServletResponse response){
		DiskFileItemFactory fac = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(fac);
		upload.setHeaderEncoding("utf-8");
		List fileList = null;
		PrintWriter outs = null;
		try {
			fileList= upload.parseRequest(request);
			outs = response.getWriter();
		} catch (FileUploadException e) {
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		//迭代器,搜索前端发送过来的文件
        Iterator<FileItem> it = fileList.iterator();
        String name = "";
        String extName = "";
        while (it.hasNext()) {
	        	FileItem item = it.next();
	    	 	//判断该表单项是否是普通类型
	         if (!item.isFormField()) {
		        	 	name = item.getName();
		             long size = item.getSize();
		             String type = item.getContentType();
		             if (name == null || name.trim().equals("")) {
		                 continue;
		             }
		             // 扩展名格式： extName就是文件的后缀,例如 .txt
		             if (name.lastIndexOf(".") >= 0) {
		                extName = name.substring(name.lastIndexOf("."));
		             }
		             String savePath = request.getSession().getServletContext().getRealPath("");
	                 //备份文件的路径
	                 savePath = savePath +SystemConstant.BACKUP_FOLDER;
	                 File file = null;
	                 String newFile=UUID.randomUUID().toString()+extName;
	                 do { 
	                	 	file = new File(savePath + newFile);
	                 }while (file.exists());
	                 	File saveFile = new File(savePath + newFile);
	                 	try {
							item.write(saveFile);
							new FileController().	uploadFtp(saveFile,SystemConstant.FTP_PATH_LICENCE);
							//添加用户信息
							User userDefault = this.userService.queryById("9fcfdb3e-3bdb-4234-a0c4-f91d023c308e");
							Licence licence = new Licence();
							licence.setLicenceId(UUID.randomUUID().toString());
							licence.setLicenceName(name);
							licence.setLicenceFileUrl(newFile);
							//license.setOwnerOn(ownerOn);
							//license.setOwnerType(ownerType);
							this.licenceService.add(licence, userDefault);
	                 	} catch (Exception e) {
							e.printStackTrace();
						}
	         	}
        	   }
	 }
}
