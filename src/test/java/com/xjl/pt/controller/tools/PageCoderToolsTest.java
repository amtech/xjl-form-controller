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
import com.xjl.rcu.monitor.domain.RcuErrorLog;
@ContextConfiguration(locations = { "classpath*:/ApplicationContext-*.xml"})  
@RunWith(SpringJUnit4ClassRunner.class)  
public class PageCoderToolsTest {
	@Autowired
	private PageCoderTools tools;
	@Test
	public void generate(){
		this.tools.generatePage(Dept.class, "/home/lilisheng/git/xjl-form-web/src/main/webapp/dept");
	}
	@Test
	public void generateSxDirItem(){
		this.tools.generatePage(SxDirItem.class, "/home/lilisheng/git/xjl-form-web/src/main/webapp/sx");
	}
	@Test
	public void generateSxImplItem(){
		this.tools.generatePage(SxImplItem.class, "/home/lilisheng/git/xjl-form-web/src/main/webapp/sx");
	}
	@Test
	public void generateNews(){
		this.tools.generatePage(NewsGovernmentAffairsOpennessCategory.class, "/home/lilisheng/git/xjl-form-web/src/main/webapp/news");
		this.tools.generatePage(NewsGovernmentAffairsOpenness.class, "/home/lilisheng/git/xjl-form-web/src/main/webapp/news");
	}
	@Test
	public void generateForm(){
		this.tools.generatePage(Form.class, "/home/lilisheng/git/xjl-form-web/src/main/webapp/form");
	}
	@Test
	public void generateCDCTerminal(){
		this.tools.generatePage(CdcTerminal.class, "/home/lilisheng/workspace/detection-center-web/src/main/webapp/cloud");
	}
	@Test
	public void generateRCUErrorLog(){
		this.tools.generatePage(RcuErrorLog.class, "/home/lilisheng/git/xjl-form-web/src/main/webapp/rcu/error");
	}
}
