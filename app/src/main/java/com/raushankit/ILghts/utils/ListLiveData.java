package com.raushankit.ILghts.utils;

import android.util.Log;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class ListLiveData<K,T> extends MutableLiveData<T> {
    private static final String TAG = "ListLiveData";
    private final Map<K, T> map = new LinkedHashMap<>();
    private final AtomicBoolean emitList = new AtomicBoolean(false);

    @MainThread
    @Override
    public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
        super.observe(owner, t -> {
            if(emitList.compareAndSet(true, false)){
                map.forEach((k, v) -> observer.onChanged(v));
            }else{
                observer.onChanged(t);
            }
        });
    }

    @MainThread
    public void mSetValue(K k, T t){
        map.put(k,t);
        setValue(t);
    }

    @Override
    protected void onActive() {
        emitList.set(true);
    }
}
