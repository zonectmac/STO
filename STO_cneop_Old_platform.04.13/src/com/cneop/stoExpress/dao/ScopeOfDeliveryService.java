package com.cneop.stoExpress.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import com.cneop.util.DataBaseManager;
import com.cneop.util.StrUtil;
import com.cneop.util.file.AssetsUtil;
import android.content.Context;
import android.database.Cursor;

/**
 * 派送区域
 * 
 * @author Administrator
 * 
 */
public class ScopeOfDeliveryService {

	private final String txtName = "area.txt";
	DataBaseManager dataBaseManager;
	AssetsUtil assetsUtil;
	StrUtil strUtil;

	public ScopeOfDeliveryService(Context context) {
		dataBaseManager = DataBaseManager.getInstance(context);
		assetsUtil = new AssetsUtil(context);
		strUtil = new StrUtil();
	}

	/**
	 * 省份数据
	 * 
	 * @return
	 */
	public List<String> getPrivinceData() {
		String columnName = "province";
		return getScopeData(columnName, "");
	}

	/**
	 * 城市数据
	 * 
	 * @param province
	 * @return
	 */
	public List<String> getCityData(String province) {
		String columnName = "city";
		String condition = " and province='" + province + "'";
		return getScopeData(columnName, condition);
	}

	/**
	 * 地区数据
	 * 
	 * @param province
	 * @param city
	 * @return
	 */
	public List<String> getAreaData(String province, String city) {
		String columnName = "area";
		String condition = " and province='" + province + "' and city='" + city + "'";
		return getScopeData(columnName, condition);
	}

	/**
	 * 未派送数据
	 * 
	 * @param condition
	 * @return
	 */
	public String getUnDeliveryData(String province, String city, String area) {
		String columnName = "scopeOfNoDelivery";
		String condition = "  and province='" + province + "' and city='" + city + "' and area='" + area + "'";
		List<String> list = getScopeData(columnName, condition);
		String unDeliveryData = "该地区没有数据!";
		if (list != null && list.size() > 0) {
			if (!strUtil.isNullOrEmpty(list.get(0))) {
				unDeliveryData = list.get(0);
			} else {
				unDeliveryData = "该地区全境派送";
			}
		}
		return unDeliveryData;
	}

	private List<String> getScopeData(String columnName, String condition) {
		List<String> list = new ArrayList<String>();
		String sql = "select distinct " + columnName + " from tb_dic_scopeofdelivery where 1=1 " + condition + " group by " + columnName;
		Cursor cursor = null;
		try {
			cursor = dataBaseManager.queryData2Cursor(sql, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					list.add(cursor.getString(0));
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return list;
	}

	/**
	 * 读取文件并插入数据库中
	 * 
	 * @return
	 */
	public int addDataFromTxt() {
		int t = 0;
		int count = getCount();
		if (count == 0) {
			InputStreamReader sr = null;
			BufferedReader reader = null;
			InputStream in = null;
			try {
				// 新增
				in = assetsUtil.getStreamFormAsset(txtName);
				sr = new InputStreamReader(in, "utf-8");
				reader = new BufferedReader(sr);
				String rowContent = reader.readLine();
				rowContent = reader.readLine();
				List<Object[]> objList = new ArrayList<Object[]>();
				while (!strUtil.isNullOrEmpty(rowContent)) {
					String[] cols = rowContent.split("\t");
					Object[] obj = new Object[7];
					for (int j = 0; j < cols.length; j++) {
						obj[j] = cols[j];
					}
					objList.add(obj);
					rowContent = reader.readLine();
				}
				reader.close();
				sr.close();
				in.close();
				if (objList.size() > 0) {
					String sql = "insert into tb_dic_ScopeOfDelivery (province,city,area,isReceipt,isDelivery,scopeOfDelivery,scopeOfNoDelivery) values(?,?,?,?,?,?,?)";
					t = dataBaseManager.executeSqlByTran(sql, objList);
				}
			} catch (Exception e) {
				// TODO: handle exception
			} finally {

				try {
					reader.close();
					sr.close();
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return t;
	}

	/**
	 * 记录数
	 * 
	 * @return
	 */
	private int getCount() {

		int t = 0;
		String sql = "select count(Id) from tb_dic_scopeofdelivery";
		try {
			String res = dataBaseManager.querySingleData(sql, null);
			if (!strUtil.isNullOrEmpty(res)) {
				t = Integer.parseInt(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return t;
	}
}
