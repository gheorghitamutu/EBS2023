package Lab05;

public class Color {

    int red;
    int green;
    int blue;

    Color() {
        red = green = blue = 0;
    }

    Color(int r, int g, int b) {
        red = r;
        green = g;
        blue = b;
    }

    Color(int rgb) {
        red = rgb / 1000000;
        green = (rgb - (red * 1000000))/1000;
        blue = rgb - (red * 1000000) - (green * 1000);
    }

    public int getRGB() {
        return red * 1000000 + green * 1000 + blue;
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }
}
