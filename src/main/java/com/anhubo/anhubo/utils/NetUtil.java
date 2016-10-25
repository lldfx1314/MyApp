package com.anhubo.anhubo.utils;

import com.anhubo.anhubo.MyApp;
import com.anhubo.anhubo.protocol.RequestResultListener;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;
import com.yolanda.nohttp.rest.SimpleResponseListener;

import java.util.HashMap;

/**
 * 网络工具类
 *
 * @author luoli
 */
public class NetUtil {

    private static final Object TAG = NetUtil.class;

    /**
     * 请求数据，内部封装了数据的缓存，并且完成了json数据解析成JavaBean的工作。
     *  @param url                   请求地址
     * @param params                请求参数
     * @param beanType              指定要把json解析什么样的JavaBean类型，如果json返回来的结果直接就是对象， 传对象的Class即可，如果返回来的json根元素是一个集合则返回一个Type对象， Type的获取方式为：Type type = new TypeToken<集合泛型>(){}.getType();
     * @param requestResultListener 用于接收请求结果的监听器
     * @param what
     */
    public static <T> void requestData(String url, HashMap<String, String> params, Object beanType,
                                       RequestResultListener<T> requestResultListener, int what) {

        Request<String> request = NoHttp.createStringRequest(url, RequestMethod.POST);
        request.add(params);

        // 获取网络数据
        getDataFromNet(request, beanType, requestResultListener,what);
    }


    /**
     * 从网络中获取数据
     *  @param request               指定请求对象
     * @param beanType              指定要把json解析成的JavaBean类型
     * @param requestResultListener 用于接收请求结果的监听器
     * @param what
     */
    private static <T> void getDataFromNet(final Request<String> request, final Object beanType,
                                           final RequestResultListener<T> requestResultListener, int what) {

        // what类似于Handler中的what，用于区别多次发的不同请求，responseListener类似于Handler
        //int what = 0;

        // 创建用于接收请求结果的监听器
        OnResponseListener<String> responseListener = new SimpleResponseListener<String>() {

            // 网络数据请求成功
            @Override
            public void onSucceed(int what, Response<String> response) {
                String json = response.get();                // 获取json数据
                T bean = JsonUtil.json2Bean(json, beanType);// 解析json数据
                requestResultListener.onRequestFinish(bean,what);// 返回json数据
                requestResultListener.showJsonStr(json,what);// 返回json数据


            }

            @Override
            public void onFailed(int what, Response<String> response) {
                super.onFailed(what, response);
                System.out.println("网络出错");
                requestResultListener.onRequestFinish(null,what);
            }
        };
        // 把请求添加到请求队列，请求列队内部会开子线程去联网获取数据
        MyApp.getRequestQueue().add(what, request, responseListener);
    }
}



























    /*@Override
    public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
        //Logger.e(TAG, "请求数据失败", exception);
        requestResultListener.onRequestFinish(null);
    }*/


/*HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.GET,
		    request.url(),
		    new RequestCallBack<String>(){
		        @Override
		        public void onLoading(long total, long current, boolean isUploading) {
		        }

		        @Override
		        public void onSuccess(ResponseInfo<String> responseInfo) {
		        	json = responseInfo.result;				// 获取json数据
					cacheData(json, request.url());				// 缓存json数据
					T bean = JsonUtil.json2Bean(json, beanType);// 解析json数据
					requestResultListener.onRequestFinish(bean);// 返回json数据
					Logger.i(TAG, "请求网络，json = " + json);
					getedData = true;
		        }

		        @Override
		        public void onStart() {
		        }

		        @Override
		        public void onFailure(HttpException error, String msg) {
		        	Logger.e(TAG, "请求数据失败", error);
					requestResultListener.onRequestFinish(null);
		        }
		});*/










