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
    private View layoutStartScreen;
    private LinearLayout layoutGameOver;
    private TextView txtWinner;

    // Bien doi ben ban co
    private boolean isCurrentlyFlipped = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boardView = findViewById(R.id.chessBoardView);
        layoutStartScreen = findViewById(R.id.layoutStartScreen);
        layoutGameOver = findViewById(R.id.layoutGameOver);
        txtWinner = findViewById(R.id.txtWinner);

        Button btnStartGame = findViewById(R.id.btnStartGame);
        Button btnRematch = findViewById(R.id.btnRematch);

        initGame(false);
        btnStartGame.setOnClickListener(v -> {
            layoutStartScreen.setVisibility(View.GONE);
            isCurrentlyFlipped = false;
            initGame(isCurrentlyFlipped);
        });

        boardView.setGameListener(winnerText -> {
            layoutGameOver.setVisibility(View.VISIBLE);
            txtWinner.setText(winnerText);
        });

        btnRematch.setOnClickListener(v -> {
            layoutGameOver.setVisibility(View.GONE);

            isCurrentlyFlipped = !isCurrentlyFlipped;

            initGame(isCurrentlyFlipped);
        });
    }

    private void initGame(boolean flipBoard) {
        pieceList = new ArrayList<>();

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

        boardView.resetGame(pieceList, flipBoard);
    }
}