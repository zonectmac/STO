package com.cneop.stoExpress.dao;

import android.content.Context;
import com.cneop.stoExpress.model.*;

import com.cneop.stoExpress.dao.BaseDao;

public class SJMDSendedRecordsDao extends BaseDao {
	private Context context;

	public SJMDSendedRecordsDao(Context context) {
		super(context);
		tableName = "tb_SJMDSendedRecords";
		this.context = context;
	}

	@Override
	protected String getSearchSql(String[] words) {
		String sql = "select extenddate,sitename,billcodebegin,billcodeend,releasequantity from tb_SJMDSendedRecords";
		return sql;
	}

	@Override
	protected Object[] getInsertParams(Object object) {
		SJMDSendedRecordsVO model = (SJMDSendedRecordsVO) object;
		Object[] objs = {
				model.getExtenddate(),
				model.getSitename(), model.getBillcodebegin(), model.getBillcodeend(),
				model.getReleasequantity() };
		return objs;
	}

	@Override
	protected String getInsertSql() {
		String sql = "insert into  tb_SJMDSendedRecords    (extenddate,sitename,billcodebegin,billcodeend,releasequantity      ) values (?,?,?,?,? ) ";
		return sql;
	}
}