package main.game.utils.math;

public class Vector {

    public double x, y;

    public Vector() {
        set(0, 0);
    }

    public Vector(double x, double y) {
        set(x, y);
    }

    public Vector(Vector v) {
        set(v);
    }

    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void set(Vector v) {
        set(v.x, v.y);
    }

    public void add(double x, double y) {
        this.x += x;
        this.y += y;
    }

    public void add(Vector v) {
        this.x += v.x;
        this.y += v.y;
    }

    @Override
    public String toString() {
        return "Vector{" + "x = " + x + ", y = " + y + '}';
    }
}

