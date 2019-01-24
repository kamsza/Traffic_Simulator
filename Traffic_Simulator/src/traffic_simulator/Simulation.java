package traffic_simulator;

import traffic_simulator.road_system.*;
import traffic_simulator.road_system.data.*;
import traffic_simulator.road_system.intersection.*;
import traffic_simulator.position.*;
import java.awt.*;

import javax.swing.*;

public class Simulation extends Thread {
    JFrame frame;
    JPanel background;
    RoadSystem roads;
    Stats stats;
    Settings settings;
    JButton startButton;

    public Simulation() {
        frame = new JFrame();
        frame.setSize(1200, 800);
        frame.setTitle("Traffic simulator");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        background = new JPanel(new GridBagLayout());
        background.setBackground(Color.lightGray);

        roads = new RoadSystem();
        background.add(roads, new GridBagConstraints(0, 0, 1, 4, 0.1, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(10, 10, 10, 0), 0, 0));

        stats = new Stats(roads);
        background.add(stats, new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH,
                new Insets(10, 0, 10, 10), 0, 0));

        Thread t = new Thread(stats);
        t.start();


        settings = new Settings();
        background.add(settings, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH,
                new Insets(10, 0, 10, 10), 0, 0));

        JButton trafficLights = new JButton("HORIZONTAL");
        trafficLights.addActionListener(Intersection.trafficlights);
        background.add(trafficLights, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.1,
                GridBagConstraints.WEST, GridBagConstraints.BOTH,
                new Insets(10, 0, 10, 10), 0, 0));

        frame.add(background);
    }

    public void addRoad(Orientation o, int location, int URlanes, int DLlanes) {
        roads.addRoad(o, location, DLlanes, URlanes);
    }

    public void addRoad(Direction direction, int numOfPerpedicularRoad, int location, int URlanes, int DLlanes) {
        roads.addRoad(direction, numOfPerpedicularRoad, location, URlanes, DLlanes);

    }

    public void addIntersection(int firstRoadNum, int secondRoadNum) {
        roads.addIntersection(firstRoadNum, secondRoadNum);
    }

    public void run() {
        int counter = 0;

        while (true) {
            roads.moveCars();
            roads.repaint();
            counter++;

            try {
                Thread.sleep(Settings.threadSleep());
            } catch (Exception ex) {
            }

            if (counter >= Settings.timeBetweenVehicles()) {
                roads.addNewCar();
                counter = 0;
            }
        }
    }
}


