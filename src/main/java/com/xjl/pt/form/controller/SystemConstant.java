package com.xjl.pt.form.controller;
/**
 * 定义系统常量
 * @author guan.zheyuan
 */
public class SystemConstant {

	/**
	 * ftp账号
	 */
	public  static final String FTP_NAME="ftpxjl";
	/**
	 * ftp密码 
	 */
	public static final String FTP_PASSWORD="A6CCxjl";
	/**
	 * ftp服务器地址
	 */
	public static final String FTP_IP="123.57.4.104";
	/**
	 * ftp目录:认证
	 */
	public static final String FTP_PATH_REALNAME ="/home/ftpxjl/approve";
	/**
	 * ftp:读取目录
	 */
	public static final String FTP_READPATH_REALNAME="/approve";
	/**
	 * ftp目录：证照
	 */
	public static final String FTP_PATH_LICENCE="/home/ftpxjl/licence";
	/**
	 * ftp:读取目录
	 */
	public static final String FTP_READPATH_LICENCE="/licence";
	
	 
	/**
	 * ftp富文本框目录
	 */
	public static final String FTP_PATH_EDITBOX="/home/ftpxjl/editbox";
	/**
	 * ftp:读取目录
	 */
	public static final String FTP_READPATH_EDITBOX="/editbox";
	/**
	 * 备份目录
	 */
	public static final String BACKUP_FOLDER="/upload/";
	/**
	 * 证照：正面
	 */
	public static final String SIGN_FRONT="front";
	public static final String SIGN_FRONT_VALUE="_FRONT";
	/**
	 * 证照：反面
	 */
	public static final String SIGN_BACK="back";
	public static final String SIGN_BACKA_VALUE="_BACK";
	/**
	 * 证照：手持
	 */
	public static final String SIGN_HAND="hand";
	public static final String SIGN_HAND_VALUE="_HAND";
	
	/**
	 * 标记：不同城市
	 */
	public static final String SIGN_CITY_VERIFY="diffcity";
	/**
	 * 标记：不同ip
	 */
	public static final String SIGN_IP_VERIFY="diffip";
	
	/**
	 * 获取ip
	 */
	public static final String LOG_GETIP="http://www.ip138.com/ip2city.asp";
	
	/**
	 * 图片后缀名
	 */
	public static final String IMAGE_SUFFIX_JPG=".jpg";
	public static final String IMAGE_SUFFIX_JPEG=".jpeg";
	public static final String IMAGE_SUFFIX_PNG=".png";
	/**
	 * 年月日时分
	 */
	public static final String FOMATDATE_MINUTE="yyyy-MM-dd-HH-mm";
	/**
	 * 年月日
	 */
	public static final String FOMATDATE_DAY="yyyy-mm-dd";
	/**
	 * Session中的用户key（不可修改）
	 */
	public static final String SESSION_USER = "SESSION_USER";
	/**
	 * 证照可信度A-D由高到低
	 */
	public static final String license_trust_A="A";
	public static final String license_trust_B="B";
	public static final String license_trust_C="C";
	public static final String license_trust_D="D";
	
}
