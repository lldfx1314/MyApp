package com.anhubo.anhubo.utils;

import android.util.Log;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * <pre>
 * json数据解析工具
 * 如果要解析的json数据根元素是一个对象，则使用{@link #json2BeanByClass(String, Class)}
 * 如果要解析的json数据根元素是一个集合，则使用{@link #json2BeanByType(String, Type)}
 * </pre>
 */
public class JsonUtil {
	
	/** 用于解析json的类 */
	private static Gson GSON = new Gson();
	
	/**
	 * 把json转换为指定类型的JavaBean
	 * @param json json 数据
	 * @param beanType 指定要把json解析什么样的JavaBean类型，如果json返回来的结果直接就是对象，
	 * 传对象的Class即可，如果返回来的json根元素是一个集合则返回一个Type对象，
	 * Type的获取方式为：Type type = new TypeToken<集合泛型>(){}.getType();
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T json2Bean(String json, Object beanType) {
		T bean;
		if (beanType instanceof Class) {
			bean = json2BeanByClass(json, (Class<T>) beanType);
		} else if (beanType instanceof Type) {
			bean = json2BeanByType(json, (Type) beanType);
		} else {
			throw new IllegalArgumentException("beanType只能是Class或一个Type类型");
		}
		return bean;
	}

	/**
	 * 把json字符串转换为JavaBean
	 * @param json json字符串
	 * @param beanClass JavaBean的Class
	 * @return
	 */
	public static <T> T json2BeanByClass(String json, Class<T> beanClass) {
		T bean = null;
		try {
			bean = GSON.fromJson(json, beanClass);
		} catch (Exception e) {
			Log.i("JsonUtil", "解析json数据时出现异常\njson = " + json, e);
		}
		return bean;
	}
	
	/**
	 * 把json字符串转换为JavaBean。如果json的根节点就是一个集合，则使用此方法<p>
	 * type参数的获取方式为：Type type = new TypeToken<集合泛型>(){}.getType();
	 * @param json json字符串
	 * @return type 指定要解析成的数据类型
	 */
	public static <T> T json2BeanByType(String json, Type type) {
		T bean = null;
		try {
			bean = GSON.fromJson(json, type);
		} catch (Exception e) {
			Log.i("JsonUtil", "解析json数据时出现异常\njson = " + json, e);
		}
		return bean;
	}
	
}
