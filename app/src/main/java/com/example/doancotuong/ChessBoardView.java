package com.example.doancotuong;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChessBoardView extends View {
    private Bitmap boardImg;
    private List<Piece> pieces = new ArrayList<>();
    private Piece selectedPiece = null;
    private boolean isGameOver = false;
    private boolean isRedTurn = true;
    private boolean isFlipped = false;
    private int moveCount = 0;

    private boolean isCoUpMode = false;

    private Map<Integer, Bitmap> bitmapCache = new HashMap<>();
    private Paint shadowPaint;
    private SoundPool soundPool;
    private int soundMove, soundEat, soundCheck;

    public interface GameListener {
        void onCheckmate(String winnerText);

        void onTurnChanged(boolean isRedTurn);
    }
    private GameListener gameListener;
    public void setGameListener(GameListener listener) {
        this.gameListener = listener;
    }

    public ChessBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        boardImg = BitmapFactory.decodeResource(getResources(), R.drawable.bancotuong);
        shadowPaint = new Paint();
        shadowPaint.setColor(android.graphics.Color.parseColor("#66000000"));

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        soundPool = new SoundPool.Builder().setMaxStreams(3).setAudioAttributes(audioAttributes).build();
        soundMove = soundPool.load(context, R.raw.move, 1);
        soundEat = soundPool.load(context, R.raw.eat, 1);
        soundCheck = soundPool.load(context, R.raw.check, 1);
    }

    private Bitmap getBitmap(int resId) {
        if (!bitmapCache.containsKey(resId)) {
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), resId);
            bitmapCache.put(resId, bmp);
        }
        return bitmapCache.get(resId);
    }

    public void setPieces(List<Piece> pieces) {
        this.pieces = pieces;
        this.isGameOver = false;
        this.isRedTurn = true;
        this.selectedPiece = null;
        invalidate();
    }

    public void resetGame(List<Piece> newPieces, boolean flipBoard) {
        this.pieces = newPieces;
        this.isFlipped = flipBoard;
        this.isGameOver = false;
        this.isRedTurn = true;
        this.selectedPiece = null;
        this.moveCount = 0;


        this.isCoUpMode = false;
        for (Piece p : newPieces) {
            if (p.isFaceDown) {
                this.isCoUpMode = true;
                break;
            }
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float boardMargin = getWidth() * 0.1f;
        RectF dstBoard = new RectF(boardMargin, boardMargin, getWidth() - boardMargin, getHeight() - boardMargin);


        canvas.save();
        if (isFlipped) {
            canvas.rotate(180, dstBoard.centerX(), dstBoard.centerY());
        }
        canvas.drawBitmap(boardImg, null, dstBoard, null);
        canvas.restore();

        float gridWidth = dstBoard.width();
        float gridHeight = dstBoard.height();
        float paddingLeft = gridWidth * 0.025f;
        float paddingRight = gridWidth * 0.025f;
        float paddingTop = gridHeight * 0.025f;
        float paddingBottom = gridHeight * 0.025f;

        float cellWidth = (gridWidth - paddingLeft - paddingRight) / 8.0f;
        float cellHeight = (gridHeight - paddingTop - paddingBottom) / 9.0f;

        if (pieces != null) {
            for (Piece p : pieces) {
                if (p == selectedPiece) continue;

                Bitmap pBitmap = p.isFaceDown ? getBitmap(R.drawable.ic_face_down) : getBitmap(p.resID);

                int drawX = isFlipped ? (8 - p.x) : p.x;
                int drawY = isFlipped ? (9 - p.y) : p.y;

                float centerX = dstBoard.left + paddingLeft + (drawX * cellWidth);
                float centerY = dstBoard.top + paddingTop + (drawY * cellHeight);
                float pieceSize = cellWidth * 1.0f;

                RectF dstPiece = new RectF(centerX - pieceSize / 2, centerY - pieceSize / 2, centerX + pieceSize / 2, centerY + pieceSize / 2);
                canvas.drawBitmap(pBitmap, null, dstPiece, null);
            }

            if (selectedPiece != null) {
                Bitmap pBitmap = selectedPiece.isFaceDown ? getBitmap(R.drawable.ic_face_down) : getBitmap(selectedPiece.resID);
                int drawX = isFlipped ? (8 - selectedPiece.x) : selectedPiece.x;
                int drawY = isFlipped ? (9 - selectedPiece.y) : selectedPiece.y;

                float trueX = dstBoard.left + paddingLeft + (drawX * cellWidth);
                float trueY = dstBoard.top + paddingTop + (drawY * cellHeight);
                float liftOffset = 20f;
                float scaleUp = 1.15f;
                float pieceSize = cellWidth * scaleUp;

                RectF shadowRect = new RectF(trueX - (cellWidth * 0.7f) / 2, trueY - (cellWidth * 0.3f) / 2 + 10f, trueX + (cellWidth * 0.7f) / 2, trueY + (cellWidth * 0.3f) / 2 + 10f);
                canvas.drawOval(shadowRect, shadowPaint);

                RectF dstPiece = new RectF(trueX - pieceSize / 2, (trueY - liftOffset) - pieceSize / 2, trueX + pieceSize / 2, (trueY - liftOffset) + pieceSize / 2);
                canvas.drawBitmap(pBitmap, null, dstPiece, null);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isGameOver) return true;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float touchX = event.getX();
            float touchY = event.getY();
            float boardMargin = getWidth() * 0.1f;
            float gridWidth = getWidth() - 2 * boardMargin;
            float gridHeight = getHeight() - 2 * boardMargin;

            float paddingLeft = gridWidth * 0.025f;
            float paddingTop = gridHeight * 0.025f;
            float cellWidth = (gridWidth - paddingLeft * 2) / 8.0f;
            float cellHeight = (gridHeight - paddingTop * 2) / 9.0f;

            int gridX = Math.round((touchX - boardMargin - paddingLeft) / cellWidth);
            int gridY = Math.round((touchY - boardMargin - paddingTop) / cellHeight);

            if (isFlipped) {
                gridX = 8 - gridX;
                gridY = 9 - gridY;
            }

            if (gridX >= 0 && gridX <= 8 && gridY >= 0 && gridY <= 9) {
                Piece clickedPiece = getPieceAt(gridX, gridY);

                if (selectedPiece == null) {
                    if (clickedPiece != null) {
                        if ((isRedTurn && clickedPiece.color == Piece.Color.Red) ||
                                (!isRedTurn && clickedPiece.color == Piece.Color.Black)) {
                            selectedPiece = clickedPiece;
                        }
                    }
                } else {
                    if (clickedPiece != null) {
                        if (clickedPiece == selectedPiece) {
                            selectedPiece = null;
                        } else if (clickedPiece.color == selectedPiece.color) {
                            selectedPiece = clickedPiece;
                        } else {
                            if (isSafeMove(selectedPiece, gridX, gridY, clickedPiece)) {
                                pieces.remove(clickedPiece);
                                selectedPiece.x = gridX;
                                selectedPiece.y = gridY;
                                soundPool.play(soundEat, 0.2f, 0.2f, 0, 0, 1);

                                if (selectedPiece.isFaceDown) selectedPiece.isFaceDown = false;

                                selectedPiece = null;
                                processTurnEnd();
                            }
                        }
                    } else {
                        if (isSafeMove(selectedPiece, gridX, gridY, null)) {
                            selectedPiece.x = gridX;
                            selectedPiece.y = gridY;
                            soundPool.play(soundMove, 1, 1, 0, 0, 1);

                            if (selectedPiece.isFaceDown) selectedPiece.isFaceDown = false;

                            selectedPiece = null;
                            processTurnEnd();
                        }
                    }
                }
            } else {
                selectedPiece = null;
            }
            invalidate();
        }
        return true;
    }

    private void processTurnEnd() {
        moveCount++;
        isRedTurn = !isRedTurn;
        Piece.Color nextColor = isRedTurn ? Piece.Color.Red : Piece.Color.Black;

        if (isInCheck(nextColor)) soundPool.play(soundCheck, 1, 1, 0, 0, 1);

        if (isCheckmate(nextColor)) {
            isGameOver = true;
            String winner = isRedTurn ? "ĐEN THẮNG!" : "ĐỎ THẮNG!";
            if(gameListener != null) {
                gameListener.onCheckmate(winner + " (Số nước đi: " + (moveCount / 2) + ")");
            }
        }
        if (gameListener != null && !isGameOver) {
            gameListener.onTurnChanged(isRedTurn);
        }
    }

    private Piece getPieceAt(int x, int y) {
        for (Piece p : pieces) {
            if (p.x == x && p.y == y) return p;
        }
        return null;
    }

    private int countPiecesBetween(int startX, int startY, int targetX, int targetY) {
        int count = 0;
        if (startX == targetX) {
            int minY = Math.min(startY, targetY);
            int maxY = Math.max(startY, targetY);
            for (int i = minY + 1; i < maxY; i++) {
                if (getPieceAt(startX, i) != null) count++;
            }
        } else if (startY == targetY) {
            int minX = Math.min(startX, targetX);
            int maxX = Math.max(startX, targetX);
            for (int i = minX + 1; i < maxX; i++) {
                if (getPieceAt(i, startY) != null) count++;
            }
        }
        return count;
    }

    private boolean isInCheck(Piece.Color myColor) {
        Piece myKing = null;
        for (Piece p : pieces) {
            if (p.type == Piece.Type.Tuong_Quan && p.color == myColor) {
                myKing = p;
                break;
            }
        }
        if (myKing == null) return false;

        for (Piece enemy : pieces) {
            if (enemy.color != myColor && !enemy.isFaceDown) {
                if (enemy.type == Piece.Type.Tuong_Quan) continue;
                if (isValidMove(enemy, myKing.x, myKing.y)) return true;
            }
        }
        return false;
    }

    private boolean isSafeMove(Piece p, int targetX, int targetY, Piece targetPiece) {
        if (!isValidMove(p, targetX, targetY)) return false;
        int oldX = p.x;
        int oldY = p.y;
        boolean isPieceEaten = false;

        p.x = targetX;
        p.y = targetY;
        if (targetPiece != null) {
            pieces.remove(targetPiece);
            isPieceEaten = true;
        }

        boolean facing = isGeneralsFacing();
        boolean inCheck = isInCheck(isRedTurn ? Piece.Color.Red : Piece.Color.Black);

        p.x = oldX;
        p.y = oldY;
        if (isPieceEaten) pieces.add(targetPiece);

        return !facing && !inCheck;
    }

    private boolean isGeneralsFacing() {
        Piece redGeneral = null;
        Piece blackGeneral = null;

        for (Piece p : pieces) {
            if (p.type == Piece.Type.Tuong_Quan) {
                if (p.color == Piece.Color.Red) redGeneral = p;
                else blackGeneral = p;
            }
        }
        if (redGeneral == null || blackGeneral == null) return false;
        if (redGeneral.x != blackGeneral.x) return false;
        return countPiecesBetween(redGeneral.x, redGeneral.y, blackGeneral.x, blackGeneral.y) == 0;
    }

    private Piece.Type getRoleOfSquare(int x, int y) {
        if (y == 0 || y == 9) {
            if (x == 0 || x == 8) return Piece.Type.Xe;
            if (x == 1 || x == 7) return Piece.Type.Ma;
            if (x == 2 || x == 6) return Piece.Type.Tuong;
            if (x == 3 || x == 5) return Piece.Type.Si;
        }
        if (y == 2 || y == 7) {
            if (x == 1 || x == 7) return Piece.Type.Phao;
        }
        if (y == 3 || y == 6) {
            if (x == 0 || x == 2 || x == 4 || x == 6 || x == 8) return Piece.Type.Tot;
        }
        return null;
    }

    private boolean isValidMove(Piece p, int targetX, int targetY) {
        if (p == null || p.type == null) return true;
        if (p.x == targetX && p.y == targetY) return false;

        int dx = Math.abs(targetX - p.x);
        int dy = Math.abs(targetY - p.y);
        Piece targetPiece = getPieceAt(targetX, targetY);

        Piece.Type ruleToApply = p.type;
        Piece.Color ruleColor = p.color;

        if (p.isFaceDown) {
            ruleToApply = getRoleOfSquare(p.x, p.y);
            if (ruleToApply == null) return false;
        }

        switch (ruleToApply) {
            case Xe:
                if (dx == 0 || dy == 0) return countPiecesBetween(p.x, p.y, targetX, targetY) == 0;
                return false;

            case Ma:
                if ((dx == 1 && dy == 2) || (dx == 2 && dy == 1)) {
                    if (dx == 2 && getPieceAt((p.x + targetX) / 2, p.y) != null) return false;
                    if (dy == 2 && getPieceAt(p.x, (p.y + targetY) / 2) != null) return false;
                    return true;
                }
                return false;

            case Phao:
                if (dx == 0 || dy == 0) {
                    int blocks = countPiecesBetween(p.x, p.y, targetX, targetY);
                    if (targetPiece == null) return blocks == 0;
                    else return blocks == 1;
                }
                return false;

            case Tot:
                if (ruleColor == Piece.Color.Black) {
                    if (p.y <= 4) return dx == 0 && targetY == p.y + 1;
                    else return (dx == 1 && targetY == p.y) || (dx == 0 && targetY == p.y + 1);
                } else {
                    if (p.y >= 5) return dx == 0 && targetY == p.y - 1;
                    else return (dx == 1 && targetY == p.y) || (dx == 0 && targetY == p.y - 1);
                }

            case Tuong:
                if (dx == 2 && dy == 2) {
                    if (!isCoUpMode) {
                        if (ruleColor == Piece.Color.Black && targetY > 4) return false;
                        if (ruleColor == Piece.Color.Red && targetY < 5) return false;
                    }
                    if (getPieceAt((p.x + targetX) / 2, (p.y + targetY) / 2) != null) return false;
                    return true;
                }
                return false;

            case Si:
                if (dx == 1 && dy == 1) {
                    if (!isCoUpMode) {
                        if (targetX < 3 || targetX > 5) return false;
                        if (ruleColor == Piece.Color.Black && targetY > 2) return false;
                        if (ruleColor == Piece.Color.Red && targetY < 7) return false;
                    }
                    return true;
                }
                return false;

            case Tuong_Quan:
                if (dx + dy == 1) {
                    if (targetX < 3 || targetX > 5) return false;
                    if (ruleColor == Piece.Color.Black && targetY > 2) return false;
                    if (ruleColor == Piece.Color.Red && targetY < 7) return false;
                    return true;
                }
                return false;
        }
        return true;
    }

    private boolean isCheckmate(Piece.Color myColor) {
        List<Piece> myPieces = new ArrayList<>();
        for (Piece p : pieces) {
            if (p.color == myColor) {
                myPieces.add(p);
            }
        }

        for (Piece p : myPieces) {
            for (int targetX = 0; targetX <= 8; targetX++) {
                for (int targetY = 0; targetY <= 9; targetY++) {
                    Piece targetPiece = getPieceAt(targetX, targetY);
                    if (targetPiece != null && targetPiece.color == myColor) continue;
                    if (isSafeMove(p, targetX, targetY, targetPiece)) return false;
                }
            }
        }
        return true;
    }
}