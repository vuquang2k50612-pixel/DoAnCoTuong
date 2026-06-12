package com.example.doancotuong;

import java.io.Serializable;

public class Piece implements Serializable {
    public enum Color {Red , Black}
    public enum Type { Xe , Ma , Tuong , Si , Tuong_Quan ,Phao , Tot}
    public Type type;
    public Color color;
    public int x,y;
    public int resID;
    public boolean isFaceDown = false;

    public Piece (Type type, Color color, int x, int y, int resID){
        this.type = type;
        this.color = color;
        this.x = x;
        this.y =y;
        this.resID = resID;
    }
}
