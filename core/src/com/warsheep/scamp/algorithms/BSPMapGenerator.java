package com.warsheep.scamp.algorithms;

import com.warsheep.scamp.adt.BSPRectangle;
import com.warsheep.scamp.adt.Container;
import com.warsheep.scamp.adt.Room;

import java.util.List;

public class BSPMapGenerator {

    private final int MAP_SIZE_X = 50;
    private final int MAP_SIZE_Y = 50;
    private final int MIN_SQUARE_SIZE = 4;
    private final int N_ITERATIONS = 5;
    private final BSPRectangle root = new BSPRectangle(0, 0, MAP_SIZE_X, MAP_SIZE_Y);

    private void growRooms() {
        List<BSPRectangle> rects = root.getLevel(N_ITERATIONS);
        for (BSPRectangle b : rects) {
            b.setRoom(new Room(b));
        }
    }

    private byte[][] tunnelPaths(BSPRectangle rect, byte[][] map) {
        if (rect.getRightChild() == null || rect.getLeftChild() == null) {
            return map;
        } else {
            tunnelPaths(rect.getLeftChild(), map);
            tunnelPaths(rect.getRightChild(), map);

            Container l;
            Container r;
            if (rect.getLeftChild().getRoom() == null) {
                l = rect.getLeftChild();
            } else {
                l = rect.getLeftChild().getRoom();
            }
            if (rect.getRightChild().getRoom() == null) {
                r = rect.getRightChild();
            } else {
                r = rect.getRightChild().getRoom();
            }
            if(rect.getLeftChild().getLeftChild() == null || rect.getRightChild().getRightChild() == null) {
                return new byte[0][0];
            } else {
                return pathFill(l, r, map, 3);
            }
        }
    }

    public byte[][] to2DArray() {
        root.partition(root, N_ITERATIONS);

        byte[][] map = new byte[MAP_SIZE_X][MAP_SIZE_Y];

        for (int i = 0; i < MAP_SIZE_X; i++) {
            for (int j = 0; j < MAP_SIZE_Y; j++) {
                map[i][j] = '#';
            }
        }

        growRooms();
        List<BSPRectangle> rects = root.getLevel(N_ITERATIONS);
        for (BSPRectangle r : rects) {
            rectFill(r.getRoom().x(), r.getRoom().y(), r.getRoom().width(), r.getRoom().height(), map);
        }

        tunnelPaths(root, map);
        return map;
    }


    private byte[][] rectFill(int x, int y, int width, int height, byte[][] in) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                in[i + x][j + y] = (byte)'.';
            }
        }
        return in;
    }

    private byte[][] pathFill(Container start, Container finish, byte[][] map, int tunnelWidth) {
        int ax, ay, bx, by;

        if (start.center().x < finish.center().x) {
            ax = (int) start.center().x;
            bx = (int) finish.center().x;
        } else {
            ax = (int) finish.center().x;
            bx = (int) start.center().x;
        }

        if (start.center().y < finish.center().y) {
            ay = (int) start.center().y;
            by = (int) finish.center().y;
        } else {
            ay = (int) finish.center().y;
            by = (int) start.center().y;
        }

        if (ax == bx) {
            rectFill(ax, ay, tunnelWidth, by - ay, map);
        } else {
            rectFill(ax, ay, bx - ax, tunnelWidth, map);
        }

        return map;
    }

    public static void main(String[] args) {
        BSPMapGenerator genny = new BSPMapGenerator();
        byte[][] pretty = genny.to2DArray();

        for (int i = 0; i < pretty.length; i++) {
            for (int j = 0; j < pretty[0].length; j++) {
                System.out.print(" ");
                System.out.print((char) pretty[i][j]);
            }
            System.out.println();
        }

    }

}
