package com.raushankit.ILghts.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.raushankit.ILghts.R;

public class WebViewDialogFragment extends DialogFragment {
    private static final String TAG = "WebViewDialogFragment";

    private String url;
    private WebView webView;
    private ProgressBar progressBar;
    private TextView textView;
    private MaterialButton okButton;
    private DialogInterface.OnClickListener listener;

    public WebViewDialogFragment() {

    }

    public static WebViewDialogFragment newInstance() {
        return new WebViewDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_web_view, container, false);
        if(url == null){
            Log.w(TAG, "onCreateView: np url to load");
        }
        textView = view.findViewById(R.id.frag_web_view_title_text);
        textView.setText(url);
        progressBar = view.findViewById(R.id.frag_web_view_progress_bar);
        webView = view.findViewById(R.id.frag_web_view_container);
        okButton = view.findViewById(R.id.frag_web_view_button);
        initWebView();
        return view;
    }

    private void initWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(false);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if(newProgress < 100){
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                }else{
                    progressBar.setVisibility(View.GONE);
                }
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                textView.setText(title);
                super.onReceivedTitle(view, title);
            }
        });
        webView.loadUrl(url);
    }

    public void setUrl(@NonNull String url) {
        this.url = url;
    }

    @Override
    public void onStart() {
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.95);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.9);
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
        }
        super.onStart();
    }

    public void addDialogButtonClickListener(@NonNull DialogInterface.OnClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        okButton.setOnClickListener(v -> {
            if(listener != null) listener.onClick(null, DialogInterface.BUTTON_POSITIVE);
            dismiss();
        });
    }
}
