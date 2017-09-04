package com.twowing.routeconfig.dialogview;

import snmp.routeritv.commservice.IAidlRouterCommService;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.twowing.routeconfig.R;
import com.twowing.routeconfig.utils.SharePrefsUtils;
import com.twowing.routeconfig.wireless.EthernetPPPoEView;

public class EthernetPPPoEDialog extends Dialog implements
		android.view.View.OnClickListener {
	private static final String TAG = "MyCustomDialog";
	public static final int PPPOE_NETWORK = 1;
	public static final int WIRELESS_NETWORK = 2;
	private TextView mDialogTitle;
	private int mNetWorkType;

	public PPPoEStateListener mClickListener;
	private EthernetPPPoEView mEthernetwirelessView;
	private IAidlRouterCommService mRouterCommService;

	public EthernetPPPoEDialog(Context context, int stely,
			IAidlRouterCommService routerCommService, int type) {
		super(context, stely);
		mRouterCommService = routerCommService;
		mNetWorkType = type;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_dialog_view);
		initView();
		initData();
		setListener();
	}

	private void setListener() {
		mEthernetwirelessView.setClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.wireless_settings_ok) {
			final String account = mEthernetwirelessView.getUserNameEditText();
			final String password = mEthernetwirelessView.getPassWordEditText();
			if (mRouterCommService == null) {
				Log.e(TAG, "onClick :: mRouterCommService="
						+ mRouterCommService);
				Toast.makeText(getContext(), "服务器异常，请检查设备。", 0).show();
				dismiss();
				return;
			}
			if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(password)) {
				switch (mNetWorkType) {
				case PPPOE_NETWORK:
					sendServerData(account, password, PPPOE_NETWORK);
					break;
				case WIRELESS_NETWORK:
					sendServerData(account, password, WIRELESS_NETWORK);
					break;
				default:
					break;
				}
				Log.e(TAG, "onClick :: mClickListener=" + mClickListener);
				if (mClickListener != null) {
					mClickListener.showPPPoEStatus();
				}
			} else {
				Toast.makeText(getContext(), "账户或密码不能空，请重新设置！", 0).show();
			}
		}
		dismiss();
	}

	private void sendServerData(final String account, final String password,
			final int type) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (mRouterCommService != null) {
					int wanIndex = 1; // wanIndex 只第几个wan口 ， 默认设置1,
										// 1指internet上网方式
					if (type == PPPOE_NETWORK) {
						try {
							mRouterCommService.setPppoeInfo(wanIndex, account,
									password);
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					} else if (type == WIRELESS_NETWORK) {
						try {
							mRouterCommService.setPppoeInfo(wanIndex, account,
									password);
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}

				} else {

				}
			}
		}).start();
	}

	public interface PPPoEStateListener {
		void showPPPoEStatus();
	}

	public void connectPPPoEState(PPPoEStateListener listener) {
		if (listener != null) {
			mClickListener = listener;
		}
	}

	private void initView() {
		mDialogTitle = (TextView) findViewById(R.id.settings_dialog_title);
		mEthernetwirelessView = (EthernetPPPoEView) findViewById(R.id.wireless_setting_view);
	}

	private void initData() {
		mDialogTitle.setTextSize(20);
		mDialogTitle.setTextColor(getContext().getResources().getColor(
				R.color.white));
		mEthernetwirelessView.setUserNameTitle("PPPoE账户");
		mEthernetwirelessView.setPassWordTitle("PPPoE密码");
		mEthernetwirelessView.setHint(1);
		}

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	}
}
