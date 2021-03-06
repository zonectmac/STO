package com.cneop.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.cneop.stoExpress.common.Enums.EDownType;
import com.cneop.stoExpress.dao.BaseDao;

import android.content.Context;
import android.util.Log;

/*
 * 反身帮助类
 */
public class ReflectorUtil {
	Context context;

	public ReflectorUtil(Context context) {
		this.context = context;
	}

	/**
	 * java反射bean的set方法
	 * 
	 * @param objectClass
	 * @param fieldName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static Method getSetMethod(Class objectClass, String fieldName) {
		try {
			Class[] parameterTypes = new Class[1];
			Field field = objectClass.getDeclaredField(fieldName);
			parameterTypes[0] = field.getType();
			StringBuffer sb = new StringBuffer();
			sb.append("set");
			sb.append(fieldName.substring(0, 1).toUpperCase());
			sb.append(fieldName.substring(1));
			Method method = objectClass.getMethod(sb.toString().trim(), parameterTypes);
			return method;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * 根据下载类型得到实例
	 */
	@SuppressWarnings("unchecked")
	public BaseDao getDao(EDownType downType) {
		BaseDao basDao = null;
		try {
			Class<BaseDao> basClass = (Class<BaseDao>) Class
					.forName("com.cneop.stoExpress.dao." + downType.toString().trim() + "Service");
			Constructor<BaseDao> construct = basClass.getConstructor(new Class[] { Context.class });
			basDao = construct.newInstance(new Object[] { context });
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return basDao;
	}

	/**
	 * 执行set方法
	 * 
	 * @param object
	 *            执行对象
	 * @param fieldName
	 *            属性
	 * @param value
	 *            值
	 */
	public static void invokeSet(Object object, String fieldName, Object value) {
		Method method = getSetMethod(object.getClass(), fieldName);

		try {
			method.invoke(object, new Object[] { value });
		} catch (Exception e) {

			e.printStackTrace();
			Log.e("DB", e.getMessage());
		}
	}

	/**
	 * 执行get方法
	 * 
	 * @param o执行对象
	 * @param fieldName属性
	 */

	public static Object invokeGet(Object o, String fieldName) {
		Method method = getGetMethod(o.getClass(), fieldName);
		try {
			return method.invoke(o, new Object[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * java反射bean的get方法
	 * 
	 * @param objectClass
	 * @param fieldName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static Method getGetMethod(Class objectClass, String fieldName) {
		StringBuffer sb = new StringBuffer();
		sb.append("get");
		sb.append(fieldName.substring(0, 1).toUpperCase());
		sb.append(fieldName.substring(1));
		try {
			return objectClass.getMethod(sb.toString().trim());
		} catch (Exception e) {
		}
		return null;
	}

}
