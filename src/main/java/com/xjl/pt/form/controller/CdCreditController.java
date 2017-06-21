package com.xjl.pt.form.controller;

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
import com.xjl.pt.core.domain.CdCredit;
import com.xjl.pt.core.domain.DictItem;
import com.xjl.pt.core.domain.User;
import com.xjl.pt.core.service.DictItemService;
import com.xjl.pt.core.tools.DictItemTools;
import com.xjl.pt.core.service.CdCreditItemService;
import com.xjl.pt.core.service.CdCreditService;
/**
 * 征信库控制器类
 * @author ControllerCoderTools Arthur
 *
 */
@Controller
@RequestMapping("/cdCredit")
public class CdCreditController {
	@Autowired
	private SessionTools sessionTools;
	@Autowired
	private DictItemService dictItemService;
	@Autowired
	private CdCreditService cdCreditService;
	@Autowired
	private CdCreditItemService cdCreditItemService;
	@ResponseBody
	@RequestMapping(value="/query/{page}/{rows}",method=RequestMethod.GET,consumes = "application/json")
	public BootstrapGridTable query(HttpServletRequest request, @PathVariable Integer page,@PathVariable Integer rows){
		String search = StringUtils.trimToNull(request.getParameter("search"));
		List<CdCredit> list = this.cdCreditService.query(search, page, rows);
		//处理字典
		List<DictItem> creditEntityTypeDictItems = this.dictItemService.queryByDictId("bd3952a7-3a45-4587-8421-76018fa58866", 1, 1000);
		for (CdCredit cdCredit : list) {
			//得到征信条目统计
			cdCredit.setCreditItemCount(this.cdCreditItemService.queryByCreditId(cdCredit.getCreditId()));
			cdCredit.setCreditEntityType$name(DictItemTools.getDictItemNames(cdCredit.getCreditEntityType(), creditEntityTypeDictItems));
		}
		//处理外键
		return BootstrapGridTable.getInstance(list);
	}

	@ResponseBody
	@RequestMapping(value="/query/{id}",method=RequestMethod.GET,consumes = "application/json")
	public CdCredit queryById(HttpServletRequest request, @PathVariable String id){
		CdCredit cdCredit = this.cdCreditService.queryById(id);
		//处理字典
		List<DictItem> creditEntityTypeDictItems = this.dictItemService.queryByDictId("bd3952a7-3a45-4587-8421-76018fa58866", 1, 1000);
		cdCredit.setCreditEntityType$name(DictItemTools.getDictItemNames(cdCredit.getCreditEntityType(), creditEntityTypeDictItems));
		//处理外键
		return cdCredit;
	}

	@ResponseBody
	@RequestMapping(value="/add",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse add(HttpServletRequest request, @RequestBody CdCredit cdCredit){
		User user = this.sessionTools.getUser(request);
		this.cdCreditService.add(cdCredit, user);
		return XJLResponse.successInstance();
	}

	@ResponseBody
	@RequestMapping(value="/modify",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse modify(HttpServletRequest request, @RequestBody CdCredit cdCredit){
		User user = this.sessionTools.getUser(request);
		this.cdCreditService.modify(cdCredit, user);
		return XJLResponse.successInstance();
	}

	@ResponseBody
	@RequestMapping(value="/delete",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse delete(HttpServletRequest request, @RequestBody List<CdCredit> list){
		User user = this.sessionTools.getUser(request);
		for (CdCredit cdCredit : list) {
			this.cdCreditService.delete(cdCredit, user);
		}
		return XJLResponse.successInstance();
	}

}

