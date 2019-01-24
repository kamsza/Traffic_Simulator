package traffic_simulator.road_system.road;

import traffic_simulator.position.*;
import traffic_simulator.road_system.intersection.*;
import traffic_simulator.road_system.vehicle.*;
import traffic_simulator.road_system.*;

import javax.swing.*;
import java.awt.Graphics;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

public class Road extends JComponent {
    public final Point startPoint, endPoint;
    public final Direction direction;

    private ArrayList<Intersection> intersections = new ArrayList<>();
    private ArrayList<Lane> dir_lanes = new ArrayList<>();   // up / right lanes
    private ArrayList<Lane> op_lanes = new ArrayList<>();   // down / left lanes


    public Road(int position, Orientation o, int DirLanesNum, int OpLanesNum) {
        if (o == Orientation.HORIZONTAL) {
            startPoint = new Point(0, position);
            endPoint = new Point(RoadSystem.WINDOW_WIDTH, position);
            direction = Direction.RIGHT;
        } else if (o == Orientation.VERTICAL) {
            startPoint = new Point(position, RoadSystem.WINDOW_HEIGHT);
            endPoint = new Point(position, 0);
            direction = Direction.UP;
        }
        else throw new IllegalArgumentException("Can not create road");

        addLanes(DirLanesNum, OpLanesNum);
    }

    public Road(Point start, Point end, int DirLanesNum, int OpLanesNum) {
        direction = Direction.getDirection(start,end);
        if(direction != Direction.RIGHT && direction != Direction.UP) throw new IllegalArgumentException("Wrong direction");

        startPoint = start;
        endPoint = end;

        addLanes(DirLanesNum, OpLanesNum);
    }

    private void addLanes(int DirLanesNum, int OpLanesNum) {
        Point sc = getCenterStartPoint(OpLanesNum);
        Point ec = getCenterEndPoint(OpLanesNum);
        Direction moveVector = direction.getNext();
        for (int i = 0; i < DirLanesNum; i++) {
            dir_lanes.add(new Lane(direction, i, sc.move(moveVector, i* Lane.LANE_SIZE),ec.move(moveVector, i* Lane.LANE_SIZE)));
        }
        for (int i = 1; i <= OpLanesNum; i++) {
            op_lanes.add(new Lane(direction.getOposite(), i-1, ec.move(moveVector.getOposite(), i* Lane.LANE_SIZE), sc.move(moveVector.getOposite(), i* Lane.LANE_SIZE)));
        }
    }

    public int dirLanesNum() {return dir_lanes.size();}
    public int opLanesNum() {return op_lanes.size();}
    public int lanesNum() {return dir_lanes.size() + op_lanes.size();}

    public int getRoadSize() {return (dir_lanes.size() + op_lanes.size()) * Lane.LANE_SIZE;}
    public Point getCenterStartPoint() {
        if(dir_lanes.isEmpty() || op_lanes.isEmpty()) throw new IllegalArgumentException("the list of lanes is empty");
        switch(direction){
            case RIGHT: return new Point(startPoint.move(direction.DOWN, op_lanes.size() * Lane.LANE_SIZE));
            case UP: return new Point(startPoint.move(direction.RIGHT, op_lanes.size() * Lane.LANE_SIZE));
            default: throw new IllegalArgumentException("this direction is not contained in getCenterStartPoint() method");
        }
    }
    public Point getCenterStartPoint(int upLanesNum) {
        switch(direction){
            case RIGHT: return new Point(startPoint.move(direction.DOWN, upLanesNum * Lane.LANE_SIZE));
            case UP: return new Point(startPoint.move(direction.RIGHT, upLanesNum * Lane.LANE_SIZE));
            default: throw new IllegalArgumentException("this direction is not contained in getCenterStartPoint() method");
        }
    }
    public Point getCenterEndPoint() {
        if(dir_lanes.isEmpty() || op_lanes.isEmpty()) throw new IllegalArgumentException("the list of lanes is empty");
        switch(direction){
            case RIGHT: return new Point(endPoint.move(direction.DOWN, op_lanes.size() * Lane.LANE_SIZE));
            case UP: return new Point(endPoint.move(direction.RIGHT, op_lanes.size() * Lane.LANE_SIZE));
            default: throw new IllegalArgumentException("this direction is not contained in getCenterEndPoint() method");
        }
    }
    public Point getCenterEndPoint(int upLanesNum) {
        switch(direction){
            case RIGHT: return new Point(endPoint.move(direction.DOWN, upLanesNum * Lane.LANE_SIZE));
            case UP: return new Point(endPoint.move(direction.RIGHT, upLanesNum * Lane.LANE_SIZE));
            default: throw new IllegalArgumentException("this direction is not contained in getCenterEndPoint() method");
        }
    }

    public void paintRoad(Graphics g) {
        int ROAD_SIZE = getRoadSize();      // road background
        switch(direction)
        {
            case RIGHT:
            case LEFT:
                g.setColor(Color.darkGray);
                g.fillRect((int) startPoint.x, (int)startPoint.y, (int)startPoint.distance(endPoint), ROAD_SIZE);

                g.setColor(Color.WHITE);                                                    // dashed lanes
                for (int y = (int) startPoint.y + Lane.LANE_SIZE; y < (int)startPoint.y + ROAD_SIZE; y += Lane.LANE_SIZE)
                    for (int x = (int) startPoint.x; x < endPoint.x; x += 5 * Lane.DASHED_LANE_LENGTH)
                        g.drawLine(x, y, x + Lane.DASHED_LANE_LENGTH, y);
                break;
            case UP:
            case DOWN:
                g.setColor(Color.darkGray);
                g.fillRect((int)endPoint.x, (int)endPoint.y, ROAD_SIZE, (int)endPoint.distance(startPoint));

                g.setColor(Color.WHITE);                                                    // dashed lanes
                for (int x = (int)endPoint.x + Lane.LANE_SIZE; x < (int)endPoint.x + ROAD_SIZE; x += Lane.LANE_SIZE)
                    for (int y = (int)endPoint.y; y < startPoint.y; y += 5 * Lane.DASHED_LANE_LENGTH)
                        g.drawLine(x, y, x, y + Lane.DASHED_LANE_LENGTH);
                break;
            default:
                throw new IllegalArgumentException("given direction is not contained in getOposite() method");
        }

        Point sc = getCenterStartPoint();
        Point ec = getCenterEndPoint();
        g.drawLine((int)sc.x, (int)sc.y , (int)ec.x, (int)ec.y);  // line between opposed lanes
    }

    public void paintCars(Graphics g) {
        for(Lane l : dir_lanes) l.drawCars(g);
        for(Lane l : op_lanes) l.drawCars(g);
    }

    private int counter = 0;
    public boolean addNewCar() {
        counter++;
        if(counter == 4) { counter = 0; return false;}
        Random rand = new Random();
        int n = rand.nextInt(dir_lanes.size() + op_lanes.size());

        if(n < dir_lanes.size()) {
            if(dir_lanes.get(n).addNewCar() == false) addNewCar();
        }

        else {
            if(op_lanes.get(n - dir_lanes.size()).addNewCar() == false) addNewCar();
        }

        return true;
    }

    public void addCar(Car c, Direction direction, int laneNum){
        switch(direction) {
            case RIGHT:
            case UP:
                if(laneNum >= dir_lanes.size()) throw new IllegalArgumentException("Lane number: " + laneNum + " don't exist");
                dir_lanes.get(laneNum).addCar(c);
                break;
            case LEFT:
            case DOWN:
                if(laneNum >= op_lanes.size()) throw new IllegalArgumentException("Lane number: " + laneNum + " don't exist");
                op_lanes.get(laneNum).addCar(c);
                break;
        }

    }

    public void moveCars() {
        for(Lane l : dir_lanes) l.moveCars();
        for(Lane l : op_lanes) l.moveCars();

        checkIntersection();
    }

    public void addIntersection(Intersection i) {
        intersections.add(i);
    }

    public void checkIntersection() {
        for(Intersection i : intersections){
            for(Lane l : dir_lanes) l.checkTraffic(i);
            for(Lane l : op_lanes) l.checkTraffic(i);
        }

    }

    public int getTrafficSize() {
        int trafficSize = 0;

        for(Lane l : dir_lanes) trafficSize += l.getTrafficSize();
        for(Lane l : op_lanes) trafficSize += l.getTrafficSize();

        return trafficSize;
    }

    public int getNumCarsOnRoad() {
        int cars = 0;

        for(Lane l : dir_lanes) cars += l.getCarsOnLaneNum();
        for(Lane l : op_lanes) cars += l.getCarsOnLaneNum();

        return cars;
    }

    public double getAvgSpeed() {
        double avgSpeed = 0.0;
        double speed;
        int lanesNum = (dir_lanes.size() + op_lanes.size());

        for(Lane l : dir_lanes){
            speed = l.getAvgSpeed();
            if(speed == -1) {
                lanesNum -= 1;
                continue;
            }
            else avgSpeed += speed;
        }
        for(Lane l : op_lanes) {
            speed = l.getAvgSpeed();
            if(speed == -1) {
                lanesNum -= 1;
                continue;
            }
            else avgSpeed += speed;
        }

        avgSpeed /= lanesNum;

        return  avgSpeed;
    }


}
