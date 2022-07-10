package com.raushankit.ILghts.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.raushankit.ILghts.model.board.BoardBasicModel;
import com.raushankit.ILghts.model.board.BoardCredentialModel;
import com.raushankit.ILghts.model.board.BoardPinsModel;

public class BoardFormViewModel extends ViewModel {

    private static final String SUB1 = "basic_data";
    private static final String SUB2 = "pins_data";
    private static final String SUB3 = "credential_data";
    private SavedStateHandle handle;

    public LiveData<BoardBasicModel> getBasicData(){
        return handle.getLiveData(SUB1, new BoardBasicModel());
    }

    public LiveData<BoardPinsModel> getPinsData(){
        return handle.getLiveData(SUB2, new BoardPinsModel());
    }

    public LiveData<BoardCredentialModel> getCredentialsData(){
        return handle.getLiveData(SUB2, new BoardCredentialModel());
    }
}
