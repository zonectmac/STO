package com.cneop.stoExpress.dao;

import java.util.ArrayList;
import java.util.List;
import com.cneop.stoExpress.model.MsgSend;
import com.cneop.stoExpress.model.QueryView;
import com.cneop.stoExpress.model.ScanDataModel;
import com.cneop.stoExpress.model.UploadView;
import com.cneop.util.StrUtil;

import android.content.Context;

public class MsgSendService extends BaseDao {

	StrUtil strUtil;

	public MsgSendService(Context context) {
		super(context);
		tableName = "tb_msgSend";
		strUtil = new StrUtil();
	}

	@Override
	protected String getSearchSql(String[] words) {

		String sql = "select id,barcode,phone,serverNo from tb_msgSend where 1=1  ";
		sql += strUtil.arrayToString(words);
		return sql;
	}

	@Override
	protected Object[] getInsertParams(Object object) {

		MsgSend model = (MsgSend) object;
		Object[] obj = { model.getBarcode(), model.getPhone(), model.getStationId(), model.getServerNo(), model.getScanUser() };
		return obj;
	}

	@Override
	protected String getInsertSql() {

		String sql = "insert into tb_msgSend (barcode,phone,stationId,serverNo,scanUser) values(?,?,?,?,?)";
		return sql;
	}

	/**
	 * 单条新增
	 * 
	 * @param model
	 * @return
	 */
	public int addRecord(MsgSend model) {
		List<MsgSend> list = new ArrayList<MsgSend>();
		list.add(model);
		return addRecord(list);
	}

	/**
	 * 删除
	 * 
	 * @param barcode
	 * @param serverCode
	 * @param phone
	 * @return
	 */
	public int delRecord(String barcode, String serverCode, String phone) {
		String whereClause = " issynchronization=0 and barcode=? and phone=? and serverNo=?";
		String[] whereArgs = { barcode, phone, serverCode };
		return delRecord(whereClause, whereArgs);
	}

	/**
	 * 根据ID删除
	 * 
	 * @param id
	 * @return
	 */
	public int delRecord(int id) {
		String[] whereArgs = { String.valueOf(id) };
		int t = dataBaseManager.deleteData("tb_msgSend", "id=?", whereArgs);
		return t;
	}

	/**
	 * 批量删除数据
	 */
	public int delData(String condition) {
		int t = -1;
		String sql = "delete from tb_msgSend where 1=1 " + condition;
		try {
			dataBaseManager.deleteDataBySql(sql, null);
			t = 1;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return t;
	}

	/**
	 * 删除7天前数据
	 * 
	 * @return
	 */
	public int delData() {
		String condition = " and datetime('now','localtime','-7 day')>datetime(scantime)";
		return delData(condition);
	}

	/**
	 * 获得未上传的短信
	 * 
	 * @return
	 */
	public List<MsgSend> getUnUpload() {
		String[] words = { " and issynchronization=0" };
		List<Object> list = getListObj(words, null, new MsgSend());
		List<MsgSend> msgSendList = new ArrayList<MsgSend>();
		if (list != null && list.size() > 0) {
			for (Object obj : list) {
				msgSendList.add((MsgSend) obj);
			}
		}
		return msgSendList;
	}

	/**
	 * 获取数量
	 * 
	 * @return
	 */
	public int getCountByStatus(String status) {
		int count = 0;
		String condition = "";
		if (!strUtil.isNullOrEmpty(status)) {
			condition += " and issynchronization=" + status;
		}
		count = getCount(condition);
		return count;
	}

	public int getCount(String condition) {
		int count = 0;
		String sql = "select count(Id) from tb_msgSend where 1=1 ";
		if (!strUtil.isNullOrEmpty(condition)) {
			sql += condition;
		}
		try {
			String str = dataBaseManager.querySingleData(sql, null);
			if (!strUtil.isNullOrEmpty(str)) {
				count = Integer.parseInt(str);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return count;
	}

	/**
	 * 上传后，更新状态
	 * 
	 * @param list
	 * @return
	 */
	public int updateStatus(List<MsgSend> list) {
		StringBuilder sql = new StringBuilder();
		sql.append("update tb_msgSend set issynchronization='1',");
		sql.append("lasttime=datetime('now','localtime') ");
		sql.append(" where barcode=? and issynchronization=0");
		List<Object[]> objectList = new ArrayList<Object[]>();
		for (MsgSend model : list) {
			Object[] objs = { model.getBarcode() };
			objectList.add(objs);
		}
		int result = dataBaseManager.executeSqlByTran(sql.toString(), objectList);
		return result;
	}

	/**
	 * 未上传短信视图
	 * 
	 * @return
	 */
	public List<UploadView> getUnUploadView() {
		StringBuilder sb = new StringBuilder();
		sb.append("select * from (");
		sb.append("select '短信' as scanTypeStr,");
		sb.append("count(id) as totalCount,");
		sb.append("'msg' as uploadType,");
		sb.append("'OTHER' as scanType ");
		sb.append("from tb_msgsend where issynchronization=0)");
		sb.append("as c where c.totalCount>0");
		List<UploadView> uploadList = new ArrayList<UploadView>();
		try {
			List<Object> list = dataBaseManager.queryData2Object(sb.toString(), null, new UploadView());
			if (list != null && list.size() > 0) {
				for (Object object : list) {
					uploadList.add((UploadView) object);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return uploadList;
	}

	/**
	 * 查询视图
	 * 
	 * @return
	 */
	public List<QueryView> getQueryView(String barcode, String scanUser, String startTime, String endTime) {
		String conditionSql = getSqlWhere(barcode, scanUser, startTime, endTime, "");
		StringBuilder sqlSb = new StringBuilder();
		sqlSb.append("select moduleName,sum(totalcount) as totalDataCount,");
		sqlSb.append("sum(unUpload) as unUpload,scanCode,siteProperties,'OTHER' as scanType,'msg'   as uploadType from(");
		sqlSb.append("select '短信' as moduleName,count(id) as totalcount,0 as unUpload,");
		sqlSb.append("'00' as scanCode,'0' as siteProperties from tb_msgsend where 1=1");
		sqlSb.append(conditionSql);
		sqlSb.append(" union all ");
		sqlSb.append("select '短信' as moduleName,0 as totalcount,count(id) as unUpload,");
		sqlSb.append("'00' as scanCode,'0' as siteProperties from tb_msgsend where issynchronization=0" + conditionSql + ") as c ");
		sqlSb.append("where (totalcount>0 or unupload>0) group by moduleName having totalDataCount>0;");
		List<QueryView> queryList = new ArrayList<QueryView>();
		try {
			List<Object> list = dataBaseManager.queryData2Object(sqlSb.toString(), null, new QueryView());
			if (list != null && list.size() > 0) {
				for (Object object : list) {
					queryList.add((QueryView) object);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return queryList;
	}

	public String getSqlWhere(String barcode, String scanUser, String startTime, String endTime, String uploadStatus) {
		StringBuilder whereSb = new StringBuilder();
		if (!strUtil.isNullOrEmpty(barcode)) {
			whereSb.append(" and barcode='" + barcode + "'");
		}
		if (!strUtil.isNullOrEmpty(scanUser)) {
			whereSb.append(" and scanUser='" + scanUser + "'");
		}
		if (!strUtil.isNullOrEmpty(startTime) && !strUtil.isNullOrEmpty(endTime)) {
			whereSb.append(" and (scanTime>='" + startTime + "' and scanTime<='" + endTime + "')");
		} else if (!strUtil.isNullOrEmpty(startTime)) {
			whereSb.append(" and scanTime>='" + startTime + "'");
		} else if (!strUtil.isNullOrEmpty(endTime)) {
			whereSb.append(" and scanTime<='" + endTime + "'");
		}
		if (!strUtil.isNullOrEmpty(uploadStatus)) {
			whereSb.append(" and issynchronization='" + uploadStatus + "'");
		}
		return whereSb.toString();
	}

	public List<MsgSend> getMsgData(String condition, int pageSize, int pageIndex) {
		List<MsgSend> list = new ArrayList<MsgSend>();
		StringBuilder sql = new StringBuilder("select * from tb_msgsend where 1=1");
		sql.append(condition);
		sql.append(" limit " + pageSize + " offset " + (pageIndex - 1) * pageSize);
		list = getList(sql.toString(), null);
		System.out.println("===========>sql \t" + sql.toString());
		return list;
	}

	private List<MsgSend> getList(String sql, String[] params) {
		List<MsgSend> list = new ArrayList<MsgSend>();
		try {
			// ScanDataModel model = new ScanDataModel();
			MsgSend model = new MsgSend();
			List<Object> listT = dataBaseManager.queryData2Object(sql, params, model);
			if (listT != null && listT.size() > 0) {
				for (Object object : listT) {
					list.add((MsgSend) object);
				}
			}
		} catch (Exception e) {
			System.out.println("========to \t" + e.toString());
			e.printStackTrace();
		}
		return list;
	}

	public int delSingleData(String barcode, String string) {
		String[] whereArgs = { barcode };
		int t = dataBaseManager.deleteData("tb_msgSend", "barcode=? and issynchronization='0'", whereArgs);
		return t;
	}

}
