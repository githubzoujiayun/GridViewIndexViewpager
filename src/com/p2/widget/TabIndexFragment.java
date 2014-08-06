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
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 利用gridview制作viewpager导航索引
 * 
 * @author jing
 * 
 */
public class TabIndexFragment extends RelativeLayout {
	public class TvClickListener implements OnClickListener {

		private int index;

		public TvClickListener(int index) {
			this.index = index;
		}

		@Override
		public void onClick(View v) {
			if (oldTabIndex != index) {
				switchTab(index);
			}

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
	/**
	 * 在状态恢复的时候会用到
	 */
	int oldTabIndex = -1;
	/**
	 * 切换标签的事件
	 */
	OnTabSwitch onTabSwitch;

	private LinearLayout layout;

	private TextView[] tvItem;

	public TabIndexFragment(Context context) {
		super(context);
	}

	public TabIndexFragment(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		// 获得xml数据
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.TabIndexFragment);
		gridViewBackground = a
				.getDrawable(R.styleable.TabIndexFragment_gridview_background);
		gridViewItemTextViewBackground = a
				.getDrawable(R.styleable.TabIndexFragment_gridview_item_textview_background);
		gridviewItemTextViewTextColor = a
				.getColorStateList(R.styleable.TabIndexFragment_gridview_item_textview_textcolor);
		gridViewPageCount = a.getInt(
				R.styleable.TabIndexFragment_gridview_pagecount, -1);
		a.recycle();
		// 获得组件
		View view = LayoutInflater.from(context).inflate(R.layout.view_layout,
				null);
		// hsv = (HorizontalScrollView) view.findViewById(R.id.view_hsv);
		// gridView = (GridView) view.findViewById(R.id.view_gv);
		layout = (LinearLayout) view.findViewById(R.id.view_linearlayout);
		layout.setBackgroundDrawable(gridViewBackground);
		this.addView(view);

	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		super.onRestoreInstanceState(state);
		SavedState ss = (SavedState) state;
		oldTabIndex = ss.tabIndex;
		switchTab(oldTabIndex);
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
		// Log.d("TabIndexFragment", string);
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

	public void setOnTabSwitch(OnTabSwitch onTabSwitch) {
		this.onTabSwitch = onTabSwitch;
	}

	private void switchTab(int index) {
		setSelectTabView(oldTabIndex, index);
		showFragment(oldTabIndex, index);
		if (onTabSwitch != null) {
			onTabSwitch.onSwitch(index);
		}
		oldTabIndex = index;
	}

	private void setTabView(int[] drawableIDs, String[] textValues,
			Fragment[] fragments, int pageSize2) {
		if (textValues != null && drawableIDs.length != textValues.length) {
			throw new RuntimeException("drawableID数与textValue数不一致");
		}
		pageSize2 = pageSize2 < 1 ? 1 : pageSize2;
		pageSize2 = pageSize2 > drawableIDs.length ? drawableIDs.length
				: pageSize2;
		// 设置每个导航索引的宽高
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);
		int itemWidth = (int) (dm.widthPixels / pageSize2);
		// 初始化导航索引
		tvItem = new TextView[drawableIDs.length];
		android.widget.LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(
				itemWidth,
				android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
		for (int i = 0; i < drawableIDs.length; i++) {
			int drawableID = drawableIDs[i];
			String textValue = textValues[i];
			tvItem[i] = new TextView(context);
			// 设置ImageView宽高
			tvItem[i].setLayoutParams(parms);
			tvItem[i].setText(textValue);
			Drawable drawable = context.getResources().getDrawable(drawableID);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(),
					drawable.getMinimumHeight());
			tvItem[i].setCompoundDrawablesWithIntrinsicBounds(null, drawable,
					null, null);
			// 要从新设置
			tvItem[i].setGravity(Gravity.CENTER);
			// tvItem[i].setBackgroundDrawable(gridViewItemTextViewBackground);
			tvItem[i].setTextColor(gridviewItemTextViewTextColor);
			tvItem[i].setOnClickListener(new TvClickListener(i));
			layout.addView(tvItem[i]);
		}

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
			FragmentManager fragmentManager, Fragment[] fragments, int pageSize) {

		printLog("setViewData");
		if (fragments.length < drawableIDs.length) {
			throw new RuntimeException("页面数小于导航索引数");
		}
		setFragment(fragments, fragmentManager);
		setTabView(drawableIDs, textValues, fragments, pageSize);
		switchTab(0);
	}

	private void showFragment(int oldindex, int index) {
		if (!fragments[index].isAdded()) {
			FragmentTransaction transaction = fragmentManager
					.beginTransaction();
			if (oldindex != -1) {
				transaction.hide(fragments[oldindex]);
			}
			transaction.add(R.id.view_frame, fragments[index]);
			// transaction
			// .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			transaction.show(fragments[index]);
			transaction.commit();
		} else {
			FragmentTransaction transaction = fragmentManager
					.beginTransaction();
			if (oldindex != -1) {
				transaction.hide(fragments[oldindex]);
			}
			// transaction
			// .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			transaction.show(fragments[index]);
			transaction.commit();
		}
	}

	private void setSelectTabView(int oldindex, int index) {
		if (oldindex != -1) {
			tvItem[oldindex].setSelected(false);
			tvItem[oldindex].setBackgroundDrawable(null);
		}
		tvItem[index].setSelected(true);// 选中的效果}
		tvItem[index].setBackgroundDrawable(gridViewItemTextViewBackground);
	}
}