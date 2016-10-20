package com.cneop.stoExpress.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import android.content.Context;

import com.cneop.stoExpress.common.Enums.EDownType;
import com.cneop.stoExpress.dao.BaseDao;

public class ReflectorUtil {

	Context context;

	public ReflectorUtil(Context context) {
		this.context = context;
	}

	/*
	 * 根据下载类型得到实例
	 */
	@SuppressWarnings("unchecked")
	public BaseDao getDao(EDownType downType) {
		BaseDao basDao = null;
		try {
			Class<BaseDao> basClass = (Class<BaseDao>) Class.forName("com.cneop.stoExpress.dao." + downType.toString().trim() + "Service");
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

}
