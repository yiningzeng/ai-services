package com.galileoai;

import okhttp3.*;

import java.io.IOException;
import java.util.logging.Handler;

/**
 * Created by baymin on 17-8-24.
 */
public class MyOkHttpClient {


    private static MyOkHttpClient myOkHttpClient;
    private OkHttpClient okHttpClient;
    private Handler handler;

    public MyOkHttpClient()
    {
        okHttpClient=new OkHttpClient();
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
            e.printStackTrace();
            return e.getMessage();
        }
    }
    public String post(String url,String body)
    {
        RequestBody requestBody = RequestBody.create(MediaType.parse("text"), body);
        Request request = new Request.Builder()
                .url(url).header("api-key","7SxikPTF7XE9xsrfW4erQWjZSCk=")
                .post(requestBody)
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
