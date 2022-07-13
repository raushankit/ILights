package com.raushankit.ILghts.fragments.board;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.raushankit.ILghts.BoardForm;
import com.raushankit.ILghts.R;
import com.raushankit.ILghts.adapter.BoardItemAdapter;
import com.raushankit.ILghts.entity.BoardFormConst;
import com.raushankit.ILghts.model.BoardItem;
import com.raushankit.ILghts.model.board.BoardBasicModel;
import com.raushankit.ILghts.model.board.BoardCredentialModel;
import com.raushankit.ILghts.model.board.BoardPinsModel;
import com.raushankit.ILghts.storage.VolleyRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardFragment extends Fragment {
    public static final String TAG = "BoardFragment";

    private static final String DELETE_URL = "https://identitytoolkit.googleapis.com/v1/accounts:delete?key=";
    private View view;
    private VolleyRequest requestQueue;
    private ExtendedFloatingActionButton fab;
    private ActivityResultLauncher<Intent> addBoardLauncher;

    public BoardFragment() {
    }

    public static BoardFragment newInstance() {
        return new BoardFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        requestQueue = VolleyRequest.newInstance(requireActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_board, container, false);
        fab = view.findViewById(R.id.board_fragment_fab);
        addBoardLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if(result.getResultCode() == Activity.RESULT_OK && result.getData() != null){
                        Intent receiveIntent = result.getData();
                        BoardBasicModel m1 = receiveIntent.getParcelableExtra(BoardFormConst.FORM1_BUNDLE_KEY);
                        BoardPinsModel m2 = receiveIntent.getParcelableExtra(BoardFormConst.FORM2_BUNDLE_KEY);
                        BoardCredentialModel m3 = receiveIntent.getParcelableExtra(BoardFormConst.FORM3_BUNDLE_KEY);
                        Log.w(TAG, "onCreateView: m1 = " + m1 + " m2 = " + m2 + " m3 = " + m3);
                    }
                    if(result.getResultCode() == Activity.RESULT_CANCELED && result.getData() != null){
                        Intent receiveIntent = result.getData();
                        deleteBoardCredentials(receiveIntent.getStringExtra(BoardFormConst.API_KEY),
                                receiveIntent.getStringExtra(BoardFormConst.ID_TOKEN));
                    }
                }
        );

        RecyclerView recyclerView = view.findViewById(R.id.board_fragment_recyclerview);
        BoardItemAdapter adapter = new BoardItemAdapter();
        recyclerView.setAdapter(adapter);
        List<BoardItem> list = new ArrayList<>();
        int i = 1;
        for(;i < 20;++i){
            list.add(new BoardItem(String.valueOf(i), "name " + i,"abc@mail.com"+i));
        }
        adapter.submitList(list);

        recyclerView.setOnScrollChangeListener((v,i1,i2,i3,i4)->{
            if(Math.abs(i4) > 25){
                if(i4 < 0 && fab.isExtended()){
                    fab.shrink();
                }
                if(i4 > 0 && !fab.isExtended()){
                    fab.extend();
                }
            }
        });

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), BoardForm.class);

            addBoardLauncher.launch(intent);
        });

        return view;
    }

    private void deleteBoardCredentials(String apiKey, String idToken){
        if(TextUtils.isEmpty(apiKey) || TextUtils.isEmpty(idToken)){
            return;
        }
        JSONObject js = new JSONObject();
        try{
            js.put("idToken", idToken);
        }catch (JSONException e){
            Log.w(TAG, "sendRequest: ", e);
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, DELETE_URL + apiKey, js, response -> Log.i(TAG, "deleteBoardCredentials: deleted credentials"), error -> Log.i(TAG, "deleteBoardCredentials: error deleting credentials")){
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        requestQueue.add(request);
    }
}