package com.cneop.stoExpress.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.hardware.BarcodeScan;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.serialport.ScantrigCon;
import android.text.method.DigitsKeyListener;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.common.Enums.EVoiceType;
import com.cneop.stoExpress.datacenter.msd.MSDServer;
import com.cneop.stoExpress.datacenter.xml.BarcodeTrackXml;
import com.cneop.stoExpress.model.BarcodTrack;
import com.cneop.util.ProgressDialogEx;
import com.cneop.util.PromptUtils;
import com.cneop.util.activity.CommonTitleActivity;
import com.cneop.util.device.NetworkUtil;
import com.cneop.util.scan.ScanManager;
import com.math.math;

public class BarcodeTrackActivity extends CommonTitleActivity {

	EditText etBarcode;
	EditText etBarcodeMsg;
	Button btnSel, btnBack;
	private ProgressDialogEx pd;
	public static final int BARCODEMSG = 0231;
	private ScantrigCon mScantrigCon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_barcode_track);
		setTitle("�����ѯ");
		super.scannerPower = true;
		super.onCreate(savedInstanceState);
		ScanManager.getInstance().getScanner().setScanResultHandler(handleScanData);
		if (Build.MODEL.equals("CN-S3")) {
			mScantrigCon = ScantrigCon.getInstance();
			mBarcodeScan = new BarcodeScan(this);
			mBarcodeScan.open();
		}
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
			boolean flag = new math().CODE1(barcode);
			if (flag) {
				// addRecord();
				etBarcode.requestFocus();
			} else {
				PromptUtils.getInstance().showToastHasFeel("�Ƿ�����", this, EVoiceType.fail, 0);
				etBarcode.setText("");
			}
		}
	}

	@Override
	protected void initializeComponent() {
		super.initializeComponent();
		etBarcode = super.bindEditText(R.id.et_barcode_track, null);

		String digits = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		etBarcode.setKeyListener(DigitsKeyListener.getInstance(digits));

		etBarcodeMsg = super.bindEditText(R.id.et_barcode_message, null);
		// etBarcodeMsg.setTextColor(R.color.gray); //����ֻ��ʱ��������ɫ

		etBarcodeMsg.setCursorVisible(false); // ����������еĹ�겻�ɼ�
		etBarcodeMsg.setFocusable(false); // �޽���
		etBarcodeMsg.setFocusableInTouchMode(false); // ����ʱҲ�ò�������

		btnSel = bindButton(R.id.bottom_2_btnOk);
		btnSel.setText("��ѯ");
		btnBack = bindButton(R.id.bottom_2_btnBack);
	}

	@Override
	protected void uiOnClick(View v) {

		super.uiOnClick(v);
		switch (v.getId()) {
		case R.id.bottom_2_btnBack:
			finish();
			break;
		case R.id.bottom_2_btnOk:
			selectBarcode();
			break;
		}
	}

	@SuppressLint("SimpleDateFormat")
	@SuppressWarnings("unused")
	public void guard_time() {
		try {
			/**
			 * 2016-3-3 ���ʱ�䱣������
			 */
			Date today = new Date();
			SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date dt1 = f.parse(f.format(today));// ��ǰϵͳʱ��
			Date dt2 = f.parse(getSharedPreferences("timer", MODE_PRIVATE).getString("time", ""));// ͬ��ʱ��

			if (dt1.getTime() < dt2.getTime()) {
				sendBroadcast(new Intent("ACTION_BAR_SCANCFG").putExtra("EXTRA_SCAN_POWER", 0));// �ر�ɨ��ͷ����ֹɨ��
				Toast.makeText(getApplicationContext(), "��ǹʱ�����������ͬ��ʱ����ٽ��в���!", 1).show();
			} else {
				sendBroadcast(new Intent("ACTION_BAR_SCANCFG").putExtra("EXTRA_SCAN_POWER", 1));
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			Toast.makeText(getApplicationContext(), e.toString() + "\t" + e.getMessage(), 1).show();
			e.printStackTrace();
		}
	}

	private BarcodeScan mBarcodeScan;

	/**
	 * ��Ҫ�����´�½��s3�豸���޸�ʱ����� Author������
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		/**
		 * ɨ���ֵ�� �м�� 139 ���� 140 �Ҳ�� 141
		 */
		switch (keyCode) {

		case KeyEvent.KEYCODE_BACK:
			if (Build.MODEL.equals("CN-S3")) {
				mBarcodeScan.close();
			} else if (Build.MODEL.equals("NLS-MT60")) {
				sendBroadcast(new Intent("ACTION_BAR_SCANCFG").putExtra("EXTRA_SCAN_POWER", 0));
			}
			finish();
			break;
		case 139:
			// Toast.makeText(getApplicationContext(), "�м�� 139", 1).show();
			guard_time();
			break;
		case 140:
			// Toast.makeText(getApplicationContext(), "�Ҳ�� 141", 1).show();
			guard_time();
			break;
		case 141:
			// Toast.makeText(getApplicationContext(), "���� 140", 1).show();
			guard_time();
			break;
		case KeyEvent.KEYCODE_VOLUME_UP:
		case KeyEvent.KEYCODE_VOLUME_DOWN:
		case KeyEvent.KEYCODE_MUTE:
		case KeyEvent.KEYCODE_CTRL_LEFT:
			if (Build.MODEL.equals("CN-S3")) {
				if (getSharedPreferences("s3", MODE_PRIVATE).getString("0", "").equals("")) {
					mBarcodeScan.open();
					mBarcodeScan.scanning();
				} else if (getSharedPreferences("s3", MODE_PRIVATE).getString("0", "").equals("��ɨ")) {
					mBarcodeScan.setScannerContinuousMode();// ��ɨ
				}
			}
			break;
		}
		Runtime.getRuntime().gc();
		return false;

	}

	String barcode;

	@SuppressLint("NewApi")
	private void selectBarcode() {

		//
		barcode = etBarcode.getText().toString().trim().trim();
		if (!barcode.isEmpty()) {
			if (!NetworkUtil.getInstance(BarcodeTrackActivity.this).getIsConnected()) {
				PromptUtils.getInstance().showAlertDialogHasFeel(BarcodeTrackActivity.this, "��ѯʧ��:����δ����", null, EVoiceType.fail, 0.1);
				return;
			}

			if (pd == null) {
				pd = ProgressDialogEx.createProgressDialogEx(this, 10 * 1000, "��ʾ", "���ڲ�ѯ�����Ժ�...", new com.cneop.util.ProgressDialogEx.OnTimeOutListener() {

					@Override
					public void onTimeOut(ProgressDialogEx dialog) {

						Toast.makeText(BarcodeTrackActivity.this, "��ѯ��ʱ", Toast.LENGTH_SHORT).show();
					}

				});
			} else {
				pd.show();
			}
			etBarcodeMsg.setText("");

			startTrack();
		}
	}

	private void startTrack() {

		new Thread() {

			@Override
			public void run() {

				super.run();
				MSDServer ms = MSDServer.getInstance(BarcodeTrackActivity.this);
				try {
					String track = ms.barcodeTrack(barcode);
					// Message msg = Message.obtain(mhandler, BARCODEMSG);
					// msg.obj = track;
					// mhandler.sendMessage(msg);
					Message msg = new Message();
					msg.obj = track;
					mhandler.sendMessage(msg);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
	}

	@SuppressLint("HandlerLeak")
	private Handler mhandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			BarcodeTrackXml xml = new BarcodeTrackXml();
			List<BarcodTrack> list = new ArrayList<BarcodTrack>();
			try {
				list = xml.paresTrack((String) msg.obj);
				if (list == null || list.size() == 0) {
					etBarcodeMsg.setText("û��Ҫ��ѯ������");
					return;
				}
				StringBuilder sb = new StringBuilder();
				for (BarcodTrack barcodTrack : list) {
					sb.append(barcodTrack.getTime().toString().trim());
					sb.append("\r\n");
					sb.append(barcodTrack.getMemo().toString().trim());
					sb.append("\r\n");
				}
				etBarcodeMsg.setText(sb);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				pd.dismiss();
				pd.hide();
			}
		}
	};

	protected void onDestroy() {

		super.onDestroy();
		if (pd != null) {
			pd.dismiss();
			pd = null;
		}

	};
}
