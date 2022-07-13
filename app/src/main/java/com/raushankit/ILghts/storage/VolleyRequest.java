package com.raushankit.ILghts.storage;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleyRequest {
    private static VolleyRequest INSTANCE;
    private final RequestQueue requestQueue;

    private VolleyRequest(Context context){
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public static VolleyRequest newInstance(Context context){
        if(INSTANCE == null){
            INSTANCE = new VolleyRequest(context);
        }
        return INSTANCE;
    }

    public <T> void add(Request<T> request){
        requestQueue.add(request);
    }


}
