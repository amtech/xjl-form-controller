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
import com.xjl.pt.core.domain.CdCreditItem;
import com.xjl.pt.core.domain.DictItem;
import com.xjl.pt.core.domain.User;
import com.xjl.pt.core.service.DictItemService;
import com.xjl.pt.core.tools.DictItemTools;
import com.xjl.pt.core.service.CdCreditItemService;
import com.xjl.pt.core.service.CdCreditService;
/**
 * 征信条目控制器类
 * @author ControllerCoderTools Arthur
 *
 */
@Controller
@RequestMapping("/cdCreditItem")
public class CdCreditItemController {
	@Autowired
	private SessionTools sessionTools;
	@Autowired
	private DictItemService dictItemService;
	@Autowired
	private CdCreditItemService cdCreditItemService;
	@Autowired
	private CdCreditService cdCreditService;
	@ResponseBody
	@RequestMapping(value="/query/{page}/{rows}",method=RequestMethod.GET,consumes = "application/json")
	public BootstrapGridTable query(HttpServletRequest request, @PathVariable Integer page,@PathVariable Integer rows){
		String search = StringUtils.trimToNull(request.getParameter("search"));
		List<CdCreditItem> list = this.cdCreditItemService.query(search, page, rows);
		//处理字典
		//处理外键
		return BootstrapGridTable.getInstance(list);
	}

	@ResponseBody
	@RequestMapping(value="/query/{id}",method=RequestMethod.GET,consumes = "application/json")
	public CdCreditItem queryById(HttpServletRequest request, @PathVariable String id){
		CdCreditItem cdCreditItem = this.cdCreditItemService.queryById(id);
		//处理字典
		//处理外键
		return cdCreditItem;
	}

	@ResponseBody
	@RequestMapping(value="/add",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse add(HttpServletRequest request, @RequestBody CdCreditItem cdCreditItem){
		User user = this.sessionTools.getUser(request);
		//得到信用总分计算目前得分
		CdCredit cdit = this.cdCreditService.queryById(cdCreditItem.getCreditId());
		if (null != cdit) {
			int score = cdit.getCreditEntityScore() -cdCreditItem.getCreditItemScore();
			cdit.setCreditEntityScore(score);
			this.cdCreditService._modify(cdit);
		}
		this.cdCreditItemService.add(cdCreditItem, user);
		return XJLResponse.successInstance();
	}

	@ResponseBody
	@RequestMapping(value="/modify",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse modify(HttpServletRequest request, @RequestBody CdCreditItem cdCreditItem){
		User user = this.sessionTools.getUser(request);
		//得到信用目录信息
		CdCreditItem cdcreditItem = this.cdCreditItemService.queryById(cdCreditItem.getCreditItemId());
		if (null != cdcreditItem) {
			int scort = cdcreditItem.getCreditItemScore() - cdCreditItem.getCreditItemScore();
			//得到信用总分计算目前得分
			CdCredit cdit = this.cdCreditService.queryById(cdCreditItem.getCreditId());
			if (null != cdit) {
				cdit.setCreditEntityScore(cdit.getCreditEntityScore() + scort);
				this.cdCreditService._modify(cdit);
			}
		}
		this.cdCreditItemService.modify(cdCreditItem, user);
		return XJLResponse.successInstance();
	}

	@ResponseBody
	@RequestMapping(value="/delete",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse delete(HttpServletRequest request, @RequestBody List<CdCreditItem> list){
		User user = this.sessionTools.getUser(request);
		//得到信用目录信息
		CdCreditItem cdcreditItem = null;
		CdCredit cdit = null;
		for (CdCreditItem cdCreditItem : list) {
			//得到条目信息
			cdcreditItem = this.cdCreditItemService.queryById(cdCreditItem.getCreditItemId());
			if(null != cdcreditItem){
				//得到信用总分计算目前得分
				cdit = this.cdCreditService.queryById(cdCreditItem.getCreditId());
				if(null != cdit){ 
					cdit.setCreditEntityScore(cdit.getCreditEntityScore() + cdcreditItem.getCreditItemScore());
					this.cdCreditService._modify(cdit);
				}
			}
			this.cdCreditItemService.delete(cdCreditItem, user);
		}
		return XJLResponse.successInstance();
	}

}

