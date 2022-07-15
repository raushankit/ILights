package com.raushankit.ILghts.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.raushankit.ILghts.model.board.BoardBasicModel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {BoardBasicModel.class}, version = 1, exportSchema = false)
public class BoardRoomDatabase extends RoomDatabase {

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
