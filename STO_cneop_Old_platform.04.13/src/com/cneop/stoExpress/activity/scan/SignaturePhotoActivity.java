package com.cneop.stoExpress.activity.scan;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.cneop.Date.DATE;
import com.cneop.stoExpress.activity.ShowImageActivity;
import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.common.Enums.EBarcodeType;
import com.cneop.stoExpress.common.Enums.EScanType;
import com.cneop.stoExpress.common.Enums.EUploadType;
import com.cneop.stoExpress.common.Enums.EVoiceType;
import com.cneop.stoExpress.common.GlobalParas;
import com.cneop.stoExpress.datacenter.AddScanDataThread;
import com.cneop.stoExpress.model.ScanDataModel;
import com.cneop.stoExpress.util.ScreenUtil;
import com.cneop.util.ImageUtil;
import com.cneop.util.PromptUtils;
import com.cneop.util.StrUtil;
import com.cneop.util.activity.CameraActivity;
import com.cneop.util.controls.ListViewEx;
import com.cneop.util.file.FileUtil;
import com.cneop.util.scan.ScanManager;
import com.math.math;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class SignaturePhotoActivity extends ScanBaseActivity {

	private EditText etSignType, etSigner;
	private Button btnPhoto;
	private Checkable cksign;
	private String strPreScanedBar = "";// ��һ��ɨ��ĵ���
	private ImageView ivPicture;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.activity_signaturephoto_scan);
		setTitle("ǩ��ɨ��");
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
		if (etBarcode != null) {
			etBarcode.setText(barcode);
			boolean flag = new math().CODE1(etBarcode.getText().toString().trim());
			if (flag) {
				addRecord();
			} else {
				PromptUtils.getInstance().showToastHasFeel("�Ƿ�����", this, EVoiceType.fail, 0);
				etBarcode.setText("");
				etBarcode.requestFocus();
			}

		}
	}

	@Override
	protected void initializeComponent() {

		super.initializeComponent();
		etSignType = bindEditText(R.id.et_sign_type_lly1, null, null);

		etSignType.setText("����ǩ��");

		etSigner = bindEditText(R.id.et_sign_type_lly2, null, null);
		btnPhoto = bindButton(R.id.btn_photograph_btnPhoto);

		cksign = (CheckBox) findViewById(R.id.ck_signphoto);

		etBarcode = bindEditText(R.id.et_barcode_etBarcode, null, null);
		btnAdd = bindButton(R.id.btn_barcode_btnAdd);
		ivPicture = (ImageView) this.findViewById(R.id.iv_photograph_ivPic);
		setImageEmpty();
		ivPicture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!strUtil.isNullOrEmpty(newPath)) {
					Intent intent = new Intent(SignaturePhotoActivity.this, ShowImageActivity.class);
					intent.putExtra(ScanBaseActivity.ImagePath, newPath);
					startActivity(intent);
				}
			}
		});
	}

	@Override
	protected void onDestroy() {

		if (bb != null) {
			bb.recycle();
		}
		super.onDestroy();
	}

	@Override
	protected void onOtherResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK && requestCode == CameraActivity.g_i_cameraRequest) {
			newPath = GlobalParas.getGlobalParas().getSignUnUploadPath() + "/" + strPreScanedBar + ".jpg";
			System.out.println("==========newpath   " + newPath);
			File fileTemp = new File(newPath);
			if (fileTemp.exists()) {
				// ��Ƭ����ͼ
				Bitmap bm = BitmapFactory.decodeFile(newPath);
				if (bm != null) {
					if (bb != null) {
						bb.recycle();
					}
					// releaseImage();
					bb = ThumbnailUtils.extractThumbnail(bm, 320, 240);
					// ��ͼ����ʾ��ImageView��ͼ��
					ivPicture.setImageBitmap(bb);
				}
				if (!bm.isRecycled()) {
					bm.recycle();
				}
			}

		}
	}

	private void setImageEmpty() {

		ivPicture.setImageResource(R.drawable.no_pic);
		newPath = "";
		strPreScanedBar = "";
	}

	@Override
	protected void refreshImgCtrl(String barcode) {

		if (strPreScanedBar.equals(barcode)) {
			setImageEmpty();
		}

	};

	String newPath = "";

	@Override
	protected void initListView() {

		lvx = (ListViewEx) findViewById(R.id.lv_signphoto_lvBarcodeList);
		lvx.inital(R.layout.list_item_three, new String[] { barcodeKey, sigerKey, signTypeKey }, new int[] {
				R.id.tv_list_item_three_tvhead1, R.id.tv_list_item_three_tvhead2, R.id.tv_list_item_three_tvhead3 });
	}

	@Override
	protected void setHeadTitle() {

		TextView tvHead1 = (TextView) this.findViewById(R.id.tv_list_head_three_tvhead1);
		TextView tvHead2 = (TextView) this.findViewById(R.id.tv_list_head_three_tvhead2);
		TextView tvHead3 = (TextView) this.findViewById(R.id.tv_list_head_three_tvhead3);
		tvHead1.setText("����");
		tvHead2.setText("ǩ���� ");
		tvHead3.setText("ǩ������");
		tvHead1.setLayoutParams(
				new LayoutParams(ScreenUtil.dip2px(SignaturePhotoActivity.this, 130), LayoutParams.WRAP_CONTENT));
	}

	@Override
	protected void initScanCode() {

		scanType = EScanType.QS;
		uploadType = EUploadType.scanData;
	}

	@Override
	protected void uiOnClick(View v) {

		super.uiOnClick(v);
		switch (v.getId()) {
		case R.id.btn_photograph_btnPhoto:
			openCamera();
			break;
		case R.id.btn_barcode_btnAdd:
			if (!DATE.guard_time(getApplicationContext())) {
				return;
			}
			if (etBarcode != null) {
				if (etSigner.getText().toString().trim().length() <= 0) {
					Toast.makeText(getApplicationContext(), "ǩ���˲���Ϊ��", 1).show();
					etSigner.requestFocus();
				} else {
					etBarcode.setText(etBarcode.getText().toString().trim());
					boolean flag = new math().CODE1(etBarcode.getText().toString().trim());
					if (flag) {
						addRecord();
					} else {
						Toast.makeText(getApplicationContext(), "�Ƿ�����", Toast.LENGTH_SHORT).show();
						etBarcode.setText("");
						etBarcode.requestFocus();
					}
				}

			}
			break;

		}
	}

	private void openCamera() {

		if (StrUtil.isNullOrEmpty(strPreScanedBar)) {
			PromptUtils.getInstance().showToast("����ɨ�赥��������", this);
			return;
		}
		// ScanManager.getInstance().setisDoingOtherOpt(true);
		String newPath = GlobalParas.getGlobalParas().getProblemUnUploadPath() + strPreScanedBar + ".jpg";
		Intent intent = new Intent(this, CameraActivity.class);
		intent.putExtra("imagePath", newPath);
		startActivityForResult(intent, CameraActivity.g_i_cameraRequest);
	}

	Bitmap mBitmap;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == CameraActivity.g_i_cameraRequest) {
			if (resultCode == CameraActivity.RESULT_OK) {
				Map<String, Object> map = lvx.GetValue(0);
				if (map != null) {
					// ����ͼƬ
					newPath = GlobalParas.getGlobalParas().getProblemUnUploadPath() + strPreScanedBar + ".jpg";
					String barcode = map.get(barcodeKey).toString();
					String destPath = GlobalParas.getGlobalParas().getProblemUnUploadPath() + barcode
							+ GlobalParas.getGlobalParas().getImageSuffix();
					if (FileUtil.isExist(destPath, false) == 0) {
						// ����ͼƬ����
						brocastUtil.sendUnUploadCountChange(1, EUploadType.pic);
					}
					FileUtil.copyFile(GlobalParas.getGlobalParas().getPhotoPath(), destPath);

					// ��ʾ����ͼ
					System.out.println("==================path \t" + GlobalParas.getGlobalParas().getPhotoPath());
					mBitmap = ImageUtil.decodeSampledBitmapFromPath(destPath, 115, 110);
					ivPicture.setImageBitmap(BitmapFactory.decodeFile(destPath));
				}
			}
		}
	}

	private Bitmap bb;

	@Override
	protected void addRecord() {

		controlUtil.setEditVeiwFocus(etBarcode);
		String barcode = etBarcode.getText().toString().trim();
		int existsBarcode = lvx.isExists(barcode, barcodeKey);//��ȡlistview�б���λ��
		if (existsBarcode>=0) {//�жϵ����Ƿ����б��Ѿ�����
			PromptUtils.getInstance().showToastHasFeel("���ţ�" + barcode + "��ɨ��00��", this, EVoiceType.fail, 0);
			return;
		}
		String signer = etSigner.getText().toString().trim();
		String signtype = etSignType.getText().toString().trim();
		if (StrUtil.isNullOrEmpty(signer)) {
			signer = signtype;// ���ǩ����Ϊ�գ���ǩ���ˣ�ǩ�����͵�ֵ(������ǩ��)
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
		// ����ǩ��Ҫ�ȹ����Ƿ��ظ���ԭ����:���߳������ӳɹ���,�ᵯ�����յ���ʾ��,
		// ԭ���ĳ�����:������ظ�Ҳ�ᵯ��,��ֻ��ListView�ϵ����ݱ�ɾ������
		if (AddScanDataThread.getIntance(SignaturePhotoActivity.this).isExists(scanDataModel)) {

			PromptUtils.getInstance().showToastHasFeel("���ţ�" + barcode + "��ɨ�裡", this, EVoiceType.fail, 0);
			return;
		}

		// ��ӵ����������
		AddScanDataThread.getIntance(SignaturePhotoActivity.this).add(scanDataModel);

		// ���½���
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(barcodeKey, barcode);
		map.put(sigerKey, signer);
		map.put(signTypeKey, signtype);
		lvx.add(map);

		scanCount++;
		updateView();
		if (!cksign.isChecked()) {
			etSigner.setText("");
		}

		strPreScanedBar = barcode;
		openCamera();// ɨ��

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
		String signer = etSigner.getText().toString().trim();
		if (!strUtil.isNullOrEmpty(signer)) {
			if (signer.length() > 14) {
				PromptUtils.getInstance().showToast("ǩ���˵ĳ��Ȳ��ܳ���14λ", SignaturePhotoActivity.this);
				flag = false;
			}
		}
		return flag;
	}

}
