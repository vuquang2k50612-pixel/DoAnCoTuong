package com.example.doancotuong;

import android.content.Context;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameRecordManager {
    private static final String FILE_NAME = "game_records.dat";
    private Context context;

    public GameRecordManager(Context context) {
        this.context = context;
    }

    public void saveGameRecord(GameRecord record) {
        try {
            List<GameRecord> records = loadAllRecords();
            records.add(0, record); // Add at the beginning (newest first)
            saveAllRecords(records);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<GameRecord> loadAllRecords() {
        try {
            File file = new File(context.getFilesDir(), FILE_NAME);
            if (!file.exists()) {
                return new ArrayList<>();
            }

            FileInputStream fis = context.openFileInput(FILE_NAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            List<GameRecord> records = (List<GameRecord>) ois.readObject();
            ois.close();
            fis.close();
            return records != null ? records : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void saveAllRecords(List<GameRecord> records) {
        try {
            FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(records);
            oos.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearAllRecords() {
        try {
            File file = new File(context.getFilesDir(), FILE_NAME);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getTotalGames() {
        return loadAllRecords().size();
    }

    public int getWinCount(String player) {
        int count = 0;
        for (GameRecord record : loadAllRecords()) {
            if (record.getWinner().equals(player)) {
                count++;
            }
        }
        return count;
    }

    public int getLossCount(String player) {
        int count = 0;
        for (GameRecord record : loadAllRecords()) {
            if (player.equals(record.getLoser())) {
                count++;
            }
        }
        return count;
    }

    public int getDrawCount() {
        int count = 0;
        for (GameRecord record : loadAllRecords()) {
            if (record.getLoser() == null) {
                count++;
            }
        }
        return count;
    }
}
