package traffic_simulator.position;

public enum Direction {
    UP(-1), DOWN(1), RIGHT(1), LEFT(-1);

    int val;
    private Direction(int v) {val = v;}

    public Direction getOposite() {
        if(this == UP) return DOWN;
        else if(this == DOWN) return UP;
        else if(this == LEFT) return RIGHT;
        else if(this == RIGHT) return LEFT;
        else throw new IllegalArgumentException("given direction is not contained in getOposite() method");
    }

    public Direction getNext() {
        if(this == UP) return RIGHT;
        else if(this == RIGHT) return DOWN;
        else if(this == DOWN) return LEFT;
        else if(this == LEFT) return UP;
        else throw new IllegalArgumentException("given direction is not contained in getNext() method");
    }

    public static boolean isParallel(Direction dir1, Direction dir2){
        if(dir1 == null || dir2 == null) return false;
        if((dir1 == UP || dir1 == DOWN) && (dir2 == UP || dir2 == DOWN)) return true;
        if((dir1 == RIGHT || dir1 == LEFT) && (dir2 == RIGHT || dir2 == LEFT)) return true;
        return false;
    }

    public static Direction getDirection(Point a, Point b) {
        if(a.y == b.y) {
            if(a.x <= b.x) return RIGHT;
            else return LEFT;
        }
        if(a.x == b.x) {
            if(a.y >= b.y) return UP;
            else return DOWN;
        }
        throw new IllegalArgumentException("Can't find direction for this points");
    }

    public int getVal() {return this.val;}

}