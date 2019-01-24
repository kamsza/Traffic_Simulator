package traffic_simulator.position;

import static java.lang.Math.sqrt;
import static java.lang.Math.pow;
import static java.lang.Math.round;

public class Point {
    public final double x,y;

    public Point(double x, double y) {this.x = x; this.y = y;}
    public Point(Point p) {this.x = p.x; this.y = p.y;}


    public Point move(double x, double y) {
        return new Point(this.x + x, this.y + y);
    }

    public Point move(Direction dir, double val) {
        switch(dir) {
            case UP:
            case DOWN:
                return this.move(0, dir.val * val);
            case RIGHT:
            case LEFT:
                return this.move(dir.val * val, 0);
            default:
                throw new IllegalArgumentException("direction not found in move function");
        }
    }


    public double distance(double x, double y) { return  round(sqrt(pow(this.x - x, 2) + pow(this.y - y, 2))); }
    public double distance(Point p) { return round(sqrt(pow(this.x - p.x, 2) + pow(this.y - p.y, 2))); }

    public String toString() { return "(" + x + " , " + y + ")"; }

    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(!(obj instanceof Point)) return false;
        Point temp = (Point) obj;
        if(temp == this) return true;
        if(temp.x == x && temp.y == y) return true;
        return false;
    }
}

