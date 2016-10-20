package com.cneop.stoExpress.activity.admin;

import java.util.List;
import java.util.Map;

import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.model.ModuleModle;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;

/*
 * 自定义适配器
 */
public class CustomAdapter extends BaseAdapter {

	public static final String ITEM_NAME = "ITEM_NAME"; // 菜单项内容
	public static final String KEY_EXPANDED = "KEY_EXPANDED";// 菜单展开状况
	public static final String ISCLICK = "ISCLICK"; // 父结点是否被点击
	private final int CHILD_BASE_OFFSET = 30;
	private int mChildOffset; // 偏移量

	private LayoutInflater mInflater;
	private List<Map<String, Object>> list;
	private List<Map<String, Object>> cacheList;
	private int layoutID;
	private String[] flag;
	private int[] itemIDs;

	public CustomAdapter(Context context, List<Map<String, Object>> list, List<Map<String, Object>> cacheList, int layoutID, String flag[], int[] itemIDs) {
		this.mInflater = LayoutInflater.from(context);
		this.list = list;
		this.cacheList = cacheList;
		this.layoutID = layoutID;
		this.flag = flag;
		this.itemIDs = itemIDs;
		mChildOffset = CHILD_BASE_OFFSET;
	}

	@Override
	public int getCount() {

		int count = 0;
		if (list != null) {
			count = list.size();
		}
		return count;
	}

	@Override
	public Object getItem(int position) {

		if (list != null && list.size() > 0 && position < list.size() && position > -1) {
			return list.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		convertView = mInflater.inflate(layoutID, null);
		Map<String, Object> item = list.get(position); // 项
		ModuleModle modle = (ModuleModle) item.get(ITEM_NAME); // 填充值
		for (int i = 0; i < flag.length; i++) {
			if (convertView.findViewById(itemIDs[i]) instanceof ImageView) {
				ImageView iv = (ImageView) convertView.findViewById(itemIDs[i]);
				if (modle.getChildrenCount() > 0) {// 有子项
					if ((Boolean) item.get(KEY_EXPANDED)) { // 展开
						iv.setImageResource(R.drawable.button_remove);
					} else {
						iv.setImageResource(R.drawable.button_add);
					}
				} else {
					iv.setImageResource(R.drawable.space);
				}
			} else if (convertView.findViewById(itemIDs[i]) instanceof CheckBox) {
				CheckBox chk = (CheckBox) convertView.findViewById(itemIDs[i]);
				chk.setText(modle.getModuleName());
				chk.setChecked(modle.getState().equals("A") ? true : false);
			}
		}
		addListener(convertView, position);
		return getOffsetChangeView(modle.getLevel(), convertView);
	}

	private View getOffsetChangeView(int level, View convertView) {
		if (convertView != null) {
			convertView.setPadding((level - 1) * mChildOffset, 0, 0, 0);
		}
		return convertView;
	}

	// 添加事件监听器
	private void addListener(View view, final int position) {
		((CheckBox) view.findViewById(R.id.func_menu_ckSel)).setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				// CHECK点击事件
				String statusStr = isChecked == true ? "A" : "D";
				Map<String, Object> map = list.get(position);
				ModuleModle modle = (ModuleModle) map.get(ITEM_NAME);
				modle.setState(statusStr);
				int level = modle.getLevel();
				// 向上遍历
				if (position > 0) {
					int k = position - 1;
					for (int j = level; j > 1; j--) {
						for (int i = k; i >= 0; i--) {
							Map<String, Object> parentMap = list.get(i);
							ModuleModle parentModel = (ModuleModle) parentMap.get(ITEM_NAME);
							int id = parentModel.getId();
							if (parentModel.getLevel() < j) {
								k = i - 1;
								if (isChecked) {
									parentModel.setState(statusStr);
									// 改变缓存状态
									setCacheListStatus(queryCachePosition(id), statusStr);
								} else {
									// 查找同级子节点的状态
									if (isCancelChecked(position, j)) {
										parentModel.setState(statusStr);
										// 改变缓存状态
										setCacheListStatus(queryCachePosition(id), statusStr);
									}
								}
								break;
							}
						}
					}
				}
				// 向下遍历
				checkALLOrNone(list, position, level, statusStr);
				// 改变缓存状态
				int id = modle.getId();
				int cachePosition = queryCachePosition(id);
				checkALLOrNone(cacheList, cachePosition, level, statusStr);
				setCacheListStatus(cachePosition, statusStr);
				notifyDataSetChanged();
			}
		});
	}

	/*
	 * 向下全选或取消
	 */
	private void checkALLOrNone(List<Map<String, Object>> tempList, int position, int level, String statusStr) {
		if (position + 1 < tempList.size()) {
			for (int i = position + 1; i < tempList.size(); i++) {
				Map<String, Object> childMap = tempList.get(i);
				ModuleModle childModle = (ModuleModle) childMap.get(ITEM_NAME);
				if (childModle.getLevel() > level) {
					childModle.setState(statusStr);
				} else {
					break;
				}
			}
		}
	}

	/*
	 * 设置缓存状态
	 */
	private void setCacheListStatus(int position, String statusStr) {
		((ModuleModle) cacheList.get(position).get(ITEM_NAME)).setState(statusStr);
	}

	/*
	 * 父结点是否取消选中状态
	 */
	private boolean isCancelChecked(int position, int level) {
		boolean flag = true;
		if (position > 0) {
			for (int i = position - 1; i >= 0; i--) {
				Map<String, Object> parentMap = list.get(i);
				ModuleModle parentModle = (ModuleModle) parentMap.get(ITEM_NAME);
				int cLevel = parentModle.getLevel();
				if (cLevel == level) {
					if (parentModle.getState().equals("A")) {
						flag = false;
						return flag;
					}
				} else if (cLevel < level) {
					break;
				}
			}
		}
		if (position + 1 < list.size()) {
			for (int i = position; i < list.size(); i++) {
				Map<String, Object> childMap = list.get(i);
				ModuleModle childModle = (ModuleModle) childMap.get(ITEM_NAME);
				int cLevel = childModle.getLevel();
				if (cLevel == level) {
					if (childModle.getState().equals("A")) {
						flag = false;
						return flag;
					}
				} else if (cLevel < level) {
					break;
				}
			}
		}
		return flag;
	}

	// 从缓存中添加子节点
	public void addChildListData(int position, int level, int id) {
		int cachePosition = queryCachePosition(id);
		for (int i = cachePosition + 1, j = position + 1; i < cacheList.size(); i++) {
			Map<String, Object> map = cacheList.get(i);
			ModuleModle modle = (ModuleModle) map.get(CustomAdapter.ITEM_NAME);
			if (modle.getLevel() == (level + 1)) {
				list.add(j, map);
				j++;// 避免有子节点，引起序号不连续
			} else if (modle.getLevel() == level) {
				break;
			}
		}
	}

	// 查找在缓存LIST中位置
	private int queryCachePosition(int id) {
		int result = -1;
		for (int i = 0; i < cacheList.size(); i++) {
			Map<String, Object> map = cacheList.get(i);
			ModuleModle modle = (ModuleModle) map.get(CustomAdapter.ITEM_NAME);
			if (modle.getId() == id) {
				result = i;
				break;
			}
		}
		return result;
	}

	/*
	 * 添加子节点
	 */
	public void addChildListData(List<Map<String, Object>> childrenList, int parent) {
		if (list == null || list.size() == 0) {
			return;
		}
		if (parent < 0 || parent >= list.size()) {
			return;
		}
		Map<String, Object> item = list.get(parent);
		boolean expanded = (Boolean) item.get(KEY_EXPANDED);
		if (expanded) {
			return;
		}
		list.addAll(parent + 1, childrenList);
		item.put(KEY_EXPANDED, true);
		notifyDataSetChanged();
	}

	/*
	 * 移除子节点
	 */
	public void removeChildListData(int parent) {
		if (list == null || list.size() == 0) {
			return;
		}
		if (parent < 0 || parent >= list.size()) {
			return;
		}
		Map<String, Object> item = list.get(parent);
		boolean expanded = (Boolean) item.get(KEY_EXPANDED);
		if (!expanded) {
			return;
		}

		ModuleModle modle = (ModuleModle) item.get(ITEM_NAME);
		int pLevel = modle.getLevel();
		for (int i = parent + 1; i < list.size();) {
			Map<String, Object> temp = list.get(i);
			int cLevel = ((ModuleModle) temp.get(ITEM_NAME)).getLevel();
			if (cLevel > pLevel) {
				list.remove(i);
			} else {
				break;
			}
		}
		// 更新缓存
		int cachePosition = queryCachePosition(modle.getId());
		for (int i = cachePosition + 1; i < cacheList.size(); i++) {
			Map<String, Object> map = cacheList.get(i);
			ModuleModle m = (ModuleModle) map.get(ITEM_NAME);
			if (m.getLevel() > pLevel) {
				map.put(KEY_EXPANDED, false);
			} else if (m.getLevel() == pLevel) {
				break;
			}
		}
		item.put(KEY_EXPANDED, false);
		notifyDataSetChanged();
	}

	public boolean isExpanded(int position) {
		if (list == null || list.size() == 0) {
			return false;
		}
		if (position < 0 || position >= list.size()) {
			return false;
		}
		Map<String, Object> item = list.get(position);
		boolean expanded = (Boolean) item.get(KEY_EXPANDED);
		return expanded;
	}

	public void setChildOffset(int offset) {
		mChildOffset = offset;
		notifyDataSetChanged();
	}

}
