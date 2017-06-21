package com.xjl.cdc.cloud.controller;

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
import com.xjl.cdc.cloud.domain.CdcTerminal;
import com.xjl.pt.core.domain.DictItem;
import com.xjl.pt.core.domain.User;
import com.xjl.pt.core.service.DictItemService;
import com.xjl.pt.core.tools.DictItemTools;
import com.xjl.cdc.cloud.service.CdcTerminalService;
/**
 * 云检测中心终端控制器类
 * @author ControllerCoderTools lilisheng
 *
 */
@Controller
@RequestMapping("/cdcTerminal")
public class CdcTerminalController {
	@Autowired
	private SessionTools sessionTools;
	@Autowired
	private DictItemService dictItemService;
	@Autowired
	private CdcTerminalService cdcTerminalService;
	@ResponseBody
	@RequestMapping(value="/query/{page}/{rows}",method=RequestMethod.GET,consumes = "application/json")
	public BootstrapGridTable query(HttpServletRequest request, @PathVariable Integer page,@PathVariable Integer rows){
		String search = StringUtils.trimToNull(request.getParameter("search"));
		List<CdcTerminal> list = this.cdcTerminalService.query(search, page, rows);
		//处理字典
		List<DictItem> terminalTypeDictItems = this.dictItemService.queryByDictId("5997cff9-5353-4974-8992-27c5b40f8ea1", 1, 1000);
		for (CdcTerminal cdcTerminal : list) {
			cdcTerminal.setTerminalType$name(DictItemTools.getDictItemNames(cdcTerminal.getTerminalType(), terminalTypeDictItems));
		}
		//处理外键
		return BootstrapGridTable.getInstance(list);
	}

	@ResponseBody
	@RequestMapping(value="/query/{id}",method=RequestMethod.GET,consumes = "application/json")
	public CdcTerminal queryById(HttpServletRequest request, @PathVariable String id){
		CdcTerminal cdcTerminal = this.cdcTerminalService.queryById(id);
		//处理字典
		List<DictItem> terminalTypeDictItems = this.dictItemService.queryByDictId("5997cff9-5353-4974-8992-27c5b40f8ea1", 1, 1000);
		cdcTerminal.setTerminalType$name(DictItemTools.getDictItemNames(cdcTerminal.getTerminalType(), terminalTypeDictItems));
		//处理外键
		return cdcTerminal;
	}
	@ResponseBody
	@RequestMapping(value="/query/guid/{guid}",method=RequestMethod.GET,consumes = "application/json")
	public CdcTerminal queryByGUID(HttpServletRequest request, @PathVariable String guid){
		CdcTerminal cdcTerminal = this.cdcTerminalService.queryByGUID(guid);
		if (cdcTerminal != null){
			//处理字典
			List<DictItem> terminalTypeDictItems = this.dictItemService.queryByDictId("5997cff9-5353-4974-8992-27c5b40f8ea1", 1, 1000);
			cdcTerminal.setTerminalType$name(DictItemTools.getDictItemNames(cdcTerminal.getTerminalType(), terminalTypeDictItems));
			//处理外键
		}
		return cdcTerminal;
	}
	@ResponseBody
	@RequestMapping(value="/add",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse add(HttpServletRequest request, @RequestBody CdcTerminal cdcTerminal){
		User user = this.sessionTools.getUser(request);
		this.cdcTerminalService.add(cdcTerminal, user);
		return XJLResponse.successInstance();
	}

	@ResponseBody
	@RequestMapping(value="/modify",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse modify(HttpServletRequest request, @RequestBody CdcTerminal cdcTerminal){
		User user = this.sessionTools.getUser(request);
		this.cdcTerminalService.modify(cdcTerminal, user);
		return XJLResponse.successInstance();
	}

	@ResponseBody
	@RequestMapping(value="/delete",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse delete(HttpServletRequest request, @RequestBody List<CdcTerminal> list){
		User user = this.sessionTools.getUser(request);
		for (CdcTerminal cdcTerminal : list) {
			this.cdcTerminalService.delete(cdcTerminal, user);
		}
		return XJLResponse.successInstance();
	}
	@ResponseBody
	@RequestMapping(value="/deploy/{code}/{guid}",method=RequestMethod.PUT,consumes = "application/json")
	public CdcTerminal deploy(HttpServletRequest request, @PathVariable String code,@PathVariable String guid){
		User user = this.sessionTools.getUser(request);
		CdcTerminal cdcTerminal = this.cdcTerminalService.queryByGUID(guid);
		if (cdcTerminal == null){
			cdcTerminal = new CdcTerminal();
			cdcTerminal.setTerminalGuid(guid);
			cdcTerminal.setTerminalType(code);
			this.cdcTerminalService.add(cdcTerminal, user);
		} else {
			cdcTerminal.setTerminalType(code);
			this.cdcTerminalService.modify(cdcTerminal, user);
		}
		return cdcTerminal;
	}
}

