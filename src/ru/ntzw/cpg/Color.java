package ru.ntzw.cpg;

public class Color {

    public int r, g, b;

    public Color(int colorCode) {
        this.r = ((colorCode >> 16) & 0xFF);
        this.g = ((colorCode >> 8) & 0xFF);
        this.b = ((colorCode) & 0xFF);
    }

    public Color(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public int getColorCode() {
        return (r << 16) | (g << 8) | b;
    }

    public int getDifferenceSq(Color color) {
        return (r - color.r)*(r - color.r) + (g - color.g)*(g - color.g) + (b - color.b)*(b - color.b);
    }

    public double getDifference(Color color) {
        return Math.sqrt(getDifferenceSq(color));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Color color = (Color) o;

        if (r != color.r) return false;
        if (g != color.g) return false;
        return b == color.b;
    }

    @Override
    public int hashCode() {
        int result = r;
        result = 31 * result + g;
        result = 31 * result + b;
        return result;
    }

    @Override
    public String toString() {
        return "Color{" +
                "r=" + r +
                ", g=" + g +
                ", b=" + b +
                '}';
    }
}
