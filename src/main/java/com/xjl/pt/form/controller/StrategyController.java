package com.xjl.pt.form.controller;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xjl.pt.core.domain.BsStrategy;
import com.xjl.pt.core.domain.DictItem;
import com.xjl.pt.core.domain.User;
import com.xjl.pt.core.service.BsStrategyService;
import com.xjl.pt.core.service.DictItemService;
import com.xjl.pt.core.service.UserService;
import com.xjl.pt.core.tools.DictItemTools;
/**
 * 办事攻略控制器
 * @author 陶杰
 *
 */
@Controller
@RequestMapping("/Strategy")
public class StrategyController {
	@Autowired
	private DictItemService dictItemService;
	@Autowired
	private UserService userService;
	@Autowired
	private BsStrategyService bsStrategyService;
	@Autowired
	private SessionTools sessionTools;
	private static SimpleDateFormat format = new SimpleDateFormat(SystemConstant.FOMATDATE_DAY);
	/**
	 * 分页
	 */
	@ResponseBody
	@RequestMapping(value="/query/{page}/{rows}",method=RequestMethod.GET,consumes = "application/json")
	public BootstrapGridTable query(HttpServletRequest request, @PathVariable Integer page,@PathVariable Integer rows){
		String search=StringUtils.trimToNull(request.getParameter("search"));
		List<BsStrategy> bsStrategylist=this.bsStrategyService.query(search, page, rows);
		//解析办事攻略状态
		for(BsStrategy bsStrategy : bsStrategylist){
			List<DictItem> strateStateItemlist=this.dictItemService.queryByDictId("39ef5499-2632-49bb-ae38-dd0d46a53985", 1, 1000);
			bsStrategy.setStrategyState$name(DictItemTools.getDictItemNames(bsStrategy.getStrategyState(),strateStateItemlist));
			bsStrategy.setStrategyCreateDateStr(format.format(bsStrategy.getCreateDate()));
		}
		return BootstrapGridTable.getInstance(bsStrategylist);
	}
	/**
	 * 给用户攻略使用的展示方法
	 */
	
	@ResponseBody
	@RequestMapping(value="/queryForUser/{page}/{rows}",method=RequestMethod.GET,consumes = "application/json")
	public BootstrapGridTable queryForUser(HttpServletRequest request, @PathVariable Integer page,@PathVariable Integer rows){
		User user = this.userService.queryById("73f94e44-bb52-4041-8a18-f0b193a970ea");
		String search = StringUtils.trim(request.getParameter("search"));
		List<BsStrategy> bsStrategyList = this.bsStrategyService.queryByUserId("73f94e44-bb52-4041-8a18-f0b193a970ea");
		for(BsStrategy bsStrategy : bsStrategyList){
			List<DictItem> strateStateItemlist=this.dictItemService.queryByDictId("39ef5499-2632-49bb-ae38-dd0d46a53985", 1, 1000);
			bsStrategy.setStrategyState$name(DictItemTools.getDictItemNames(bsStrategy.getStrategyState(),strateStateItemlist));
			bsStrategy.setStrategyCreateDateStr(format.format(bsStrategy.getCreateDate()));
		}
		return BootstrapGridTable.getInstance(bsStrategyList);
	}
	
	/**
	 * 通过传入id返回攻略对象到详情页面
	 */
	@ResponseBody
	@RequestMapping(value="/queryStrategy")
	public BsStrategy queryStrategy(HttpServletRequest request,HttpServletResponse response){
		String strategyId=StringUtils.trim(request.getParameter("strategyId"));
		BsStrategy bsStrategy=this.bsStrategyService.queryById(strategyId);
		bsStrategy.setStrategyCreateDateStr(format.format(bsStrategy.getCreateDate()));
		return bsStrategy;
	}
	
	
	/**
	 * 用户新增攻略 自定义方法  没法与前台列表直接配合使用
	 */
	@ResponseBody
	@RequestMapping(value="/addStrategy")
	public XJLResponse addStrategy(HttpServletRequest request,HttpServletResponse response){
		User userDefault = this.userService.queryById("73f94e44-bb52-4041-8a18-f0b193a970ea");
		String title=StringUtils.trim(request.getParameter("title"));
		String content=StringUtils.trim(request.getParameter("content"));
		BsStrategy strategy=new BsStrategy();
		strategy.setStrategyId(UUID.randomUUID().toString());
		strategy.setStrategyTitle(title);
		strategy.setStrategyContent(content);
		strategy.setStrategyState("01");
		this.bsStrategyService.add(strategy, userDefault);
		return XJLResponse.successInstance();
	}
	/**
	 * 用户新增攻略
	 */
	@ResponseBody
	@RequestMapping(value="/add",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse add(HttpServletRequest request, @RequestBody BsStrategy bsStrategy){
	//	User user = this.sessionTools.getUser(request);
		User user=this.userService.queryById("73f94e44-bb52-4041-8a18-f0b193a970ea");
		bsStrategy.setStrategyId(UUID.randomUUID().toString());
		bsStrategy.setStrategyState("01");
		this.bsStrategyService.add(bsStrategy, user);
		return XJLResponse.successInstance();
	}
	
	/**
	 * 删除用户攻略
	 * @param request
	 * @param bsStrategylist
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/delete",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse delete(@RequestBody Map<String,Object> models,HttpServletRequest request){
	//	User user = this.sessionTools.getUser(request);
		String strategyId=String.valueOf(models.get("strategyId"));
		User user=this.userService.queryById("73f94e44-bb52-4041-8a18-f0b193a970ea");
		BsStrategy bsStrategy=this.bsStrategyService.queryById(strategyId);
		this.bsStrategyService.delete(bsStrategy, user);
		return XJLResponse.successInstance();
	}
	
	/**
	 * 屏蔽操作
	 * @param request
	 * @param bsStrategylist
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/hide")
	public XJLResponse hide(HttpServletRequest request,HttpServletResponse response){
		String StrategyId=StringUtils.trim(request.getParameter("strategyId"));
		this.bsStrategyService.modifyStateById(StrategyId);
		return XJLResponse.successInstance();
	}
	/**
	 * 办事攻略用户修改方法
	 * @param model
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/modify",method=RequestMethod.POST,consumes = "application/json")
	public XJLResponse modify(@RequestBody Map<String,Object> model,HttpServletRequest request){
		User user=this.userService.queryById("73f94e44-bb52-4041-8a18-f0b193a970ea");
		String StrategyId=String.valueOf(model.get("strategyId"));
		String StrategyContent=String.valueOf(model.get("strategyContent"));
		String strategyTitle=String.valueOf(model.get("strategyTitle"));
		String strategyState=String.valueOf(model.get("strategyState"));
		//判断攻略状态如果是已屏蔽(00),则修改时相当于加了一条新的攻略;如果状态是已发布(01),则修改时修改该条攻略
		BsStrategy bsStrategy=this.bsStrategyService.queryById(StrategyId);
		if("01".equals(strategyState)){
			bsStrategy.setStrategyContent(StrategyContent);
			bsStrategy.setStrategyTitle(strategyTitle);
			this.bsStrategyService.modify(bsStrategy,user);
		}else if("00".equals(strategyState)){
			bsStrategy.setStrategyId(UUID.randomUUID().toString());
			this.bsStrategyService.add(bsStrategy, user);
			bsStrategy.setStrategyState("01");
			bsStrategy.setStrategyContent(StrategyContent);
			bsStrategy.setStrategyTitle(strategyTitle);
			this.bsStrategyService.modify(bsStrategy, user);
		}
		return XJLResponse.successInstance();
}
	
	
}
