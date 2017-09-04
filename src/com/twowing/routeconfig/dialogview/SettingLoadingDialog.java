package com.twowing.routeconfig.dialogview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.twowing.routeconfig.R;

public class SettingLoadingDialog extends Dialog implements
		android.view.View.OnClickListener {
	private static final String TAG = "MyCustomDialog";
	private ImageView mLoadingView;
	private TextView mLoadingInfo;
	private Button mSettingsOK;
	private TextView mLoadingTitle;
	private Context mContext;

	public SettingLoadingDialog(Context context, int stely) {
		super(context, stely);
		mContext=context;
	}

	public SettingLoadingDialog(Context context) {
		super(context);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_loading_dialog_view);

		initView();
		startRefresh();
		setListener();
	}

	private void initView() {
		mLoadingView = (ImageView) findViewById(R.id.settings_loading_view);
		mLoadingInfo = (TextView) findViewById(R.id.settings_loading_info);
		mLoadingTitle = (TextView) findViewById(R.id.settings_loading_title);
		mSettingsOK = (Button) findViewById(R.id.settings_loading_ok);
	}

	private void setListener() {
		mSettingsOK.setOnClickListener((android.view.View.OnClickListener) this);
	}

	public void upDataCompletedLoading(boolean flag) {
		mLoadingView.setVisibility(flag ? View.GONE : View.VISIBLE);
		mSettingsOK.setVisibility(flag ? View.VISIBLE : View.GONE);
		if (flag) {
			clearLoadingAnimation();
		}
	}

	public TextView getLoadingInfo() {
		return mLoadingInfo;
	}

	public void setLoadingInfo(int resid) {
		mLoadingInfo.setText(resid);
	}

	public void setLoadingInfo(String resid) {
		mLoadingInfo.setText(resid);
	}

	public void setLoadingTitle(String resid) {
		mLoadingTitle.setText(resid);
	}

	private void startRefresh() {
		Animation anim = AnimationUtils.loadAnimation(getContext(),
				R.anim.loading);
		LinearInterpolator lir = new LinearInterpolator();
		anim.setInterpolator(lir);
		mLoadingView.startAnimation(anim);
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
	}

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		clearLoadingAnimation();
		SettingLoadingDialog.this.dismiss();
	}

	private void clearLoadingAnimation() {
		if (mLoadingView != null) {
			mLoadingView.clearAnimation();
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.settings_loading_ok) {
			this.dismiss();
		}
	}
}