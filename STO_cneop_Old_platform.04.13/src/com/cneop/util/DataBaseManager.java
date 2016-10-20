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
	 * ���캯��
	 * 
	 * @param context
	 *            �����Ķ���
	 */
	private DataBaseManager(Context context) {
		dbHelper = new DBHelper(context);// ��ʼ�����ݿ�
		try {
			sqliteDatabase = dbHelper.getWritableDatabase();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/***
	 * ��ȡ�������ʵ��
	 * 
	 * @param context
	 *            �����Ķ���
	 * @return
	 */
	public static final DataBaseManager getInstance(Context context) {
		if (instance == null)
			instance = new DataBaseManager(context);
		return instance;
	}

	/**
	 * �ر����ݿ�
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
	 * ��������ִ��SQL
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
	 * ����ִ������
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
	 * ��������
	 * 
	 * @param sql
	 *            ִ�и��²�����sql���
	 * @param bindArgs
	 *            sql����еĲ���,������˳���Ӧռλ��˳��
	 * @return result ���������¼���кţ�������id�޹�
	 */
	public Long insertDataBySql(String sql, String[] bindArgs) throws Exception {
		long result = 0;
		synchronized (obj_LOCK) {
			if (sqliteDatabase.isOpen()) {
				SQLiteStatement statement = sqliteDatabase.compileStatement(sql);
				if (bindArgs != null) {
					int size = bindArgs.length;
					for (int i = 0; i < size; i++) {
						// ��������ռλ���󶨣���Ӧ
						statement.bindString(i + 1, bindArgs[i]);
					}
					result = statement.executeInsert();
					statement.close();
				}
			} else {
				Log.i("info", "���ݿ��ѹر�");
			}
		}
		return result;
	}

	/**
	 * ��������
	 * 
	 * @param table
	 *            ����
	 * @param values
	 *            Ҫ���������
	 * @return result ���������¼���кţ�������id�޹�
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
	 * ��������
	 * 
	 * @param sql
	 *            ִ�и��²�����sql���
	 * @param bindArgs
	 *            sql����еĲ���,������˳���Ӧռλ��˳��
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
				Log.i("info", "���ݿ��ѹر�");
			}
		}
	}

	/**
	 * ��������
	 * 
	 * @param table
	 *            ����
	 * @param values
	 *            ��ʾ���µ�����
	 * @param whereClause
	 *            ��ʾSQL������������ֵ����
	 * @param whereArgs
	 *            ��ʾռλ����ֵ
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
	 * ɾ������
	 * 
	 * @param sql
	 *            ִ�и��²�����sql���
	 * @param bindArgs
	 *            sql����еĲ���,������˳���Ӧռλ��˳��
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
					// * ����鿴�Ƿ��ܻ�ȡexecuteUpdateDelete����
					// * �鿴Դ���֪ executeUpdateDelete��public�ķ��������Ǻ������������Բ��ܱ����ã�
					// * ���÷���ò��ֻ����root�Ժ�Ļ����ϲ��ܵ��ã�С���ǿ��ԣ���������ȴ���У����Ի��ǲ����á�
					// */
					// }
				}
				statement.execute();
				statement.close();
			} else {
				Log.i("info", "���ݿ��ѹر�");
			}
		}
	}

	/**
	 * ɾ������
	 * 
	 * @param table
	 *            ����
	 * @param whereClause
	 *            ��ʾSQL������������ֵ����
	 * @param whereArgs
	 *            ��ʾռλ����ֵ
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
	 * ��ѯ�������� *
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
	 * ��ѯ����
	 * 
	 * @param searchSQL
	 *            ִ�в�ѯ������sql���
	 * @param selectionArgs
	 *            ��ѯ����
	 * @return ���ز�ѯ���α꣬�ɶ��������в�������Ҫ�Լ��ر��α�
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
	 * ��ѯ����
	 * 
	 * @param sql
	 *            ִ�в�ѯ������sql���
	 * @param selectionArgs
	 *            ��ѯ����
	 * @param object
	 *            Object�Ķ���
	 * @return List<Object> ���ز�ѯ���
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
	 * ��ѯ����
	 * 
	 * @param sql
	 *            ִ�в�ѯ������sql���
	 * @param selectionArgs
	 *            ��ѯ����
	 * @param object
	 *            Object�Ķ���
	 * @return List<Map<String, Object>> ���ز�ѯ���
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
							Log.i("info", "sql �����:" + sql + ",��Ӧ�ı��У��������ֶ�:" + key);
							continue;
						}
						map.put(key, cursor.getString(columnIndex));

					}
					mList.add(map);
					// }
				}
				cursor.close();
			} else {
				Log.i("info", "���ݿ��ѹر�");
			}
		}
		return mList;
	}
}
