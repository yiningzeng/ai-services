package com.galileoai;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;

/**
 * Created by baymin on 17-8-24.
 */
@Slf4j
public class MyOkHttpClient {


    private static MyOkHttpClient myOkHttpClient;
    private OkHttpClient okHttpClient;
    private Handler handler;

    public MyOkHttpClient()
    {
        //okHttpClient=new OkHttpClient();
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(6000, TimeUnit.SECONDS)
                .readTimeout(6000, TimeUnit.SECONDS)
                .build();
    }

    public static MyOkHttpClient getInstance() {
        if (myOkHttpClient == null) {
            synchronized (MyOkHttpClient.class) {
                if (myOkHttpClient == null) {
                    myOkHttpClient = new MyOkHttpClient();
                }
            }
        }
        return myOkHttpClient;
    }

    public String aiPost(String url,String imgBase64String) {
        try {
            FormBody formBody = new FormBody
                    .Builder()
                    .add("file", imgBase64String)//设置参数名称和参数值
                    .build();
            Request request = new Request.Builder()
                    .url(url)
//                .header("Content-Type","application/x-www-form-urlencoded; charset=UTF-8")
                    .post(formBody)
                    .build();
            return okHttpClient.newCall(request).execute().body().string();
        } catch (IOException e) {
//            e.printStackTrace();
            log.debug("eeeee-----访问ai接口出错：{}",e);
            return "{\"num\":-1, \"data\":\"" + e.getMessage() + "\"}";
        }
    }
    /*OkHttpClient client = new OkHttpClient.Builder().build();
    Request request = new Request.Builder()
            .url(ENDPOINT)
            .build();
    Response response = client.newCall(request).execute();
    FormBody body = new FormBody.Builder()
     .add("param1", "value1")
     .add("param2", "value2")
     .build();*/
    public String postNoHeader(String url,FormBody body)
    {
        //RequestBody requestBody = RequestBody.create(MediaType.parse("text"), body);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try {
            return okHttpClient.newCall(request).execute().body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public String get(String url)
    {
        Request request = new Request.Builder().url(url).build();
        try {
            return okHttpClient.newCall(request).execute().body().string();
        } catch (IOException e) {
            log.info("出错！！！！！！！");
            e.printStackTrace();
            return e.getMessage();
        }
    }
    public String platePost(String url,String body)
    {
        RequestBody requestBody = RequestBody.create(MediaType.parse("text"), body);
        Request request = new Request.Builder()
                .url(url).header("Authorization","APPCODE f6f3f2d2dffe4e06908e77a5e38e50f1")
                .header("Content-Type","application/x-www-form-urlencoded; charset=UTF-8")
                .post(requestBody)
                .build();
        try {
            return okHttpClient.newCall(request).execute().body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String lplatePost(String url,String imgBase64String)
    {
        FormBody formBody = new FormBody
                .Builder()
                .add("image",imgBase64String)//设置参数名称和参数值
                .build();
        Request request = new Request.Builder()
                .url(url).header("Authorization","APPCODE f6f3f2d2dffe4e06908e77a5e38e50f1")
                .header("Content-Type","application/x-www-form-urlencoded; charset=UTF-8")
                .post(formBody)
                .build();
        try {
            return okHttpClient.newCall(request).execute().body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void asyncGet(String url, Callback callback) {
        Request request = new Request.Builder().url(url).build();
        okHttpClient.newCall(request).enqueue(callback);
    }


    public void asyncPost(String url, FormBody formBody, Callback callback) {
        Request request = new Request.Builder().url(url).post(formBody).build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public void asyncPostJson(String url, String json, Callback callback) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        Request request = new Request.Builder().url(url).post(requestBody).build();
        okHttpClient.newCall(request).enqueue(callback);
    }
    public String postJson(String url, String json) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        Request request = new Request.Builder().url(url).post(requestBody).build();
        try {
            return okHttpClient.newCall(request).execute().body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void asyncDelete(String url, String headerName,String headerValue, Callback callback) {
        Request request = new Request.Builder().url(url).header(headerName,headerValue).delete().build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public String push(String url,String json)
    {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        Request request = new Request.Builder()
                .url(url).header("Authorization","Basic MTM4N2ZlZjNmYzE0OGQ2ZTI0NjljNDhlOmUyZDk2Njg5ZGFjNTY2MGZjMTU1NjU5Mg==")
                .post(requestBody)
                .build();
        try {
            return okHttpClient.newCall(request).execute().body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
