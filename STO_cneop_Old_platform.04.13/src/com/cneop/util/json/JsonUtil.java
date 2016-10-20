package com.cneop.util.json;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.cneop.util.ReflectorUtil;
import com.cneop.util.StrUtil;

/**
 * json的操作类
 * 
 * @author NanGuoCan
 * 
 */
public class JsonUtil {

	/**
	 * @param object
	 *            任意对象
	 * @return java.lang.String
	 */
	private static String objectToJson(Object object) {
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
	 *            bean对象
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
					if ("java.util.List".equals(f[i].getType().getName())) {
						String name = objectToJson(f[i].getName());
						json.append(name);
						json.append(":");
						json.append(listToJson((List<?>) ReflectorUtil.invokeGet(object, f[i].getName())));
						json.append(",");
					} else {
						String name = objectToJson(f[i].getName());
						String value = objectToJson(ReflectorUtil.invokeGet(object, f[i].getName()));
						json.append(name);
						json.append(":");
						json.append(value);
						json.append(",");
					}
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
	 *            列表对象
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

	/**
	 * JSON字符串转对象
	 * 
	 * @param jsonData
	 * @param obj
	 * @return
	 */
	public static List<Object> jsonToList(String jsonData, Object obj) {
		List<Object> list = null;
		StrUtil strUtil = new StrUtil();
		try {
			JSONArray jsonArray = new JSONArray(jsonData);
			list = new ArrayList<Object>();
			Field[] f = obj.getClass().getDeclaredFields();
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				obj = obj.getClass().newInstance();
				for (int j = 0; j < f.length; j++) {
					Field ff = f[j];
					String fieldName = ff.getName();
					if (jsonObject.has(fieldName)) {
						String typeName = ff.getType().getName();
						if ("java.lang.String".equalsIgnoreCase(typeName)) {
							ReflectorUtil.invokeSet(obj, fieldName, jsonObject.getString(fieldName));
						} else if ("java.lang.Integer".equalsIgnoreCase(typeName) || typeName.equals("int")) {
							ReflectorUtil.invokeSet(obj, fieldName, jsonObject.getInt(fieldName));
						} else if ("java.lang.Boolean".equalsIgnoreCase(typeName)) {
							ReflectorUtil.invokeSet(obj, fieldName, jsonObject.getBoolean(fieldName));
						} else {
							Log.i("JSON", "未被解析到的数据:" + typeName);
						}
					}
				}
				list.add(obj);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
	}
}