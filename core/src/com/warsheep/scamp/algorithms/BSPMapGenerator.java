package com.warsheep.scamp.algorithms;

import com.warsheep.scamp.adt.BSPRectangle;
import com.warsheep.scamp.adt.Container;
import com.warsheep.scamp.adt.Room;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

public class BSPMapGenerator {

    private final int MAP_SIZE_X;
    private final int MAP_SIZE_Y;
    private final int MIN_TUNNEL_SIZE;
    private final int MAX_TUNNEL_SIZE;
    private final int N_ITERATIONS;

    private final BSPRectangle root;
    private ArrayList<Room> rooms;
    private Random rand;

    public BSPMapGenerator(int width, int height, int minimumTunnelSize, int maximumTunnelSize, int iterations) {
        this.MAP_SIZE_X = width;
        this.MAP_SIZE_Y = height;
        this.MIN_TUNNEL_SIZE = minimumTunnelSize;
        this.MAX_TUNNEL_SIZE = maximumTunnelSize;
        this.N_ITERATIONS = iterations;
        this.rooms = new ArrayList<>();

        this.root = new BSPRectangle(0, 0, MAP_SIZE_X, MAP_SIZE_Y);
        this.rand = new Random();
    }


    private void growRooms() {
        this.rooms.addAll(
                root.getLevel(N_ITERATIONS)
                        .parallelStream()
                        .map(Room::new)
                        .collect(Collectors.toList()));
    }


    private byte[][] tunnelPaths(BSPRectangle rect, byte[][] map) {
        if (rect.getRightChild() == null || rect.getLeftChild() == null) {
            return map;
        } else {
            tunnelPaths(rect.getLeftChild(), map);
            tunnelPaths(rect.getRightChild(), map);
            if (rect.getLeftChild().getLeftChild() == null || rect.getRightChild().getRightChild() == null) {
                return new byte[0][0];
            } else {
                return pathFill(rect.getLeftChild(), rect.getRightChild(), map,
                        rand.nextInt(MAX_TUNNEL_SIZE - MIN_TUNNEL_SIZE + 1) + MIN_TUNNEL_SIZE);
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
        for (Room r : rooms) {
            rectFill(r.x(), r.y(), r.width(), r.height(), map);
        }
        tunnelPaths(root, map);
        cleanWalls(map);
        return map;
    }


    private byte[][] rectFill(int x, int y, int width, int height, byte[][] in) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                in[i + x][j + y] = (byte) '.';
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

    private void cleanWalls(byte[][] map) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                boolean allWall = true;
                if (i > 0) {
                    if (map[i - 1][j] != '#' && map[i - 1][j] != ' ') {
                        allWall = false;
                    }
                    if (j > 0) {
                        if (map[i - 1][j - 1] != '#' && map[i - 1][j - 1] != ' ') {
                            allWall = false;
                        }
                    }
                    if (j < map[0].length - 1) {
                        if (map[i - 1][j + 1] != '#' && map[i - 1][j + 1] != ' ') {
                            allWall = false;
                        }
                    }
                }
                if (i < map.length - 1) {
                    if (map[i + 1][j] != '#' && map[i + 1][j] != ' ') {
                        allWall = false;
                    }
                    if (j > 0) {
                        if (map[i + 1][j - 1] != '#' && map[i + 1][j - 1] != ' ') {
                            allWall = false;
                        }
                    }
                    if (j < map[0].length - 1) {
                        if (map[i + 1][j + 1] != '#' && map[i + 1][j + 1] != ' ') {
                            allWall = false;
                        }
                    }
                }
                if (j > 0) {
                    if (map[i][j - 1] != '#' && map[i][j - 1] != ' ') {
                        allWall = false;
                    }
                }
                if (j < map.length - 1) {
                    if (map[i][j + 1] != '#' && map[i][j + 1] != ' ') {
                        allWall = false;
                    }
                }
                if (allWall) {
                    map[i][j] = ' ';
                }
            }
        }
    }

    public static void main(String[] args) {
        BSPMapGenerator genny = new BSPMapGenerator(50, 50, 1, 3, 5);
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
