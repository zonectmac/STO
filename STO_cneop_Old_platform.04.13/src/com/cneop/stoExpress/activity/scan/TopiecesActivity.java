package com.cneop.stoExpress.activity.scan;

import java.util.HashMap;
import java.util.Map;

import com.cneop.Date.DATE;
import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.common.Enums.EDownType;
import com.cneop.stoExpress.common.Enums.EGoodsType;
import com.cneop.stoExpress.common.Enums.EProgramRole;
import com.cneop.stoExpress.common.Enums.EScanType;
import com.cneop.stoExpress.common.Enums.EUploadType;
import com.cneop.stoExpress.common.Enums.EVoiceType;
import com.cneop.stoExpress.common.GlobalParas;
import com.cneop.stoExpress.dataValidate.StationValidate;
import com.cneop.stoExpress.datacenter.AddScanDataThread;
import com.cneop.stoExpress.model.ScanDataModel;
import com.cneop.util.AppContext;
import com.cneop.util.PromptUtils;
import com.cneop.util.controls.ListViewEx;
import com.cneop.util.scan.ScanManager;
import com.math.math;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

public class TopiecesActivity extends ScanBaseActivity {

	private ViewStub vsGoodsType, vsPreStation, vsListHead, vsWeigth;
	private RadioButton rdoGoods;
	private TextView tvWeight;
	private EditText etPreStation;
	private Button btnSelPreStation;
	private StationValidate stationValidate;
	private String expressType = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_topieces);
		setTitle("到件");
		super.scannerPower = true;
		super.onCreate(savedInstanceState);
		ScanManager.getInstance().getScanner().setScanResultHandler(handleScanData);
	}

	/**
	 * 扫描数据
	 */
	@SuppressWarnings("static-access")
	@Override
	protected void setScanData(String barcode) {

		PromptUtils.getInstance().closeAlertDialog();
		if (etPreStation != null) {
			try {
				if (barcode.length() == 6) {
					controlUtil.setEditVeiwFocus(etPreStation);
					etPreStation.setText(barcode);
					controlUtil.setEditVeiwFocus(etBarcode);
					return;
				}
				if (new math().CODE1(barcode)) {
					etBarcode.setText(barcode);
					etBarcode.requestFocus();
				} else {
					PromptUtils.getInstance().showToastHasFeel("非法条码", this, EVoiceType.fail, 0);
					etBarcode.setText("");
					return;
				}
				addRecord();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			if (!new math().CODE1(barcode)) {
				PromptUtils.getInstance().showToastHasFeel("非法单号", this, EVoiceType.fail, 0);
				etBarcode.requestFocus();
			} else {
				etBarcode.setText(barcode);
				addRecord();
			}
		}

	}

	@Override
	protected void initializeComponent() {

		super.initializeComponent();
		etBarcode = bindEditText(R.id.et_barcode_etBarcode, null, null);

		// 打开蓝牙电子称才显示重量
		if (AppContext.getAppContext().getBluetoothIsOpen()) {
			vsWeigth = (ViewStub) findViewById(R.id.vs_topieces_tvWeight);
			vsWeigth.inflate();
			tvWeight = (TextView) this.findViewById(R.id.tv_weight_tvShowWeight);
		}
		btnAdd = bindButton(R.id.btn_barcode_btnAdd);
		if (GlobalParas.getGlobalParas().getProgramRole() == EProgramRole.center
				|| GlobalParas.getGlobalParas().getProgramRole() == EProgramRole.air) {
			vsGoodsType = (ViewStub) this.findViewById(R.id.vs_topieces_vsItemCategory);
			vsGoodsType.inflate();
			rdoGoods = (RadioButton) this.findViewById(R.id.rdo_goods_category_rbGoods);
		}
		if (GlobalParas.getGlobalParas().getProgramRole() == EProgramRole.air) {
			vsPreStation = (ViewStub) this.findViewById(R.id.vs_topieces_vsPreStation);
			vsPreStation.inflate();
			etPreStation = bindEditText(R.id.et_pre_station_etPreStation, null, null);
			btnSelPreStation = bindButton(R.id.btn_pre_station_btnSelStation);
			etPreStation.setInputType(InputType.TYPE_CLASS_NUMBER);
		}

	}

	@Override
	protected void initListView() {

		lvx = (ListViewEx) this.findViewById(R.id.lv_topieces_lvBarcodeList);
		if (GlobalParas.getGlobalParas().getProgramRole() == EProgramRole.station) {
			lvx.inital(R.layout.list_item_two, new String[] { barcodeKey, weightKey },
					new int[] { R.id.tv_list_item_two_tvhead1, R.id.tv_list_item_two_tvhead2 });
		} else if (GlobalParas.getGlobalParas().getProgramRole() == EProgramRole.center) {
			lvx.inital(R.layout.list_item_three, new String[] { barcodeKey, weightKey, itemCategoryKey },
					new int[] { R.id.tv_list_item_three_tvhead1, R.id.tv_list_item_three_tvhead2,
							R.id.tv_list_item_three_tvhead3 });
		} else if (GlobalParas.getGlobalParas().getProgramRole() == EProgramRole.air) {
			lvx.inital(R.layout.list_item_four, new String[] { barcodeKey, weightKey, nextStatoinKey, itemCategoryKey },
					new int[] { R.id.tv_list_item_four_tvhead1, R.id.tv_list_item_four_tvhead2,
							R.id.tv_list_item_four_tvhead3, R.id.tv_list_item_four_tvhead4 });
		}
	}

	@Override
	protected void initializeValues() {

		super.initializeValues();
		stationValidate = new StationValidate(this);
		expressType = initValue;
		openBluetooth();

	}

	@Override
	protected void showWeight(String text) {

		tvWeight.setText(text);
		super.showWeight(text);
	}

	@Override
	protected void uiOnClick(View v) {

		super.uiOnClick(v);
		switch (v.getId()) {
		case R.id.btn_pre_station_btnSelStation:
			openSlecotr(EDownType.Station);
			break;
		case R.id.btn_barcode_btnAdd:
			if (!DATE.guard_time(getApplicationContext())) {
				return;
			}
			addRecord();
			break;
		}
	}

	@Override
	protected void setHeadTitle() {

		TextView tvHead1 = null;
		TextView tvHead2 = null;
		TextView tvHead3 = null;
		TextView tvHead4 = null;
		if (GlobalParas.getGlobalParas().getProgramRole() == EProgramRole.station) {
			vsListHead = (ViewStub) this.findViewById(R.id.vs_topieces_vsHead2);
			vsListHead.inflate();
			tvHead1 = (TextView) this.findViewById(R.id.tv_list_head_two_tvhead1);
			tvHead2 = (TextView) this.findViewById(R.id.tv_list_head_two_tvhead2);
			tvHead1.setText("单号");
			tvHead2.setText("重量");
			// 设置布局宽度
			controlUtil.setControlLayoutWidth(tvHead1, 150, this);
			controlUtil.setControlLayoutWidth(tvHead2, 139, this);
		} else if (GlobalParas.getGlobalParas().getProgramRole() == EProgramRole.center) {
			vsListHead = (ViewStub) this.findViewById(R.id.vs_topieces_vsHead3);
			vsListHead.inflate();
			tvHead1 = (TextView) this.findViewById(R.id.tv_list_head_three_tvhead1);
			tvHead2 = (TextView) this.findViewById(R.id.tv_list_head_three_tvhead2);
			tvHead3 = (TextView) this.findViewById(R.id.tv_list_head_three_tvhead3);
			tvHead1.setText("单号");
			tvHead2.setText("重量");
			tvHead3.setText("物品类别");
			// 设置布局宽度
			controlUtil.setControlLayoutWidth(tvHead1, 130, this);
			controlUtil.setControlLayoutWidth(tvHead3, 95, this);
		} else if (GlobalParas.getGlobalParas().getProgramRole() == EProgramRole.air) {
			vsListHead = (ViewStub) this.findViewById(R.id.vs_topieces_vsHead4);
			vsListHead.inflate();
			tvHead1 = (TextView) this.findViewById(R.id.tv_list_head_four_tvhead1);
			tvHead2 = (TextView) this.findViewById(R.id.tv_list_head_four_tvhead2);
			tvHead3 = (TextView) this.findViewById(R.id.tv_list_head_four_tvhead3);
			tvHead4 = (TextView) this.findViewById(R.id.tv_list_head_four_tvhead4);
			tvHead1.setText("单号");
			tvHead2.setText("重量");
			tvHead3.setText("上一站");
			tvHead4.setText("物品类别");
		}
	}

	@Override
	protected void initScanCode() {

		scanType = EScanType.DJ;
		uploadType = EUploadType.scanData;
	}

	@SuppressWarnings("static-access")
	@Override
	protected void addRecord() {

		// if (GlobalParas.getGlobalParas().getStationName().equals("中转t航空测试"))
		// {
		if (etPreStation != null) {
			String preStation = etPreStation.getText().toString().trim();
			stationValidate.showName(etPreStation);
			if (!strUtil.isNullOrEmpty(preStation)) {
				if (!stationValidate.vlidateInputData(etPreStation)) {
					PromptUtils.getInstance().showAlertDialogHasFeel(TopiecesActivity.this, "站点名异常！", null,
							EVoiceType.fail, 0);
					return;
				}
			} else {
				PromptUtils.getInstance().showAlertDialogHasFeel(TopiecesActivity.this, "站点名异常！", null, EVoiceType.fail,
						0);
				return;
			}
		}
		// }
		etBarcode.setText(etBarcode.getText().toString().trim());
		boolean flag = new math().CODE1(etBarcode.getText().toString().trim());
		if (!flag) {
			PromptUtils.getInstance().showToastHasFeel("非法条码", this, EVoiceType.fail, 0);
			etBarcode.setText("");
			return;
		}

		String barcode = etBarcode.getText().toString().trim();
		int existsBarcode = lvx.isExists(barcode, barcodeKey);//获取listview列表单号位置
		if (existsBarcode>=0) {//判断单号是否在列表已经存在
			PromptUtils.getInstance().showToastHasFeel("单号：" + barcode + "已扫描00！", this, EVoiceType.fail, 0);
			return;
		}
		String weightStr = "";
		if (tvWeight != null) {
			weightStr = tvWeight.getText().toString().trim();
		}
		EGoodsType goodsType = EGoodsType.非货样;
		if (rdoGoods != null && rdoGoods.isChecked()) {
			goodsType = EGoodsType.货样;
		}
		String preStationNo = "";
		String preStationName = "";
		if (etPreStation != null) {
			preStationName = etPreStation.getText().toString().trim();
			if (!strUtil.isNullOrEmpty(preStationName)) {
				preStationNo = etPreStation.getTag().toString().trim();
			}
		}
		String weight = "";
		if (AppContext.getAppContext().getBluetoothIsOpen()) {
			if (rdoGoods != null && rdoGoods.isChecked()) {
				// 货样
				if (strUtil.isNullOrEmpty(weightStr) || weightStr.equals("0kg") || weightStr.startsWith("-")) {
					PromptUtils.getInstance().showAlertDialogHasFeel(TopiecesActivity.this,
							"电子秤显示重量异常，为0或负数或无数据，请检查电子秤！", null, EVoiceType.fail, 0);
					return;
				}
			} else if (rdoGoods != null && !rdoGoods.isChecked()) {
				// 非货样
				if (strUtil.isNullOrEmpty(weightStr) || weightStr.startsWith("-")) {
					weightStr = "0.2kg";
				} else {
					double t = Double.parseDouble(weightStr.substring(0, weightStr.length() - 2));
					if (t < 0.2) {
						weightStr = "0.2kg";
					}
				}
			} else {
				if (strUtil.isNullOrEmpty(weightStr) || weightStr.equals("0kg") || weightStr.startsWith("-")) {
					PromptUtils.getInstance().showAlertDialogHasFeel(TopiecesActivity.this,
							"电子秤显示重量异常，为0或负数或无数据，请检查电子秤！", null, EVoiceType.fail, 0);
					return;
				}
			}
			weight = weightStr.substring(0, weightStr.length() - 2);
		}
		// =======================保存数据库
		ScanDataModel scanDataModel = new ScanDataModel();
		scanDataModel.setScanCode(scanType.value());
		scanDataModel.setBarcode(barcode);
		if (rdoGoods != null) {
			scanDataModel.setSampleType(goodsType.value());
		}
		scanDataModel.setPreStationCode(preStationNo);
		scanDataModel.setExpressType(expressType);
		scanDataModel.setScanUser(GlobalParas.getGlobalParas().getUserNo());
		scanDataModel.setWeight(weight);
		scanDataModel.setSiteProperties(String.valueOf(siteProperties.value()));

		// 添加到插入队列中
		AddScanDataThread.getIntance(TopiecesActivity.this).add(scanDataModel);

		// 更新界面
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(barcodeKey, barcode);
		map.put(weightKey, weightStr);
		if (rdoGoods != null) {
			map.put(itemCategoryKey, goodsType.toString().trim());
		}
		if (etPreStation != null) {
			map.put(nextStatoinKey, preStationName);
		}
		lvx.add(map);
		scanCount++;
		updateView();
		if (AppContext.getAppContext().getIsLockScreen()) {
			lockScreen();// 锁屏
		}
	}

	@Override
	protected boolean validateInput() {

		if (etPreStation != null) {
			String preStation = etPreStation.getText().toString().trim();
			if (!strUtil.isNullOrEmpty(preStation)) {
				if (!stationValidate.vlidateInputData(etPreStation)) {
					return false;
				}
			}
		}
		if (!validateScanBarcode(etBarcode)) {
			return false;
		}
		return true;
	}

	@Override
	protected void setSelResult(EDownType selType, String res_1, String res_2, String res_3, String res_4) {

		if (selType == EDownType.Station) {
			etPreStation.setText(res_2);
			etPreStation.setTag(res_1);

			controlUtil.setEditVeiwFocus(etBarcode);
		}
	}

	@Override
	public void editFocusChange(View v, boolean hasFocus) {

		switch (v.getId()) {
		case R.id.et_barcode_etBarcode:
			if (hasFocus) {
				if (etPreStation != null) {// 这个地方如果改为获取字符串长度大于零的话，在900000网点的到件会直接挂掉（在900001网点到件正常）
					stationValidate.showName(etPreStation);
					String preStation = etPreStation.getText().toString().trim();
					if (!strUtil.isNullOrEmpty(preStation)) {
						if (!stationValidate.vlidateInputData(etPreStation)) {
							return;
						}
					}
				}
			}
			break;
		case R.id.et_pre_station_etPreStation:
			if (hasFocus) {
				stationValidate.restoreNo(etPreStation);
			}
			break;
		}
	}

}
