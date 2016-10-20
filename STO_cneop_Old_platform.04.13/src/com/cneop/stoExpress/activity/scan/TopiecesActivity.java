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
		setTitle("����");
		super.scannerPower = true;
		super.onCreate(savedInstanceState);
		ScanManager.getInstance().getScanner().setScanResultHandler(handleScanData);
	}

	/**
	 * ɨ������
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
					PromptUtils.getInstance().showToastHasFeel("�Ƿ�����", this, EVoiceType.fail, 0);
					etBarcode.setText("");
					return;
				}
				addRecord();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			if (!new math().CODE1(barcode)) {
				PromptUtils.getInstance().showToastHasFeel("�Ƿ�����", this, EVoiceType.fail, 0);
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

		// ���������ӳƲ���ʾ����
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
			tvHead1.setText("����");
			tvHead2.setText("����");
			// ���ò��ֿ��
			controlUtil.setControlLayoutWidth(tvHead1, 150, this);
			controlUtil.setControlLayoutWidth(tvHead2, 139, this);
		} else if (GlobalParas.getGlobalParas().getProgramRole() == EProgramRole.center) {
			vsListHead = (ViewStub) this.findViewById(R.id.vs_topieces_vsHead3);
			vsListHead.inflate();
			tvHead1 = (TextView) this.findViewById(R.id.tv_list_head_three_tvhead1);
			tvHead2 = (TextView) this.findViewById(R.id.tv_list_head_three_tvhead2);
			tvHead3 = (TextView) this.findViewById(R.id.tv_list_head_three_tvhead3);
			tvHead1.setText("����");
			tvHead2.setText("����");
			tvHead3.setText("��Ʒ���");
			// ���ò��ֿ��
			controlUtil.setControlLayoutWidth(tvHead1, 130, this);
			controlUtil.setControlLayoutWidth(tvHead3, 95, this);
		} else if (GlobalParas.getGlobalParas().getProgramRole() == EProgramRole.air) {
			vsListHead = (ViewStub) this.findViewById(R.id.vs_topieces_vsHead4);
			vsListHead.inflate();
			tvHead1 = (TextView) this.findViewById(R.id.tv_list_head_four_tvhead1);
			tvHead2 = (TextView) this.findViewById(R.id.tv_list_head_four_tvhead2);
			tvHead3 = (TextView) this.findViewById(R.id.tv_list_head_four_tvhead3);
			tvHead4 = (TextView) this.findViewById(R.id.tv_list_head_four_tvhead4);
			tvHead1.setText("����");
			tvHead2.setText("����");
			tvHead3.setText("��һվ");
			tvHead4.setText("��Ʒ���");
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

		// if (GlobalParas.getGlobalParas().getStationName().equals("��תt���ղ���"))
		// {
		if (etPreStation != null) {
			String preStation = etPreStation.getText().toString().trim();
			stationValidate.showName(etPreStation);
			if (!strUtil.isNullOrEmpty(preStation)) {
				if (!stationValidate.vlidateInputData(etPreStation)) {
					PromptUtils.getInstance().showAlertDialogHasFeel(TopiecesActivity.this, "վ�����쳣��", null,
							EVoiceType.fail, 0);
					return;
				}
			} else {
				PromptUtils.getInstance().showAlertDialogHasFeel(TopiecesActivity.this, "վ�����쳣��", null, EVoiceType.fail,
						0);
				return;
			}
		}
		// }
		etBarcode.setText(etBarcode.getText().toString().trim());
		boolean flag = new math().CODE1(etBarcode.getText().toString().trim());
		if (!flag) {
			PromptUtils.getInstance().showToastHasFeel("�Ƿ�����", this, EVoiceType.fail, 0);
			etBarcode.setText("");
			return;
		}

		String barcode = etBarcode.getText().toString().trim();
		int existsBarcode = lvx.isExists(barcode, barcodeKey);//��ȡlistview�б���λ��
		if (existsBarcode>=0) {//�жϵ����Ƿ����б��Ѿ�����
			PromptUtils.getInstance().showToastHasFeel("���ţ�" + barcode + "��ɨ��00��", this, EVoiceType.fail, 0);
			return;
		}
		String weightStr = "";
		if (tvWeight != null) {
			weightStr = tvWeight.getText().toString().trim();
		}
		EGoodsType goodsType = EGoodsType.�ǻ���;
		if (rdoGoods != null && rdoGoods.isChecked()) {
			goodsType = EGoodsType.����;
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
				// ����
				if (strUtil.isNullOrEmpty(weightStr) || weightStr.equals("0kg") || weightStr.startsWith("-")) {
					PromptUtils.getInstance().showAlertDialogHasFeel(TopiecesActivity.this,
							"���ӳ���ʾ�����쳣��Ϊ0�����������ݣ�������ӳӣ�", null, EVoiceType.fail, 0);
					return;
				}
			} else if (rdoGoods != null && !rdoGoods.isChecked()) {
				// �ǻ���
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
							"���ӳ���ʾ�����쳣��Ϊ0�����������ݣ�������ӳӣ�", null, EVoiceType.fail, 0);
					return;
				}
			}
			weight = weightStr.substring(0, weightStr.length() - 2);
		}
		// =======================�������ݿ�
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

		// ��ӵ����������
		AddScanDataThread.getIntance(TopiecesActivity.this).add(scanDataModel);

		// ���½���
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
			lockScreen();// ����
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
				if (etPreStation != null) {// ����ط������Ϊ��ȡ�ַ������ȴ�����Ļ�����900000����ĵ�����ֱ�ӹҵ�����900001���㵽��������
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
