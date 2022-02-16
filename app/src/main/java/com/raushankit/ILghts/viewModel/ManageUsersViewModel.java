package com.raushankit.ILghts.viewModel;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.raushankit.ILghts.entity.PagingLoader;
import com.raushankit.ILghts.model.User;
import com.raushankit.ILghts.observer.AdminUserLiveData;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class ManageUsersViewModel extends ViewModel {
    private static final String TAG = "ManageUsersViewModel";
    private final Map<String, User> mp = new LinkedHashMap<>();
    private final MediatorLiveData<Map<String, User>> data = new MediatorLiveData<>();
    private final MutableLiveData<PagingLoader> messageData = new MutableLiveData<>();
    private final DatabaseReference db = FirebaseDatabase.getInstance().getReference("/users");
    private final Set<LiveData<Map<String, User>>> liveDataSet = new LinkedHashSet<>();
    private boolean isEmail;
    private String searchText;

    public ManageUsersViewModel() {
    }

    public LiveData<Map<String, User>> getData(boolean isEmail) {
        this.isEmail = isEmail;
        return data;
    }

    public LiveData<PagingLoader> getMessageData() {
        return messageData;
    }

    public void loadMoreData(@NonNull String text) {
        if (!TextUtils.equals(text, searchText)) {
            messageData.setValue(PagingLoader.IS_LOADING);
            mp.clear();
            for (LiveData<Map<String, User>> liveData : liveDataSet) {
                data.removeSource(liveData);
            }
            liveDataSet.clear();
            searchText = text;
            fetchData(text.toLowerCase(Locale.ROOT));
        } else {
            Log.w(TAG, "loadMoreData: queried text is same");
        }
    }

    private void fetchData(String text) {
        LiveData<Map<String, User>> mps = new AdminUserLiveData(getQuery(text));
        liveDataSet.add(mps);
        data.addSource(mps, stringUserMap -> {
            if (stringUserMap != null) {
                stringUserMap.forEach(mp::put);
                data.setValue(mp);
                messageData.setValue(PagingLoader.LOADED);
            } else {
                messageData.setValue(PagingLoader.ERROR);
            }
        });
    }

    private Query getQuery(@NonNull String text) {
        return db.orderByChild(isEmail ? "email" : "name").equalTo(text);
    }
}
