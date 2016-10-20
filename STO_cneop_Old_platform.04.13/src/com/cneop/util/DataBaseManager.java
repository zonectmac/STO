package com.cneop.util;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class DataBaseManager {
	private DBHelper dbHelper;
	private static DataBaseManager instance = null;
	private SQLiteDatabase sqliteDatabase;
	private static Object obj_LOCK = new Object();

	/**
	 * 构造函数
	 * 
	 * @param context
	 *            上下文对象
	 */
	private DataBaseManager(Context context) {
		dbHelper = new DBHelper(context);// 初始化数据库
		try {
			sqliteDatabase = dbHelper.getWritableDatabase();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/***
	 * 获取本类对象实例
	 * 
	 * @param context
	 *            上下文对象
	 * @return
	 */
	public static final DataBaseManager getInstance(Context context) {
		if (instance == null)
			instance = new DataBaseManager(context);
		return instance;
	}

	/**
	 * 关闭数据库
	 */
	public void close() {
		if (sqliteDatabase.isOpen())
			sqliteDatabase.close();
		if (dbHelper != null)
			dbHelper.close();
		if (instance != null)
			instance = null;
	}

	/**
	 * 事务批量执行SQL
	 */
	public synchronized int executeSqlByTran(String sql, List<Object[]> objectList) {
		int rows = 0;
		try {
			for (int i = 0; i < objectList.size(); i++) {
				sqliteDatabase.execSQL(sql, objectList.get(i));
				rows++;
			}

		} catch (Exception e) {
			e.printStackTrace();
			rows = 0;
		}

		return rows;
	}

	/**
	 * 批量执行事务
	 * 
	 * @param sqlList
	 * @return
	 */
	public int executeSqlByTran(List<String> sqlList) {
		int rows = 0;
		synchronized (obj_LOCK) {
			if (sqliteDatabase.isOpen()) {
				sqliteDatabase.beginTransaction();
				try {
					for (int i = 0; i < sqlList.size(); i++) {
						sqliteDatabase.execSQL(sqlList.get(i));
						rows++;
					}
					sqliteDatabase.setTransactionSuccessful();
				} catch (Exception e) {
					e.printStackTrace();
					// Log.i("com.cneop.sto", e.getMessage());
					rows = 0;
				} finally {
					sqliteDatabase.endTransaction();
				}
			}
		}
		return rows;
	}

	/**
	 * 
	 * @param sqlDic
	 * @return
	 */
	public int executeSqlByTran(List<String> sqlList, List<Object[]> paramsList) {
		int rows = 0;
		synchronized (obj_LOCK) {
			if (sqliteDatabase.isOpen()) {
				sqliteDatabase.beginTransaction();
				try {
					for (int i = 0; i < sqlList.size(); i++) {
						sqliteDatabase.execSQL(sqlList.get(i), paramsList.get(i));
						rows++;
					}
					sqliteDatabase.setTransactionSuccessful();
				} catch (Exception e) {
					e.printStackTrace();
					// Log.i("com.cneop.sto", e.getMessage());
					rows = 0;
				} finally {
					sqliteDatabase.endTransaction();
				}
			}
		}
		return rows;
	}

	public int executeSql(String sql) {
		int rows = 0;
		synchronized (obj_LOCK) {
			if (sqliteDatabase.isOpen()) {
				sqliteDatabase.beginTransaction();
				try {

					sqliteDatabase.execSQL(sql);
					rows++;

					sqliteDatabase.setTransactionSuccessful();
				} catch (Exception e) {
					e.printStackTrace();
					// Log.e(TAG, e.getMessage());
					rows = 0;
				} finally {
					sqliteDatabase.endTransaction();
				}
			}
		}
		return rows;
	}

	/**
	 * 插入数据
	 * 
	 * @param sql
	 *            执行更新操作的sql语句
	 * @param bindArgs
	 *            sql语句中的参数,参数的顺序对应占位符顺序
	 * @return result 返回新添记录的行号，与主键id无关
	 */
	public Long insertDataBySql(String sql, String[] bindArgs) throws Exception {
		long result = 0;
		synchronized (obj_LOCK) {
			if (sqliteDatabase.isOpen()) {
				SQLiteStatement statement = sqliteDatabase.compileStatement(sql);
				if (bindArgs != null) {
					int size = bindArgs.length;
					for (int i = 0; i < size; i++) {
						// 将参数和占位符绑定，对应
						statement.bindString(i + 1, bindArgs[i]);
					}
					result = statement.executeInsert();
					statement.close();
				}
			} else {
				Log.i("info", "数据库已关闭");
			}
		}
		return result;
	}

	/**
	 * 插入数据
	 * 
	 * @param table
	 *            表名
	 * @param values
	 *            要插入的数据
	 * @return result 返回新添记录的行号，与主键id无关
	 */
	public Long insertData(String table, ContentValues values) {
		long result = 0;
		synchronized (obj_LOCK) {
			if (sqliteDatabase.isOpen()) {
				result = sqliteDatabase.insert(table, null, values);
			}
		}
		return result;
	}

	/**
	 * 更新数据
	 * 
	 * @param sql
	 *            执行更新操作的sql语句
	 * @param bindArgs
	 *            sql语句中的参数,参数的顺序对应占位符顺序
	 */
	public void updateDataBySql(String sql, String[] bindArgs) throws Exception {
		synchronized (obj_LOCK) {
			if (sqliteDatabase.isOpen()) {
				SQLiteStatement statement = sqliteDatabase.compileStatement(sql);
				if (bindArgs != null) {
					int size = bindArgs.length;
					for (int i = 0; i < size; i++) {
						statement.bindString(i + 1, bindArgs[i]);
					}
				}
				statement.execute();
				statement.close();
			} else {
				Log.i("info", "数据库已关闭");
			}
		}
	}

	/**
	 * 更新数据
	 * 
	 * @param table
	 *            表名
	 * @param values
	 *            表示更新的数据
	 * @param whereClause
	 *            表示SQL语句中条件部分的语句
	 * @param whereArgs
	 *            表示占位符的值
	 * @return
	 */
	public int updataData(String table, ContentValues values, String whereClause, String[] whereArgs) {
		int result = 0;
		synchronized (obj_LOCK) {
			if (sqliteDatabase.isOpen()) {
				result = sqliteDatabase.update(table, values, whereClause, whereArgs);
			}
		}
		return result;
	}

	public void closeDB() {
		if (sqliteDatabase.isOpen()) {
			sqliteDatabase.close();
		}
	}

	/**
	 * 删除数据
	 * 
	 * @param sql
	 *            执行更新操作的sql语句
	 * @param bindArgs
	 *            sql语句中的参数,参数的顺序对应占位符顺序
	 */
	public void deleteDataBySql(String sql, String[] bindArgs) throws Exception {
		synchronized (obj_LOCK) {
			if (sqliteDatabase.isOpen()) {
				SQLiteStatement statement = sqliteDatabase.compileStatement(sql);
				if (bindArgs != null) {
					int size = bindArgs.length;
					for (int i = 0; i < size; i++) {
						statement.bindString(i + 1, bindArgs[i]);
					}
					// Method[] mm = statement.getClass().getDeclaredMethods();
					// for (Method method : mm) {
					// Log.i("info", method.getName());
					// /**
					// * 反射查看是否能获取executeUpdateDelete方法
					// * 查看源码可知 executeUpdateDelete是public的方法，但是好像被隐藏了所以不能被调用，
					// * 利用反射貌似只能在root以后的机器上才能调用，小米是可以，其他机器却不行，所以还是不能用。
					// */
					// }
				}
				statement.execute();
				statement.close();
			} else {
				Log.i("info", "数据库已关闭");
			}
		}
	}

	/**
	 * 删除数据
	 * 
	 * @param table
	 *            表名
	 * @param whereClause
	 *            表示SQL语句中条件部分的语句
	 * @param whereArgs
	 *            表示占位符的值
	 * @return
	 */
	public int deleteData(String table, String whereClause, String[] whereArgs) {
		int result = 0;
		synchronized (obj_LOCK) {
			result = sqliteDatabase.delete(table, whereClause, whereArgs);
			System.out.println("==========result \t" + result);
		}
		return result;
	}

	/**
	 * 查询单条数据 *
	 */
	public String querySingleData(String sql, String[] selectionArgs) {
		String str = null;
		synchronized (obj_LOCK) {
			if (sqliteDatabase.isOpen()) {
				Cursor cursor = sqliteDatabase.rawQuery(sql, selectionArgs);
				if (cursor != null) {
					if (cursor.moveToNext()) {
						str = cursor.getString(0);
					}
					cursor.close();
				}
			}
		}
		return str;
	}

	/**
	 * 查询数据
	 * 
	 * @param searchSQL
	 *            执行查询操作的sql语句
	 * @param selectionArgs
	 *            查询条件
	 * @return 返回查询的游标，可对数据自行操作，需要自己关闭游标
	 */
	public Cursor queryData2Cursor(String sql, String[] selectionArgs) throws Exception {
		synchronized (obj_LOCK) {
			if (sqliteDatabase.isOpen()) {
				Cursor cursor = sqliteDatabase.rawQuery(sql, selectionArgs);
				if (cursor != null) {
					cursor.moveToFirst();
				}
				return cursor;
			}
		}
		return null;
	}

	/**
	 * 查询数据
	 * 
	 * @param sql
	 *            执行查询操作的sql语句
	 * @param selectionArgs
	 *            查询条件
	 * @param object
	 *            Object的对象
	 * @return List<Object> 返回查询结果
	 */
	@SuppressLint("NewApi")
	public List<Object> queryData2Object(String sql, String[] selectionArgs, Object object) throws Exception {
		List<Object> mList = new ArrayList<Object>();
		Cursor cursor = sqliteDatabase.rawQuery(sql, selectionArgs);
		Field[] f;
		if (cursor != null && cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				object = object.getClass().newInstance();
				f = object.getClass().getDeclaredFields();
				for (int i = 0; i < f.length; i++) {
					int columnIndex = cursor.getColumnIndex(f[i].getName());
					Object objValue = null;
					switch (cursor.getType(columnIndex)) {
					case Cursor.FIELD_TYPE_BLOB:
						objValue = cursor.getBlob(columnIndex);
						System.out.println("=======dd " + cursor.getBlob(columnIndex));
						break;
					case Cursor.FIELD_TYPE_FLOAT:
						objValue = cursor.getFloat(columnIndex);
						Log.i("Tag", cursor.getFloat(columnIndex) + "");
						break;
					case Cursor.FIELD_TYPE_INTEGER:
						objValue = cursor.getInt(columnIndex);
						break;
					case Cursor.FIELD_TYPE_STRING:
						objValue = cursor.getString(columnIndex);
						Log.i("Tag", cursor.getString(columnIndex));
					}
					Class<?> type = f[i].getType();
					if (type == java.util.Date.class) {
						if (objValue != null && objValue.toString().trim().length() > 0) {
							try {
								SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								ReflectorUtil.invokeSet(object, f[i].getName(), sf.parse(objValue.toString()));
							} catch (Exception ex) {

							}
						}
					} else {
						ReflectorUtil.invokeSet(object, f[i].getName(), objValue);
					}

				}
				mList.add(object);
			}
		}
		cursor.close();

		return mList;
	}

	/**
	 * 查询数据
	 * 
	 * @param sql
	 *            执行查询操作的sql语句
	 * @param selectionArgs
	 *            查询条件
	 * @param object
	 *            Object的对象
	 * @return List<Map<String, Object>> 返回查询结果
	 * @throws Exception
	 */
	public List<Map<String, Object>> queryData2Map(String sql, String[] selectionArgs, Object object) throws Exception {
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		synchronized (obj_LOCK) {
			if (sqliteDatabase.isOpen()) {
				Cursor cursor = sqliteDatabase.rawQuery(sql, selectionArgs);
				Field[] f;
				Map<String, Object> map;
				while (cursor.moveToNext()) {
					// if(cursor != null && cursor.getCount() > 0) {
					// while(cursor.moveToNext()){
					map = new HashMap<String, Object>();
					f = object.getClass().getDeclaredFields();
					String key = "";
					for (int i = 0; i < f.length; i++) {
						key = f[i].getName();
						int columnIndex = cursor.getColumnIndex(f[i].getName());
						if (columnIndex < 0) {
							Log.i("info", "sql 语句是:" + sql + ",对应的表中，不存在字段:" + key);
							continue;
						}
						map.put(key, cursor.getString(columnIndex));

					}
					mList.add(map);
					// }
				}
				cursor.close();
			} else {
				Log.i("info", "数据库已关闭");
			}
		}
		return mList;
	}
}
