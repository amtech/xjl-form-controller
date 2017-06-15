package com.xjl.pt.form.controller;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketException;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import sun.misc.BASE64Decoder;
import org.apache.commons.net.ftp.FTPClient;
/**
 * 文件控制器
 * @author guan.zheyuan
 */
@SuppressWarnings("restriction")
@Controller
@RequestMapping("/file")
public class FileController {
	
	
	
	/**
	 * 富文本文件上传
	 */
	@SuppressWarnings({ "unchecked", "unused", "rawtypes"})
	@RequestMapping(value="/uploadEditBox")
	public void fileUploadEditBox(HttpServletRequest request,HttpServletResponse response){
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
             String newName = UUID.randomUUID().toString();
             do { 
                 // 生成文件名：
                 file = new File(savePath + newName + extName);
             } while (file.exists());
             	File saveFile = new File(savePath + newName + extName);
             try {
                 item.write(saveFile);
                 uploadFtp(saveFile,SystemConstant.FTP_PATH_EDITBOX);
                 outs.write(newName + extName);
             } catch (Exception e) {
                 e.printStackTrace();
             }
         }
	}
	}
	/**
	 * 执行证照上传
	 */
	@SuppressWarnings({ "unchecked", "unused", "rawtypes"})
	@RequestMapping(value="/uploadFile")
	public void fileUpload(HttpServletRequest request,HttpServletResponse response){
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
                 do { 
                     // 生成文件名：
                     String sign= request.getParameter("sign");
                     name = this.gainNewFileName(sign,request.getParameter("cardNo"));
                     file = new File(savePath + name + extName);
                 } while (file.exists());
                 	File saveFile = new File(savePath + name + extName);
                 try {
                     item.write(saveFile);
                     uploadFtp(saveFile,SystemConstant.FTP_PATH_REALNAME);
                     outs.write(name + extName);
                 } catch (Exception e) {
                     e.printStackTrace();
                 }
             }
		}
	}
	
	/**
	 * 上传ftp服务器
	 */
	public  void  uploadFtp(File file,String ftpPath){
		//创建ftp  
        FTPClient ftpClient = new FTPClient();  
        ByteArrayInputStream bis = null;
        byte[] buffer = null;
        // 建立FTP连接  
        try {
        		//链接ftp
			ftpClient.connect(SystemConstant.FTP_IP);
			//判断是否登录成功
			if(ftpClient.login(SystemConstant.FTP_NAME, SystemConstant.FTP_PASSWORD)){
				//判断路径
				if(ftpClient.changeWorkingDirectory(ftpPath)){
						if(null != file){
							FileInputStream fis = new FileInputStream(file);
							ByteArrayOutputStream bos = new ByteArrayOutputStream();
							byte[] b=new byte[1024];
							int n;
							while((n=fis.read(b))!=-1){
								bos.write(b, 0, n);
							}
							fis.close();
							bos.close();
							buffer = bos.toByteArray();
							bis = new ByteArrayInputStream(buffer);
							ftpClient.setBufferSize(1024);
							if(ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE)){
								ftpClient.enterLocalPassiveMode();
								ftpClient.storeFile(file.getName(), bis);
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
	@SuppressWarnings("restriction")
	public static String  uploadFtp(String base64str,String picturename){
		//创建ftp  
        FTPClient ftpClient = new FTPClient();  
        ByteArrayInputStream bis = null;
        byte[] buffer = null;
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
