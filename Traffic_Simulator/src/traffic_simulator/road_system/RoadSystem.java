package traffic_simulator.road_system;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Random;

import traffic_simulator.road_system.road.*;
import traffic_simulator.road_system.intersection.*;
import traffic_simulator.position.*;



public class RoadSystem extends JPanel {
    public static final int WINDOW_WIDTH = 900;
    public static final int WINDOW_HEIGHT = 740;

    private ArrayList<Road> roads = new ArrayList<> ();
    private ArrayList<Intersection> intersections = new ArrayList<>();


    public RoadSystem() {
        setBackground(new Color(0,140,0));
        setPreferredSize(new Dimension(WINDOW_WIDTH,WINDOW_HEIGHT));
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        for(Road r : roads) r.paintRoad(g);
        for(Intersection i : intersections) i.paintIntersection(g);
        for(Road r : roads) r.paintCars(g);
    }

    public void addRoad(Orientation o, int location, int URlanes, int DLlanes) {
        switch(o) {
            case HORIZONTAL:
                if(location < 0 || location > WINDOW_WIDTH) throw new IllegalArgumentException("location out of window size");
                roads.add(new Road( location, o, URlanes, DLlanes));
                break;
            case VERTICAL:
                if(location < 0 || location > WINDOW_HEIGHT) throw new IllegalArgumentException("location out of window size");
                roads.add(new Road(location, o,  URlanes, DLlanes));
                break;
        }
    }

    public void addRoad(Direction dir, int numOfPerpedicularRoad, int location,  int URlanes, int DLlanes) {
        Road perpRoad = roads.get(numOfPerpedicularRoad);
        Road thisRoad;
        if(Direction.isParallel(perpRoad.direction, dir)) throw new IllegalArgumentException("Roads are parallel");

        switch(dir) {
            case UP:
                thisRoad =new Road(new Point(location, perpRoad.startPoint.y), new Point(location, 0), URlanes, DLlanes);
                break;
            case DOWN:
                thisRoad = new Road(new Point(location, RoadSystem.WINDOW_HEIGHT), new Point(location,  perpRoad.startPoint.y + perpRoad.getRoadSize()), URlanes, DLlanes);
                break;
            case RIGHT:
                thisRoad = new Road(new Point(perpRoad.startPoint.x + perpRoad.getRoadSize(), location), new Point(RoadSystem.WINDOW_WIDTH, location),  URlanes, DLlanes);
                break;
            case LEFT:
                thisRoad = new Road(new Point(0,location), new Point(perpRoad.startPoint.x, location),  URlanes, DLlanes);
                break;
            default:
                throw new IllegalArgumentException("direction not found");
        }
        roads.add(thisRoad);
        addIntersection(thisRoad, perpRoad);

    }

    public void addIntersection(int firstRoadNum, int secondRoadNum) {
        Intersection i = new Intersection(roads.get(firstRoadNum),roads.get(secondRoadNum));
        intersections.add(i);
    }

    public void addIntersection(Road r1, Road r2) {
        intersections.add(new Intersection(r1,r2));
    }

    public void addNewCar() {
        Random rand = new Random();
        int n = rand.nextInt(roads.size());
        boolean added = roads.get(n).addNewCar();
        if(added == false) addNewCar();
    }

    public void moveCars() {
        for(Road r : roads) r.moveCars();
        for(Intersection i : intersections) i.moveCarsOnIntersection();
    }

    public int getTrafficSize() {
        int trafficSize = 0;
        for(Road r : roads) trafficSize += r.getTrafficSize();
        return trafficSize;
    }

    public int getNumCarsOnRoads() {
        int cars = 0;
        for(Road r : roads) cars += r.getNumCarsOnRoad();
        return cars;
    }

    public double getAvgSpeed() {
        double avgSpeed = 0.0;
        for(Road r : roads) avgSpeed += r.getAvgSpeed();
        avgSpeed /= roads.size();

        return avgSpeed;
    }
}
