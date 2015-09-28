package com.example.chunyung.allmusic;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

public class ResultFragment extends Fragment {
    public ResultFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_result, container, false);
        ListView result = (ListView)rootView.findViewById(R.id.resultView);
        result.setAdapter(((MainActivity) getActivity()).tableAdapter); // show result in the ListView
        result.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) { // set on click event for each cell in order to show further information like urls, post to FB
                if (position == 0) {
                    return;
                }
                final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View dialogview = inflater.inflate(R.layout.dialog, null);
                Button btnPost = (Button) dialogview.findViewById(R.id.Postbtn);// button for posting to FB
                Button btnDetail = (Button) dialogview.findViewById(R.id.detailbtn); // button to open detail webpage on AllMusic.com
                Button btnCancel = (Button) dialogview.findViewById(R.id.calcelbtn); // button to close the dialog
                final String details = ((TableAdapter.TableRow) ((TableAdapter) parent.getAdapter()).getItem(position)).details;
                btnPost.setText("Post to FB");
                btnDetail.setText("Details");
                btnCancel.setText("Cancel");
                btnDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(details));
                        startActivity(browserIntent);
                        alertDialog.cancel();
                    }
                });
                btnPost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ShareLinkContent content = new ShareLinkContent.Builder()
                                .setContentUrl(Uri.parse(details))
                                .build();
                        ShareDialog.show(getActivity(), content);
                        alertDialog.cancel();
                    }
                });
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.cancel();
                    }
                });
                alertDialog.setView(dialogview);
                alertDialog.setTitle("Please choose:");
                alertDialog.show();
            }
        });
        return rootView;
    }
}
