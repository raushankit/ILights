package com.raushankit.ILghts.viewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.ExperimentalPagingApi;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.rxjava3.PagingRx;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.raushankit.ILghts.model.Notification;
import com.raushankit.ILghts.room.BoardRoomDatabase;
import com.raushankit.ILghts.room.NotificationDao;
import com.raushankit.ILghts.room.NotificationFetcher;
import com.raushankit.ILghts.room.NotificationRemoteMediator;

import io.reactivex.rxjava3.core.Flowable;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.ExperimentalCoroutinesApi;

public class NotificationViewModel extends AndroidViewModel {
    private static final String TAG = "NotificationViewModel";
    private final NotificationFetcher fetcher;
    private final Flowable<PagingData<Notification>> flowable;
    private final DatabaseReference remoteDb;
    private final String userId;


    @ExperimentalPagingApi
    @ExperimentalCoroutinesApi
    public NotificationViewModel(@NonNull Application application, @NonNull String userId) {
        super(application);
        Log.e(TAG, "NotificationViewModel: " + userId);
        BoardRoomDatabase db = BoardRoomDatabase.getDatabase(application);
        remoteDb =  FirebaseDatabase.getInstance().getReference();
        this.userId = userId;
        fetcher = new NotificationFetcher(remoteDb, db, userId);
        Pager<Integer, Notification> pager = new Pager<>(
                new PagingConfig(20),
                null,
                fetcher::getSource
        );
        flowable = PagingRx.getFlowable(pager);
        CoroutineScope scope = ViewModelKt.getViewModelScope(this);
        PagingRx.cachedIn(flowable, scope);
    }

    public Flowable<PagingData<Notification>> getFlowable() {
        return flowable;
    }

}
