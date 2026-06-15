package com.example.doancotuong;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GameRecord implements Serializable {
    private String winner;      // "ĐỎ", "ĐEN", "HÒA"
    private String loser;       // "ĐỎ", "ĐEN", or null if draw
    private int totalMoves;     // Tổng số nước đi
    private String gameType;    // "NORMAL" (Cờ Tướng) hoặc "UP" (Cờ Úp)
    private long timestamp;     // Thời gian tạo ván cờ

    public GameRecord(String winner, String loser, int totalMoves, String gameType) {
        this.winner = winner;
        this.loser = loser;
        this.totalMoves = totalMoves;
        this.gameType = gameType;
        this.timestamp = System.currentTimeMillis();
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getLoser() {
        return loser;
    }

    public void setLoser(String loser) {
        this.loser = loser;
    }

    public int getTotalMoves() {
        return totalMoves;
    }

    public void setTotalMoves(int totalMoves) {
        this.totalMoves = totalMoves;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    public String getGameTypeDisplay() {
        return "UP".equals(gameType) ? "Cờ Úp" : "Cờ Tướng";
    }

    @Override
    public String toString() {
        return String.format("%s thắng - %s | %d nước | %s | %s",
                winner, getGameTypeDisplay(), totalMoves, getFormattedDate(), loser != null ? loser + " thua" : "Hòa");
    }
}
