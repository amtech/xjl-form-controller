package com.xjl.pt.controller.tools;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xjl.pt.core.annotation.TableDB;
import com.xjl.pt.core.domain.Dict;
import com.xjl.pt.core.domain.Table;
import com.xjl.pt.core.domain.TableField;
import com.xjl.pt.core.domain.User;
import com.xjl.pt.core.service.DictService;
import com.xjl.pt.core.service.TableFieldService;
import com.xjl.pt.core.service.TableService;
import com.xjl.pt.core.tools.XJLCoderTools;
import com.xjl.pt.form.controller.BootstrapGridTable;
import com.xjl.pt.form.controller.SessionTools;
import com.xjl.pt.form.controller.XJLResponse;

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
		sb.append("import com.xjl.pt.form.controller.SessionTools;\r\n");
		sb.append("import com.xjl.pt.form.controller.XJLResponse;\r\n");
		sb.append("import com.xjl.pt.form.controller.BootstrapGridTable;\r\n");
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
		sb.append("\t@Autowired\r\n");
		sb.append("\tprivate SessionTools sessionTools;\r\n");
		sb.append("\t@Autowired\r\n");
		String serviceClassName = domainClass.getSimpleName() + "Service";
		String serviceName = XJLCoderTools.getDomainFieldName(serviceClassName);
		sb.append("\tprivate " + serviceClassName + " " + serviceName + ";\r\n");
		sb.append(this.generateQuery(domainClass,serviceName));
		sb.append(this.generateQueryById(domainClass, serviceName));
		sb.append(this.generateAdd(domainClass, serviceName));
		sb.append(this.generateModify(domainClass, serviceName));
		sb.append(this.generateDelete(domainClass, serviceName));
		sb.append("}\r\n");
		sb.append("\r\n");
		return sb.toString();
	}
	private String generateQuery(Class domainClass,String serviceName){
		StringBuffer sb = new StringBuffer();
		sb.append("\t@ResponseBody\r\n");
		sb.append("\t@RequestMapping(value=\"/query/{page}/{rows}\",method=RequestMethod.GET,consumes = \"application/json\")\r\n");
		sb.append("\tpublic BootstrapGridTable query(HttpServletRequest request, @PathVariable Integer page,@PathVariable Integer rows){\r\n");
		sb.append("\t\tString search = StringUtils.trimToNull(request.getParameter(\"search\"));\r\n");
		sb.append("\t\tList<"+domainClass.getSimpleName() + "> list = this." + serviceName + ".query(search, page, rows);\r\n");
		sb.append("\t\treturn BootstrapGridTable.getInstance(list);\r\n");
		sb.append("\t}\r\n");
		sb.append("\r\n");
		return sb.toString();
	}
	private String generateQueryById(Class domainClass,String serviceName){
		String domainName = XJLCoderTools.getDomainFieldName(domainClass.getSimpleName());
		StringBuffer sb = new StringBuffer();
		sb.append("\t@ResponseBody\r\n");
		sb.append("\t@RequestMapping(value=\"/query/{id}\",method=RequestMethod.GET,consumes = \"application/json\")\r\n");
		sb.append("\tpublic " + domainClass.getSimpleName() + " queryById(HttpServletRequest request, @PathVariable String id){\r\n");
		sb.append("\t\t" + domainClass.getSimpleName() + " " + domainName + " = this." + serviceName + ".queryById(id);\r\n");
		sb.append("\t\treturn " + domainName + ";\r\n");
		sb.append("\t}\r\n");
		sb.append("\r\n");
		return sb.toString();
	}
	private String generateAdd(Class domainClass,String serviceName){
		String domainName = XJLCoderTools.getDomainFieldName(domainClass.getSimpleName());
		StringBuffer sb = new StringBuffer();
		sb.append("\t@ResponseBody\r\n");
		sb.append("\t@RequestMapping(value=\"/add\",method=RequestMethod.POST,consumes = \"application/json\")\r\n");
		sb.append("\tpublic XJLResponse add(HttpServletRequest request, @RequestBody " + domainClass.getSimpleName() + " " + domainName + "){\r\n");
		sb.append("\t\tUser user = this.sessionTools.getUser(request);\r\n");
		sb.append("\t\tthis." + serviceName + ".add(" + domainName + ", user);\r\n");
		sb.append("\t\treturn XJLResponse.successInstance();\r\n");
		sb.append("\t}\r\n");
		sb.append("\r\n");
		return sb.toString();
	}
	private String generateModify(Class domainClass,String serviceName){
		String domainName = XJLCoderTools.getDomainFieldName(domainClass.getSimpleName());
		StringBuffer sb = new StringBuffer();
		sb.append("\t@ResponseBody\r\n");
		sb.append("\t@RequestMapping(value=\"/modify\",method=RequestMethod.POST,consumes = \"application/json\")\r\n");
		sb.append("\tpublic XJLResponse modify(HttpServletRequest request, @RequestBody " + domainClass.getSimpleName() + " " + domainName + "){\r\n");
		sb.append("\t\tUser user = this.sessionTools.getUser(request);\r\n");
		sb.append("\t\tthis." + serviceName + ".modify(" + domainName + ", user);\r\n");
		sb.append("\t\treturn XJLResponse.successInstance();\r\n");
		sb.append("\t}\r\n");
		sb.append("\r\n");
		
		return sb.toString();
	}
	private String generateDelete(Class domainClass,String serviceName){
		String domainName = XJLCoderTools.getDomainFieldName(domainClass.getSimpleName());
		StringBuffer sb = new StringBuffer();
		sb.append("\t@ResponseBody\r\n");
		sb.append("\t@RequestMapping(value=\"/delete\",method=RequestMethod.POST,consumes = \"application/json\")\r\n");
		sb.append("\tpublic XJLResponse delete(HttpServletRequest request, @RequestBody List<" + domainClass.getSimpleName() + "> list){\r\n");
		sb.append("\t\tUser user = this.sessionTools.getUser(request);\r\n");
		sb.append("\t\tfor (" + domainClass.getSimpleName() + " " + domainName + " : list) {\r\n");
		sb.append("\t\t\tthis." + serviceName + ".delete(" + domainName + ", user);\r\n");
		sb.append("\t\t}\r\n");
		sb.append("\t\treturn XJLResponse.successInstance();\r\n");
		sb.append("\t}\r\n");
		sb.append("\r\n");
		
		return sb.toString();
	}
}
