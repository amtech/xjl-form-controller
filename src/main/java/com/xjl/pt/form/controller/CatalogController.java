package com.xjl.pt.form.controller;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.xjl.pt.core.domain.Licence;
import com.xjl.pt.core.domain.ZzCatalog;
import com.xjl.pt.core.domain.ZzCatalogLicence;
import com.xjl.pt.core.service.LicenceService;
import com.xjl.pt.core.service.ZzCatalogLicenceService;
import com.xjl.pt.core.service.ZzCatalogService;


@Controller
@RequestMapping("/catalog")
public class CatalogController {
	
	@Autowired
	private LicenceService licenceService;
	@Autowired
	private ZzCatalogService zzCatalogService;
	@Autowired
	private ZzCatalogLicenceService zzCatalogLicenceService;
	
	
	@ResponseBody
	@RequestMapping(value = "/showZZcatalog")
	public List<Map<String,Object>>  showzzCatalog(){
		//获取全部有效证照
		List<Licence> list=licenceService.queryUrlByOwnid("320423199102108613");
		//通过用户编号过滤目录文件信息
		List<ZzCatalog> zzCatalogList=this.zzCatalogService.queryByUserId("73f94e44-bb52-4041-8a18-f0b193a970ea");
		//声明存储目录文件下条目集合
		List<ZzCatalogLicence> zzCatalogLicenceList = null;
		for (ZzCatalog zzCatalog : zzCatalogList) {
			 zzCatalogLicenceList = this.zzCatalogLicenceService.queryByCatalogId(zzCatalog.getCatalogId());
		}
		list = this.removeRep(list, zzCatalogLicenceList);
		List<Map<String,Object>> allList = new ArrayList<>();
		Map<String,Object> _tempMap = new HashMap<>();
		_tempMap.put("licence", list);
		_tempMap.put("catalog", zzCatalogList);
		allList.add(_tempMap);
		return allList;
	}
	
	public List<Licence>  removeRep(List<Licence> list1,List<ZzCatalogLicence> list2){
		if(null != list1 && list1.size() > 0 && (null != list2 && list2.size() > 0)){
			for(int i= 0;i<list1.size();i++){
				for(int j = 0;j<list2.size();j++){
					if(list2.get(j).getLicenceId().equals(list1.get(i).getLicenceId())){
						list1.remove(i);
					}
				}
			} 	
		}
		return list1;
	}
	
}
