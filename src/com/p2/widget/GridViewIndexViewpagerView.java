package com.p2.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * 利用gridview制作viewpager导航索引
 * 
 * @author jing
 * 
 */
public class GridViewIndexViewpagerView extends RelativeLayout {
	public class MainTabOnItemClickListener implements OnItemClickListener {
		GridViewAdapter tabAdapter;

		public MainTabOnItemClickListener(GridViewAdapter tabAdapter,
				Fragment[] fragments) {
			this.tabAdapter = tabAdapter;
			switchTab(0);
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (oldTabIndex != position) {
				switchTab(position);
				
			}
		}

		public void switchTab(int index) {
			tabAdapter.setSelected(oldTabIndex, index);
			showFragment(oldTabIndex, index);
			if (onTabSwitch != null) {
				onTabSwitch.onSwitch(index);
			}
			oldTabIndex = index;
		}

	}

	public interface OnTabSwitch {
		void onSwitch(int index);
	}

	/**
	 * User interface state that is stored by TextView for implementing
	 * {@link View#onSaveInstanceState}.
	 */
	public static class SavedState extends BaseSavedState {
		@SuppressWarnings("hiding")
		public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};

		int tabIndex;

		private SavedState(Parcel in) {
			super(in);
			tabIndex = in.readInt();
		}

		SavedState(Parcelable superState) {
			super(superState);
		}

		@Override
		public void writeToParcel(Parcel out, int flags) {
			super.writeToParcel(out, flags);
			out.writeInt(tabIndex);
		}
	}

	private Context context;

	private FragmentManager fragmentManager;
	private Fragment[] fragments;
	private GridView gridView;
	Drawable gridViewBackground, gridViewItemTextViewBackground;
	ColorStateList gridviewItemTextViewTextColor;
	int gridViewPageCount;
	private HorizontalScrollView hsv;
	/**
	 * 在状态恢复的时候会用到
	 */
	int oldTabIndex = -1;
	/**
	 * 切换标签的事件
	 */
	OnTabSwitch onTabSwitch;

	private MainTabOnItemClickListener itemClickListener;

	public GridViewIndexViewpagerView(Context context) {
		super(context);
	}

	public GridViewIndexViewpagerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		// 获得xml数据
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.GridViewIndexViewpagerView);
		gridViewBackground = a
				.getDrawable(R.styleable.GridViewIndexViewpagerView_gridview_background);
		gridViewItemTextViewBackground = a
				.getDrawable(R.styleable.GridViewIndexViewpagerView_gridview_item_textview_background);
		gridviewItemTextViewTextColor = a
				.getColorStateList(R.styleable.GridViewIndexViewpagerView_gridview_item_textview_textcolor);
		gridViewPageCount = a.getInt(
				R.styleable.GridViewIndexViewpagerView_gridview_pagecount, -1);
		a.recycle();
		// 获得组件
		View view = LayoutInflater.from(context).inflate(R.layout.view_layout,
				null);
		hsv = (HorizontalScrollView) view.findViewById(R.id.view_hsv);
		gridView = (GridView) view.findViewById(R.id.view_gv);
		this.addView(view);

	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		super.onRestoreInstanceState(state);
		SavedState ss = (SavedState) state;
		oldTabIndex = ss.tabIndex;
		itemClickListener.switchTab(oldTabIndex);
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		// TODO Auto-generated method stub
		Parcelable superState = super.onSaveInstanceState();
		SavedState ss = new SavedState(superState);
		ss.tabIndex = oldTabIndex;
		return ss;
	}

	private void printLog(String string) {
		// Log.d("GridViewIndexViewpagerView", string);
	}

	private void setFragment(Fragment[] fragments,
			FragmentManager fragmentManager) {
		this.fragments = fragments;
		this.fragmentManager = fragmentManager;
		// FragmentTransaction transaction = fragmentManager.beginTransaction();
		// for (int i = 0; i < fragments.length; i++) {
		// if (!fragments[i].isAdded()) {
		// transaction.add(R.id.view_frame, fragments[i], i + "");
		// }
		// }
		// transaction.commit();
		// showFragment(0);
	}

	private void setGridView(int[] drawableIDs, String[] textValues,
			Fragment[] fragments) {
		// gridviewAdapter
		GridViewAdapter tabAdapter = new GridViewAdapter(context, drawableIDs,
				textValues, gridViewPageCount, gridViewItemTextViewBackground,
				gridviewItemTextViewTextColor);
		// tabAdapter.setSelected(0);
		// gridview
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				tabAdapter.getAllColumnsWidth(),
				LinearLayout.LayoutParams.WRAP_CONTENT);
		gridView.setLayoutParams(params);
		gridView.setNumColumns(tabAdapter.getAllColumnsNum());
		gridView.setGravity(Gravity.CENTER);
		gridView.setVerticalSpacing(0);
		gridView.setAdapter(tabAdapter);
		if (gridViewBackground != null) {
			gridView.setBackgroundDrawable(gridViewBackground);
		}
		// gridview事件
		itemClickListener = new MainTabOnItemClickListener(tabAdapter,
				fragments);
		gridView.setOnItemClickListener(itemClickListener);

	}

	/**
	 * 
	 * @param drawableIDs
	 *            索引图片id
	 * @param textValues
	 *            文本内容
	 * @param pagerAdapter
	 */
	public void setViewData(int[] drawableIDs, String[] textValues,
			FragmentManager fragmentManager, Fragment[] fragments) {

		printLog("setViewData");
		if (fragments.length < drawableIDs.length) {
			throw new RuntimeException("页面数小于导航索引数");
		}
		setFragment(fragments, fragmentManager);
		setGridView(drawableIDs, textValues, fragments);
	}

	private void showFragment(int oldindex, int index) {
		if (!fragments[index].isAdded()) {
			FragmentTransaction transaction = fragmentManager
					.beginTransaction();
			if (oldindex != -1) {
				transaction.hide(fragments[oldindex]);
			}
			transaction.add(R.id.view_frame, fragments[index]);
//			transaction
//					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			transaction.show(fragments[index]);
			transaction.commit();
		} else {
			FragmentTransaction transaction = fragmentManager
					.beginTransaction();
			if (oldindex != -1) {
				transaction.hide(fragments[oldindex]);
			}
//			transaction
//					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			transaction.show(fragments[index]);
			transaction.commit();
		}
	}
}