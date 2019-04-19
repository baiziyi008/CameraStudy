package com.mfx.camerastudy.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class Screen {

	public static float LEFTMENU_UI_PERCENT = 0.15f;
	public static int mNotificationBarHeight;

	public static int mScreenWidth;
	public static int mScreenHeight;
	public static int mWidth;
	public static int mHeight;
	public static float densityDpi;
	public static float density;

	public static float drawWidth;
	public static float drawHeight;

	private static final int PADDING_L = 30;
	private static final int PADDING_R = 30;
	private static final int PADDING_T = 50;
	private static final int PADDING_B = 40;

	public static float drawPaddingLeft;
	public static float drawPaddingRight;
	public static float drawPaddingTop;
	public static float drawPaddingBottom;

	public static int drawRows;
	public static float lineHeight;
	public static float line_space = 0;
	public static float charHeight;

	public static void initialize(Context context) {
		if (drawWidth == 0 || drawHeight == 0 || mWidth == 0 || mHeight == 0
				|| density == 0) {
			Resources res = context.getResources();
			DisplayMetrics metrics = res.getDisplayMetrics();

			DisplayMetrics dm = new DisplayMetrics();
			((Activity)context).getWindowManager().getDefaultDisplay().getRealMetrics(dm);
			mWidth = dm.widthPixels;  // 屏幕宽
			mHeight = dm.heightPixels;  // 屏幕高
			// TODO // - 50
			density = metrics.density;
			mNotificationBarHeight = (int) (35 * density);
			ILogPrint.logw("screen","mNotificationBarHeight ="+mNotificationBarHeight);
			// (int)(50
			// *
			// density)
			ILogPrint.logw("screen","mWidth ="+mWidth);
			ILogPrint.logw("screen","mHeight ="+mHeight);
			mScreenWidth = metrics.widthPixels;
			mScreenHeight = metrics.heightPixels;
			ILogPrint.logw("screen","mScreenWidth ="+mScreenWidth);
			ILogPrint.logw("screen","mScreenHeight ="+mScreenHeight);
			densityDpi = metrics.densityDpi;

			drawPaddingLeft = density * PADDING_L;
			drawPaddingRight = density * PADDING_R;
			drawPaddingTop = density * PADDING_T;
			drawPaddingBottom = density * PADDING_B;

			drawWidth = mWidth - drawPaddingLeft - drawPaddingRight;
			//  如果非全屏，减去标题栏的高度
			drawHeight = mHeight - drawPaddingTop - drawPaddingBottom;
		}
	}

	public static String clipImageUrl(String url, String add) {
		String temp = null;
		if (url != null) {
			if (add != null) {
				if (url.endsWith(".jpg") || url.endsWith(".png")
						|| url.endsWith(".gif") || url.endsWith(".bmp")) {
					String end = url.substring(url.length() - 4, url.length());
					int point = url.lastIndexOf(".");
					int index = url.lastIndexOf("/");
					if (index != -1) {
						String sub = url.substring(index + 1, point);
						if (sub.endsWith("_m") || sub.endsWith("_b")
								|| sub.endsWith("_s")) {
							String clip = sub.substring(0, sub.length() - 2);
							temp = url.substring(0, index + 1) + clip + add
									+ end;
						} else {
							temp = url.substring(0, index + 1) + sub + add
									+ end;
						}
					}
				}
			} else {
				temp = url;
			}
		}
		return temp;
	}

	/**
	 * 根据手机分辨率从DP转成PX
	 * @param context
	 * @param dpValue
	 * @return
	 */
	public static int dip2px(Context context, float dpValue) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
}