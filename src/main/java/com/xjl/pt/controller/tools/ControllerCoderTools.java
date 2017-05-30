package com.xjl.pt.controller.tools;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.xjl.pt.core.annotation.TableDB;
import com.xjl.pt.core.domain.Table;
import com.xjl.pt.core.domain.TableField;
import com.xjl.pt.core.service.TableFieldService;
import com.xjl.pt.core.service.TableService;
import com.xjl.pt.core.tools.XJLCoderTools;

/**
 * 生成控制器
 * @author lilisheng
 *
 */
@Component
public class ControllerCoderTools {
	private static Log log = LogFactory.getLog(ControllerCoderTools.class);
	@Autowired
	private TableService tableService;
	@Autowired
	private TableFieldService tableFieldService;
	/**
	 * 生成控制器
	 * @param tableName 表名称
	 * @param basePackageName 基础包名
	 */
	public void generateController(Class domainClass,String controllerPackageName){
		String tableName = ((TableDB)domainClass.getAnnotation(TableDB.class)).name();
		log.debug("tableName:" + tableName + " controllerPackageName:" + controllerPackageName);
		Table table = this.tableService.queryByName(tableName);
		log.debug("table:" + table);
		List<TableField> fieldList = this.tableFieldService.queryByTableId(table.getTableId(), 1, 100);
		String tableShortName = XJLCoderTools.getShortName(tableName);
		log.debug("tableShortName:" + tableShortName);
		String domainName = XJLCoderTools.getDomainName(tableShortName);
		log.debug("domainName:" + domainName);
		String domainClassName = domainClass.getName();
		log.debug("domainClassName:" + domainClassName);
		String controllerClassName = controllerPackageName + "." + domainName + "Controller";
		log.debug("controllerClassName:" + controllerClassName);
		File controllerFile = XJLCoderTools.getClassFile(controllerClassName);
		log.debug("controllerFile:" + controllerFile.getAbsolutePath());
		XJLCoderTools.forceCreateFile(controllerFile);
		String controllerContent = this.generateContent(controllerPackageName, domainName + "Controller", domainClass,table, fieldList);
		try {
			log.debug("写入domainContent内容");
			FileUtils.write(controllerFile, controllerContent);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	public String generateContent(String controllerPackageName,String controllerName, Class domainClass,Table table, List<TableField> fieldList){
		StringBuffer sb = new StringBuffer();
		sb.append(this.generateHeaderContent(controllerPackageName, controllerName, domainClass, table, fieldList));
		sb.append(this.generateBodyContent(controllerPackageName, controllerName, domainClass, table, fieldList));
		return sb.toString();
	}
	public String generateHeaderContent(String controllerPackageName,String controllerName, Class domainClass,Table table, List<TableField> fieldList){
		StringBuffer sb = new StringBuffer();
		sb.append("package " + controllerPackageName + ";\r\n");
		sb.append("\r\n");
		sb.append("import java.util.List;\r\n");
		sb.append("\r\n");
		sb.append("import javax.servlet.http.HttpServletRequest;\r\n");
		sb.append("import org.apache.commons.lang3.StringUtils;\r\n");
		sb.append("import org.apache.commons.lang3.math.NumberUtils;\r\n");
		sb.append("import org.springframework.beans.factory.annotation.Autowired;\r\n");
		sb.append("import org.springframework.stereotype.Controller;\r\n");
		sb.append("import org.springframework.web.bind.annotation.PathVariable;\r\n");
		sb.append("import org.springframework.web.bind.annotation.RequestBody;\r\n");
		sb.append("import org.springframework.web.bind.annotation.RequestMapping;\r\n");
		sb.append("import org.springframework.web.bind.annotation.RequestMethod;\r\n");
		sb.append("import org.springframework.web.bind.annotation.ResponseBody;\r\n");
		sb.append("\r\n");
		sb.append("import " + domainClass.getName() + ";\r\n");
		sb.append("import com.xjl.pt.core.domain.User;\r\n");
		String domainPackage = domainClass.getPackage().getName();
		String servicePackage = domainPackage.substring(0,domainPackage.length()-6) + "service";
		String serviceName = domainClass.getSimpleName() + "Service";
		sb.append("import " + servicePackage + "." + serviceName + ";\r\n");
		return sb.toString();
	}
	public String generateBodyContent(String controllerPackageName,String controllerName, Class domainClass,Table table, List<TableField> fieldList){
		StringBuffer sb = new StringBuffer();
		sb.append("/**\r\n");
		sb.append(" * 字典控制器类\r\n");
		sb.append(" * @author ControllerCoderTools\r\n");
		sb.append(" *\r\n");
		sb.append(" */\r\n");
		sb.append("@Controller\r\n");
		sb.append("@RequestMapping(\"/" + XJLCoderTools.getDomainFieldName(domainClass.getSimpleName()) + "\")\r\n");
		sb.append("public class " + controllerName + " {\r\n");
		sb.append("\r\n");
		sb.append("}\r\n");
		sb.append("\r\n");
		return sb.toString();
	}
}
