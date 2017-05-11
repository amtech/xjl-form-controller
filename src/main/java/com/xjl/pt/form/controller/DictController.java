package com.xjl.pt.form.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.Page;
import com.xjl.pt.core.domain.Dict;
import com.xjl.pt.core.service.DictService;
import com.xjl.pt.form.domain.Form;
/**
 * 字典控制器类
 * @author li.lisheng
 *
 */
@Controller
@RequestMapping("/dict")
public class DictController {
	@Autowired
	private DictService dictService;
	@ResponseBody
	@RequestMapping(value="/all",method=RequestMethod.GET,consumes = "application/json")
	public BootstrapGridTable all(HttpServletRequest request){
		int rows = BootstrapGridTable.getRows(request);
		int page = BootstrapGridTable.getPage(request, rows);
		String search = StringUtils.trimToNull(request.getParameter("search"));
		List<Dict> list = this.dictService.queryByName(search, page, rows);
		return BootstrapGridTable.getInstance(list);
	}
}
