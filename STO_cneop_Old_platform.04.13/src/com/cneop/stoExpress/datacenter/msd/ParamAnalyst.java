package com.cneop.stoExpress.datacenter.msd;

import java.util.ArrayList;
import java.util.List;

import com.cneop.stoExpress.common.Enums.EDownTableName;
import com.cneop.stoExpress.common.Enums.*;

public class ParamAnalyst {

	List<ParamEnt> peList;
	private final String tableSplite = "_T_";
	private final String paramSplite = "_P_";

	public ParamAnalyst() {
		peList = new ArrayList<ParamEnt>();
	}

	/**
	 * 添加参数
	 * 
	 * @param tableName
	 * @param params
	 */
	public void add(EDownTableName tableName, String[] params) {
		ParamEnt pe = new ParamEnt();
		pe.setTableName(tableName.toString().trim());
		if (params != null) {
			for (int i = 0; i < params.length; i++) {
				pe.addParam(params[i]);
			}
		}
		peList.add(pe);
	}

	/**
	 * 参数格式
	 * 
	 * @return
	 */
	public String paramToString() {
		StringBuilder sb = new StringBuilder();
		if (peList.size() == 0) {
			return sb.toString().trim();
		}
		for (ParamEnt pe : peList) {
			sb.append("{");
			sb.append(pe.getTableName());
			if (pe.getParamList().size() > 0) {
				sb.append(tableSplite);
			}
			int size = pe.getParamList().size();
			for (int i = 0; i < size; i++) {
				if (i == size - 1) {
					sb.append(pe.getParamList().get(i));
				} else {
					sb.append(pe.getParamList().get(i));
					sb.append(paramSplite);
				}
			}
			sb.append("},");
		}
		return sb.toString().trim().substring(0, sb.length() - 1);
	}
}
