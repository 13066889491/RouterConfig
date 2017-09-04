package com.twowing.routeconfig.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.twowing.routeconfig.R;

/**
 * Created by chong on 2017/4/25.
 */

public class IpEditView extends LinearLayout implements TextWatcher {
	private static final String TAG = "IPEditText";

	private int width;
	private int height;
	private Paint paint;

	private static final int DEFAULT_TEXT_MAX_LENGTH = 3;
	private static final int DEFAULT_TEXT_SIZE = 12;
	private static final int DEFAULT_TEXT_COLOR = Color.parseColor("#FEFEFE");
	private static final int DEFAULT_BORDER_COLOR = Color.parseColor("#333333");
	private static final int DEFAULT_BORDER_WIDTH = 1;
	private static final int DEFAULT_POINT_COLOR = Color.BLACK;
	private static final int DEFAULT_POINT_WIDTH = 2;
	private static final int DEFAULT_IP_EDITTEXT_LENGTH = 4;

	private int textLength;
	private int textSize;
	private int textColor;

	private int borderColor;
	private int borderWidth;

	private int pointColor;
	private int pointWidth;
	private int editNumber;

	private int default_height = px2dp(17);
	private int default_width = px2dp(55);

	private List<EditText> data = new ArrayList<EditText>();

	public IpEditView(Context context) {
		this(context, null);
	}

	public IpEditView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public IpEditView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		TypedArray ta = context.obtainStyledAttributes(attrs,
				R.styleable.SuperEditText, defStyleAttr, 0);
		textLength = ta.getInt(R.styleable.SuperEditText_textLength,
				DEFAULT_TEXT_MAX_LENGTH);

		textSize = (int) ta.getDimension(R.styleable.SuperEditText_textSize,
				DEFAULT_TEXT_SIZE);
		textColor = ta.getColor(R.styleable.SuperEditText_textColor,
				DEFAULT_TEXT_COLOR);

		borderColor = ta.getColor(R.styleable.SuperEditText_borderColor,
				DEFAULT_BORDER_COLOR);
		borderWidth = (int) ta.getDimension(
				R.styleable.SuperEditText_borderWidth, DEFAULT_BORDER_WIDTH);

		pointColor = ta.getColor(R.styleable.SuperEditText_pointColor,
				DEFAULT_POINT_COLOR);
		pointWidth = (int) ta.getDimension(
				R.styleable.SuperEditText_pointWidth, DEFAULT_POINT_WIDTH);

		editNumber = ta.getInt(R.styleable.SuperEditText_editNumber,
				DEFAULT_IP_EDITTEXT_LENGTH);

		init(context);
		initPaint();
	}

	public float getTextSize() {
		return data.get(0).getTextSize();
	}

	public String getText() {
		return data.get(0).getText().toString();
	}

	private void initPaint() {
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStrokeWidth(2);
		paint.setStyle(Paint.Style.FILL);
	}

	private void init(Context context) {
		for (int i = 0; i < editNumber; i++) {
			EditText edit = new EditText(context);
			edit.setBackgroundDrawable(null);
			edit.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
					textLength) });
			edit.setTextSize(textSize);
			edit.setTextColor(textColor);
			edit.setGravity(Gravity.CENTER);
			edit.setInputType(InputType.TYPE_CLASS_NUMBER);
			edit.setMinHeight(default_height);
			edit.setMinWidth(default_width);
			edit.setTag(i);
			edit.setMaxLines(1);
			edit.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT, 1));
			edit.addTextChangedListener(this);
			addView(edit);
			data.add(edit);
		}

		setDividerDrawable(getResources().getDrawable(
				android.R.drawable.divider_horizontal_textfield));
		setOrientation(LinearLayout.HORIZONTAL);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		int l = data.get(0).getLeft();
		int t = data.get(0).getTop() - getPaddingTop();
		int r = width - getPaddingRight();
		int b = height;

		Rect rect = new Rect(l, t, r, b);

		paint.setColor(borderColor);
		paint.setStrokeWidth(borderWidth);
		paint.setStyle(Paint.Style.STROKE);
		canvas.drawRect(rect, paint);

		int y = height / 2;
		int x = width / editNumber;
		paint.setStrokeWidth(pointWidth);
		paint.setColor(pointColor);
		for (int i = 1; i < data.size(); i++) {
			canvas.drawPoint(x * i, y, paint);
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		width = w;
		height = h;
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		Log.e(TAG, "beforeTextChanged :: s="+s+", start="+start+", count="+count);
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		Log.e(TAG, "onTextChanged :: s="+s+", start="+start+", count="+count+",before="+before);
	}

	@Override
	public void afterTextChanged(Editable s) {
		Log.e(TAG, "afterTextChanged :: s.length="+s.length()+",s.tostring"+s.toString());
		if (listener != null) {
			listener.afterTextChanged(getSuperEditTextValue());
		}
		if (s.length() == 3) {
			Integer n = new Integer(s.toString());
			if (n < 256) {
				for (int i = 0; i < data.size(); i++) {
					EditText edit = data.get(i);
					String val = edit.getText().toString();
					if (val.length() == 0) {
						edit.requestFocus();
						return;
					}
				}
			} else {
				// TODO
				// SweetAlertDialog pDialog = new
				// SweetAlertDialog(UpdateData.contextMap.get("StaticIpActivity"),
				// SweetAlertDialog.WARNING_TYPE)
				// .setTitleText("")
				// .setContentText(""+n+" 不是有效项。请指定一个介于 0 和 255 间的值。")
				// .setConfirmText("OK");
				// pDialog.show();
				// pDialog.setCancelable(true);
			}
		} else if (s.length() == 0) {
			for (int i = data.size() - 1; i >= 0; i--) {
				EditText edit = data.get(i);
				edit.setFocusable(true);
				String val = edit.getText().toString();
				if (val.length() == 3) {
					edit.requestFocus();
					edit.setSelection(3);
					return;
				}
			}
		}
	}

	public String[] getSuperEditTextValue() {
		String[] val = new String[editNumber];
		for (int i = 0; i < editNumber; i++) {
			val[i] = data.get(i).getText().toString();
			Log.e(TAG, "getSuperEditTextValue :: val[i]="+val[i]);
			// KLog.d(data.get(i).getText().toString());
		}
		return val;
	}

	public String getFormattedIp() {
		String ip = "";
		for (int i = 0; i < data.size(); i++) {
			String tmp = data.get(i).getText().toString();
			Integer n;
			if (TextUtils.isEmpty(tmp)) {
				n = 0;
			} else {
				n = new Integer(tmp);
			}
			if (i == 0) {
				ip = ip + n;
			} else {
				ip = ip + "." + n;
			}
		}

		return ip;
	}

	public void setSuperEdittextValue(String[] s) {
		for (int i = 0; i < s.length; i++) {
			data.get(i).setText(s[i]);
		}
	}

	public boolean getSuperCompile() {
		for (int i = 0; i < editNumber; i++) {
			String str = data.get(i).getText().toString();
			if (Integer.parseInt(str) <= 255) {
				return true;
			}
		}
		return false;
	}

	public int px2dp(int val) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				val, getResources().getDisplayMetrics());
	}

	public int px2sp(int val) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, val,
				getResources().getDisplayMetrics());
	}

	public interface SuperTextWatcher {
		public void afterTextChanged(String[] s);
	}

	private SuperTextWatcher listener;

	public void setSuperTextWatcher(SuperTextWatcher listener) {
		this.listener = listener;
	}
}
