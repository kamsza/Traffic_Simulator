import traffic_simulator.*;
import traffic_simulator.position.*;


public class Main {
    public static void main(String[] args) {
        Simulation s = new Simulation();

        s.addRoad(Orientation.HORIZONTAL, 50, 5,3);
        s.addRoad(Orientation.HORIZONTAL, 500,1,4);
        s.addRoad(Orientation.VERTICAL, 100,3,2);
        s.addRoad(Orientation.VERTICAL, 600,2,3);
        s.addRoad(Orientation.HORIZONTAL, 300,2,2);
       // s.addRoad(Direction.DOWN, 1,300, 2,3);
       // s.addRoad(Direction.RIGHT, 3 ,340,1,2);
       // s.addRoad(Direction.UP,0,500,2,1);
       // s.addRoad(Direction.LEFT,2, 400,1,2);
        s.addIntersection(0,2);
        s.addIntersection(3,0);
        s.addIntersection(2,1);
        s.addIntersection(1,3);
        s.addIntersection(4,3);
        s.addIntersection(4,2);
        s.start();
    }
}
