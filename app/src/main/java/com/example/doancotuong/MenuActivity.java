package com.example.doancotuong;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView; // Nhớ là hôm qua đổi sang ImageView rồi nhé
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu);

        ImageView btnCoTuong = findViewById(R.id.btnCoTuong);
        ImageView btnCoUp = findViewById(R.id.btnCoUp);
        android.widget.Button btnHistory = findViewById(R.id.btnHistory);

        btnCoTuong.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, MainActivity.class);
            intent.putExtra("GAME_MODE", "NORMAL");
            startActivity(intent);
        });

        btnCoUp.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, MainActivity.class);
            intent.putExtra("GAME_MODE", "UP");
            startActivity(intent);
        });

        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, HistoryActivity.class);
            startActivity(intent);
        });
    }
}