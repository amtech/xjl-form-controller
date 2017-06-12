package com.xjl.news.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.xjl.pt.form.controller.SessionTools;
import com.xjl.pt.form.controller.XJLResponse;
import com.xjl.pt.form.controller.BootstrapGridTable;
import com.xjl.pt.news.domain.NewsGovernmentAffairsOpennessCategory;
import com.xjl.pt.core.domain.DictItem;
import com.xjl.pt.core.domain.User;
import com.xjl.pt.core.service.DictItemService;
import com.xjl.pt.core.tools.DictItemTools;
import com.xjl.pt.news.service.NewsGovernmentAffairsOpennessCategoryService;
/**
 * 政务公开类别控制器类
 * @author ControllerCoderTools lilisheng
 *
 */
@Controller
@RequestMapping("/newsGovernmentAffairsOpennessCategory")
public class NewsGovernmentAffairsOpennessCategoryController {
	@Autowired
	private SessionTools sessionTools;
	@Autowired
	private DictItemService dictItemService;
	@Autowired
	private NewsGovernmentAffairsOpennessCategoryService newsGovernmentAffairsOpennessCategoryService;
	@ResponseBody
	@RequestMapping(value="/query/{page}/{rows}",method=RequestMethod.GET,consumes = "application/json")
	public BootstrapGridTable query(HttpServletRequest request, @PathVariable Integer page,@PathVariable Integer rows){
		String search = StringUtils.trimToNull(request.getParameter("search"));
		List<NewsGovernmentAffairsOpennessCategory> list = this.newsGovernmentAffairsOpennessCategoryService.query(search, page, rows);
		//处理字典
		//处理外键
		return BootstrapGridTable.getInstance(list);
	}

	@ResponseBody
	@RequestMapping(value="/query/{id}",method=RequestMethod.GET,consumes = "application/json")
	public NewsGovernmentAffairsOpennessCategory queryById(HttpServletRequest request, @PathVariable String id){
		NewsGovernmentAffairsOpennessCategory newsGovernmentAffairsOpennessCategory = this.newsGovernmentAffairsOpennessCategoryService.queryById(id);
		//处理字典
		//处理外键
		return newsGovernmentAffairsOpennessCategory;
	}

	@ResponseBody
	@RequestMapping(value="/add",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse add(HttpServletRequest request, @RequestBody NewsGovernmentAffairsOpennessCategory newsGovernmentAffairsOpennessCategory){
		User user = this.sessionTools.getUser(request);
		this.newsGovernmentAffairsOpennessCategoryService.add(newsGovernmentAffairsOpennessCategory, user);
		return XJLResponse.successInstance();
	}

	@ResponseBody
	@RequestMapping(value="/modify",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse modify(HttpServletRequest request, @RequestBody NewsGovernmentAffairsOpennessCategory newsGovernmentAffairsOpennessCategory){
		User user = this.sessionTools.getUser(request);
		this.newsGovernmentAffairsOpennessCategoryService.modify(newsGovernmentAffairsOpennessCategory, user);
		return XJLResponse.successInstance();
	}

	@ResponseBody
	@RequestMapping(value="/delete",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse delete(HttpServletRequest request, @RequestBody List<NewsGovernmentAffairsOpennessCategory> list){
		User user = this.sessionTools.getUser(request);
		for (NewsGovernmentAffairsOpennessCategory newsGovernmentAffairsOpennessCategory : list) {
			this.newsGovernmentAffairsOpennessCategoryService.delete(newsGovernmentAffairsOpennessCategory, user);
		}
		return XJLResponse.successInstance();
	}

}

