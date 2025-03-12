package com.example.predatorsandpreys;

public abstract class Creature {
    protected int x, y;
    public boolean stepped;
    private String imagepath;
    private char signSymbol;
    protected LivingSpace livingSpace;
    public boolean newborn;

    public Creature(LivingSpace lm, int x, int y) {
        this.livingSpace = lm;
        this.x = x;
        this.y = y;
        this.stepped = false;
        this.newborn = false;
    }

    protected void setImage(String imagepath) {
        this.imagepath = imagepath;
    }

    protected void setSignSymbol(char symbol) {
        this.signSymbol = symbol;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    public char getSignSymbol() {
        return signSymbol;
    }
    abstract void move();
}
