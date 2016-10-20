package com.cneop.util;

import java.lang.reflect.Field;
import java.util.List;

/**
 * json�Ĳ�����
 * @author NanGuoCan
 *
 */
public class JsonUtil {
	  
	    /**
	      * @param object
	      *             �������
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
	      * ��������:��������һ�� javabean ��������һ��ָ�������ַ���
	      *
	      * @param bean
	      *             bean����
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
	      * ��������:ͨ������һ���б����,����ָ���������б��е���������һ��JSON���ָ���ַ���
	      *
	      * @param list
	      *             �б����
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