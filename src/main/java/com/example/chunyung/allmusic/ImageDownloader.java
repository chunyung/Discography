package com.example.chunyung.allmusic;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ImageDownloader extends AsyncTask<List<String>, Integer, String> { // An asyncTask used to download photos for display
    private List<String> imgPath;
    private List<TableAdapter.TableRow> rows;
    private int imgIndex, urls;
    private Activity activity;
    private ProgressDialog pDialog;

    public ImageDownloader(Activity activity, List<TableAdapter.TableRow> rows, int imgIndex) {
        super();
        this.activity = activity;
        this.imgPath = new ArrayList<String>();
        this.rows = rows;
        this.imgIndex = imgIndex;
        this.urls = rows.size();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.pDialog = new ProgressDialog(activity);
        this.pDialog.setMessage("Retrieving Data...");
        this.pDialog.setCancelable(false);
        this.pDialog.setMax(this.urls);
        this.pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        this.pDialog.show();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (this.imgIndex != -1) {
            int index = 0;
            for (int i = 1; i < this.rows.size(); i++) {
                rows.get(i).getCell(this.imgIndex).value = this.imgPath.get(index++);
            }
        }
        ((MainActivity)this.activity).tableAdapter = new TableAdapter(this.activity, this.rows);
        pDialog.dismiss();
        Fragment showResult = new ResultFragment();
        FragmentManager manager = this.activity.getFragmentManager();
        FragmentTransaction trans = manager.beginTransaction();
        trans.replace(R.id.main_fragment, showResult).addToBackStack(null).commit();
        manager.executePendingTransactions();
    }

    @Override
    protected String doInBackground(List<String>... params) {
        HttpURLConnection connection;
        int index = 0;
        for (String url : params[0]) {
            if (url != null && !url.isEmpty()) {
                InputStream in = null;
                try {
                    in = new URL(url).openStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                this.imgPath.add(saveTolocalStorage(BitmapFactory.decodeStream(in), index)); // record the download path for each photos
            } else {
                this.imgPath.add("");
            }
            index++;
            publishProgress(index);
        }
        return "OK";
    }

    private String saveTolocalStorage(Bitmap bitmap, int id) { // save downloaded images to local storage
        ContextWrapper wrapper = new ContextWrapper(activity.getApplicationContext());
        File dir = wrapper.getDir("imgs", Context.MODE_PRIVATE);
        File savedIMG = new File(dir, String.valueOf(id) + ".jpg");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(savedIMG);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return savedIMG.getAbsolutePath();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        this.pDialog.setProgress(values[0]);
    }
}
