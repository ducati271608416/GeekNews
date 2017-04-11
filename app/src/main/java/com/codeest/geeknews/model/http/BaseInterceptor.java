package com.codeest.geeknews.model.http;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.codeest.geeknews.util.SystemUtil;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created on 2017/4/11.
 *
 * @author WangJian
 */

public class BaseInterceptor implements Interceptor {
    private Map<String, String> headers;
    private Context context;

    public BaseInterceptor(Map<String, String> headers, Context context) {
        this.headers = headers;
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request.Builder builder = chain.request()
                .newBuilder();
        builder.cacheControl(CacheControl.FORCE_CACHE)
                .url(chain.request().url())
                .build();

        if (!SystemUtil.isNetworkConnected()) {

            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "当前无网络!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (headers != null && headers.size() > 0) {
            Set<String> keys = headers.keySet();
            for (String headerKey : keys) {
                builder.addHeader(headerKey, headers.get(headerKey)).build();
            }
        }

        if (SystemUtil.isNetworkConnected()) {
            int maxAge = 60; // read from cache for 60 s
            builder
                    .removeHeader("Pragma")
                    //Cache-control:用于控制HTTP缓存
                    //public:所有内容都将被缓存(客户端和代理服务器都可缓存)
                    //max-age:指示客户机可以接收生存期不大于指定时间（以秒为单位）的响应。在maxAge秒内再次请求该地址则不会请求服务器，直接获取缓存内容。
                    .addHeader("Cache-Control", "public, max-age=" + maxAge)
                    .build();
        } else {
            int maxStale = 60 * 60 * 24 * 14; // tolerate 2-weeks stale
            builder
                    .removeHeader("Pragma")
                    .addHeader("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                    .build();
        }
        return chain.proceed(builder.build());

    }
}