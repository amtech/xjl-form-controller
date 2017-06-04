package com.xjl.pt.controller.tools;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xjl.pt.core.annotation.TableDB;
import com.xjl.pt.core.domain.Table;
import com.xjl.pt.core.domain.TableField;
import com.xjl.pt.core.service.TableFieldService;
import com.xjl.pt.core.service.TableService;
import com.xjl.pt.core.tools.XJLCoderTools;

/**
 * 页面代码生成工具
 * @author lilisheng
 *
 */
@Component
public class PageCoderTools {
	private static Log log = LogFactory.getLog(PageCoderTools.class);
	@Autowired
	private TableService tableService;
	@Autowired
	private TableFieldService tableFieldService;
	/**
	 * 生成控制器
	 * @param tableName 表名称
	 * @param basePackageName 基础包名
	 */
	public void generatePage(Class domainClass,String pagePath){
		String tableName = ((TableDB)domainClass.getAnnotation(TableDB.class)).name();
		log.debug("tableName:" + tableName + " pagePath:" + pagePath);
		Table table = this.tableService.queryByName(tableName);
		log.debug("table:" + table);
		List<TableField> fieldList = this.tableFieldService.queryByTableId(table.getTableId(), 1, 100);
		String tableShortName = XJLCoderTools.getShortName(tableName);
		log.debug("tableShortName:" + tableShortName);
		String domainName = XJLCoderTools.getDomainName(tableShortName);
		log.debug("domainName:" + domainName);
		String domainClassName = domainClass.getName();
		log.debug("domainClassName:" + domainClassName);
		String controllerUrl = "/" +  XJLCoderTools.getDomainFieldName(domainName);
		File pageFile = new File(pagePath+"/" + XJLCoderTools.getDomainFieldName(domainName)+".html");
		log.debug("pageFile:" + pageFile.getAbsolutePath());
		XJLCoderTools.forceCreateFile(pageFile);
		String jsPath = "";
		File pageParent = pageFile.getParentFile();
		
		while (!pageParent.getName().equals("webapp")){
			log.debug("fileName:" + pageParent.getName());
			jsPath += "../";
			pageParent = pageParent.getParentFile();
		}
		String content = this.generateContent(jsPath, controllerUrl, domainClass,table, fieldList);
		try {
			log.debug("写入content内容");
			FileUtils.write(pageFile, content);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	} 
	private String generateContent(String jsPath, String controllerUrl, Class domain, Table table, List<TableField> fieldList){
		StringBuffer sb = new StringBuffer();
		sb.append(this.generateHeader(jsPath,controllerUrl,domain,table,fieldList));
		sb.append(this.generateGrid(jsPath,controllerUrl,domain,table,fieldList));
		sb.append(this.generateModal(jsPath,controllerUrl,domain,table,fieldList));
		sb.append(this.generateFooter());
		return sb.toString();
	}
	private String generateFooter(){
		StringBuffer sb = new StringBuffer();
		sb.append("</body>\r\n");
		sb.append("</html>\r\n");
		return sb.toString();
	}
	private String generateModal(String jsPath,String controllerUrl, Class domain,Table table, List<TableField> fieldList){
		StringBuffer sb = new StringBuffer();
		sb.append("\r\n");
		sb.append("		<div data-backdrop=\"false\" class=\"modal fade\" id=\"myModal\" tabindex=\"-1\" role=\"dialog\" aria-labelledby=\"myModalLabel\">\r\n");
		sb.append("        <div class=\"modal-dialog\" role=\"document\">\r\n");
		sb.append("            <div class=\"modal-content\">\r\n");
		sb.append("                <div class=\"modal-header\">\r\n");
		sb.append("                    <button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-label=\"Close\"><span aria-hidden=\"true\">&times;</span></button>\r\n");
		sb.append("                   <h4 class=\"modal-title\" id=\"myModalLabel\">操作</h4>\r\n");
		sb.append("                </div>\r\n");
		sb.append("                <div class=\"modal-body\">\r\n");
		sb.append("                		\r\n");
		for (TableField tableField : fieldList) {
			String fieldName = XJLCoderTools.getDomainFieldName(tableField.getFieldName());
			if (TableField.FIELD_TYPE_PK.equals(tableField.getFieldType())){
				continue;
			}
			if (TableField.FIELD_TYPE_FK.equals(tableField.getFieldType())){
				Table fkTable = this.tableService.queryById(tableField.getTableId());
				sb.append("                    <div class=\"input-group\" style=\"display: none\">\r\n");
				sb.append("                        <label for=\"txt_" + fieldName + "\">"+ tableField.getFieldDesc() +"</label>\r\n");
				sb.append("                        <input type=\"text\" name=\"txt_" + fieldName + "\" data-bind=\"value:" + fieldName + "\" class=\"form-control\" id=\"txt_" + fieldName + "\" placeholder=\""+ tableField.getFieldDesc() +"\">\r\n");
				sb.append("                    </div>\r\n");
				sb.append("                    <div>\r\n");
				sb.append("                        <label for=\"txt_" + fieldName + "$name\">"+ tableField.getFieldDesc() +"</label>\r\n");
				sb.append("                        <div class=\"input-group\">\r\n");
				sb.append("                            <input type=\"text\" name=\"txt_" + fieldName + "$name\" data-bind=\"value:" + fieldName + "$name\" class=\"form-control\" id=\"txt_" + fieldName + "$name\" placeholder=\""+ tableField.getFieldDesc() +"\">\r\n");
				sb.append("                            <span class=\"input-group-btn\">\r\n");
				sb.append("                                <button id=\"btn_" + fieldName + "_select\" type=\"button\" class=\"btn btn-default\" onclick=\"window.open('"+XJLCoderTools.getDomainFieldName(fkTable.getTableName())+".html?parentSelectEvent=set" + StringUtils.capitalize(fieldName) + "&selectFlag=true')\">选择</button>\r\n");
				sb.append("                	            </span>\r\n");
				sb.append("                        </div>\r\n");
				sb.append("                    </div>\r\n");
			} else {
				sb.append("                    <div class=\"form-group\">\r\n");
				sb.append("                        <label for=\"txt_" + fieldName + "\">"+ tableField.getFieldDesc() +"</label>\r\n");
				sb.append("                        <input type=\"text\" name=\"txt_" + fieldName + "\" data-bind=\"value:" + fieldName + "\" class=\"form-control\" id=\"txt_" + fieldName + "\" placeholder=\""+ tableField.getFieldDesc() +"\">\r\n");
				sb.append("                    </div>\r\n");
			}
		}
		sb.append("                </div>\r\n");
		sb.append("                <div class=\"modal-footer\">\r\n");
		sb.append("                    <button type=\"button\" class=\"btn btn-default\" data-dismiss=\"modal\"><span class=\"glyphicon glyphicon-remove\" aria-hidden=\"true\"></span>关闭</button>\r\n");
		sb.append("                    <button type=\"button\" id=\"btn_submit\" class=\"btn btn-primary\" data-dismiss=\"modal\"><span class=\"glyphicon glyphicon-floppy-disk\" aria-hidden=\"true\"></span>保存</button>\r\n");
		sb.append("                </div>\r\n");
		sb.append("            </div>\r\n");
		sb.append("        </div>\r\n");
		sb.append("    </div>\r\n");
		return sb.toString();
	}
	private String generateGrid(String jsPath,String controllerUrl, Class domain,Table table, List<TableField> fieldList){
		StringBuffer sb = new StringBuffer();
		sb.append("\r\n");
		sb.append("<div class=\"panel-body\" style=\"padding-bottom:0px;\">\r\n");
		sb.append("<div id=\"toolbar\" class=\"btn-group\">\r\n");
		sb.append("    <button id=\"btn_add\" type=\"button\" class=\"btn btn-default\">\r\n");
		sb.append("        <span class=\"glyphicon glyphicon-plus\" aria-hidden=\"true\" style=\"display: none\"></span>新增\r\n");
		sb.append("    </button>\r\n");
		sb.append("    <button id=\"btn_edit\" type=\"button\" class=\"btn btn-default\" style=\"display: none\">\r\n");
		sb.append("        <span class=\"glyphicon glyphicon-pencil\" aria-hidden=\"true\"></span>修改\r\n");
		sb.append("    </button>\r\n");
		sb.append("    <button id=\"btn_delete\" type=\"button\" class=\"btn btn-default\" style=\"display: none\">\r\n");
		sb.append("        <span class=\"glyphicon glyphicon-remove\" aria-hidden=\"true\"></span>删除\r\n");
		sb.append("    </button>\r\n");
		sb.append("	    <button id=\"btn_select\" type=\"button\" class=\"btn btn-default\" style=\"display: none\" >\r\n");
		sb.append("        <span class=\"glyphicon glyphicon-ok-circle\" aria-hidden=\"true\"></span>选择\r\n");
		sb.append("    </button>\r\n");
		sb.append("    <button id=\"btn_back\" type=\"button\" class=\"btn btn-default\" style=\"display: none\">\r\n");
		sb.append("        <span class=\"glyphicon glyphicon-ban-circle\" aria-hidden=\"true\"></span>返回\r\n");
		sb.append("    </button>\r\n");
		sb.append("</div>\r\n");
		sb.append("<table id=\"tb_" + XJLCoderTools.getDomainFieldName(domain.getSimpleName()) + "\" data-bind=\"myBootstrapTable:$root\">\r\n");
		sb.append("    <thead>\r\n");
		sb.append("        <tr>\r\n");
		sb.append("            <th data-checkbox=\"true\"></th>\r\n");
		for (TableField tableField : fieldList) {
			if (tableField.getFieldType().equals(TableField.FIELD_TYPE_PK)){
				//如果是主键，一般不需要显示
				continue;
			} 
			sb.append("            <th data-field=\"");
			 if (tableField.getFieldType().equals(TableField.FIELD_TYPE_FK)){
				sb.append(XJLCoderTools.getDomainFieldName(tableField.getFieldName())+"$name");
			} else {
				sb.append(XJLCoderTools.getDomainFieldName(tableField.getFieldName()));
			}
			sb.append("\">"+ tableField.getFieldDesc() +"</th>\r\n");
		}
		sb.append("        </tr>\r\n");
		sb.append("    </thead>\r\n");
		sb.append("</table>\r\n");
		sb.append("\r\n");
		sb.append("</div>\r\n");
		return sb.toString();
	}
	private String generateHeader(String jsPath,String controllerUrl, Class domain,Table table, List<TableField> fieldList){
		StringBuffer sb = new StringBuffer();
		sb.append("\r\n");
		sb.append("<html>\r\n");
		sb.append("<meta charset=\"utf-8\">\r\n");
		sb.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\r\n");
		sb.append("<head>\r\n");
		sb.append("<link rel=\"stylesheet\" href=\"" + jsPath + "js/bootstrap/css/bootstrap.min.css\" >\r\n");
		sb.append("<link rel=\"stylesheet\" href=\"" + jsPath + "js/bootstrap/css/bootstrap-theme.min.css\" >\r\n");
		sb.append("<link rel=\"stylesheet\" href=\"" + jsPath + "js/bootstraptable/css/bootstrap-table.min.css\">\r\n");
		sb.append("<link rel=\"stylesheet\" href=\"" + jsPath + "js/bootstrapselect/css/bootstrap-select.min.css\">\r\n");
		sb.append("<script src=\"" + jsPath + "js/jquery/js/jquery-3.2.1.min.js\"></script>\r\n");
		sb.append("<script src=\"" + jsPath + "js/jquery/js/jquery-rest.js\"></script>\r\n");
		sb.append("<script src=\"" + jsPath + "js/bootstrap/js/bootstrap.min.js\"></script>\r\n");
		sb.append("<script src=\"" + jsPath + "js/bootstraptable/js/tableExport.js\"></script>\r\n");
		sb.append("<script src=\"" + jsPath + "js/bootstraptable/js/bootstrap-table.js\"></script>\r\n");
		sb.append("<script src=\"" + jsPath + "js/bootstraptable/js/bootstrap-table-export.js\"></script>\r\n");
		sb.append("<script src=\"" + jsPath + "js/knockout/js/knockout-3.4.2.js\"></script>\r\n");
		sb.append("<script src=\"" + jsPath + "js/knockout/js/knockout.mapping-latest.js\"></script>\r\n");
		sb.append("<script src=\"" + jsPath + "js/bootstraptable/js/knockout.bootstraptable.js\"></script>\r\n");
		sb.append("<script src=\"" + jsPath + "js/bootstrapselect/js/bootstrap-select.min.js\"></script>\r\n");
		sb.append("<script src=\"" + jsPath + "js/bootstrapselect/js/i18n/defaults-zh_CN.min.js\"></script>\r\n");
		sb.append("<script src=\"" + jsPath + "js/bootstrapcheckbox/js/bootstrap-checkbox.min.js\"></script>\r\n");
		sb.append("<script src=\"" + jsPath + "js/xjl/js/xjl.js\"></script>\r\n");
		sb.append("<script src=\"" + jsPath + "js/xjl/js/xjl-table.js\"></script>\r\n");
		sb.append("<script>\r\n");
		sb.append("XJL.bindCloseWindowEvent();\r\n");
		sb.append("//初始化\r\n");
		sb.append("$(function () {\r\n");
		sb.append("   //1、初始化表格\r\n");
		sb.append("    XJL.initTable(\"tb_" + XJLCoderTools.getDomainFieldName(domain.getSimpleName()) + "\",\"../rest" + controllerUrl + "\",10);\r\n");
		sb.append("    //2、注册增删改事件\r\n");
		sb.append("    XJL.initOperate2(\"../rest" + controllerUrl + "\",\"");
		for (int i = 0; i < fieldList.size(); i++){
			TableField field = fieldList.get(i);
			if (i>0){
				sb.append(",");
			}
			sb.append(XJLCoderTools.getDomainFieldName(field.getFieldName()));
			//如果是外键，增加外键的名称
			if (field.getFieldType().equals(TableField.FIELD_TYPE_FK)){
				sb.append("," + XJLCoderTools.getDomainFieldName(field.getFieldName()) + "$name");
			}
		}
		sb.append("\");\r\n");
		sb.append("});\r\n");
		sb.append("\r\n");
		for (TableField tableField : fieldList) {
			if (tableField.getFieldType().equals(TableField.FIELD_TYPE_FK)){
				Table fkTable = this.tableService.queryById(tableField.getForeignTableId());
				String fkDomainName = XJLCoderTools.getDomainFieldName(fkTable.getTableName());
				TableField fkTablePKField = this.getPKField(fkTable.getTableId());
				
				sb.append("function set" + XJLCoderTools.getDomainName(tableField.getFieldName()) + "("+fkDomainName+"){\r\n");
				sb.append("	if ("+fkDomainName+"){\r\n");
				sb.append("		$('#txt_" + XJLCoderTools.getDomainFieldName(tableField.getFieldName()) + "').val("+fkDomainName+"."+XJLCoderTools.getDomainFieldName(fkTablePKField.getFieldName())+").change();\r\n");
				sb.append("		$('[id=\"txt_" + XJLCoderTools.getDomainFieldName(tableField.getFieldName()) + "$name\"]').val("+fkDomainName+"."+XJLCoderTools.getDomainFieldName(fkTablePKField.getFieldName())+").change();\r\n");
				sb.append("	}\r\n");
				sb.append("}\r\n");
			}
		}
		
		sb.append("</script>\r\n");
		sb.append("</head>\r\n");
		sb.append("<body>\r\n");
		return sb.toString();
	}
	private TableField getPKField(String tableId){
		List<TableField> fieldList = this.tableFieldService.queryByTableId(tableId, 1, 100);
		for (TableField tableField : fieldList) {
			if (tableField.getFieldType().equals(TableField.FIELD_TYPE_PK)){
				return tableField;
			}
		}
		return null;
	}
}
