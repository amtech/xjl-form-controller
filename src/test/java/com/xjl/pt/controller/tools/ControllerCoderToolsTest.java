package com.xjl.pt.controller.tools;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.xjl.pt.core.domain.Dept;
import com.xjl.pt.sx.domain.SxDirItem;
import com.xjl.pt.web.controller.BaseControllerTest;
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
}
