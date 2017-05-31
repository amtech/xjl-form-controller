package com.xjl.pt.controller.tools;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.xjl.pt.core.domain.Dept;
@ContextConfiguration(locations = { "classpath*:/ApplicationContext-*.xml"})  
@RunWith(SpringJUnit4ClassRunner.class)  
public class PageCoderToolsTest {
	@Autowired
	private PageCoderTools tools;
	@Test
	public void generate(){
		this.tools.generatePage(Dept.class, "/home/lilisheng/git/xjl-form-web/src/main/webapp/dept");
	}
	
}
