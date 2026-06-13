package com.example.doancotuong;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ChessBoardView boardView;
    private List<Piece> pieceList = new ArrayList<>();

    // Cac bien giao dien
    private LinearLayout layoutGameOver;
    private TextView txtWinner;
    private TextView tvRedTimer, tvBlackTimer;

    // Bien doi ben ban co
    private boolean isCurrentlyFlipped = false;
    private List<String> gameHistory = new ArrayList<>();
    private String gameMode = "NORMAL";

    private Handler timerHandler = new Handler();
    private boolean isRedTurnTimer = true;
    private boolean isGameRunning = false;

    private final int MAX_TOTAL_TIME = 15 * 60;
    private final int MAX_TURN_TIME = 60;

    private int redTotalTime = MAX_TOTAL_TIME;
    private int blackTotalTime = MAX_TOTAL_TIME;
    private int currentTurnTime = MAX_TURN_TIME;


    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isGameRunning) return;

            if (isRedTurnTimer) {
                redTotalTime--;
                currentTurnTime--;
                if (currentTurnTime <= 0 || redTotalTime <= 0) {
                    timeOutWin("ĐEN THẮNG! (Đỏ hết giờ)");
                    return;
                }
            } else {
                blackTotalTime--;
                currentTurnTime--;
                if (currentTurnTime <= 0 || blackTotalTime <= 0) {
                    timeOutWin("ĐỎ THẮNG! (Đen hết giờ)");
                    return;
                }
            }

            updateTimerUI();
            timerHandler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boardView = findViewById(R.id.chessBoardView);
        layoutGameOver = findViewById(R.id.layoutGameOver);
        txtWinner = findViewById(R.id.txtWinner);
        tvRedTimer = findViewById(R.id.tvRedTimer);
        tvBlackTimer = findViewById(R.id.tvBlackTimer);

        Button btnRematch = findViewById(R.id.btnRematch);
        Button btnHistory = findViewById(R.id.btnHistory);

        if (getIntent().hasExtra("GAME_MODE")) {
            gameMode = getIntent().getStringExtra("GAME_MODE");
        }

        initGame(false);

        // BẢN MỚI CẬP NHẬT: Lắng nghe Chiếu Bí và Lắng nghe Đổi Lượt
        boardView.setGameListener(new ChessBoardView.GameListener() {
            @Override
            public void onCheckmate(String winnerText) {
                stopTimer(); // Bị chiếu bí thì dừng đồng hồ
                layoutGameOver.setVisibility(View.VISIBLE);
                txtWinner.setText(winnerText);
                gameHistory.add("Ván " + (gameHistory.size() + 1) + ": " + winnerText);
            }

            @Override
            public void onTurnChanged(boolean isRedTurn) {
                // Đổi phe -> Reset 60s cho người tiếp theo
                isRedTurnTimer = isRedTurn;
                currentTurnTime = MAX_TURN_TIME;
                updateTimerUI();
            }
        });

        btnRematch.setOnClickListener(v -> {
            layoutGameOver.setVisibility(View.GONE);
            isCurrentlyFlipped = !isCurrentlyFlipped;
            initGame(isCurrentlyFlipped);
        });

        btnHistory.setOnClickListener(v -> {
            // Thêm dữ liệu mẫu nếu lịch sử trống để bạn kiểm tra
            if (gameHistory.isEmpty()) {
                gameHistory.add("Ván 1: ĐỎ THẮNG! (Số nước đi: 25)");
                gameHistory.add("Ván 2: ĐEN THẮNG! (Số nước đi: 32)");
                gameHistory.add("Ván 3: ĐỎ THẮNG! (Số nước đi: 18)");
            }

            StringBuilder historyText = new StringBuilder("Lịch sử ván đấu:\n\n");
            for (String record : gameHistory) {
                historyText.append(record).append("\n");
            }
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Lịch sử")
                    .setMessage(historyText.toString())
                    .setPositiveButton("Đóng", null)
                    .show();
        });

        Button btnSurrenderTop = findViewById(R.id.btnSurrenderTop);
        Button btnDrawTop = findViewById(R.id.btnDrawTop);
        Button btnSurrenderBottom = findViewById(R.id.btnSurrenderBottom);
        Button btnDrawBottom = findViewById(R.id.btnDrawBottom);


        btnSurrenderBottom.setOnClickListener(v -> {
            if (!isGameRunning) return;
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Đỏ Đầu Hàng?")
                    .setMessage("Bạn chắc chắn xin thua không?")
                    .setPositiveButton("Chấp nhận thua", (dialog, which) -> timeOutWin("ĐEN THẮNG! (Đỏ xin hàng)"))
                    .setNegativeButton("Đánh tiếp", null)
                    .show();
        });

        btnDrawBottom.setOnClickListener(v -> {
            if (!isGameRunning) return;
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Đỏ Xin Hòa")
                    .setMessage("Người chơi 1 (Đỏ) muốn hòa. Đen có đồng ý không?")
                    .setPositiveButton("Đồng ý hòa", (dialog, which) -> timeOutWin("HÒA NHAU!"))
                    .setNegativeButton("Không đồng ý", (dialog, which) ->
                            android.widget.Toast.makeText(this, "Đen từ chối! Đánh tiếp!", android.widget.Toast.LENGTH_SHORT).show()
                    ).setCancelable(false).show();
        });

        btnSurrenderTop.setOnClickListener(v -> {
            if (!isGameRunning) return;
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Đen Đầu Hàng?")
                    .setMessage("Bạn chắc chắn xin thua không?")
                    .setPositiveButton("Chấp nhận thua", (dialog, which) -> timeOutWin("ĐỎ THẮNG! (Đen xin hàng)"))
                    .setNegativeButton("Đánh tiếp", null)
                    .show();
        });

        btnDrawTop.setOnClickListener(v -> {
            if (!isGameRunning) return;
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Đen Xin Hòa")
                    .setMessage("Người chơi 2 (Đen) muốn hòa. Đỏ có đồng ý không?")
                    .setPositiveButton("Đồng ý hòa", (dialog, which) -> timeOutWin("HÒA NHAU!"))
                    .setNegativeButton("Đánh tiếp", (dialog, which) ->
                            android.widget.Toast.makeText(this, "Đỏ từ chối! Đánh tiếp!", android.widget.Toast.LENGTH_SHORT).show()
                    ).setCancelable(false).show();
        });
    }


    private void startTimer() {
        stopTimer();
        redTotalTime = MAX_TOTAL_TIME;
        blackTotalTime = MAX_TOTAL_TIME;
        currentTurnTime = MAX_TURN_TIME;
        isRedTurnTimer = true;
        isGameRunning = true;
        updateTimerUI();
        timerHandler.postDelayed(timerRunnable, 1000);
    }

    private void stopTimer() {
        isGameRunning = false;
        timerHandler.removeCallbacks(timerRunnable);
    }

    private void timeOutWin(String winnerText) {
        stopTimer();
        layoutGameOver.setVisibility(View.VISIBLE);
        txtWinner.setText(winnerText);
        gameHistory.add("Ván " + (gameHistory.size() + 1) + ": " + winnerText);
    }

    private void updateTimerUI() {
        int rMin = redTotalTime / 60;
        int rSec = redTotalTime % 60;

        String redText = String.format("Người chơi 1 (Đỏ) - Time :%02d:%02d | %02ds", rMin, rSec, (isRedTurnTimer ? currentTurnTime : 0));
        tvRedTimer.setText(redText);
        int bMin = blackTotalTime / 60;
        int bSec = blackTotalTime % 60;
        // Chuỗi định dạng đã được đổi thành Người chơi 2 (Đen)
        String blackText = String.format("Người chơi 2 (Đen) - Time :%02d:%02d | %02ds", bMin, bSec, (!isRedTurnTimer ? currentTurnTime : 0));
        tvBlackTimer.setText(blackText);
        tvRedTimer.setTextColor(isRedTurnTimer ? android.graphics.Color.parseColor("#D32F2F") : android.graphics.Color.parseColor("#9E9E9E"));
        tvBlackTimer.setTextColor(!isRedTurnTimer ? android.graphics.Color.parseColor("#D32F2F") : android.graphics.Color.parseColor("#9E9E9E"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isCurrentlyFlipped", isCurrentlyFlipped);
        outState.putStringArrayList("gameHistory", new ArrayList<>(gameHistory));
        outState.putSerializable("pieceList", new ArrayList<>(pieceList));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isCurrentlyFlipped = savedInstanceState.getBoolean("isCurrentlyFlipped", false);
        gameHistory = savedInstanceState.getStringArrayList("gameHistory");
        if (gameHistory == null) {
            gameHistory = new ArrayList<>();
        }

        List<Piece> savedPieces = (List<Piece>) savedInstanceState.getSerializable("pieceList");
        if (savedPieces != null) {
            pieceList = savedPieces;
            boardView.setPieces(pieceList);
        }
    }

    private void initGame(boolean flipBoard) {
        pieceList = new ArrayList<>();
        startTimer();

        if ("UP".equals(gameMode)) {
            pieceList.add(new Piece(Piece.Type.Tuong_Quan, Piece.Color.Black, 4, 0, R.drawable.b_king));
            pieceList.add(new Piece(Piece.Type.Tuong_Quan, Piece.Color.Red, 4, 9, R.drawable.r_king));

            List<Piece> redBag = new ArrayList<>();
            List<Piece> blackBag = new ArrayList<>();

            Piece.Type[] types = {Piece.Type.Xe, Piece.Type.Ma, Piece.Type.Tuong, Piece.Type.Si, Piece.Type.Phao};
            int[] redRes = {R.drawable.r_xe, R.drawable.r_ma, R.drawable.r_tuong, R.drawable.r_si, R.drawable.r_phao};
            int[] blackRes = {R.drawable.b_xe, R.drawable.b_ma, R.drawable.b_tuong, R.drawable.b_si, R.drawable.b_phao};

            for (int i = 0; i < 5; i++) {
                redBag.add(new Piece(types[i], Piece.Color.Red, 0, 0, redRes[i]));
                redBag.add(new Piece(types[i], Piece.Color.Red, 0, 0, redRes[i]));
                blackBag.add(new Piece(types[i], Piece.Color.Black, 0, 0, blackRes[i]));
                blackBag.add(new Piece(types[i], Piece.Color.Black, 0, 0, blackRes[i]));
            }
            for (int i = 0; i < 5; i++) {
                redBag.add(new Piece(Piece.Type.Tot, Piece.Color.Red, 0, 0, R.drawable.r_tot));
                blackBag.add(new Piece(Piece.Type.Tot, Piece.Color.Black, 0, 0, R.drawable.b_tot));
            }

            java.util.Collections.shuffle(redBag);
            java.util.Collections.shuffle(blackBag);

            int[] blackStartX = {0, 1, 2, 3, 5, 6, 7, 8, 1, 7, 0, 2, 4, 6, 8};
            int[] blackStartY = {0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 3, 3, 3, 3, 3};
            for (int i = 0; i < 15; i++) {
                Piece p = blackBag.get(i);
                p.x = blackStartX[i];
                p.y = blackStartY[i];
                p.isFaceDown = true;
                pieceList.add(p);
            }

            int[] redStartX = {0, 1, 2, 3, 5, 6, 7, 8, 1, 7, 0, 2, 4, 6, 8};
            int[] redStartY = {9, 9, 9, 9, 9, 9, 9, 9, 7, 7, 6, 6, 6, 6, 6};
            for (int i = 0; i < 15; i++) {
                Piece p = redBag.get(i);
                p.x = redStartX[i];
                p.y = redStartY[i];
                p.isFaceDown = true;
                pieceList.add(p);
            }

        } else {
            Piece.Type[] rowTypes = {
                    Piece.Type.Xe, Piece.Type.Ma, Piece.Type.Tuong,
                    Piece.Type.Si, Piece.Type.Tuong_Quan, Piece.Type.Si,
                    Piece.Type.Tuong, Piece.Type.Ma, Piece.Type.Xe
            };

            int[] blackRow0 = {R.drawable.b_xe, R.drawable.b_ma, R.drawable.b_tuong, R.drawable.b_si, R.drawable.b_king, R.drawable.b_si, R.drawable.b_tuong, R.drawable.b_ma, R.drawable.b_xe};
            for (int i = 0; i < 9; i++) {
                pieceList.add(new Piece(rowTypes[i], Piece.Color.Black, i, 0, blackRow0[i]));
            }

            pieceList.add(new Piece(Piece.Type.Phao, Piece.Color.Black, 1, 2, R.drawable.b_phao));
            pieceList.add(new Piece(Piece.Type.Phao, Piece.Color.Black, 7, 2, R.drawable.b_phao));

            for (int i = 0; i < 9; i += 2) {
                pieceList.add(new Piece(Piece.Type.Tot, Piece.Color.Black, i, 3, R.drawable.b_tot));
            }

            int[] redRow9 = {R.drawable.r_xe, R.drawable.r_ma, R.drawable.r_tuong, R.drawable.r_si, R.drawable.r_king, R.drawable.r_si, R.drawable.r_tuong, R.drawable.r_ma, R.drawable.r_xe};
            for (int i = 0; i < 9; i++) {
                pieceList.add(new Piece(rowTypes[i], Piece.Color.Red, i, 9, redRow9[i]));
            }

            pieceList.add(new Piece(Piece.Type.Phao, Piece.Color.Red, 1, 7, R.drawable.r_phao));
            pieceList.add(new Piece(Piece.Type.Phao, Piece.Color.Red, 7, 7, R.drawable.r_phao));

            for (int i = 0; i < 9; i += 2) {
                pieceList.add(new Piece(Piece.Type.Tot, Piece.Color.Red, i, 6, R.drawable.r_tot));
            }
        }

        boardView.resetGame(pieceList, flipBoard);
    }
}