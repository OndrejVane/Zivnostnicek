package com.example.ondrejvane.zivnostnicek.server;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.toolbox.Volley;

/**
 * Created by Abhi on 20 Jan 2018 020.
 */

public class RequestQueue {
    private static RequestQueue mInstance;
    private com.android.volley.RequestQueue mRequestQueue;
    private Context mCtx;

    private RequestQueue(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized RequestQueue getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new RequestQueue(context);
        }
        return mInstance;
    }

    private com.android.volley.RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
