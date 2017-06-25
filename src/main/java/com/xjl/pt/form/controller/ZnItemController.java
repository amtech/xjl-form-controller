package com.xjl.pt.form.controller;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
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
import com.xjl.pt.core.domain.ZnItem;
import com.xjl.pt.core.domain.User;
import com.xjl.pt.core.service.DictItemService;
import com.xjl.pt.core.service.ZnItemService;
/**
 * 办事指南控制器类
 * @author ControllerCoderTools Arthur
 *
 */
@Controller
@RequestMapping("/znItem")
public class ZnItemController {
	@Autowired
	private SessionTools sessionTools;
	@Autowired
	private DictItemService dictItemService;
	@Autowired
	private ZnItemService znItemService;
	@ResponseBody
	@RequestMapping(value="/query/{page}/{rows}",method=RequestMethod.GET,consumes = "application/json")
	public BootstrapGridTable query(HttpServletRequest request, @PathVariable Integer page,@PathVariable Integer rows){
		String search = StringUtils.trimToNull(request.getParameter("search"));
		List<ZnItem> list = this.znItemService.query(search, page, rows);
		//处理字典
		//处理外键
		return BootstrapGridTable.getInstance(list);
	}

	@ResponseBody
	@RequestMapping(value="/query/{id}",method=RequestMethod.GET,consumes = "application/json")
	public ZnItem queryById(HttpServletRequest request, @PathVariable String id){
		ZnItem znItem = this.znItemService.queryById(id);
		if (znItem != null){
			//处理字典
			//处理外键
		}
		return znItem;
	}

	@ResponseBody
	@RequestMapping(value="/add",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse add(HttpServletRequest request, @RequestBody ZnItem znItem){
		User user = this.sessionTools.getUser(request);
		this.znItemService.add(znItem, user);
		return XJLResponse.successInstance();
	}

	@ResponseBody
	@RequestMapping(value="/modify",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse modify(HttpServletRequest request, @RequestBody ZnItem znItem){
		User user = this.sessionTools.getUser(request);
		this.znItemService.modify(znItem, user);
		return XJLResponse.successInstance();
	}

	@ResponseBody
	@RequestMapping(value="/delete",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse delete(HttpServletRequest request, @RequestBody List<ZnItem> list){
		User user = this.sessionTools.getUser(request);
		for (ZnItem znItem : list) {
			this.znItemService.delete(znItem, user);
		}
		return XJLResponse.successInstance();
	}

}

