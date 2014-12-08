package com.warsheep.scamp.algorithms;

public class Compositor {

    public static byte[][] union(byte[][] a, byte[][] b) {
        int lowestX = a.length < b.length ? a.length : b.length;
        int lowestY = a[0].length < b[0].length ? a[0].length : b[0].length;

        byte[][] out = new byte[lowestX][lowestY];
        for (int i = 0; i < lowestX; i++) {
            for (int j = 0; j < lowestY; j++) {
                if (a[i][j] == '.' && a[i][j] == '.') {
                    out[i][j] = '.';
                } else {
                    out[i][j] = 'X';
                }
            }
        }
        return out;
    }
}
