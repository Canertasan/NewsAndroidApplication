package com.example.canertasanhomework3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.contentcapture.ContentCaptureCondition;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity{
    ProgressDialog prgDialog;
    ProgressDialog prgDialogCategory;
    RecyclerView newsRecView;
    List<NewsItem> data;
    List<CategoryItem> categories;
    NewsAdapter adp;
    Spinner spCategories;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        data = new ArrayList<>();
        categories = new ArrayList<>();
        setTitle("News");
        spCategories = findViewById(R.id.spCategories);
        spCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               String selectedCategory = spCategories.getSelectedItem().toString();
               for (int i = 0; i< categories.size();i++){
                   if(categories.get(i).getName().equals(selectedCategory)){
                       NewsTask tsk = new NewsTask();
                       String url = "http://94.138.207.51:8080/NewsApp/service/news/getbycategoryid/" + categories.get(i).getId();
                       tsk.execute(url);
                       break;
                   }
                   else if(selectedCategory.equals("All")){
                       NewsTask tsk = new NewsTask();
                       tsk.execute("http://94.138.207.51:8080/NewsApp/service/news/getall");
                       break;
                   }
               }
           }

           @Override
           public void onNothingSelected(AdapterView<?> parent) {
               Log.e("DEV", "nothing select");
           }
       });
        newsRecView = findViewById(R.id.newsrec);
        adp = new NewsAdapter(data, this, new NewsAdapter.NewsItemClickListener() {
            @Override
            public void newItemClicked(NewsItem selectedNewsItem) {
                Intent i = new Intent(getApplicationContext(), NewsDetailActivity.class);
                i.putExtra("new", selectedNewsItem);
                startActivity(i);
            }
        });
        newsRecView.setLayoutManager(new LinearLayoutManager(this));
        newsRecView.setAdapter(adp);

        NewsTask tsk = new NewsTask();
        tsk.execute("http://94.138.207.51:8080/NewsApp/service/news/getall");

        CategoryTask ct = new CategoryTask();
        ct.execute("http://94.138.207.51:8080/NewsApp/service/news/getallnewscategories");
    }

    class CategoryTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            prgDialogCategory = new ProgressDialog(MainActivity.this);
            prgDialogCategory.setTitle("Loading");
            prgDialogCategory.setMessage("Please wait...");
            prgDialogCategory.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            prgDialogCategory.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String urlStr = strings[0];
            StringBuilder buffer = new StringBuilder();
            try {
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return buffer.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            // categories.clear();
            try {
                JSONObject obj = new JSONObject(s);

                if (obj.getInt("serviceMessageCode") == 1) {

                    JSONArray arr = obj.getJSONArray("items");
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject curr = (JSONObject) arr.get(i);
                        CategoryItem ct = new CategoryItem(curr.getInt("id"), curr.getString("name"));
                        categories.add(ct);
                    }
                } else {
                    //alert dialog in here
                }
                prgDialogCategory.dismiss();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class NewsTask extends AsyncTask<String,Void,String>{

        @Override
        protected void onPreExecute() {
            prgDialog = new ProgressDialog(MainActivity.this);
            prgDialog.setTitle("Loading");
            prgDialog.setMessage("Please wait...");
            prgDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            prgDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String urlStr = strings[0];
            StringBuilder buffer = new StringBuilder();
            try {
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String line = "";
                while((line = reader.readLine()) != null){
                    buffer.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return buffer.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            if(data.size() != 0){
                data.clear();
            }
            try {
                JSONObject obj = new JSONObject(s);

                if(obj.getInt("serviceMessageCode") == 1){

                    JSONArray arr = obj.getJSONArray("items");
                    for (int i = 0; i< arr.length();i++){
                        JSONObject curr = (JSONObject) arr.get(i);
                        long date = curr.getLong("date");
                        Date objDate = new Date(date);
                        NewsItem item = new NewsItem(curr.getInt("id"),
                                curr.getString("title"),
                                curr.getString("text"),
                                curr.getString("image"),
                                objDate
                        );
                        data.add(item);
                    }
                }
                else{
                    //alert dialog in here
                }
                adp.notifyDataSetChanged();
                prgDialog.dismiss();
            } catch (JSONException e) {
                Log.e("DEV", e.getMessage());
            }

        }
    }
}
