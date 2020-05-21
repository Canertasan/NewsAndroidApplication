package com.example.canertasanhomework3;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.zip.Inflater;

public class NewsDetailActivity extends AppCompatActivity {
    TextView txtTitle;
    TextView txtDate;
    ImageView imgNewItem;
    TextView txtDesc;
    NewsItem newItem;
    public static int id = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_detail_layout);
        setTitle("News Details");
        ActionBar currBar = getSupportActionBar();
        currBar.setHomeButtonEnabled(true);
        currBar.setDisplayHomeAsUpEnabled(true);
        currBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24px);
        txtTitle = findViewById(R.id.txtTitle);
        txtDate = findViewById(R.id.txtDate);
        imgNewItem = findViewById(R.id.imgNewItem);
        txtDesc = findViewById(R.id.txtDesc);


        newItem = (NewsItem)getIntent().getSerializableExtra("new");
        id = newItem.getId();
        txtTitle.setText(newItem.getTitle());
        txtDate.setText(new SimpleDateFormat("dd/MM/yy").format(newItem.getNewsDate()));
        new ImageDownloadTask(imgNewItem).execute(newItem);
        imgNewItem.setImageBitmap(newItem.getBitmap());
        txtDesc.setText(newItem.getText());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.mn_comment){
            // open comment page with inflate
            Intent i = new Intent(getApplicationContext(), CommentsActivity.class);
            i.putExtra("new", newItem);
            startActivity(i);
        }
        else if(item.getItemId() == android.R.id.home){
            finish();
            // go back here
        }
        return true;
    }
}
