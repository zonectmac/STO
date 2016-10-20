package com.cneop.stoExpress.activity.scan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cneop.Date.DATE;
import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.activity.SignerManagerActivity;
import com.cneop.stoExpress.common.Enums.EBarcodeType;
import com.cneop.stoExpress.common.Enums.EScanType;
import com.cneop.stoExpress.common.Enums.EUploadType;
import com.cneop.stoExpress.common.Enums.EVoiceType;
import com.cneop.stoExpress.common.GlobalParas;
import com.cneop.stoExpress.dao.BaseDao;
import com.cneop.stoExpress.dao.SignerService;
import com.cneop.stoExpress.datacenter.AddScanDataThread;
import com.cneop.stoExpress.model.ScanDataModel;
import com.cneop.stoExpress.model.Signer;
import com.cneop.stoExpress.util.ScreenUtil;
import com.cneop.util.AppContext;
import com.cneop.util.PromptUtils;
import com.cneop.util.StrUtil;
import com.cneop.util.controls.ListViewEx;
import com.cneop.util.scan.ScanManager;
import com.math.math;

public class SignatureActivity extends ScanBaseActivity {
	public static String flag;
	BaseDao baseDao;
	Button btnsignerMa;
	TextView tvsigner;
	EditText etsigner;
	Spinner spsigner;
	ArrayAdapter<String> adapter;
	List<String> listSource;
	String type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_signature_scan);
		setTitle("签收扫描");
		super.scannerPower = true;
		super.onCreate(savedInstanceState);
		ScanManager.getInstance().getScanner().setScanResultHandler(handleScanData);
		flag = "STO";
	}

	/**
	 * 扫描数据
	 */
	@SuppressWarnings("static-access")
	@Override
	protected void setScanData(String barcode) {
		PromptUtils.getInstance().closeAlertDialog();
		if (etBarcode != null) {
			etBarcode.setText(barcode);
			boolean flag = new math().CODE1(etBarcode.getText().toString().trim());
			if (flag) {
				addRecord();
			} else {
				PromptUtils.getInstance().showToastHasFeel("非法条码", this, EVoiceType.fail, 0);
				etBarcode.setText("");
				etBarcode.requestFocus();
			}
		}
	}

	/*
	 * 计算字节数
	 */
	public static int count(String str) {
		if (str == null || str.length() == 0) {
			return 0;
		}
		int count = 0;
		char[] chs = str.toCharArray();
		for (int i = 0; i < chs.length; i++) {
			count += (chs[i] > 0xff) ? 2 : 1;
		}
		return count;
	}

	@Override
	protected void initializeComponent() {

		super.initializeComponent();

		spsigner = (Spinner) findViewById(R.id.sp_sign_etSign);
		btnsignerMa = bindButton(R.id.btn_signer_btnMa);
		btnsignerMa.setText("编辑");

		adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.sign_spinner, getSignType());
		adapter.setDropDownViewResource(R.layout.drop_down_item);
		spsigner.setAdapter(adapter);

		tvsigner = (TextView) findViewById(R.id.tv_carlotnumber_tvCarLotNumber);
		tvsigner.setText("签收人");
		tvsigner.setSingleLine(false);
		etsigner = super.bindEditText(R.id.et_carlotnumber_etCarLotNumber, null);
		etsigner.setInputType(InputType.TYPE_CLASS_TEXT);
		etsigner.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if (count(etsigner.getText().toString().trim()) > 14) {
					Toast.makeText(getApplicationContext(), "超过限制长度", 1).show();
					etsigner.requestFocus();
					etsigner.setText("");
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
		etsigner.setFilters(new InputFilter[] { new InputFilter.LengthFilter(14) });
		etBarcode = bindEditText(R.id.et_barcode_etBarcode, null, null);
		btnAdd = bindButton(R.id.btn_barcode_btnAdd);
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		type = bundle.getString("type");
	}

	@Override
	protected void initListView() {

		lvx = (ListViewEx) findViewById(R.id.lv_signphoto_lvBarcodeList);
		lvx.inital(R.layout.list_item_three, new String[] { barcodeKey, sigerKey, signTypeKey }, new int[] { R.id.tv_list_item_three_tvhead1, R.id.tv_list_item_three_tvhead2, R.id.tv_list_item_three_tvhead3 });
	}

	private List<String> getSignType() {
		Signer signerModel = new Signer();
		SignerService baseDao = new SignerService(getApplicationContext());
		List<Object> listObj = baseDao.getListObj(null, null, signerModel);
		listSource = new ArrayList<String>();
		for (Object object : listObj) {
			signerModel = (Signer) object;
			String str = signerModel.getSignerName();
			listSource.add(str);
		}
		return listSource;
	}

	@Override
	protected void setHeadTitle() {

		TextView tvHead1 = (TextView) this.findViewById(R.id.tv_list_head_three_tvhead1);
		TextView tvHead2 = (TextView) this.findViewById(R.id.tv_list_head_three_tvhead2);
		TextView tvHead3 = (TextView) this.findViewById(R.id.tv_list_head_three_tvhead3);
		tvHead1.setText("单号");
		tvHead2.setText("签收人 ");
		tvHead3.setText("签收类型");
		tvHead1.setLayoutParams(new LayoutParams(ScreenUtil.dip2px(SignatureActivity.this, 130), LayoutParams.WRAP_CONTENT));
	}

	@Override
	protected void HandleScanData(String barcode) {
		super.HandleScanData(barcode);
		etBarcode.setText(barcode);
		addRecord();
	};

	@Override
	protected void initScanCode() {

		scanType = EScanType.QS;
		uploadType = EUploadType.scanData;
	}

	@Override
	protected void uiOnClick(View v) {

		super.uiOnClick(v);
		switch (v.getId()) {
		case R.id.btn_signer_btnMa:
			openMana();
			break;
		case R.id.btn_barcode_btnAdd:

			if (!DATE.guard_time(getApplicationContext())) {
				return;
			}
			if (etBarcode != null) {

				if (etsigner.getText().toString().trim().length() <= 0) {
					Toast.makeText(getApplicationContext(), "签收人不能为空", 1).show();
					etsigner.requestFocus();
				} else {
					etBarcode.setText(etBarcode.getText().toString().trim());
					boolean flag = new math().CODE1(etBarcode.getText().toString().trim());
					if (flag) {
						addRecord();
					} else {
						Toast.makeText(getApplicationContext(), "非法条码", Toast.LENGTH_SHORT).show();
						etBarcode.setText("");
						etBarcode.requestFocus();
					}
				}
			}
			break;

		}
	}

	private void openMana() {
		Intent intent = new Intent(SignatureActivity.this, SignerManagerActivity.class);
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0) {
			initializeComponent();
			initializeValues();

		}
	}

	@Override
	protected void addRecord() {

		controlUtil.setEditVeiwFocus(etBarcode);
		String barcode = etBarcode.getText().toString().trim();
		int existsBarcode = lvx.isExists(barcode, barcodeKey);//获取listview列表单号位置
		if (existsBarcode>=0) {//判断单号是否在列表已经存在
			PromptUtils.getInstance().showToastHasFeel("单号：" + barcode + "已扫描00！", this, EVoiceType.fail, 0);
			return;
		}
		String signer = etsigner.getText().toString().trim();
		String signtype = spsigner.getSelectedItem().toString().trim();

		if (strUtil.isNullOrEmpty(signer)) {
			signer = signtype;
		}
		signer = StrUtil.FilterSpecial(signer);
		if (!validateInput()) {
			return;
		}

		ScanDataModel scanDataModel = new ScanDataModel();
		scanDataModel.setScanCode(scanType.value());
		scanDataModel.setBarcode(barcode);
		scanDataModel.setScanUser(GlobalParas.getGlobalParas().getUserNo());
		scanDataModel.setSigner(signer);
		scanDataModel.setSignTypeCode(signtype);
		scanDataModel.setSiteProperties(String.valueOf(siteProperties.value()));
		// 添加到插入队列中
		AddScanDataThread.getIntance(SignatureActivity.this).add(scanDataModel);

		// 更新界面
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(barcodeKey, barcode);
		map.put(sigerKey, signer);
		map.put(signTypeKey, signtype);
		lvx.add(map);

		scanCount++;
		updateView();

		if (type.equals("signsiger")) {
			etsigner.setText("");
		}
		if (AppContext.getAppContext().getIsLockScreen()) {
			lockScreen();// 锁屏
		}
	}

	@Override
	protected boolean validateInput() {

		if (!validateBarcode(etBarcode, EBarcodeType.barcode, barcodeValidateErrorTip)) {
			return false;
		}
		if (!validateSignerInput()) {
			return false;
		}
		return true;
	}

	private boolean validateSignerInput() {
		boolean flag = true;
		String signer = etsigner.getText().toString().trim();
		if (!strUtil.isNullOrEmpty(signer)) {
			if (signer.length() > 14 || ContainsSpecialChar(signer)) {
				PromptUtils.getInstance().showToast("签收人的长度不能超过14位", SignatureActivity.this);
				flag = false;
			}
		}
		return flag;
	}

	public boolean ContainsSpecialChar(String userInput) {
		boolean flag = false;
		char[] specialChar = { '<', '>', '&', '=', '+', '?', '？' };
		for (int i = 0; i < specialChar.length; i++) {
			if (userInput.indexOf(specialChar[i]) >= 0) {
				flag = true;
			}
		}
		if (flag) {
			controlUtil.setEditVeiwFocus(etsigner);
			PromptUtils.getInstance().showToast("添加失败，包含特殊字符[<,>,?,&]！", SignatureActivity.this);
		}
		return flag;
	}
}
