package com.cneop.stoExpress.dao;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cneop.stoExpress.model.ScopeOfDelivery;

import android.content.Context;
import android.widget.Toast;

public class AreaQueryService extends BaseDao {
	public AreaQueryService(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		tableName = "tb_dic_scopeofdelivery";
	}

	@Override
	protected String getSearchSql(String[] words) {
		String sql = "select province,city,area,isreceipt,isdelivery,scopeofdelivery,scopeofnodelivery,state,lasttime from tb_dic_scopeOfDelivery where state='A'  ";
		sql += strUtil.arrayToString(words);
		return sql;
	}

	@Override
	protected Object[] getInsertParams(Object object) {
		return null;
	}

	@Override
	protected String getInsertSql() {
		String sql = "insert into tb_dic_scopeOfDelivery (province,city,area,isreceipt,isdelivery,scopeofdelivery,scopeofnodelivery) values(?,?,?,?,?,?,?)";
		return sql;
	}

	public void addDataFromTxt(final InputStream is) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String sql = "insert into tb_dic_scopeOfDelivery (province,city,area,isreceipt,isdelivery,scopeofdelivery,scopeofnodelivery) values(?,?,?,?,?,?,?)";
				List<Object[]> sqllist = new ArrayList<Object[]>();
				try {
					if (is != null) {
						BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, "Unicode"));
						BufferedReader reader = new BufferedReader(bufferedReader);
						String line;
						// ---------------------------------------------------
						while ((line = reader.readLine()) != null) {
							String[] data = line.split("\t");
							sqllist.add(data);
							dataBaseManager.executeSqlByTran(sql, sqllist);
							sqllist.clear();
						}
						is.close();
					}
				} catch (Exception e) {
					System.out.println(e.toString());
					e.printStackTrace();
				}
			}
		}).start();

		// int result = dataBaseManager.executeSqlByTran(sql, sqllist);
		// return result;
	}

	public List<Map<String, Object>> getProvince(String colunmName, String condition) {
		
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String sql = "select distinct " + colunmName + " from tb_dic_scopeOfDelivery " + condition + "  group by " + colunmName;
		try {
			list = dataBaseManager.queryData2Map(sql, null, new ScopeOfDelivery());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public boolean isExist() {

		boolean flag = false;
		try {

			String sql = "select count(id) from tb_dic_scopeofdelivery";
			String result = dataBaseManager.querySingleData(sql, null);
			if (Integer.parseInt(result) > 0) {
				flag = true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return flag;
	}

}
