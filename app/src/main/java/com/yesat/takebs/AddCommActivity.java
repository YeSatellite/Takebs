package com.yesat.takebs;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;

public class AddCommActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comm);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final EditText comment = (EditText) findViewById(R.id.comment);
        final RatingBar bar = (RatingBar) findViewById(R.id.ratingBar2);

        findViewById(R.id.btn_submit)
                .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("rating", (int)bar.getRating()+"");
                resultIntent.putExtra("comment", comment.getText().toString());
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });


    }
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
