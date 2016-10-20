package com.cneop.util;

import java.util.regex.Pattern;

public class CheckUtils {

	/**
	 * 判断是否是数字
	 * @param str
	 * @return 
	 */
	public static Boolean isNumeric(String str){
		if(str==null || str.trim().length()==0){
			return false;
		}
		Pattern pattern = Pattern.compile("[0-9]*"); 
		return pattern.matcher(str).matches(); 
	}
	/**
	 * 判断是否是字符
	 * @param str
	 * @return 
	 */
	public static Boolean isCharacter(String str){
		Pattern pattern =Pattern.compile("[a-zA-Z]");
		return pattern.matcher(str).matches(); 
	}
	/**
	 * 判断是否是汉字
	 * @param str
	 * @return 
	 */
	public static Boolean IsChinsese(String str){
		Pattern pattern =Pattern.compile("[\u4e00-\u9fa5]");
		return pattern.matcher(str).matches(); 
	}
	/**
	 * 判断是否由数字和字母组成
	 * @param password
	 * @return
	 */
	 public static boolean isBarcode(String password){  
	        String string = "^[A-Za-z0-9@#$%&*]+$";  
	        Pattern mPattern = Pattern.compile(string);  
	        return  mPattern.matcher(password).matches();  
	    } 
	 
		/**
		 * 是否为有效的手机号
		 * 
		 * @param phone
		 * @return
		 */
		public static boolean isMobilePhone(String phone) {
			String string = "^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";
			Pattern mPattern = Pattern.compile(string);
			return mPattern.matcher(phone).matches();
		}
}
