package com.example.chunyung.allmusic;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class SearchFragment extends Fragment {

    public SearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        final Spinner spinner = (Spinner) rootView.findViewById(R.id.typeSpinner);
        final TextView keywordField = (TextView) rootView.findViewById(R.id.keywords);
        String[] arraySpinner = getResources().getStringArray(R.array.types);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, arraySpinner);
        spinner.setAdapter(spinnerAdapter);
        final Button searchButton = (Button) rootView.findViewById(R.id.searchButton);
        keywordField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(keywordField);
                }
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = spinner.getSelectedItem().toString().toLowerCase();
                String url = "http://1-dot-allmusic-1057.appspot.com/" + type;
                String[] keywords = keywordField.getText().toString().split("\\s+");
                Query query = new Query();
                for (String keyword : keywords) {
                    if (!keyword.isEmpty()) {
                        query.keywords.add(keyword);
                    }
                }
                if (query.keywords.size() == 0) {
                    TextView reportArea = (TextView) rootView.findViewById(R.id.ErrorReport);
                    reportArea.setText("Please input keywords!!");
                } else {
                    FetchJSON fetchjson = new FetchJSON((MainActivity) getActivity(), query.toJSON());
                    fetchjson.execute(url); // fetch JSON result in AsyncTask
                    hideKeyboard(searchButton);
                }
            }
        });
        return rootView;
    }
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
