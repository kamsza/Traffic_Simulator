package traffic_simulator.road_system.road;

import traffic_simulator.position.*;
import traffic_simulator.road_system.RoadSystem;
import traffic_simulator.road_system.intersection.*;
import traffic_simulator.road_system.vehicle.*;


import java.awt.Color;
import java.awt.Graphics;
import java.util.Iterator;
import java.util.LinkedList;



public class Lane {
    public static final int LANE_SIZE = 10;
    public static final int DASHED_LANE_LENGTH = 3;

    private Point startPoint, endPoint;
    public final int LANE_NUM;
    Direction direction;

    private LinkedList<Car> cars = new LinkedList<>();
    private LinkedList<Car> traffic = new LinkedList<>();


    public Lane(Direction d, int laneNum, Point start, Point end) {
        direction = d;
        startPoint = start;
        endPoint = end;
        LANE_NUM = laneNum;
    }

    public Point getStartPoint() {return startPoint;}

    public Direction getDirection() {return direction;}

    public void drawCars(Graphics g) {
        for (Car c : cars) {
            g.setColor(c.getColor());
            switch (direction) {
                case UP:
                case DOWN:
                    int y = c.getY() + Lane.LANE_SIZE / 2 - c.HEIGHT / 2;
                    g.fillRect(c.getX(), y, c.HEIGHT, c.WIDTH);
                    break;
                case RIGHT:
                case LEFT:
                    int x = c.getX() + Lane.LANE_SIZE / 2 - c.HEIGHT / 2;
                    g.fillRect(x, c.getY(), c.WIDTH, c.HEIGHT);
                    break;
                default:
                    throw new IllegalArgumentException("given direction is not contained in drawCars() method in Lane class");
            }
        }
    }

    public void moveCars() {
        collision();
        for (Car c : cars) {
            c.move(direction);
        }

        if (!cars.isEmpty()) {
            Car last = cars.getLast();
            if (last.getX() < 0 || last.getX() > RoadSystem.WINDOW_WIDTH) cars.removeLast();
            else if (last.getY() < 0 || last.getY() > RoadSystem.WINDOW_HEIGHT) cars.removeLast();

            //   if((direction == Direction.RIGHT || direction == Direction.LEFT) && last.getX() > endPoint.x)  cars.removeLast();
            //    if((direction == Direction.UP || direction == Direction.DOWN) && last.getY() > endPoint.y)  cars.removeLast();
        }
    }

    public boolean addNewCar() {
        if (startPoint.x != 0 && startPoint.x != RoadSystem.WINDOW_WIDTH && startPoint.y != 0 && startPoint.y != RoadSystem.WINDOW_HEIGHT)
            return false;
        Car c = new Car(startPoint);
        if (!cars.isEmpty() && c.collides(cars.getFirst())) return false;
        cars.addFirst(c);
        return true;
    }

    public void addCar(Car c) {
        if(cars.isEmpty()) cars.add(c);
        for (int i = 0; i < cars.size(); i++) {
            if (c.compareTo(cars.get(i)) < 0){
                cars.add(i, c);
                return;
            }
            cars.addLast(c);
        }
    }

    private void collision() {
        Iterator<Car> c = cars.iterator();
        Iterator<Car> nc = cars.iterator();
        if (nc.hasNext()) nc.next();
        while (nc.hasNext()) {
            Car car = (Car) c.next();
            Car nextCar = (Car) nc.next();
            boolean collides = false;
            collides = car.collides(nextCar);

            if(!collides && !traffic.contains(car)) car.speedUp();
        }
    }

    public boolean isOnLane(Car c) {
        switch (direction) {
            case RIGHT:
                if (c.getPosition().y - startPoint.y < LANE_SIZE) return true;
                break;
            case UP:
                if (c.getPosition().y - startPoint.y < LANE_SIZE) return true;
                break;
        }
        return false;
    }

    public void checkTraffic(Intersection i) {
        if (!traffic.isEmpty() && i.canGo(this)) {
            for(Car c : traffic) c.speedUp();
            traffic.clear();
        }

        int intersPos = i.getSidePosition(direction);

        for (Car c : cars) {
            int pos = c.getFrontPos(direction);

            if (this.direction.getVal() == 1 && intersPos < pos) break;
            if (this.direction.getVal() == -1 && pos < intersPos) break;

            if (Math.abs(intersPos - pos) < 15 * c.getSpeed() & i.canGo(this)) {
                //c.speedUp();
                c.setStartSpeed();
                return;
            }

            if (Math.abs(intersPos - pos) < 15 * c.getSpeed()) c.slowDown(0.4);

            if (Math.abs(intersPos - pos) < 2 * c.getSpeed()) {
                if (!traffic.contains(c)) traffic.add(c);
                if (!i.canGo(this)) {
                    c.stop();
                }

            }

            if (c.getSpeed() == 0 && !traffic.contains(c)) traffic.add(c);
        }
    }

    public int getTrafficSize() {return traffic.size();}
    public int getCarsOnLaneNum() {return cars.size();}
    public double getAvgSpeed() {
        double avgSpeed = 0.0;
        for(Car c :cars) avgSpeed += c.getSpeed();
        if(cars.size() == 0) return -1;
        avgSpeed /= cars.size();
        return avgSpeed;
    }
}
