package com.example.doancotuong;

import android.os.Bundle;
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

    // Bien doi ben ban co
    private boolean isCurrentlyFlipped = false;
    private List<String> gameHistory = new ArrayList<>();
    private String gameMode = "NORMAL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boardView = findViewById(R.id.chessBoardView);
        layoutGameOver = findViewById(R.id.layoutGameOver);
        txtWinner = findViewById(R.id.txtWinner);

        Button btnRematch = findViewById(R.id.btnRematch);
        Button btnHistory = findViewById(R.id.btnHistory);

        if (getIntent().hasExtra("GAME_MODE")) {
            gameMode = getIntent().getStringExtra("GAME_MODE");
        }

        initGame(false);

        boardView.setGameListener(winnerText -> {
            layoutGameOver.setVisibility(View.VISIBLE);
            txtWinner.setText(winnerText);
            gameHistory.add("Ván " + (gameHistory.size() + 1) + ": " + winnerText);
        });

        btnRematch.setOnClickListener(v -> {
            layoutGameOver.setVisibility(View.GONE);
            isCurrentlyFlipped = !isCurrentlyFlipped;
            initGame(isCurrentlyFlipped);
        });

        btnHistory.setOnClickListener(v -> {
            if (gameHistory.isEmpty()) {
                gameHistory.add("Chưa có ván nào hoàn thành.");
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