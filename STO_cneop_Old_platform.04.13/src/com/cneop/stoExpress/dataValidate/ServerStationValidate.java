package com.cneop.stoExpress.dataValidate;

import com.cneop.stoExpress.common.GlobalParas;
import com.cneop.stoExpress.dao.ServerStationService;
import com.cneop.stoExpress.model.ServerStation;
import com.cneop.util.PromptUtils;

import android.content.Context;
import android.widget.EditText;

public class ServerStationValidate extends DataValidate implements IDataValidate{

	ServerStationService serverStation;
	EditText tvServerAdd;
	EditText tvServerPho;
	
	public ServerStationValidate(Context context,EditText tvServerAdd,EditText tvServerPho) {
		super(context);
		// TODO Auto-generated constructor stub
		serverStation = new ServerStationService(context);
		this.tvServerAdd = tvServerAdd;
		this.tvServerPho = tvServerPho;
	}

	@Override
	public void showName(EditText et) {
	 
		if (et.getTag() == null) {
			boolean flag = true;
			String serverNo = GlobalParas.getGlobalParas().getStationId()+et.getText().toString().trim().trim();
			if (!strUtil.isNullOrEmpty(serverNo)) {
				com.cneop.stoExpress.model.ServerStation serverStation = queryServerStation(serverNo);
				if (serverStation != null) {
					et.setTag(et.getText().toString().trim());
					et.setText(et.getText().toString().trim());
					
					tvServerAdd.setText(serverStation.getServerAddress());
					tvServerPho.setText(serverStation.getServerPhone());
					
					flag = false;
				}
			}
			if (flag) {
				tvServerAdd.setText("");
				tvServerPho.setText("");
			}
		}
	}

	@Override
	public void restoreNo(EditText et) {
	 
		if (et.getTag() != null) {
			String serverNo = et.getTag().toString().trim().trim();
			et.setText(serverNo);
			et.setTag(null);
		}
	}

	@Override
	public boolean vlidateInputData(EditText et) {
	 
		boolean flag = true;
		if (et.getTag() == null) {
			flag = false;
			controlUtil.setEditVeiwFocus(et);
			PromptUtils.getInstance().showToast("服务点代码异常！", context);
		}
		return flag;
	}
	
	private com.cneop.stoExpress.model.ServerStation queryServerStation(String serverNo) {
		return  serverStation.getServerStation(serverNo);
	 
	}

}
