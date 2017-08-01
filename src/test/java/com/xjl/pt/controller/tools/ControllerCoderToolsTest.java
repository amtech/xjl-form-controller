package com.xjl.pt.controller.tools;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.xjl.cdc.cloud.domain.CdcTerminal;
import com.xjl.pt.core.domain.Dept;
import com.xjl.pt.form.domain.Form;
import com.xjl.pt.news.domain.NewsGovernmentAffairsOpenness;
import com.xjl.pt.news.domain.NewsGovernmentAffairsOpennessCategory;
import com.xjl.pt.sx.domain.SxDirItem;
import com.xjl.pt.sx.domain.SxImplItem;
import com.xjl.pt.web.controller.BaseControllerTest;
import com.xjl.rcu.monitor.domain.RcuErrorLog;
@ContextConfiguration(locations = { "classpath*:/ApplicationContext-*.xml"})  
@RunWith(SpringJUnit4ClassRunner.class)  
public class ControllerCoderToolsTest {
	@Autowired
	private ControllerCoderTools tools;
	@Test
	public void generate(){
		this.tools.generateController(Dept.class, "com.xjl.pt.controller.core");
	}
	@Test
	public void generateDirItem(){
		this.tools.generateController(SxDirItem.class, "com.xjl.pt.sx");
	}
	@Test
	public void generateImplItem(){
		this.tools.generateController(SxImplItem.class, "com.xjl.pt.sx");
	}
	@Test
	public void generateNewsOpenness(){
		this.tools.generateController(NewsGovernmentAffairsOpennessCategory.class, "com.xjl.news");
		this.tools.generateController(NewsGovernmentAffairsOpenness.class, "com.xjl.news");
	}
	@Test
	public void generateForm(){
		this.tools.generateController(Form.class, "com.xjl.pt.form");
	}
	@Test
	public void generateCDCTerminal(){
		this.tools.generateController(CdcTerminal.class, "com.xjl.cdc.cloud");
	}
	@Test
	public void generateRCUErrorLog(){
		this.tools.generateController(RcuErrorLog.class, "com.xjl.rcu.monitor");
	}
}
