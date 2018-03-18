package io.github.vl4fhsdatr.appflask.ui.readme;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import io.github.vl4fhsdatr.appflask.R;

public class ReadmeFragment extends Fragment {

    @SuppressWarnings("unused")
    private static final String TAG = "ReadmeFragment";
    
    public ReadmeFragment() {

    }

    @SuppressWarnings("unused")
    public static ReadmeFragment newInstance() {
        return new ReadmeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_readme, container, false);
        WebView readmeContainer = view.findViewById(R.id.web_view_readme);
        readmeContainer.loadUrl("file:///android_asset/README.html");
        return view;
    }

}
