package com.cneop.util;

import java.lang.reflect.Field;
import java.util.List;

/**
 * json的操作类
 * @author NanGuoCan
 *
 */
public class JsonUtil {
	  
	    /**
	      * @param object
	      *             任意对象
	      * @return java.lang.String
	      */  
	    public static String objectToJson(Object object) {   
	         StringBuilder json = new StringBuilder();   
	        if (object == null) {   
	             json.append("\"\"");   
	         } else if (object instanceof String || object instanceof Integer) { 
	             json.append("\"").append(object.toString().trim()).append("\"");  
	         } else {   
	             json.append(beanToJson(object));   
	         }   
	        return json.toString().trim();   
	     }   
	  
	    /**
	      * 功能描述:传入任意一个 javabean 对象生成一个指定规格的字符串
	      *
	      * @param bean
	      *             bean对象
	      * @return String
	      */  
	    public static String beanToJson(Object object) {   
	         StringBuilder json = new StringBuilder();   
	         json.append("{");   
	         Field[] f;  
	         f = object.getClass().getDeclaredFields(); 
	        if (f != null) {   
	            for (int i = 0; i < f.length; i++) {   
	                try {  
	                     String name = objectToJson(f[i].getName());   
	                     String value = objectToJson(ReflectorUtil.invokeGet(object, f[i].getName()));  
	                     json.append(name);   
	                     json.append(":");   
	                     json.append(value);   
	                     json.append(",");  
	                 } catch (Exception e) {   
	                 }   
	             }   
	             json.setCharAt(json.length() - 1, '}');   
	         } else {   
	             json.append("}");   
	         }   
	        return json.toString().trim();   
	     }   
	  
	    /**
	      * 功能描述:通过传入一个列表对象,调用指定方法将列表中的数据生成一个JSON规格指定字符串
	      *
	      * @param list
	      *             列表对象
	      * @return java.lang.String
	      */  
	    public static String listToJson(List<?> list) {   
	         StringBuilder json = new StringBuilder();   
	         json.append("[");   
	        if (list != null && list.size() > 0) {   
	            for (Object obj : list) {   
	                 json.append(objectToJson(obj));   
	                 json.append(",");   
	             }   
	             json.setCharAt(json.length() - 1, ']');   
	         } else {   
	             json.append("]");   
	         }   
	        return json.toString().trim();   
	     }
}