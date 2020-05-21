package com.example.canertasanhomework3;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CommentsActivity extends AppCompatActivity {
    List<CommentItem> data;
    ProgressDialog prgDialog;
    NewsItem newItem;
    RecyclerView comRecView;
    CommentsAdapter adp;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        data = new ArrayList<>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comments_layout);
        setTitle("Post Comments");
        ActionBar currBar = getSupportActionBar();
        currBar.setHomeButtonEnabled(true);
        currBar.setDisplayHomeAsUpEnabled(true);
        currBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24px);
        comRecView = findViewById(R.id.comrec);
        adp = new CommentsAdapter(data, this);
        comRecView.setLayoutManager(new LinearLayoutManager(this));
        comRecView.setAdapter(adp);
        newItem = (NewsItem)getIntent().getSerializableExtra("new");
        String url = "http://94.138.207.51:8080/NewsApp/service/news/getcommentsbynewsid/" + newItem.getId();
        CommentTask tsk = new CommentTask();
        tsk.execute(url);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_comment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.mn_add_comment){
            Log.e("DEV","comment item clicked");
            Intent i = new Intent(this, PostCommentActivity.class);
            i.putExtra("new_item", newItem);
            startActivity(i);
        }
        else if(item.getItemId() == android.R.id.home){
            finish();
            // go back here
        }
        return true;
    }

    class CommentTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            prgDialog = new ProgressDialog(CommentsActivity.this);
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
            data.clear();
            try {
                JSONObject obj = new JSONObject(s);

                if (obj.getInt("serviceMessageCode") == 1) {

                    JSONArray arr = obj.getJSONArray("items");
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject curr = (JSONObject) arr.get(i);
                        CommentItem ct = new CommentItem(curr.getInt("id"), curr.getString("name"), curr.getString("text"));
                        data.add(ct);
                    }
                } else {
                    //alert dialog in here
                }
                adp.notifyDataSetChanged();
                prgDialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
