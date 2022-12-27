package com.raushankit.ILghts.room;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.raushankit.ILghts.entity.NotificationConst;
import com.raushankit.ILghts.entity.NotificationType;
import com.raushankit.ILghts.fetcher.BoardDataFetcher;
import com.raushankit.ILghts.fetcher.BoardPublicFetcher;
import com.raushankit.ILghts.fetcher.BoardSearchUserFetcher;
import com.raushankit.ILghts.model.FilterModel;
import com.raushankit.ILghts.model.User;
import com.raushankit.ILghts.model.board.BoardCredModel;
import com.raushankit.ILghts.model.board.BoardRequestModel;
import com.raushankit.ILghts.model.board.BoardSearchUserModel;
import com.raushankit.ILghts.model.board.FavBoard;
import com.raushankit.ILghts.model.room.BoardRoomData;
import com.raushankit.ILghts.model.room.BoardRoomUserData;
import com.raushankit.ILghts.response.BoardSearchResponse;
import com.raushankit.ILghts.utils.StringUtils;
import com.raushankit.ILghts.utils.callbacks.CallBack;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public class BoardRepository {
    private static final String TAG = "BoardRepository";

    private static volatile BoardRepository INSTANCE;
    private final DatabaseReference db;
    private final String userId;
    private final BoardDataFetcher boardFetcher;
    private final BoardPublicFetcher boardPublicFetcher;
    private final BoardSearchUserFetcher boardSearchUserFetcher;

    private BoardRepository(Application application) {
        Log.d(TAG, "BoardRepository: private constructor");
        BoardRoomDatabase roomDatabase = BoardRoomDatabase.getDatabase(application);
        userId = FirebaseAuth.getInstance().getUid();
        assert userId != null;
        db = FirebaseDatabase.getInstance().getReference();
        boardFetcher = new BoardDataFetcher(db, roomDatabase, userId);
        boardSearchUserFetcher = new BoardSearchUserFetcher(db);
        boardPublicFetcher = new BoardPublicFetcher(db, roomDatabase, userId);
    }

    public static BoardRepository getInstance(Application application) {
        Log.d(TAG, "BoardRepository: new instance");
        if (INSTANCE == null) {
            synchronized (BoardRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new BoardRepository(application);
                }
            }
        }
        return INSTANCE;
    }

    public void setFavBoard(FavBoard board){
        boardFetcher.insertFavBoard(board);
    }

    public Single<BoardCredModel> getCredentialData(@NonNull String boardId){
        return boardFetcher.getBoardAuthResult(boardId);
    }

    public Single<List<BoardSearchUserModel>> getSearchableUsersByQuery(@NonNull String boardId, @NonNull String query){
        return boardSearchUserFetcher.getSearchableUsersByQuery(boardId, query);
    }

    public Single<Integer> addUsersToBoard(Map<String, Object> mp){
        return boardSearchUserFetcher.putUsersInBoard(mp);
    }

    public LiveData<List<BoardRoomUserData>> getData() {
        return boardFetcher.getUserBoardsList();
    }

    public void forceCleanBoardUserList(){
        boardFetcher.forceCleanBoardUserList();
    }

    public Flowable<BoardSearchResponse> getPublicBoards(@NonNull FilterModel model) {
        return boardPublicFetcher.getData(model);
    }

    public void requestAccess(BoardRoomData data, User user, int level, CallBack<String> callBack, boolean isSearch) {
        boardPublicFetcher.requestAccess(data, user, level, callBack, isSearch);
    }

    public void requestEditorAccessFromBoard(BoardRoomData data, User user, int level, CallBack<String> callBack) {
        db.child("board_requests/" + userId + "/" + data.getBoardId())
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        BoardRequestModel md = task.getResult().getValue(BoardRequestModel.class);
                        if(md == null || md.getLevel() != level) {
                            boardPublicFetcher.requestAccess(data, user, level, callBack,false);
                        } else {
                            callBack.onClick(md.getLevel() != level? "unknown error occurred"
                                    : "You have already requested for " + (level == 1? "user": "editor") + " level access");
                        }
                    } else {
                        callBack.onClick(task.getException() == null
                                ? "unknown error occurred": task.getException().getLocalizedMessage());
                    }
                });
    }

    public void deleteBoard(@NonNull BoardRoomUserData board, CallBack<Integer> callBack) {
        String boardId = board.getBoardId();
        db.child("board_auth").child(boardId).get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful() && task.getResult() != null) {
                        final Map<String, Object> mp = new LinkedHashMap<>();
                        long timeStamp = StringUtils.TIMESTAMP();
                        task.getResult().getChildren()
                                .forEach(sp -> {
                                    String key = "user_notif/" + sp.getKey() + "/" + UUID.randomUUID().toString();
                                    mp.put(key + "/body", userId.equals(sp.getKey())
                                            ? "Deleted board " + board.getData().getTitle() + " id = [" + boardId + "]"
                                            : board.getData().getTitle() + " id = [" + boardId + "] was deleted by owner");
                                    mp.put(key + "/time", -1* timeStamp);
                                    mp.put(key + "/type", NotificationType.TEXT);
                                    mp.put("user_boards/" + sp.getKey() + "/boards/" + boardId, null);
                                    mp.put("user_boards/" + sp.getKey() + "/num", ServerValue.increment(-1));
                                });
                        mp.put("board_auth/" + boardId, null);
                        mp.put("board_meta/" + boardId, null);
                        mp.put("board_cred/" + boardId, null);
                        mp.put("board_details/" + boardId, null);
                        mp.put("board_public/" + boardId, null);
                        mp.put("board_private/" + boardId, null);
                        db.updateChildren(mp, (error, ref) -> callBack.onClick(error == null? null
                                : StringUtils.getDataBaseErrorMessageFromCode(error.getCode())));
                    } else {
                        callBack.onClick(StringUtils.getDataBaseErrorMessageFromCode(-1));
                    }
                });
    }

    public void leaveBoard(User user, BoardRoomUserData board, @NonNull CallBack<Integer> errorCallBack){
        Map<String, Object> mp = new LinkedHashMap<>();
        String boardId = board.getBoardId();
        mp.put("board_auth/" + boardId + "/" + userId, null);
        mp.put("user_boards/" + userId + "/boards/" + boardId, null);
        mp.put("user_boards/" + userId + "/num", ServerValue.increment(-1));
        String key = "user_notif/" + board.getOwnerId() + "/" + UUID.randomUUID().toString();
        mp.put(key + "/body", String.format(NotificationConst.LEAVE_BOARD_BODY_OWNER_SIDE,
                StringUtils.capitalize(user.getName()),
                user.getEmail(), board.getData().getTitle(), board.getBoardId()));
        mp.put(key + "/time", -1* StringUtils.TIMESTAMP());
        mp.put(key + "/type", NotificationType.TEXT);
        key = "user_notif/" + userId + "/" + UUID.randomUUID().toString();
        mp.put(key + "/body", String.format(NotificationConst.LEAVE_BOARD_BODY_USER_SIDE,
                board.getData().getTitle(), board.getBoardId()));
        mp.put(key + "/time", -1* StringUtils.TIMESTAMP());
        mp.put(key + "/type", NotificationType.TEXT);
        db.updateChildren(mp, (error, ref) -> errorCallBack.onClick(error == null? null
                : StringUtils.getDataBaseErrorMessageFromCode(error.getCode())));
    }
}
