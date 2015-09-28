package com.example.chunyung.allmusic;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FetchJSON extends AsyncTask<String, Integer, String> {// AsyncTask used to fetch JSON information according to keywords
    private JSONObject jsonResult;
    private ProgressDialog pDialog;
    private Activity currActivity;
    private String querry;
    public FetchJSON(Activity activity, String querry) {
        super();
        this.currActivity = activity;
        this.querry = querry;
    }

    @Override
    protected String doInBackground(String... params) {//request JSON info using POST method
        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection) new URL(params[0]).openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Content-Length", String.valueOf(this.querry.getBytes().length));
            new DataOutputStream(connection.getOutputStream()).writeBytes(querry);
            publishProgress(1);
            return new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8")).readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(currActivity);
        pDialog.setMessage("Searching...");
        pDialog.setCancelable(false);
        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pDialog.setMax(1);
        pDialog.show();
    }

    /*public JSONObject getResult() {
        return this.jsonResult;
    }
    */

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        pDialog.setProgress(100);
        pDialog.dismiss();
        if (s == null) {
            this.jsonResult = null;
        }
        try {
            this.jsonResult = new JSONObject(s);
        } catch (JSONException e) {
            e.printStackTrace();
            TextView reportArea = (TextView) this.currActivity.findViewById(R.id.ErrorReport);
            reportArea.setText("Internet connection fails!, please try later!");
            return;
        }
        buildTableAdapter(jsonResult); //call to create result table
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        pDialog.setProgress(values[0]);
    }

    private String getTitleJSON(String type) {//return the title strings for different search types
        if (type.equals("artists")) {
            return "{\"text\":\"Cover\", \"artist\":\"Artist\", \"genre\":\"Genre\", \"year\":\"Year\"}";
        } else if (type.equals("albums")) {
            return "{\"text\":\"Cover\", \"title\":\"Title\", \"artist\":\"Artist\", \"genre\":\"Genre\", \"year\":\"Year\"}";
        } else {
            return "{\"title\":\"Title\", \"performer\":\"Performer\", \"composer\":\"Composer\"}";
        }
    }

    private void buildTableAdapter(JSONObject jsonobject) {//create result table with according JSON return
        String type = jsonobject.keys().next();
        int totalWidth = this.currActivity.getWindowManager().getDefaultDisplay().getWidth();
        int totalHeight = this.currActivity.getWindowManager().getDefaultDisplay().getHeight();
        JSONArray data = null;
        try {
            data = jsonobject.getJSONArray(type);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        List<String> urls = new ArrayList<String>();
        List<TableAdapter.TableRow> rows = new ArrayList<TableAdapter.TableRow>();
        String jsonTitle = getTitleJSON(type);
        int imgCell = -1, cellsNum = 0;
        try {
            cellsNum = data.getJSONObject(0).length() - 1;// get nums of cells for different search type
            data.put(0, new JSONObject(jsonTitle));  //put title strings in JSON object in order to output title cell
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int index = 0; index < data.length(); index++) {// insert result cells into table
            List<TableAdapter.TableCell> cells = new ArrayList<TableAdapter.TableCell>();
            String details = null;
            try {
                if (index > 0) {
                    imgCell = createCells(data.getJSONObject(index), urls, cells, totalHeight / 8, totalWidth / cellsNum); // create info cells
                    details = data.getJSONObject(index).getString("details"); //get links for each cell
                } else {
                    imgCell = createCells(data.getJSONObject(index), urls, cells, 50, totalWidth / cellsNum);// create title cell
                }
                rows.add(new TableAdapter.TableRow(cells, details)); //add cells into table
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        ImageDownloader downloader = new ImageDownloader(this.currActivity, rows, imgCell);
        downloader.execute(urls); // start to download albums' or singers' photo in another AsyncTask
    }

    private int createCells(JSONObject jsonObject, List<String> urls, List<TableAdapter.TableCell> cells, int height, int width) {
        Iterator<String> it = jsonObject.keys();
        int imgIndex = -1;
        while (it.hasNext()) {
            String key = it.next();
            try {
                if (key.equals("cover")) {
                    urls.add(jsonObject.get(key).toString());
                    imgIndex = cells.size();
                    cells.add(new TableAdapter.TableCell("IMG", jsonObject.get(key).toString(),height, width));
                } else if (!key.equals("details")) {
                    String content = jsonObject.get(key).toString().equals("null") ? "N.A.":jsonObject.get(key).toString();
                    cells.add(new TableAdapter.TableCell("TEXT", content, height, width));
                }
            } catch (JSONException e) {
            }
        }
        return imgIndex;
    }
}
