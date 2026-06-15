package com.example.doancotuong;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private GameRecordManager recordManager;
    private LinearLayout historyContainer;
    private TextView tvTotalGames, tvRedWins, tvBlackWins, tvDraws;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recordManager = new GameRecordManager(this);

        tvTotalGames = findViewById(R.id.tvTotalGames);
        tvRedWins = findViewById(R.id.tvRedWins);
        tvBlackWins = findViewById(R.id.tvBlackWins);
        tvDraws = findViewById(R.id.tvDraws);
        historyContainer = findViewById(R.id.historyContainer);

        Button btnClearHistory = findViewById(R.id.btnClearHistory);
        Button btnBack = findViewById(R.id.btnBack);

        btnClearHistory.setOnClickListener(v -> showClearConfirmDialog());
        btnBack.setOnClickListener(v -> finish());

        loadHistoryData();
    }

    private void loadHistoryData() {
        List<GameRecord> records = recordManager.loadAllRecords();

        // Update statistics
        int totalGames = recordManager.getTotalGames();
        int redWins = recordManager.getWinCount("ĐỎ");
        int blackWins = recordManager.getWinCount("ĐEN");
        int draws = recordManager.getDrawCount();

        tvTotalGames.setText("Tổng số ván: " + totalGames);
        tvRedWins.setText("Đỏ thắng: " + redWins);
        tvBlackWins.setText("Đen thắng: " + blackWins);
        tvDraws.setText("Hòa: " + draws);

        // Clear previous history items
        historyContainer.removeAllViews();

        // Add history items
        if (records.isEmpty()) {
            TextView emptyView = new TextView(this);
            emptyView.setText("Chưa có ván cờ nào");
            emptyView.setTextSize(16);
            emptyView.setTextColor(getColor(android.R.color.darker_gray));
            emptyView.setPadding(20, 40, 20, 40);
            historyContainer.addView(emptyView);
        } else {
            for (int i = 0; i < records.size(); i++) {
                GameRecord record = records.get(i);
                historyContainer.addView(createHistoryItemView(record, i + 1));
            }
        }
    }

    private android.view.View createHistoryItemView(GameRecord record, int index) {
        LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.VERTICAL);
        itemLayout.setPadding(16, 16, 16, 16);
        itemLayout.setBackgroundColor(getColor(android.R.color.white));

        // Add bottom border with margin
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 8);
        itemLayout.setLayoutParams(params);

        // Elevation for card effect
        itemLayout.setElevation(4);

        // Game info in main text
        TextView tvInfo = new TextView(this);
        String gameType = record.getGameTypeDisplay();
        String moveCount = record.getTotalMoves() + " nước đi";
        String result = record.getWinner() + " thắng";
        tvInfo.setText(gameType + " | " + moveCount + " | " + result);
        tvInfo.setTextSize(16);
        tvInfo.setTextColor(0xFF5D4037); // Brown color instead of black
        tvInfo.setTypeface(null, android.graphics.Typeface.BOLD);
        itemLayout.addView(tvInfo);

        // Date
        TextView tvDate = new TextView(this);
        tvDate.setText(record.getFormattedDate());
        tvDate.setTextSize(12);
        tvDate.setTextColor(0xFF8D6E63); // Lighter brown
        tvDate.setPadding(0, 8, 0, 0);
        itemLayout.addView(tvDate);

        return itemLayout;
    }

    private void showClearConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xóa lịch sử")
                .setMessage("Bạn có chắc chắn muốn xóa tất cả lịch sử ván cờ?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    recordManager.clearAllRecords();
                    loadHistoryData();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
