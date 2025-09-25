package com.wtg.colorpicker;

public class ColorPal {
    private int color;
    private boolean check;

    public ColorPal(int color, boolean check) {
        this.color = color;
        this.check = check;
    }

   /* @Override
    public boolean equals(Object o) {
        return o instanceof ColorPal && ((ColorPal) o).color == color;
    }*/
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColorPal colorPal = (ColorPal) o;
        return color == colorPal.color && check == colorPal.check;
    }

    @Override
    public int hashCode() {
        int result = color;
        result = 31 * result + (check ? 1 : 0);
        return result;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }
}