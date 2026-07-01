package com.example.doancotuong;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu);



        ImageView btnCoTuong = findViewById(R.id.btnCoTuong);
        ImageView btnCoUp = findViewById(R.id.btnCoUp);
        Button btnHistory = findViewById(R.id.btnHistory);

        btnCoTuong.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, MainActivity.class);
            intent.putExtra("GAME_MODE", "NORMAL"); // Gửi tín hiệu chế độ NORMAL
            startActivity(intent);
        });

        btnCoUp.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, MainActivity.class);
            intent.putExtra("GAME_MODE", "UP"); // Gửi tín hiệu chế độ UP
            startActivity(intent);
        });

        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, HistoryActivity.class);
            startActivity(intent);
        });
    }
}