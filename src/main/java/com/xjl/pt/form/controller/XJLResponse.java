package com.xjl.pt.form.controller;
/**
 * ajax返回数据，一般用于提交/更新/删除等方法的返回值
 * @author lilisheng
 *
 */
public class XJLResponse {
	private boolean success;
	private String errorMsg;
	private static XJLResponse successInstance;
	//得到一个成功的返回对象
	public static XJLResponse successInstance(){
		if (successInstance==null){
			successInstance = new XJLResponse();
			successInstance.setSuccess(true);
		}
		return successInstance;
	}
	//得到一个错误的返回对象
	public static XJLResponse errorInstance(String msg){
		XJLResponse errorInstance = new XJLResponse();
		errorInstance.setErrorMsg(msg);
		errorInstance.setSuccess(false);
		return errorInstance;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
}
