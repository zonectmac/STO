package com.cneop.stoExpress.datacenter.msd;

import java.util.ArrayList;
import java.util.List;

class ParamEnt {
	private String tableName;
	private List<String> paramList;

	public ParamEnt() {
		paramList = new ArrayList<String>();
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public List<String> getParamList() {
		return paramList;
	}

	public void addParam(String param) {
		paramList.add(param);
	}

}
