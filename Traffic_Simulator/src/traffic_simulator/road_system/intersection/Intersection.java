package traffic_simulator.road_system.intersection;

import traffic_simulator.road_system.road.*;
import traffic_simulator.position.*;
import traffic_simulator.road_system.vehicle.*;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.LinkedList;



public class Intersection extends Rectangle {
    public static TrafficLights trafficlights = new TrafficLights();
    private Road verticalRoad;
    private Road horizontalRoad;
    private boolean[] isRoad = new boolean[4];
    private HashMap<lanePointer, lanePointer> nextLane = new HashMap<>();

    private LinkedList<carOnIntersection> cars = new LinkedList<>();


    public Intersection(Road roadOne, Road roadTwo) {
        if(roadOne.direction == roadTwo.direction) throw new IllegalArgumentException("Parallel roads");

        switch(roadOne.direction) {
            case RIGHT:
                horizontalRoad = roadOne;
                verticalRoad = roadTwo;
                break;
            case UP:
                horizontalRoad = roadTwo;
                verticalRoad = roadOne;
                break;
            default:
                throw new UnsupportedOperationException();
        }

        x = (int) verticalRoad.startPoint.x;
        y = (int) horizontalRoad.startPoint.y;
        width = verticalRoad.getRoadSize();
        height = horizontalRoad.getRoadSize();

        if(verticalRoad.endPoint.y < this.y) {
            isRoad[0] = true;
            setDirection(Direction.UP, Direction.UP, verticalRoad.dirLanesNum());
        }
        else {
            setDirection(Direction.UP, Direction.RIGHT, Math.min(horizontalRoad.dirLanesNum(), verticalRoad.dirLanesNum()));

            if(verticalRoad.dirLanesNum() > horizontalRoad.dirLanesNum())
                setDirection(Direction.UP, Direction.LEFT, verticalRoad.dirLanesNum() - horizontalRoad.dirLanesNum());
        }


        if(horizontalRoad.endPoint.x > this.x + this.width) {
            isRoad[1] = true;
            setDirection(Direction.RIGHT, Direction.RIGHT, horizontalRoad.dirLanesNum());
        }
        else {
            setDirection(Direction.RIGHT, Direction.DOWN, Math.min(horizontalRoad.dirLanesNum(), verticalRoad.opLanesNum()));

            if(horizontalRoad.dirLanesNum() > verticalRoad.opLanesNum())
                setDirection(Direction.RIGHT, Direction.UP, horizontalRoad.dirLanesNum() - verticalRoad.opLanesNum());
        }


        if(verticalRoad.startPoint.y > this.y + this.height) {
            isRoad[2] = true;
            setDirection(Direction.DOWN, Direction.DOWN, verticalRoad.opLanesNum());
        }
        else {
            setDirection(Direction.DOWN, Direction.LEFT, Math.min(verticalRoad.opLanesNum(), horizontalRoad.opLanesNum()));

            if(verticalRoad.opLanesNum() > horizontalRoad.opLanesNum())
                setDirection(Direction.DOWN, Direction.RIGHT, verticalRoad.opLanesNum() - horizontalRoad.opLanesNum());
        }


        if(horizontalRoad.startPoint.x < this.x) {
            isRoad[3] = true;
            setDirection(Direction.LEFT, Direction.LEFT, horizontalRoad.opLanesNum());
        }
        else {
            setDirection(Direction.LEFT, Direction.UP, Math.min(horizontalRoad.opLanesNum(), verticalRoad.dirLanesNum()));

            if(horizontalRoad.opLanesNum() > verticalRoad.dirLanesNum())
                setDirection(Direction.LEFT, Direction.DOWN, horizontalRoad.opLanesNum() - verticalRoad.dirLanesNum());
        }


        horizontalRoad.addIntersection(this);
        verticalRoad.addIntersection(this);
    }

    public void paintIntersection(Graphics g) {
        g.setColor(Color.darkGray);
        g.fillRect(x, y, width, height);

        g.setColor(Color.WHITE);
        for (int y = this.y ; y < this.y + height; y += Lane.LANE_SIZE) {
            if(isRoad[3]) g.drawLine( this.x, y, this.x, y + Lane.LANE_SIZE  / 4);
            if(isRoad[1]) g.drawLine( this.x + width, y, this.x + width, y + Lane.LANE_SIZE  / 4);
        }
        for (int x = this.x ; x < this.x + width; x += Lane.LANE_SIZE) {
            if(isRoad[0])  g.drawLine(x,  this.y, x  + Lane.LANE_SIZE  / 4,  this.y);
            if(isRoad[2]) g.drawLine(x,  this.y + height, x  + Lane.LANE_SIZE  / 4,  this.y + height);
        }

        if(canGo(Orientation.HORIZONTAL)) g.setColor(new Color(0,130,0));
        else  g.setColor(new Color(150,0,0));

        int trafficLightSize = (int)(0.6 * Lane.LANE_SIZE);
        if(isRoad[3]) {
            int startY = (int) this.y + horizontalRoad.opLanesNum() * Lane.LANE_SIZE + (int)(0.3 * Lane.LANE_SIZE);
            for (int y = startY; y < this.y + height; y += Lane.LANE_SIZE)
                g.fillOval(x, y, trafficLightSize, trafficLightSize);
        }
        if(isRoad[1]) {
            int startY = (int) this.y + (int) 0.8 * Lane.LANE_SIZE;
            for (int y = startY; y < this.y + horizontalRoad.opLanesNum() * Lane.LANE_SIZE ; y += Lane.LANE_SIZE)
                g.fillOval(x + width - 5, y, trafficLightSize, trafficLightSize);
        }

        if(canGo(Orientation.VERTICAL)) g.setColor(new Color(0,130,0));
        else  g.setColor(new Color(150,0,0));

        if(isRoad[0]) {
            int startX = (int) this.x + (int)(0.3 * Lane.LANE_SIZE);
            for (int x = startX; x < this.x + verticalRoad.opLanesNum()*Lane.LANE_SIZE; x += Lane.LANE_SIZE)
                g.fillOval(x, this.y, trafficLightSize, trafficLightSize);
        }
        if(isRoad[2]) {
            int startX = (int) this.x + verticalRoad.opLanesNum() * Lane.LANE_SIZE + (int)(0.3 * Lane.LANE_SIZE);
            for (int x = startX; x < this.x + width; x += Lane.LANE_SIZE)
                g.fillOval(x , this.y + height - 5, trafficLightSize, trafficLightSize);
        }

    }

    public void setDirection(Direction origin, Direction target, int laneNumber) {
        if(target == origin.getOposite()) throw new IllegalArgumentException("Origin and target directions are the same");
        // sprawdzić czy taka droga istnieje

        if(target == origin.getNext()) {
            int originLanesNum;
            int targetLanesNum;

            if(origin == Direction.UP) {
                originLanesNum = verticalRoad.dirLanesNum();
                targetLanesNum = horizontalRoad.dirLanesNum();
            }
            else if(origin == Direction.DOWN) {
                originLanesNum = verticalRoad.opLanesNum();
                targetLanesNum = horizontalRoad.opLanesNum();
            }
            else if(origin == Direction.RIGHT) {
                originLanesNum = horizontalRoad.dirLanesNum();
                targetLanesNum = verticalRoad.opLanesNum();
            }
            else if(origin == Direction.LEFT) {
                originLanesNum = horizontalRoad.opLanesNum();
                targetLanesNum = verticalRoad.dirLanesNum();
            }
            else
                throw new IllegalArgumentException("Enum not found");

            originLanesNum -= 1;
            targetLanesNum -= 1;

            while(originLanesNum >=0 && targetLanesNum >= 0){
                nextLane.put(new lanePointer(origin, originLanesNum), new lanePointer(target, targetLanesNum));
                originLanesNum--;
                targetLanesNum--;
            }
        }

        for(int i = 0; i < laneNumber; i++){
            nextLane.put(new lanePointer(origin, i), new lanePointer(target, i));
        }
    }

    public void addCar(Car c, Lane l) {
        lanePointer next = nextLane.get(new lanePointer(l.getDirection(),l.LANE_NUM));

        Road nextRoad;
        if(next.dir == Direction.RIGHT || next.dir == Direction.LEFT) nextRoad = horizontalRoad;
        else nextRoad = verticalRoad;

        cars.add(new carOnIntersection(c, nextRoad, next.dir, next.num));
    }

    public void moveCarsOnIntersection() {
        for(carOnIntersection c : cars) c.removeFromIntersection();
    }

    public String toString(){return "Intersection on pos: (" + x + " , " + y + " )";};

    public int getSidePosition(Direction direction){
        switch(direction) {
            case UP:
                return y + height;
            case DOWN:
                return y;
            case RIGHT:
                return x;
            case LEFT:
                return x + width;
            default:
                throw new IllegalArgumentException("Enum not found");
        }
    }

    public boolean canGo(Lane l) {
        return trafficlights.canGo(l);
    }

    public boolean canGo(Orientation o) {
        return trafficlights.canGo(o);
    }

    private class lanePointer {
        Direction dir;
        int num;

        public lanePointer(Direction d, int laneNumber) {
            dir = d;

            if(d == Direction.RIGHT && laneNumber > horizontalRoad.dirLanesNum()) throw new IllegalArgumentException("Lane number: " + laneNumber + " don't exist");
            if(d == Direction.LEFT && laneNumber > horizontalRoad.opLanesNum()) throw new IllegalArgumentException("Lane number: " + laneNumber + " don't exist");
            if(d == Direction.UP && laneNumber > verticalRoad.dirLanesNum()) throw new IllegalArgumentException("Lane number: " + laneNumber + " don't exist");
            if(d == Direction.DOWN && laneNumber > verticalRoad.opLanesNum()) throw new IllegalArgumentException("Lane number: " + laneNumber + " don't exist");

            num = laneNumber;
        }

        public String toString() {
            return "Next lane: " + dir + " number: " + num;
        }

        public boolean equals(Object o){                      // coś popierdzielone z hashmap - nie zwraca wartości dla klucza
            if(o == null) return false;
            if(!(o instanceof lanePointer)) return false;
            lanePointer pointer = (lanePointer) o;
            if(this.dir == pointer.dir && this.num == pointer.num) return true;
            return false;
        }

        public int hashCode() {
            int dirCode;
            switch(dir) {
                case UP:
                    dirCode = 0;
                    break;
                case RIGHT:
                    dirCode = 1;
                    break;
                case DOWN:
                    dirCode = 2;
                    break;
                case LEFT:
                    dirCode = 3;
                    break;
                default:
                    throw new IllegalArgumentException("Enum not found");
            }
            return 100*dirCode + num;
        }
    }

    private class carOnIntersection {
        Car c;
        Road nextRoad;
        int laneNum;
        Direction d;


        public carOnIntersection(Car c, Road nextRoad, Direction dir, int laneNum) {
            this.c = c;
            this.nextRoad = nextRoad;
            this.laneNum = laneNum;
            d = dir;
        }

        public void removeFromIntersection(){
            switch(d){
                case UP:
                    c.setPosition(new Point(c.getX(), c.getY() - height - 10));
                    break;
                case DOWN:
                    c.setPosition(new Point(c.getX(), c.getY() + height + 10));
                    break;
                case RIGHT:
                    c.setPosition(new Point(c.getX() + width + 10, c.getY()));
                    break;
                case LEFT:
                    c.setPosition(new Point(c.getX() - width - 10, c.getY()));
                    break;
            }
            nextRoad.addCar(c, d, laneNum);
            cars.remove(this);
        }
    }
}

