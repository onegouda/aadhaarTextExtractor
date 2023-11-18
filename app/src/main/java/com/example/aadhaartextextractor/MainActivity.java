package com.example.aadhaartextextractor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void openRelevantActivity(View view){
        Intent intent=new Intent(this,aadhaarActivity.class);
        startActivity(intent);







    }
}