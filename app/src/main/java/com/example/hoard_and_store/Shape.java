package com.example.hoard_and_store;

public abstract class Shape {
    protected int x;
    protected int y;
    String color = "red";

    public Shape(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public abstract void draw();

    // Getters and setters for x and y
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
