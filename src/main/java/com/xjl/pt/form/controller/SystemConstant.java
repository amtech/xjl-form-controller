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
	 * ftp目录地址
	 */
	public static final String FTP_PATH="/home/ftpxjl";
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
	 * Session中的用户key（不可修改）
	 */
	public static final String SESSION_USER = "SESSION_USER";
	
	
}
