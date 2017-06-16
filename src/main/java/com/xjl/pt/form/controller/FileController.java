package com.xjl.pt.form.controller;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import sun.misc.BASE64Decoder;
import org.apache.commons.net.ftp.FTPClient;
/**
 * 文件控制器
 * @author guan.zheyuan
 */
@Controller
@RequestMapping("/file")
@SuppressWarnings("rawtypes")
public class FileController {
	
	private static final Log log = LogFactory.getLog(FileController.class);
	
	/**
	 * 富文本文件上传
	 */
	@RequestMapping(value="/uploadEditBox")
	public void fileUploadEditBox(HttpServletRequest request,HttpServletResponse response){
		uploadFTP(request, response,SystemConstant.FTP_PATH_EDITBOX);
	}
	/**
	 * 执行证照上传
	 */
	@RequestMapping(value="/uploadFile")
	public void fileUpload(HttpServletRequest request,HttpServletResponse response){
		this.uploadFTP(request, response, SystemConstant.FTP_PATH_LICENCE);
	}
	 
	/**
	 * 控制器调用 返回上传信息
	 * @param request
	 * @param response
	 * @param type
	 * @return
	 */
	public Map<String,Object> uploadFTPForController(HttpServletRequest request,HttpServletResponse response,String type){
		 Map<String, Object> tempMap = null;
		List<FileItem> fileList = getFileList(request);  
		log.debug("文件数量:" + fileList.size());
		 //迭代器,搜索前端发送过来的文件
		for (FileItem item : fileList) {
    	 	//判断该表单项是否是普通类型
         if (!item.isFormField()) {
        	 String name = item.getName();
        	 log.debug("name:" + name);
             if (StringUtils.isBlank(name)) {
                 continue;
             }
             File saveFile = this.getTempFile(request, name,SystemConstant.BACKUP_FOLDER);
             log.debug("saveFile:" + saveFile.getName());
             try {
            	 	log.debug("开始写入临时文件");
	            	item.write(saveFile);
	            	log.debug("开始上传ftp");
	            String ftpURL = uploadFtp(saveFile,type);
	            log.debug("ftp地址：" + ftpURL);
	            tempMap = new HashMap<>();
	            tempMap.put("fileName", saveFile.getName());
	            tempMap.put("ftpURL", ftpURL);
             } catch (Exception e) {
                 throw new RuntimeException(e);
             }
         }
	}
		return tempMap;
	}
	/**
	 * 上传ftpf 输出页面
	 * @param request
	 * @param response
	 * @param type
	 */
	@SuppressWarnings("unchecked")
	public void uploadFTP(HttpServletRequest request, HttpServletResponse response,String type) {
		List<FileItem> fileList = getFileList(request);  
		log.debug("文件数量:" + fileList.size());
		 //迭代器,搜索前端发送过来的文件
		for (FileItem item : fileList) {
    	 	//判断该表单项是否是普通类型
         if (!item.isFormField()) {
        	 String name = item.getName();
        	 log.debug("name:" + name);
             if (StringUtils.isBlank(name)) {
                 continue;
             }
             File saveFile = this.getTempFile(request, name,SystemConstant.BACKUP_FOLDER);
             log.debug("saveFile:" + saveFile.getName());
             try {
            	 log.debug("开始写入临时文件");
            	item.write(saveFile);
            	log.debug("开始上传ftp");
                String ftpURL = uploadFtp(saveFile,type);
                log.debug("ftp地址：" + ftpURL);
                response.getWriter().write(ftpURL);
             } catch (Exception e) {
                 throw new RuntimeException(e);
             }
         }
	}
	}
	/**
	 * 得到文件列表
	 */
	private List getFileList(HttpServletRequest request) {
		DiskFileItemFactory fac = new DiskFileItemFactory();
		log.debug("DiskFileItemFactory"+fac);
		ServletFileUpload upload = new ServletFileUpload(fac);
		log.debug("ServletFileUpload"+upload);
		upload.setHeaderEncoding("utf-8");
		List fileList = null;
		try {
			fileList= upload.parseRequest(request);
			log.debug("得到文件列表："+fileList.size());
		} catch (FileUploadException e) {
			throw new RuntimeException(e);
		}
		return fileList;
	}
	/**
	 * 得到临时文件
	 * @param request
	 * @param name
	 * @param type
	 * @return
	 */
	private File getTempFile(HttpServletRequest request,String name,String type){
		// 扩展名格式： extName就是文件的后缀,例如 .txt
        String extName = FilenameUtils.getExtension(name);
        log.debug("文件扩展名："+extName);
        //备份文件的路径
        String savePath = getTempFilePath(request,type);
        log.debug("临时文件目录："+savePath);
        String newName = UUID.randomUUID().toString();
        // 生成文件名：
        File saveFile = new File(savePath + newName + "."+extName);
        log.debug("生成文件："+savePath.getBytes());
        return saveFile;
	}
	/**
	 * 得到临时目录
	 * @param request
	 * @param pathType
	 * @return
	 */
	private String getTempFilePath(HttpServletRequest request,String pathType){
		String savePath = request.getSession().getServletContext().getRealPath("");
		return savePath +pathType;
	}
	/**
	 * 组装ftp路径
	 * @param ip
	 * @param name
	 * @param password
	 * @param ftpPath ftp文件的路径
	 */
	public String getFtpURL(String ip,String name,String password,String ftpPath){
		return "ftp://"+name+":"+password+"@"+ip+ftpPath;
	}
	
	/**
	 * 上传ftp服务器
	 * @return ftp路径
	 */
	public  String  uploadFtp(File file,String ftpPath){
		//创建ftp  
        FTPClient ftpClient = new FTPClient();  
        log.debug("创建 ftp："+ftpClient);
        ByteArrayInputStream bis = null;
        // 建立FTP连接  
        try {
        		//链接ftp
			ftpClient.connect(SystemConstant.FTP_IP);
			log.debug("链接ftp："+ftpClient.getStatus());
			//判断是否登录成功
			if(ftpClient.login(SystemConstant.FTP_NAME, SystemConstant.FTP_PASSWORD)){
				log.debug("ftp链接成功");
				//判断路径
				if(ftpClient.changeWorkingDirectory(ftpPath)){
						if(null != file){
							FileInputStream fis = new FileInputStream(file);
							ftpClient.setBufferSize(1024);
							if(ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE)){
								ftpClient.enterLocalPassiveMode();
								ftpClient.storeFile(file.getName(), fis);
							}
						}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				// 关闭输入流
				IOUtils.closeQuietly(bis);
				// 关闭连接  
				ftpClient.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
        return getFtpURL(SystemConstant.FTP_IP,SystemConstant.FTP_NAME,SystemConstant.FTP_PASSWORD,SystemConstant.FTP_READPATH_EDITBOX+"/"+file.getName());
	}
	/**
	 * 生成新文件名
	 */
	public String gainNewFileName(String sign,String name){
		if (SystemConstant.SIGN_FRONT.equals(sign)) {
			name+=SystemConstant.SIGN_FRONT_VALUE;
		}else if (SystemConstant.SIGN_BACK.equals(sign)) {
			name+=SystemConstant.SIGN_BACKA_VALUE;
		}else if (SystemConstant.SIGN_HAND.equals(sign)) {
			name+=SystemConstant.SIGN_HAND_VALUE;
		}else {
			name+="_";
		}
		return name;
	}
	
	/**
	 * base64码直接上传ftp服务器
	 */
	public static String  uploadFtp(String base64str,String picturename){
		//创建ftp  
        FTPClient ftpClient = new FTPClient();  
        ByteArrayInputStream bis = null;
        byte[] buffer = null;
        @SuppressWarnings("restriction")
		BASE64Decoder decoder = new BASE64Decoder();
        // 建立FTP连接  
        try {
        		//链接ftp
			ftpClient.connect(SystemConstant.FTP_IP);
			//判断是否登录成功
			if(ftpClient.login(SystemConstant.FTP_NAME, SystemConstant.FTP_PASSWORD)){
				//判断路径
				if(ftpClient.changeWorkingDirectory(SystemConstant.FTP_PATH_REALNAME)){
						
						if(base64str!=null || base64str!=""){
							@SuppressWarnings("restriction")
							byte[] b = decoder.decodeBuffer(base64str);
							ByteArrayOutputStream bos = new ByteArrayOutputStream();
							//Base64解码  
				            for(int i=0;i<b.length;++i)  
				            {  
				                if(b[i]<0)  
				                {//调整异常数据  
				                    b[i]+=256;  
				                }  
				            }
				            bos.write(b);
				            bos.flush();
							bos.close();
							buffer = bos.toByteArray();
							bis = new ByteArrayInputStream(buffer);
							ftpClient.setBufferSize(1024);
							if(ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE)){
								ftpClient.enterLocalPassiveMode();
								ftpClient.storeFile(picturename, bis);
							}
						}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				// 关闭输入流
				IOUtils.closeQuietly(bis);
				// 关闭连接  
				ftpClient.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
        return SystemConstant.FTP_PATH_REALNAME+"/"+picturename;
	}
}
