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
import org.apache.commons.net.ftp.FTPClient;
/**
 * �ļ�������
 * @author guan.zheyuan
 */
@Controller
@RequestMapping("/file")
public class FileController {

	private static final String FTP_NAME="ftpxjl";
	private static final String FTP_PASSWORD="A6CCxjl";
	private static final String FTP_IP="123.57.4.104";
	private static final String FTP_PATH="/home/ftpxjl";
	
	private static final String SIGN_FRONT="front";
	private static final String SIGN_BACK="back";
	private static final String SIGN_HAND="hand";
	/**
	 * ִ��֤���ϴ�
	 */
	@SuppressWarnings({ "unchecked", "unused", "rawtypes", "static-access" })
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
                 File file = null;
                 String savePath = request.getSession().getServletContext().getRealPath("");
                 //�����ļ���·��
                 savePath = savePath + "/upload/";
                 do { 
                     // �����ļ�����
                     name = UUID.randomUUID().toString();
                     String sign= request.getParameter("sign");
                     if(this.SIGN_HAND.equals(sign)){
                    	 name+="_HAND";
                     }else if(this.SIGN_BACK.equals(sign)){
                    	 name+="_BACK";
                     }else if (this.SIGN_FRONT.equals(sign)) {
                    	 name+="_FRONT";
 					}
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
	@SuppressWarnings("static-access")
	public  void  uploadFtp(File file){
		//����ftp  
        FTPClient ftpClient = new FTPClient();  
        ByteArrayInputStream bis = null;
        byte[] buffer = null;
        // ����FTP����  
        try {
        		//����ftp
			ftpClient.connect(this.FTP_IP);
			//�ж��Ƿ��¼�ɹ�
			if(ftpClient.login(this.FTP_NAME, this.FTP_PASSWORD)){
				//�ж�·��
				if(ftpClient.changeWorkingDirectory(this.FTP_PATH)){
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
}
