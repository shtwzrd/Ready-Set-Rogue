package com.warsheep.scamp.algorithms;

import com.warsheep.scamp.adt.BSPRectangle;
import com.warsheep.scamp.adt.Container;
import com.warsheep.scamp.adt.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BSPMapGenerator {

    private final int MAP_SIZE_X = 60;
    private final int MAP_SIZE_Y = 60;
    private final int MIN_SQUARE_SIZE = 6;
    private final int N_ITERATIONS = 6;
    private final BSPRectangle root = new BSPRectangle(0, 0, MAP_SIZE_X, MAP_SIZE_Y);
    private List<Room> rooms = new ArrayList<>();


    private void growRooms() {
        this.rooms.addAll(
                root.getLevel(N_ITERATIONS)
                        .stream()
                        .map(Room::new)
                        .collect(Collectors.toList()));
    }

    private byte[][] tunnelPaths(BSPRectangle rect, byte[][] map) {
        if (rect.getRightChild() == null || rect.getLeftChild() == null) {
            return map;
        } else {
           // map = pathFill(rect.getRightChild(), rect.getLeftChild(), map);

            return Compositor.union(
                    tunnelPaths(rect.getLeftChild(), map),
                    tunnelPaths(rect.getRightChild(), map));
        }
    }

    public byte[][] to2DArray() {
        root.partition(root, N_ITERATIONS);

        byte[][] map = new byte[MAP_SIZE_X][MAP_SIZE_Y];

        for (int i = 0; i < MAP_SIZE_X; i++) {
            for (int j = 0; j < MAP_SIZE_Y; j++) {
                map[i][j] = '.';
            }
        }

        growRooms();
        for (Room r : rooms) {
            map = rectFill(r.x(), r.y(), r.width(), r.height(), map);
        }

        tunnelPaths(root, map);
        System.out.println(this.rooms.size());
        return map;
    }


    private byte[][] rectFill(int x, int y, int width, int height, byte[][] in) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                in[i + x][j + y] = 'X';
            }
        }
        return in;
    }

    private byte[][] pathFill(Container start, Container finish, byte[][] map) {
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

        for (int i = 0; i < bx; i++) {
            for (int j = 0; j < by; j++) {
                map[i + ax][j + ay] = 'X';
            }
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
