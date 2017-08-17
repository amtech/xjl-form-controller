package com.xjl.cdc.cloud.controller;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.dom4j.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import com.xjl.cdc.cloud.domain.CdcProject;
import com.xjl.cdc.cloud.domain.CdcProjectModule;
import com.xjl.cdc.cloud.service.CdcProjectModuleService;
import com.xjl.cdc.cloud.service.CdcProjectService;
import com.xjl.pt.core.domain.User;
import com.xjl.pt.form.controller.BootstrapGridTable;
import com.xjl.pt.form.controller.SessionTools;
import com.xjl.pt.form.controller.XJLResponse;

@Controller
@RequestMapping("/demo")
public class DemoController {
	private static Log log = LogFactory.getLog(DemoController.class);
	@RequestMapping(value = "/image/{name}/{json}", method = RequestMethod.GET)  
	public ResponseEntity<byte[]> image(HttpServletRequest request,@PathVariable String name,@PathVariable String json) throws IOException {  
		ServletContext servletContext = request.getSession().getServletContext();
		name = URLDecoder.decode(name, "UTF-8");
		json = URLDecoder.decode(json, "UTF-8");
		log.debug("json:" + json);
		JSONObject paramsObj = (JSONObject)JSONObject.parse(json);
		String 身份证号码 = paramsObj.getString("身份证号码");
		log.debug("身份证号码:" + 身份证号码);
		File path = new File(servletContext.getRealPath("/demo/template/"+name));
		File imageFile = new File(path, "pic.png");
		File pointFile = new File(path, "point.json");
		JSONObject pointObj = (JSONObject)JSONObject.parse(FileUtils.readFileToString(pointFile, "UTF-8"));
		JSONObject fontObj = pointObj.getJSONObject("font");
		JSONObject dataObj = pointObj.getJSONObject("data");
		 Image src = ImageIO.read(imageFile);
         int width = src.getWidth(null);
         int height = src.getHeight(null);
         BufferedImage image = new BufferedImage(width, height,
                 BufferedImage.TYPE_INT_RGB);
         Graphics2D g = image.createGraphics();

//         Color.BLACK
         g.setFont(new Font("宋体", Font.PLAIN, 12));
         if (fontObj != null){
        	 if (fontObj.containsKey("color")){
        		 g.setColor(Color.BLACK);
        	 }
         }
         
         g.drawImage(src, 0, 0, width, height, null);
         Set<Entry<String,Object>> names = dataObj.entrySet();
         int fontSize = 5;
         
         for (Entry entry : names) {
        	 
			String key = (String)entry.getKey();
			log.debug("key:" + key);
			if (paramsObj.containsKey(key)){
				log.debug("url的json中包含该key");
				String pressText = paramsObj.getString(key);
				log.debug("对应的值:"+pressText);
				JSONObject point = (JSONObject)entry.getValue();
				int x = point.getIntValue("x");
				int y = point.getIntValue("y");
		        g.drawString(pressText, x, y);
			}
		}
         g.dispose();
		ByteArrayOutputStream book = new ByteArrayOutputStream();
		ImageIO.write(image, "png", book);
		
	    byte[] zp = book.toByteArray();
	    HttpHeaders headers = new HttpHeaders();  
	    headers.setContentType(MediaType.IMAGE_PNG);  
	    return new ResponseEntity<byte[]>(zp, headers, HttpStatus.OK);  
	} 
	
	/**
     * 计算text的长度（一个中文算两个字符）
     * @param text
     * @return
     */
    public final static int getLength(String text) {
        int length = 0;
        for (int i = 0; i < text.length(); i++) {
            if (new String(text.charAt(i) + "").getBytes().length > 1) {
                length += 2;
            } else {
                length += 1;
            }
        }
        return length / 2;
    }
	
}
