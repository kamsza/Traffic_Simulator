package traffic_simulator.road_system.data;

import  traffic_simulator.road_system.*;

import javax.swing.*;
import java.awt.*;

public class Stats  extends JPanel implements Runnable{
    private Color background = new Color(255,255,204);
    RoadSystem roadSystem;
    JLabel carsOnRoads = new JLabel();
    JLabel carsInTraffic = new JLabel();
    JLabel averageSpeed = new JLabel();

    public Stats(RoadSystem roadSystem) {
        this.roadSystem = roadSystem;

        this.setPreferredSize(new Dimension(50,50));

        setBackground(background);
        setLayout(new GridLayout(3,2));

        this.add(new JLabel("Cars on roads: ", SwingConstants.CENTER));
        this.add(carsOnRoads);

        this.add(new JLabel("Cars in traffic: ", SwingConstants.CENTER));
        this.add(carsInTraffic);

        this.add(new JLabel("Average speed: ", SwingConstants.CENTER));
        this.add(averageSpeed);
    }

    public void run() {
        while(true) {
             carsOnRoads.setText(Integer.toString(roadSystem.getNumCarsOnRoads()));
             carsInTraffic.setText(Integer.toString(roadSystem.getTrafficSize()));

             int avgSpeed = (int)(10 * roadSystem.getAvgSpeed());
             averageSpeed.setText(Integer.toString(avgSpeed));
            try {
                Thread.sleep(1000);
            } catch (Exception ex) {
            }
        }
    }


}
