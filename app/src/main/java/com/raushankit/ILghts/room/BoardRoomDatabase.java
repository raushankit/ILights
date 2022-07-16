package com.raushankit.ILghts.room;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.raushankit.ILghts.model.board.BoardBasicModel;
import com.raushankit.ILghts.model.room.BoardRoomData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {BoardRoomData.class}, version = 1, exportSchema = false)
public abstract class BoardRoomDatabase extends RoomDatabase {

    public abstract BoardDao boardDao();

    public static volatile BoardRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;

    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static BoardRoomDatabase getDatabase(final Context context){
        if(INSTANCE == null){
            synchronized (BoardRoomDatabase.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), BoardRoomDatabase.class,
                            "board_database").build();
                }
            }
        }
        return INSTANCE;
    }
}
