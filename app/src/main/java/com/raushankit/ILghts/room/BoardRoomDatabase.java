package com.raushankit.ILghts.room;

import android.content.Context;

import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.raushankit.ILghts.model.board.BoardAuthUser;
import com.raushankit.ILghts.model.room.BoardRoomData;
import com.raushankit.ILghts.model.room.BoardRoomUserData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {
        BoardRoomData.class,
        BoardRoomUserData.class,
        BoardAuthUser.class
        },
        autoMigrations = { @AutoMigration(from = 1, to = 2)},
        version = 2, exportSchema = true)
public abstract class BoardRoomDatabase extends RoomDatabase {

    public abstract BoardDao boardDao();
    public abstract BoardMemberDao boardMemberDao();
    public static volatile BoardRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;

    public static final ExecutorService databaseExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static BoardRoomDatabase getDatabase(final Context context){
        if(INSTANCE == null){
            synchronized (BoardRoomDatabase.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), BoardRoomDatabase.class,
                            "board_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
