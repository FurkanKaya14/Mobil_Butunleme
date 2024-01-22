package com.example.mobil5;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.mobil5.databinding.ActivitySelamBinding;


public class SelamActivity extends AppCompatActivity {

    private Button selam_buton1;
    private Button selam_buton2;
    private AppBarConfiguration appBarConfiguration;
    private ActivitySelamBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selam);
        selam_buton1 = findViewById(R.id.selam_buton1);
        selam_buton2 = findViewById(R.id.selam_buton2);
    }

    public void ilk_buton1(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void ilk_buton2(View view) {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }
}