package gsp;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;

public class Maze {

    private Tile[][] maze;
    private int height, width, goalX, goalY, startX, startY;

    enum GraphSearchMode {
        BFS, DFS, ASTAR
    }

    public Maze(int height, int width) {
        this.height = height;
        this.width = width;
        maze = new Tile[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int traversable = random() > .1 ? 1 : -1;
                // int cost = (int) (traversable * Math.random() * 10.0);
                int cost = traversable;
                maze[y][x] = new Tile(x, y, cost);
            }
        }
        while (startX == goalX && startY == goalY) {
            this.goalX = (int) (random() * width);
            this.goalY = (int) (random() * height);
            this.startX = (int) (random() * width);
            this.startY = (int) (random() * height);
        }
        maze[goalY][goalX] = new Tile(goalX, goalY, 1);
        maze[startY][startX] = new Tile(startX, startY, 1);
    }

    public Tile getTile(int x, int y) {
        if (x < width && x >= 0 && y < height && y >= 0)
            return maze[y][x];
        return null;
    }

    public void printMaze() {
        printMaze(getStringMaze());
    }

    private void printMaze(String[][] strMaze) {
        for (String[] arr : strMaze) {
            for (String str : arr) {
                System.out.print(str);
            }
            System.out.println();
        }
    }

    private String[][] getStringMaze() {

        String[][] strMaze = new String[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Tile tile = maze[y][x];
                String result;
                if (startX == tile.getX() && startY == tile.getY()) {
                    result = "ME";
                } else if (tile.isGoal(goalX, goalY)) {
                    result = "GL";
                } else if (tile.isTraversable()) {
                    result = "[]";
                } else {
                    result = "><";
                }
                strMaze[y][x] = result;
            }
        }
        return strMaze;
    }

    public void printShortestPath(GraphSearchMode mode) {
        List<Tile> path = calculateShortestPath(mode);
        if (path == null) {
            // No path, print maze
            printMaze(getStringMaze());
            return;
        }
        String[][] strMaze = getStringMaze();
        for (Tile tile : path) {
            strMaze[tile.getY()][tile.getX()] = "HI";
        }
        printMaze(strMaze);
    }

    private List<Tile> getAdjacentTiles(Tile tile) {
        return getAdjacentTiles(tile.getX(), tile.getY());
    }

    private List<Tile> getAdjacentTiles(int x, int y) {
        ArrayList<Tile> adjTiles = new ArrayList<Tile>();
        Tile up = getTile(x, y + 1);
        if (up != null && up.isTraversable() && !up.isVisited()) {
            up.visited = true;
            up.setParent(getTile(x, y));
            adjTiles.add(up);
        }
        Tile down = getTile(x, y - 1);
        if (down != null && down.isTraversable() && !down.isVisited()) {
            down.visited = true;
            down.setParent(getTile(x, y));
            adjTiles.add(down);
        }
        Tile left = getTile(x - 1, y);
        if (left != null && left.isTraversable() && !left.isVisited()) {
            left.visited = true;
            left.setParent(getTile(x, y));
            adjTiles.add(left);
        }
        Tile right = getTile(x + 1, y);
        if (right != null && right.isTraversable() && !right.isVisited()) {
            right.visited = true;
            right.setParent(getTile(x, y));
            adjTiles.add(right);
        }
        return adjTiles;
    }

    public List<Tile> calculateShortestPath(GraphSearchMode mode) {
        switch (mode) {
            case DFS:
                return calculateDFS();
            case ASTAR:
                return calculateAStar();
            default:
                return calculateBFS();
        }
    }

    private List<Tile> calculateBFS() {

        Tile search = new Tile(startX, startY, 1);
        ArrayList<Tile> BFSqueue = new ArrayList<Tile>();
        while (search.getX() != goalX || search.getY() != goalY) {

            BFSqueue.addAll(getAdjacentTiles(search));
            search = BFSqueue.remove(0);
        }

        List<Tile> path = new ArrayList<Tile>();
        while (search.parent.getX() != startX || search.parent.getY() != startY) {
            path.add(search.parent);
            search = search.parent;
        }


        return path;
    }

    private boolean searchDFS(Tile a) {

        boolean found = false;
        List<Tile> temp = getAdjacentTiles(a);

        if(a.getX() == goalX && a.getY() == goalY)
            found = true;
        if(temp.size() == 0)
            found = false;

        for(int i = 0 ; i < temp.size() ; i++) {

            searchDFS(temp.get(i));

        }

        return found;

    }

    private List<Tile> calculateDFS() {

        Tile search = getTile(goalX, goalY);

        searchDFS(getTile(startX, startY));

        List<Tile> path = new ArrayList<Tile>();
        while (search.parent.getX() != startX || search.parent.getY() != startY) {
            path.add(search.parent);
            search = search.parent;
        }


        return path;
    }

    private double findCost(Tile a , int X , int Y) {

        double posX = a.getX();
        double posY = a.getY();

        posX = abs(X - posX);
        posY = abs(Y - posY);

        return (sqrt((posX * posX) + (posY * posY)));
}

    private List<Tile> calculateAStar() {

        int x = startX;
        int y = startY;
        double cost;
        double value;

        Tile nextMove;
        nextMove = null;

        List<Tile> candidate;
        List<Tile> path = new ArrayList<Tile>();

        while ((x != goalX || y != goalY)){

            cost = 1000000;

            candidate = getAdjacentTiles(x, y);
            for (Tile aCandidate : candidate) {

                value = findCost(aCandidate, goalX, goalY);

                if (value < cost) {

                    cost = value;
                    nextMove = aCandidate;

                }
            }

            int xMove = x - nextMove.getX();
            int yMove = y - nextMove.getY();

            if(xMove > 0) {

                System.out.println("going left ");
            }

            if(xMove < 0) {

                System.out.println("going right ");
            }

            if(yMove < 0) {

                System.out.println("going down");
            }

            if(yMove > 0) {

                System.out.println("going up ");
            }


            x = nextMove.getX();
            y = nextMove.getY();

        }

        nextMove = nextMove.parent;


        System.out.println("path found");


        while(nextMove.getX() != startX || nextMove.getY() != startY) {

            path.add(nextMove);
            nextMove = nextMove.parent;

        }


        return path;
    }
}
