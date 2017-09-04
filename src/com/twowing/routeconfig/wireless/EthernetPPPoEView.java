package com.twowing.routeconfig.wireless;

import snmp.routeritv.commservice.IAidlRouterCommService;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.twowing.routeconfig.R;
import com.twowing.routeconfig.utils.SharePrefsUtils;

public class EthernetPPPoEView extends LinearLayout {
	private static final String TAG = "EthernetPPPoEView";
	private TextView mUserNameTitle;
	private TextView mPassWordTitle;
	private EditText mUserNameEditText;
	private EditText mPassWordEditText;
	private Button mSettingsOkBt;
	private Button mSettingsCancelBt;
	private View mUserNameView;
	private View mPassWorldView;
	private String mUserName = "Founder_0001";
	private String mPassWord = "12345678";

	private IAidlRouterCommService mRouterCommService;

	public void getRouterServiceListener(IAidlRouterCommService l) {
		this.mRouterCommService = l;
		Log.e(TAG, "setRouterServiceListener  :: mRouterCommService="
				+ mRouterCommService);
	}

	public String getUserNameEditText() {
		return mUserNameEditText.getText().toString().trim();
	}

	public String getPassWordEditText() {
		return mPassWordEditText.getText().toString().trim();
	}

	public void setClickListener(OnClickListener l) {
		mSettingsOkBt.setOnClickListener(l);
		mSettingsCancelBt.setOnClickListener(l);
	}

	public void setUserNameTitle(String userName) {
		mUserNameTitle.setText(userName);
	}

	public void setPassWordTitle(String passWord) {
		mPassWordTitle.setText(passWord);
	}

	public EthernetPPPoEView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public EthernetPPPoEView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public EthernetPPPoEView(Context context) {
		super(context);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mUserNameView = findViewById(R.id.wireless_username_view);
		mPassWorldView = findViewById(R.id.wireless_passworld_view);

		mUserNameTitle = (TextView) findViewById(R.id.wireless_username_title);
		mPassWordTitle = (TextView) findViewById(R.id.wireless_password_title);

		mUserNameEditText = (EditText) findViewById(R.id.wireless_username_editText);
		mPassWordEditText = (EditText) findViewById(R.id.wireless_passworld_editText);

		mSettingsOkBt = (Button) findViewById(R.id.wireless_settings_ok);
		mSettingsCancelBt = (Button) findViewById(R.id.wireless_settings_cancel);

		if (mPassWordTitle == null || mUserNameTitle == null
				|| mUserNameView == null || mPassWorldView == null
				|| mUserNameEditText == null || mPassWordEditText == null
				|| mSettingsOkBt == null || mSettingsCancelBt == null) {
			throw new InflateException("Miss a child?");
		}
	}

	public void setHint(int type) {
		String userName = null;
		String passWord = null;
		if (type == 0) {
			userName = SharePrefsUtils.getWirelessUserName(getContext());
			passWord = SharePrefsUtils.getWirelessPassWord(getContext());
			if (TextUtils.isEmpty(userName)) {
				userName = mUserName;
			}
			if (TextUtils.isEmpty(passWord)) {
				passWord = mPassWord;
			}
		} else if (type == 1) {
			userName = SharePrefsUtils.getPppoePassWord(getContext());
			passWord = SharePrefsUtils.getPppoeUserName(getContext());
			if (TextUtils.isEmpty(userName)) {
				userName="";
			}
			if (TextUtils.isEmpty(passWord)) {
				passWord="";
			}
		}
		mUserNameEditText.setHint(userName);
		mPassWordEditText.setHint(passWord);
	}

	public void setFocusChangeListener() {
		mUserNameEditText.setOnFocusChangeListener(mOnFocusChangeListener);
		mPassWordEditText.setOnFocusChangeListener(mOnFocusChangeListener);
	}

	private OnFocusChangeListener mOnFocusChangeListener = new OnFocusChangeListener() {

		@SuppressWarnings("deprecation")
		@Override
		public void onFocusChange(View view, boolean hasFocus) {
			switch (view.getId()) {
			case R.id.wireless_username_editText:
				if (mUserNameView != null) {
					mUserNameView.setBackgroundDrawable(hasFocus ? getContext()
							.getResources().getDrawable(
									R.drawable.base_item_focused) : null);
				}
				break;
			case R.id.wireless_passworld_editText:
				if (mPassWorldView != null) {
					mPassWorldView
							.setBackgroundDrawable(hasFocus ? getContext()
									.getResources().getDrawable(
											R.drawable.base_item_focused)
									: null);
				}
				break;
			default:
				break;
			}
		}
	};

}
