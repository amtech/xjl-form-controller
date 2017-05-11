package com.xjl.pt.form.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;

import com.github.pagehelper.Page;
/**
 * bootstrap表格需要的数据格式
 * @author li.lisheng
 *
 */
public class BootstrapGridTable {
	/**
	 * 得到当前页请求的数据条数，bootstrap默认使用的是limit
	 * @param request
	 * @return
	 */
	public static final int getRows(HttpServletRequest request){
		return NumberUtils.toInt(request.getParameter("limit"));
	}
	/**
	 * 得到当前是请求的数据是第几页，bootstrap默认使用的偏移量offset
	 * @param request
	 * @param rows 当前页面请求的数据条数
	 * @return
	 */
	public static final int getPage(HttpServletRequest request, int rows){
		int offset = NumberUtils.toInt(request.getParameter("offset"));
		int page = offset/rows+1;
		return page;
	}
	/**
	 * 得到一个实例，如果list是经过pageHepler对象获取的分页数据，则获取里面的total，如果不是，则使用list大小作为total
	 * @param list
	 * @return
	 */
	public static final BootstrapGridTable getInstance(List list){
		BootstrapGridTable data = new BootstrapGridTable();
		data.setRows(list);
		if (list instanceof Page){
			data.setTotal(((Page)list).getTotal());
		} else {
			data.setTotal(list.size());
		}
		return data;
	}
	private long total;
	private List rows;
	private BootstrapGridTable() {
	}
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	public List getRows() {
		return rows;
	}
	public void setRows(List rows) {
		this.rows = rows;
	}
	
	
}
