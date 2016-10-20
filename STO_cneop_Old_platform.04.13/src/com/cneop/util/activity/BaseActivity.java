package com.cneop.util.activity;

import com.cneop.util.scan.ScanManager;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class BaseActivity extends Activity {
	@SuppressWarnings("unused")
	private int iListViewSelectedIndex = -1;// ListView��ѡ���е�����
	@SuppressWarnings("unused")
	private boolean isGroupKeyDowned = false;
	protected boolean scannerPower;
	protected boolean isMenuKeyWork = false;// menu���Ƿ�����(������ʱ,������е��Ҽ���)
	public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000; // ��Ҫ�Լ������־

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		initializeComponent();
		super.onCreate(savedInstanceState);
		initializeValues();
		setScannerPower();
	}

	/**
	 * ��ʼ���ؼ�
	 */
	protected void initializeComponent() {

		this.getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);// �ؼ�����
	}

	/**
	 * 1.�����ݵ������� 2.��һЩ��Ա������ֵ(e.g:�޸�ʱ����id����ҳ��)
	 * 
	 */
	protected void initializeValues() {

	}

	protected void setScannerPower() {

		ScanManager.getInstance().getScanner().setPower(scannerPower);
	}

	public OnClickListener ctrlOnClick = new OnClickListener() {

		@Override
		public void onClick(View v) {

			uiOnClick(v);
		}
	};

	protected Button bindButton(int resourceId) {

		Button btn = (Button) findViewById(resourceId);
		if (btn != null) {
			btn.setOnClickListener(ctrlOnClick);
		}
		return btn;
	}

	protected EditText bindEditText(int resourceId, OnFocusChangeListener onfocus) {

		EditText et = (EditText) this.findViewById(resourceId);
		et.setOnFocusChangeListener(et_focus_changeEvent);

		return et;
	}

	protected EditText bindEditText(int resourceId, OnFocusChangeListener onfocus, TextWatcher txtChangeListener) {

		EditText et = (EditText) findViewById(resourceId);
		et.setOnFocusChangeListener(et_focus_changeEvent);
		if (txtChangeListener != null) {
			et.addTextChangedListener(txtChangeListener);
		}
		return et;
	}

	private OnFocusChangeListener et_focus_changeEvent = new OnFocusChangeListener() {
		// ע��һ���ص�����������ʱ�Ľ�����һ�۵㷢���˸ı䡣
		@Override
		public void onFocusChange(View view, boolean hasFocus) {

			editFocusChange(view, hasFocus);
			if (hasFocus) {
				EditText et = (EditText) view;
				et.setSelection(et.getText().length());// �������õ��ı������
			}
		}

	};

	protected void editFocusChange(View view, boolean hasFocus) {
	}

	protected ListView bindListView(int resourceId) {
		ListView lv = (ListView) this.findViewById(resourceId);
		lv.setOnItemClickListener(lstItemClick);
		return lv;
	}

	protected OnItemClickListener lstItemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			iListViewSelectedIndex = position;
			if (((ListView) parent).getTag() != null) {
				((View) ((ListView) parent).getTag()).setBackgroundColor(Color.WHITE);
			}
			((ListView) parent).setTag(view);
			view.setBackgroundColor(Color.BLUE);
			view.clearFocus();
			view.requestFocusFromTouch();

		}
	};

	// ������Լ̳еķ���
	protected void uiOnClick(View v) {

	}

	protected void back() {

		finish();
	}

	protected void groupButtonEvent0() {

	}

	protected void groupButtonEvent1() {

	}

	protected void groupButtonEvent2() {

	}

	protected void groupButtonEvent3() {

	}

	protected void groupButtonEvent4() {

	}

	protected void groupButtonEvent5() {

	}

	protected void groupButtonEvent6() {

	}

	protected void groupButtonEvent7() {

	}

	protected void buttonEvent1() {

	}

	protected void buttonEvent2() {

	}

	protected void buttonEvent3() {

	}

	protected void buttonEvent4() {

	}

	protected void buttonEvent5() {

	}

	protected void buttonEvent6() {

	}

	protected void buttonEvent7() {

	}

	protected void buttonEvent8() {

	}

	protected void buttonEvent9() {

	}

	/**
	 * �����ݼ�
	 */
	protected void doLeftButton() {

	}

	/*
	 * �˸�� ������ʵ��
	 */
	protected boolean doKeyCode_Back() {

		return true;
	}

	/**
	 * F1��
	 */
	protected void doF1KeyEvent() {

	}

	/**
	 * F2����
	 */
	protected void doF2KeyEent() {

	}

	/**
	 * ɨ���������ʵ��
	 */
	protected void doStartScan() {

	}

	/**
	 * �ر�ɨ��
	 */
	protected void doStopScan() {

	}

}
