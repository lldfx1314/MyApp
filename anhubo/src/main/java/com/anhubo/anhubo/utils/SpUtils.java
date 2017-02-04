package com.anhubo.anhubo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SpUtils {
	private static SharedPreferences sp ;
	public static void getSp(Context context) {
		if (sp == null) {
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
	}
	public static void putParam(Context context, String paramName, int value) {
		getSp(context);
		Editor edit = sp.edit();
		edit.putInt(paramName, value);
		edit.commit();
	}
	public static void putParam(Context context, String paramName, boolean value) {
		getSp(context);
		Editor edit = sp.edit();
		edit.putBoolean(paramName, value);
		edit.commit();
	}
	public static void putParam(Context context, String paramName, String value) {
		getSp(context);
		Editor edit = sp.edit();
		edit.putString(paramName, value);
		edit.commit();
	}
	public static String getStringParam(Context context, String paramName) {
		getSp(context);
		return sp.getString(paramName, null);
	}
	public static String getStringParam(Context context, String paramName, String defValue) {
		getSp(context);
		return sp.getString(paramName, defValue);
	}
	public static int getIntParam(Context context, String paramName) {
		getSp(context);
		return sp.getInt(paramName, 0);
	}
	public static int getIntParam(Context context, String paramName, int defValue) {
		getSp(context);
		return sp.getInt(paramName, defValue);
	}

	public static boolean getBooleanParam(Context context, String paramName) {
		getSp(context);
		return sp.getBoolean(paramName, false);
	}
	
	public static boolean getBooleanParam(Context context, String paramName, boolean defValue) {
		getSp(context);
		return sp.getBoolean(paramName, defValue);
	}

	
}
