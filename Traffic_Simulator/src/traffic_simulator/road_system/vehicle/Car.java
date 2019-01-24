package traffic_simulator.road_system.vehicle;

import traffic_simulator.position.*;

import java.awt.Color;
import java.util.Random;

import static java.lang.StrictMath.max;

public class Car implements Comparable<Car>{
    public static final int HEIGHT = 8, WIDTH = 12;
    public static final int[] SAFE_BUFFOR = {0,5,20};
    private static final double MIN_SPEED = 1;
    private static final double MAX_SPEED = 3;
    private static final double ACCELERATION = 0.1;

    private Point position;
    private double speed;
    public final double START_SPEED;


    public Car(Point position) {
        this.position = position;

        Random rand = new Random();
        double rd = rand.nextDouble();
        this.speed = MIN_SPEED + rd * (MAX_SPEED - MIN_SPEED);
        this.START_SPEED = this.speed;
    }

    public int getX() {return (int)position.x;}
    public int getY() {return (int)position.y;}
    public int getFrontPos(Direction d) {
        if (d == Direction.RIGHT) return (int)position.x + WIDTH;
        if (d == Direction.LEFT) return (int)position.x;
        if (d == Direction.UP)  return (int)position.y;
        if (d == Direction.DOWN) return (int)position.y + WIDTH;
        throw new IllegalArgumentException("Enum not found");
    }
    public Point getPosition() {return position;}
    public double getSpeed() {return speed;}



    public void stop() {speed = 0;}

    public void setStartSpeed() {speed = START_SPEED;}

    public void slowDown(double minSpeed) {speed = Math.max(speed - ACCELERATION, minSpeed);}

    public void speedUp() {if(speed < START_SPEED) speed += ACCELERATION;}

    public void move(Direction d) { position = position.move(d,speed); }

    public Color getColor() {
        if(speed > 0.9 * MAX_SPEED) return new Color(0,0,250);
        else if(speed > 0.75 * MAX_SPEED) return new Color(0,0,200);
        else if(speed > 0.5 * MAX_SPEED) return new Color(0,0,150);
        else if(speed > 0.25 * MAX_SPEED) return new Color(0,0,100);
        else if(speed > 0) return new Color(0,0,50);
        else return new Color(0,0,0);
    }

    public boolean collides(Car c) {
        if(c == null) return false;
        if(position.distance(c.getPosition()) < WIDTH + SAFE_BUFFOR[0]) {return true;}
        else if(position.distance(c.getPosition()) < WIDTH + SAFE_BUFFOR[1]) {speed = c.getSpeed(); return true;}
        else if(position.distance(c.getPosition()) < WIDTH + SAFE_BUFFOR[2]) {if(speed - c.getSpeed() > 0.2) speed = max(c.getSpeed(), speed - ACCELERATION); return true;}
        return false;
    }

    public int compareTo(Car c) {
        if(position.y == c.position.y) return (int) position.x - (int) c.position.x;
        return (int)position.y - (int)c.position.y;
    }

    public String toString() {
        return "Car on position : " + position.toString();
    }

    public void setPosition(Point position) {
        this.position = position;
    }
}
