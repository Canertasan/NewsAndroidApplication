package com.example.canertasanhomework3;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class PostCommentActivity extends AppCompatActivity {
    ProgressDialog prgDialog;
    NewsItem newItem;
    EditText txtName;
    EditText txtMessage;
    Button btn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_comment_layout);
        setTitle("Comments");
        ActionBar currBar = getSupportActionBar();
        currBar.setHomeButtonEnabled(true);
        currBar.setDisplayHomeAsUpEnabled(true);
        currBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24px);
        newItem = (NewsItem)getIntent().getSerializableExtra("new_item");
        txtName = findViewById(R.id.txtName);
        txtMessage = findViewById(R.id.txtMessage);
        btn = findViewById(R.id.btnPost);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = txtName.getText().toString();
                String text = txtMessage.getText().toString();
                AnswerTask tsk = new AnswerTask(name, text, String.valueOf(newItem.getId()));
                tsk.execute("http://94.138.207.51:8080/NewsApp/service/news/savecomment");
                finish();
            }
        });
    }

    class AnswerTask extends AsyncTask<String, Void, String> {

        String name;
        String text;
        String news_id;

        public AnswerTask(String name, String text, String news_id) {
            this.name = name;
            this.text = text;
            this.news_id = news_id;
        }

        @Override
        protected void onPreExecute() {
            prgDialog = new ProgressDialog(PostCommentActivity.this);
            prgDialog.setTitle("Loading");
            prgDialog.setMessage("Please wait...");
            prgDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            prgDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String urlStr = strings[0];
            StringBuilder buffer = new StringBuilder();
            JSONObject obj = new JSONObject();
            try {
                obj.put("name",name);
                obj.put("text",text);
                obj.put("news_id",news_id);

                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type","application/json");
                conn.connect();

                DataOutputStream writer = new DataOutputStream(conn.getOutputStream());
                writer.writeBytes(obj.toString());
                if(conn.getResponseCode()==HttpURLConnection.HTTP_OK){

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    String line ="";

                    while ((line = reader.readLine())!=null){
                        buffer.append(line);
                    }


                }
            } catch (JSONException | MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return buffer.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            prgDialog.dismiss();
        }
    }

}
