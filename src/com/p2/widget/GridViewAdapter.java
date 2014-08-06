package com.p2.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class GridViewAdapter extends BaseAdapter {
	/**
	 * 所有导航索引的个数
	 */
	private int allColumnsNum;
	/**
	 * 整个导航索引的宽度
	 */
	private int allColumnsWidth;
	Context context;
	/**
	 * 每个导航索引的高度
	 */
	int itemHeight;
	/**
	 * 每个导航索引的宽度
	 */
	int itemWidth;
	/**
	 * 导航索引视图
	 */
	private TextView tvItem[];
	/**
	 * 每页显示多个条导航索引按纽
	 */
	int pageSize;

	// ColorStateList gridviewItemTextViewTextColorList;
	// Drawable gridViewItemTextViewBackground;

	/**
	 * 
	 * @param context
	 * @param drawableIDs
	 *            导航索引显示图片id
	 * @param textValues
	 *            导航索引显示文本 可为空
	 * @param pageSize
	 *            每页显示多个条导航索引按纽 小于0为单面显示所有索引 即值为图片id个数
	 * @param gridviewItemTextViewTextColorList
	 *            文本颜色
	 * @param gridViewItemTextViewBackground
	 *            文本背景
	 * @param gridViewItemSelectedBackground
	 *            item背景
	 */
	public GridViewAdapter(Context context, int[] drawableIDs,
			String[] textValues, int pageSize,
			Drawable gridViewItemTextViewBackground,
			ColorStateList gridviewItemTextViewTextColorList) {
		super();
		if (textValues != null && drawableIDs.length != textValues.length) {
			throw new RuntimeException("drawableID数与textValue数不一致");
		}
		// this.gridviewItemTextViewTextColorList =
		// gridviewItemTextViewTextColorList;
		this.context = context;
		allColumnsNum = drawableIDs.length;
		if (pageSize < 1) {
			pageSize = allColumnsNum;
		}
		this.pageSize = pageSize;
		// 设置每个导航索引的宽高
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);
		itemWidth = (int) (dm.widthPixels / pageSize);
		allColumnsWidth = (int) (allColumnsNum * itemWidth);
		itemHeight = (int) (dm.density * 60);
		// 初始化导航索引
		tvItem = new TextView[drawableIDs.length];
		LayoutParams parms = new GridView.LayoutParams(itemWidth, itemHeight);
		for (int i = 0; i < drawableIDs.length; i++) {
			printLog("GridViewAdapter:" + i);
			int drawableID = drawableIDs[i];
			String textValue = textValues[i];
			tvItem[i] = new TextView(context);
			// 设置ImageView宽高
			// tvItem[i].setLayoutParams(parms);
			tvItem[i].setText(textValue);
			Drawable drawable = context.getResources().getDrawable(drawableID);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(),
					drawable.getMinimumHeight());
			tvItem[i].setCompoundDrawablesWithIntrinsicBounds(null, drawable,
					null, null);
			// 要从新设置
			tvItem[i].setGravity(Gravity.CENTER);
			tvItem[i].setBackgroundDrawable(gridViewItemTextViewBackground);
			tvItem[i].setTextColor(gridviewItemTextViewTextColorList);
		}

	}

	public int getAllColumnsNum() {
		return allColumnsNum;
	}

	public int getAllColumnsWidth() {
		return allColumnsWidth;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return tvItem.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return tvItem[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public int getItemWidth() {
		return itemWidth;
	}

	public int getPageSize() {
		return pageSize;
	}

	@Override
	public View getView(int position, View cv, ViewGroup parent) {
		return tvItem[position];
	}

	/**
	 * 设置选中的效果
	 * 
	 * @param i2
	 */
	public void setSelected(int oldindex, int index) {
		printLog("setSelected:"+index+","+oldindex);
		if (oldindex != -1) {
			tvItem[oldindex].setSelected(false);
		}
		tvItem[index].setSelected(true);// 选中的效果}
	}

	private void printLog(String string) {
		Log.d("GridViewAdapter", string);

	}
}
