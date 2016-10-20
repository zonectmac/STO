package com.cneop.util.controls;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cneop.util.PromptUtils;

import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

/**
 * 
 * @author Administrator 鐠嬪啰鏁ら弬瑙勭《: 1.ListViewEx(..) 2.initContext(..)
 *         3.inital(,,) 4
 */

public class ListViewEx extends ListView {

	// public interface IItemClickEvent {
	// public void onItemClick(int position);
	// }
	//
	public interface IItemClickEvent {

		public void onItemClick(int position);
	}

	// Item 閻ㄥ嚋lick 閹存湥elected閻ㄥ嫪绨ㄦ禒锟�
	public interface IItemSelected {

		public void onItemSelected(int position);
	}

	// 閻€劋绨崚鐘绘珟閹存劕濮涢崥搴㈠⒔鐞涘瞼娈戦崶鐐剁殶閺傝纭�楠炴儼绻戦崶鐐插灩闂勩倖鍨氶崝鐔烘畱閸掓鎮曢崣濠傚灙閸婏拷
	public interface IListenDelSelRowSuc {

		public void delSuc(String columnName, String value);
	}

	public interface IDoubleClickItem {

		public void onItemDoubleClick(int position);
	}

	// private Context mContext;
	private List<Map<String, Object>> mDataSource;
	private BaseAdapter mAdapter;
	public IItemClickEvent itemClick;
	private int itemCount = 20; // 閺勫墽銇氱拋鏉跨秿閺侊拷
	private long lastClickTime;
	private int lastClickPostion;
	private IDoubleClickItem doubleClickItem;

	public void SetDoubleClickItem(IDoubleClickItem doubleClick) {

		this.doubleClickItem = doubleClick;
	}

	private IListenDelSelRowSuc deleteSelectedRowListener;
	public int iListSelectedIndex = -1;
	private IItemSelected onItemSelected;

	public void setOnItemSelected(IItemSelected itemSelected) {

		this.onItemSelected = itemSelected;
	}

	/**
	 * 璁剧疆鑷畾涔変簨浠�
	 * 
	 * @param itemclick
	 */
	public void SetOnIItemClick(IItemClickEvent itemclick) {

		this.itemClick = itemclick;
	}

	public ListViewEx(Context context) {

		super(context);
		init();
	}

	// This example uses this method since being built from XML
	public ListViewEx(Context context, AttributeSet attrs) {

		super(context, attrs);
		init();
	}

	// Build from XML layout
	public ListViewEx(Context context, AttributeSet attrs, int defStyle) {

		super(context, attrs, defStyle);
		init();
	}

	private void init() {

		// setFocusableInTouchMode(true);
		//
		// this.setSelector()
	}

	public void inital(int itemLayoutResId, String[] arrayColName, int[] arrayColResId) {

		if (mDataSource == null) {
			mDataSource = new ArrayList<Map<String, Object>>();
		}
		mAdapter = new SimpleAdapter(this.getContext(), mDataSource, itemLayoutResId, arrayColName, arrayColResId);
		this.setAdapter(mAdapter);
		setFocusableInTouchMode(true);
		this.setSelector(com.cneop.stoExpress.cneops.R.drawable.list_item_foucsed);
		this.setCacheColorHint(0);

		this.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				iListSelectedIndex = position;

				if (itemClick != null) {
					itemClick.onItemClick(position);
				}
			}
		});

		this.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {

				iListSelectedIndex = position;
				if (onItemSelected != null) {
					onItemSelected.onItemSelected(position);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
	}

	/**
	 * 閹绘帒鍙嗛崚鐗堟付閸撳秹娼�
	 * 
	 * @param value
	 */
	public void add(Map<String, Object> value, String barcode, String barcodeKey) {

		if (isExists(barcode, barcodeKey) < 0) {
			mDataSource.add(0, value);
			notifyDataSetChanged();
		}
	}

	/**
	 * 閹绘帒鍙嗛崚鐗堟付閸撳秹娼�
	 * 
	 * @param value
	 */
	public void add(Map<String, Object> value, String[] args, String[] columns) {

		// 閺勵垰鎯佺�妯烘躬
		if (isExists(args, columns) < 0) {
			if (mDataSource.size() >= itemCount) {
				mDataSource.remove(mDataSource.size() - 1);
			}
			mDataSource.add(0, value);
			notifyDataSetChanged();
		}
	}

	/**
	 * 閹绘帒鍙嗛崚鐗堟付閸撳秹娼�
	 * 
	 * @param value
	 */
	public void add(Map<String, Object> value) {

		// if (mDataSource.size() >= itemCount) {
		// mDataSource.remove(mDataSource.size() - 1);
		// }
		mDataSource.add(0, value);
		notifyDataSetChanged();
	}

	/**
	 * 閹绘帒鍙嗛崚鐗堟付閸氾拷
	 * 
	 * @param map
	 */
	public void addAtLast(Map<String, Object> map) {

		mDataSource.add(map);
		notifyDataSetChanged();
	}

	public void add(List<Map<String, Object>> list) {

		mDataSource.clear();
		mDataSource.addAll(list);
		notifyDataSetChanged();
	}

	public void clear() {

		if (this.mDataSource != null) {
			this.mDataSource.clear();
			notifyDataSetChanged();
		}
	}

	public void delete(Object paramObject) {

		if (this.mDataSource.remove(paramObject)) {
			notifyDataSetChanged();
		}
	}

	public void delete(String barcode, String mapKey) {

		int index = isExists(barcode, mapKey);
		if (index > -1) {
			mDataSource.remove(index);
			notifyDataSetChanged();
		}
	}

	public void setDelSelectedRowListener(IListenDelSelRowSuc listener) {

		this.deleteSelectedRowListener = listener;
	}

	/**
	 * 閸掔娀娅庨柅澶夎厬鐞涳拷
	 * 
	 * @param keyColumn
	 *            :閸忔娊鏁惃鍕灙閸氾拷婵★拷"barcode"
	 * @return 鐠嬪啰鏁ゅ銈嗘煙濞夋洘妞傞敍宀勩�濞夈劍鍓伴敍姘帥鐎规矮绠烻etDelSelectedRowListener鐎电钖�
	 *         瀵洘甯撮崣锝勮厬閻ㄥ嫭鏌熷▔鏇炵殺閸︺劌鍨归梽銈嗗灇閸旂喎鎮楅敍灞肩窗鐞氼偅澧界悰灞肩濞嗭拷
	 */
	public void deleteSelectedRow(final String keyColumn) {

		if (iListSelectedIndex < 0) {
			Toast.makeText(this.getContext(), "数据小于零", Toast.LENGTH_SHORT).show();
			return;
		}
		Map<String, Object> map = this.GetValue(iListSelectedIndex);

		final String barcode = (String) map.get(keyColumn);
		Context cnotext = this.getContext();

		PromptUtils.getInstance().showAlertDialog(cnotext, "鍒犻櫎" + barcode + "", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				mDataSource.remove(iListSelectedIndex);
				notifyDataSetChanged();
				iListSelectedIndex = -1;
				if (deleteSelectedRowListener != null)
					deleteSelectedRowListener.delSuc(keyColumn, barcode);
			}

		}, null);
	}

	public void notifyDataSetChanged() {

		if (this.mAdapter != null) {
			this.mAdapter.notifyDataSetChanged();
			iListSelectedIndex = -1;
			if (mDataSource.size() > 0) {
				this.requestFocusFromTouch();
				this.setSelection(0);
				iListSelectedIndex = 0;
			}
		}

	}

	/**
	 * 閼惧嘲褰囪ぐ鎾冲ListView閻ㄥ嫬銇囩亸锟�
	 * 
	 * @return
	 */
	public int getSize() {

		if (mDataSource == null) {
			return 0;
		}
		return mDataSource.size();
	}

	/**
	 * 閼惧嘲褰囪ぐ鎾冲List缁楃惞ocation妞ゅ湱娈戦崐鑲╂畱闂嗗棗鎮�
	 * 
	 * @param location
	 * @return
	 */
	public Map<String, Object> GetValue(int location) {

		if (mDataSource.size() == 0 || location > mDataSource.size() - 1) {
			return null;
		}
		return mDataSource.get(location); // 鐎瑰啫鎷癵etItemAtPosition閻ㄥ嫬灏崚顐ｆЦ閿涳拷
	}

	/**
	 * 閼惧嘲褰囪ぐ鎾冲闁瀚ㄩ惃鍕�
	 * 
	 * @return
	 */
	public Map<String, Object> GetSelValue() {

		if (iListSelectedIndex != -1) {
			return mDataSource.get(iListSelectedIndex);
		}
		return null;
	}

	/**
	 * 閺嶈宓侀崚妤�倳閺夈儱鍨介弬顓熺厙妞ょ懓锟介幍锟芥躬閻ㄥ嫮鍌ㄥ锟芥俊鍌涙弓閹垫儳鍩岄崚娆掔箲閸ワ拷1
	 * 
	 * @param value
	 * @param columnName
	 * @return
	 */
	public int isExists(String value, String columnName) {
 
		int index = -1;
		for (int i = 0; i < mDataSource.size(); i++) {
			Map<String, Object> map = GetValue(i);
			if (value.equals(map.get(columnName))) {
				index = i;
				break;
			}
		}

		return index;
	}

	/**
	 * 閺勵垰鎯佺�妯烘躬
	 * 
	 * @param value
	 * @param columnName
	 * @return
	 */
	public int isExists(String[] value, String[] columnName) {

		int index = -1;
		for (int i = 0; i < mDataSource.size(); i++) {
			Map<String, Object> map = GetValue(i);
			if (value[0].equals(map.get(columnName[0])) && value[1].equals(map.get(columnName[1]))
					&& value[2].equals(map.get(columnName[2]))) {
				index = i;
				break;
			}
		}
		return index;
	}

	public boolean isRegDelEvent() {

		return false;
	}

}
