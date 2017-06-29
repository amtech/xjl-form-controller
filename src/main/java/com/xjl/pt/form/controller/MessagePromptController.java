package com.xjl.pt.form.controller;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xjl.pt.core.domain.MessagePrompt;
import com.xjl.pt.core.domain.User;
import com.xjl.pt.core.service.MessagePromptService;
import com.xjl.pt.core.service.UserService;

/**
 * 站内信控制类
 * @author 陶杰
 *
 */
@Controller
@RequestMapping(value="/messageprompt")
public class MessagePromptController {
	
	@Autowired
	private MessagePromptService messagePromptService;
	@Autowired
	private UserService userService;
	SimpleDateFormat format =new SimpleDateFormat(SystemConstant.FOMATEDATE_SECOND);
	
	/**
	 * 分页
	 * @param request
	 * @param page
	 * @param rows
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/query/{page}/{rows}",method=RequestMethod.GET,consumes="application/json")
	public BootstrapGridTable query(HttpServletRequest request,@PathVariable Integer page,@PathVariable Integer rows){
		String search = StringUtils.trimToNull(request.getParameter("search"));
		List<MessagePrompt> list= this.messagePromptService.query(search, 1, 1000);
		for(MessagePrompt messagePrompt : list){
			messagePrompt.setCreateDateStr(format.format(messagePrompt.getCreateDate()));
		}
		return BootstrapGridTable.getInstance(list);
	}
	/**
	 * 删除站内信
	 * @param model
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/delete",method=RequestMethod.POST,consumes="application/json")
	public XJLResponse delete(@RequestBody Map<String,Object> model,HttpServletRequest request){
		String promptId = String.valueOf(model.get("promptId"));
		MessagePrompt messagePrompt = this.messagePromptService.queryById(promptId);
		User userDefault = this.userService.queryById("73f94e44-bb52-4041-8a18-f0b193a970ea");
		this.messagePromptService.delete(messagePrompt, userDefault);
		return XJLResponse.successInstance();
	}
	/**
	 * 增加站内信
	 * @param model
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/add",method=RequestMethod.POST,consumes="application/json")
	public XJLResponse add(@RequestBody Map model,HttpServletRequest request){
		User userDefault = this.userService.queryById("73f94e44-bb52-4041-8a18-f0b193a970ea");
		String senderUsername=String.valueOf(model.get("senderUsername"));
		String promptTitle=String.valueOf(model.get("promptTitle"));
		String promptContent=String.valueOf(model.get("promptContent"));
		MessagePrompt messagePrompt = new MessagePrompt();
		messagePrompt.setSenderUsername(senderUsername);
		messagePrompt.setPromptTitle(promptTitle);
		messagePrompt.setPromptContent(promptContent);
		messagePrompt.setPromptId(UUID.randomUUID().toString());
		this.messagePromptService.add(messagePrompt, userDefault);
		return XJLResponse.successInstance();
	}
	
	
	
}
