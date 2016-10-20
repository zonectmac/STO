package com.cneop.stoExpress.activity.scan;

import java.util.HashMap;
import java.util.Map;

import com.cneop.Date.DATE;
import com.cneop.stoExpress.activity.ShowImageActivity;
import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.common.Enums.EBarcodeType;
import com.cneop.stoExpress.common.Enums.EDownType;
import com.cneop.stoExpress.common.Enums.EScanType;
import com.cneop.stoExpress.common.Enums.EUploadType;
import com.cneop.stoExpress.common.Enums.EVoiceType;
import com.cneop.stoExpress.common.GlobalParas;
import com.cneop.stoExpress.dataValidate.AbnormalValidate;
import com.cneop.stoExpress.datacenter.AddScanDataThread;
import com.cneop.stoExpress.model.ScanDataModel;
import com.cneop.stoExpress.util.ControlUtil;
import com.cneop.util.AppContext;
import com.cneop.util.ImageUtil;
import com.cneop.util.PromptUtils;
import com.cneop.util.activity.CameraActivity;
import com.cneop.util.controls.ListViewEx;
import com.cneop.util.file.FileUtil;
import com.cneop.util.scan.ScanManager;
import com.math.math;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ExceptionActivity extends ScanBaseActivity {

	EditText etAbnormal;
	EditText etAbnromalDesc;
	Button btnSelAbnormal;
	Button btnPhoto;
	ImageView ivPicture;
	Bitmap mBitmap;
	AbnormalValidate abnormalValidate;
	private String strPreScanedBar = "";// 上一次扫描的单号

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_exception);
		setTitle("问题件");
		super.onCreate(savedInstanceState);
		super.scannerPower = true;
		ScanManager.getInstance().getScanner().setScanResultHandler(handleScanData);
	}

	/**
	 * 扫描数据
	 */
	@Override
	protected void setScanData(String barcode) {

		PromptUtils.getInstance().closeAlertDialog();
		int length = barcode.trim().length();
		if (length <= 4) {
			controlUtil.setEditVeiwFocus(etAbnormal);
			etAbnormal.setText(barcode);
			controlUtil.setEditVeiwFocus(etBarcode);
			if (etAbnromalDesc.getText().toString().length() <= 0)
				etAbnormal.setText("");
			return;
		}
		if (etAbnormal.getText().toString().trim().length() <= 0) {
			PromptUtils.getInstance().showToastHasFeel("类型不能为空", this, EVoiceType.fail, 0);
			etAbnormal.requestFocus();
		} else {
			if (etBarcode != null) {
				etBarcode.setText(barcode);
				boolean flag = new math().CODE1(barcode);
				if (flag) {
					addRecord();
					etBarcode.requestFocus();
				} else {
					PromptUtils.getInstance().showToastHasFeel("非法条码", this, EVoiceType.fail, 0);
					etBarcode.setText("");
					etBarcode.requestFocus();
				}
			}
		}

	}

	@Override
	protected void initializeComponent() {

		super.initializeComponent();
		etAbnormal = bindEditText(R.id.et_exception_etType, null, null);
		etAbnormal.setInputType(InputType.TYPE_CLASS_NUMBER);
		etAbnromalDesc = bindEditText(R.id.et_exceptin_etReason, null, null);
		etBarcode = bindEditText(R.id.et_exception_etBarcode, null, null);
		btnSelAbnormal = bindButton(R.id.btn_exception_btnSel);
		btnPhoto = bindButton(R.id.btn_photograph_btnPhoto);
		btnAdd = bindButton(R.id.btn_exception_btnAdd);
		ivPicture = (ImageView) this.findViewById(R.id.iv_photograph_ivPic);
		setImageEmpty();
		ivPicture.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (!strUtil.isNullOrEmpty(newPath)) {
					Intent intent = new Intent(ExceptionActivity.this, ShowImageActivity.class);
					intent.putExtra(ScanBaseActivity.ImagePath, newPath);
					startActivity(intent);
				}
			}
		});
	}

	@Override
	protected void initializeValues() {

		super.initializeValues();
		abnormalValidate = new AbnormalValidate(ExceptionActivity.this, etAbnromalDesc);
	}

	@Override
	protected void HandleScanData(String barcode) {

		super.HandleScanData(barcode);
		etBarcode.setText(barcode);

		// addRecord();
	};

	@Override
	protected void uiOnClick(View v) {

		super.uiOnClick(v);
		switch (v.getId()) {
		case R.id.btn_exception_btnSel:
			openSlecotr(EDownType.Abnormal);
			break;
		case R.id.btn_exception_btnAdd:
			if (!DATE.guard_time(getApplicationContext())) {
				return;
			}
			if (etAbnormal.getText().toString().trim().length() <= 0) {
				PromptUtils.getInstance().showToastHasFeel("类型不能为空", this, EVoiceType.fail, 0);
				etAbnormal.requestFocus();
			} else {
				if (etBarcode != null) {
					etBarcode.setText(etBarcode.getText().toString().trim());
					boolean flag = new math().CODE1(etBarcode.getText().toString().trim());
					if (flag) {
						addRecord();
						etBarcode.requestFocus();
					} else {
						PromptUtils.getInstance().showToastHasFeel("非法条码", this, EVoiceType.fail, 0);
						etBarcode.setText("");
						etBarcode.requestFocus();
					}
				}
			}
			break;
		case R.id.btn_photograph_btnPhoto:
			// 打开摄像头
			openCamera();
			break;
		default:
			break;
		}
	}

	private void openCamera() {

		if (new com.cneop.util.StrUtil().isNullOrEmpty(strPreScanedBar)) {
			PromptUtils.getInstance().showToast("请先扫描单号再拍照", ExceptionActivity.this);
			return;
		}

		String newPath = GlobalParas.getGlobalParas().getProblemUnUploadPath() + strPreScanedBar + ".jpg";
		Intent intent = new Intent(ExceptionActivity.this, CameraActivity.class);
		intent.putExtra("imagePath", newPath);
		startActivityForResult(intent, CameraActivity.g_i_cameraRequest);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == CameraActivity.g_i_cameraRequest) {
			if (resultCode == CameraActivity.RESULT_OK) {
				Map<String, Object> map = lvx.GetValue(0);
				if (map != null) {
					// 保存图片
					newPath = GlobalParas.getGlobalParas().getProblemUnUploadPath() + strPreScanedBar + ".jpg";
					String barcode = map.get(barcodeKey).toString();
					String destPath = GlobalParas.getGlobalParas().getProblemUnUploadPath() + barcode
							+ GlobalParas.getGlobalParas().getImageSuffix();
					if (FileUtil.isExist(destPath, false) == 0) {
						// 更新图片数量
						brocastUtil.sendUnUploadCountChange(1, EUploadType.pic);
					}
					FileUtil.copyFile(GlobalParas.getGlobalParas().getPhotoPath(), destPath);

					// 显示缩略图
					System.out.println("==================path \t" + GlobalParas.getGlobalParas().getPhotoPath());
					mBitmap = ImageUtil.decodeSampledBitmapFromPath(destPath, 115, 110);
					ivPicture.setImageBitmap(mBitmap);
				}
			}
		}
	}

	@Override
	protected void refreshImgCtrl(String barcode) {

		if (strPreScanedBar.equals(barcode)) {
			setImageEmpty();

		}

	};

	private void setImageEmpty() {

		ivPicture.setImageResource(R.drawable.no_pic);
		newPath = "";
		strPreScanedBar = "";
	}

	@Override
	protected void onDestroy() {

		if (bb != null) {
			bb.recycle();
		}
		super.onDestroy();
	}

	private Bitmap bb;
	String newPath = "";

	@Override
	protected void setSelResult(EDownType selType, String res_1, String res_2, String res_3, String res_4) {

		if (selType == EDownType.Abnormal) {
			etAbnormal.setText(res_1);
			etAbnormal.setTag(res_1);
			etAbnromalDesc.setText(res_2);

			ControlUtil.setEditVeiwFocus(this.etBarcode);
		}
	}

	@Override
	protected void initListView() {

		lvx = (ListViewEx) this.findViewById(R.id.lv_exception_lvBarcode);

		lvx.inital(R.layout.list_item_two_a, new String[] { barcodeKey, abnormalKey },
				new int[] { R.id.tv_list_item_two_a_tvhead1, R.id.tv_list_item_two_a_tvhead2 });
	}

	@Override
	protected void setHeadTitle() {

		TextView tvHead1 = (TextView) this.findViewById(R.id.tv_list_head_two_tvhead1);
		TextView tvHead2 = (TextView) this.findViewById(R.id.tv_list_head_two_tvhead2);
		tvHead1.setText("单号");
		tvHead2.setText("原因");
		controlUtil.setControlLayoutWidth(tvHead1, 130, ExceptionActivity.this);
	}

	@Override
	protected void initScanCode() {

		scanType = EScanType.YN;
		uploadType = EUploadType.scanData;
	}

	@Override
	protected void addRecord() {

		controlUtil.setEditVeiwFocus(etBarcode);
		if (!validateInput()) {
			return;
		}
		String abnormalNo = etAbnormal.getText().toString().trim();
		String abnromalDesc = etAbnromalDesc.getText().toString().trim();
		String barcode = etBarcode.getText().toString().trim();
		int existsBarcode = lvx.isExists(barcode, barcodeKey);//获取listview列表单号位置
		if (existsBarcode>=0) {//判断单号是否在列表已经存在
		 PromptUtils.getInstance().showToastHasFeel("单号：" + barcode + "已扫描！", this, EVoiceType.fail, 0);
		 return;
		}
		// 保存数据库
		ScanDataModel scanDataModel = new ScanDataModel();
		scanDataModel.setScanCode(scanType.value());
		scanDataModel.setAbnormalCode(abnormalNo);
		scanDataModel.setBarcode(barcode);
		scanDataModel.setAbnormalDesc(etAbnromalDesc.getText().toString().trim());
		scanDataModel.setScanUser(GlobalParas.getGlobalParas().getUserNo());
		scanDataModel.setSiteProperties(String.valueOf(siteProperties.value()));
		// 添加到插入队列中
		AddScanDataThread.getIntance(ExceptionActivity.this).add(scanDataModel);

		// 更新界面
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(barcodeKey, barcode);
		map.put(abnormalKey, abnromalDesc);
		lvx.add(map);
		scanCount++;
		if (mBitmap != null) {
			mBitmap = null;
			ivPicture.setImageResource(R.drawable.no_pic);
		}
		updateView();
		strPreScanedBar = barcode;
		if (AppContext.getAppContext().getIsLockScreen()) {
			lockScreen();// 锁屏
		}
		openCamera();// 打开拍照
	}

	@Override
	protected boolean validateInput() {

		if (!abnormalValidate.vlidateInputData(etAbnormal)) {
			return false;
		}
		if (!validateBarcode(etBarcode, EBarcodeType.barcode, barcodeValidateErrorTip)) {
			return false;
		}
		return true;
	}

	@Override
	public void editFocusChange(View v, boolean hasFocus) {
		switch (v.getId()) {
		case R.id.et_exception_etType:
			if (hasFocus) {
				abnormalValidate.restoreNo(etAbnormal);
			}
			break;
		case R.id.et_exceptin_etReason:
		case R.id.et_exception_etBarcode:
			if (hasFocus) {
				abnormalValidate.showName(etAbnormal);
				if (!abnormalValidate.vlidateInputData(etAbnormal)) {
					return;
				}
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected boolean delImage(String barcode) {

		boolean flag = false;
		String path = GlobalParas.getGlobalParas().getProblemUnUploadPath() + barcode
				+ GlobalParas.getGlobalParas().getImageSuffix();
		flag = FileUtil.delFile(path);
		setImageEmpty();
		return flag;
	}

}
