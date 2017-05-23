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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.apache.commons.net.ftp.FTPClient;
/**
 * �ļ�������
 * @author guan.zheyuan
 */
@Controller
@RequestMapping("/file")
public class FileController {
	
	
	/**
	 * ִ��֤���ϴ�
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
		 //������,����ǰ�˷��͹������ļ�
        Iterator<FileItem> it = fileList.iterator();
        String name = "";
        String extName = "";
        while (it.hasNext()) {
        	 	FileItem item = it.next();
        	 	//�жϸñ����Ƿ�����ͨ����
             if (!item.isFormField()) {
            	 	name = item.getName();
                 long size = item.getSize();
                 String type = item.getContentType();
                 if (name == null || name.trim().equals("")) {
                     continue;
                 }
                 // ��չ����ʽ�� extName�����ļ��ĺ�׺,���� .txt
                 if (name.lastIndexOf(".") >= 0) {
                    extName = name.substring(name.lastIndexOf("."));
                 }
                 String savePath = request.getSession().getServletContext().getRealPath("");
                 //�����ļ���·��
                 savePath = savePath +SystemConstant.BACKUP_FOLDER;
                 File file = null;
                 do { 
                     // �����ļ�����
                     String sign= request.getParameter("sign");
                     name = this.gainNewFileName(sign,request.getParameter("cardNo"));
                     file = new File(savePath + name + extName);
                 } while (file.exists());
                 	File saveFile = new File(savePath + name + extName);
                 try {
                     item.write(saveFile);
                     uploadFtp(saveFile);
                     outs.write(name + extName);
                 } catch (Exception e) {
                     e.printStackTrace();
                 }
             }
		}
	}
	
	/**
	 * �ϴ�ftp������
	 */
	public  void  uploadFtp(File file){
		//����ftp  
        FTPClient ftpClient = new FTPClient();  
        ByteArrayInputStream bis = null;
        byte[] buffer = null;
        // ����FTP����  
        try {
        		//����ftp
			ftpClient.connect(SystemConstant.FTP_IP);
			//�ж��Ƿ��¼�ɹ�
			if(ftpClient.login(SystemConstant.FTP_NAME, SystemConstant.FTP_PASSWORD)){
				//�ж�·��
				if(ftpClient.changeWorkingDirectory(SystemConstant.FTP_PATH)){
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
				// �ر�������
				IOUtils.closeQuietly(bis);
				// �ر�����  
				ftpClient.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * �������ļ���
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
}
