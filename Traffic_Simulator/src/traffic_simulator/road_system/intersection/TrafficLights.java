package traffic_simulator.road_system.intersection;

import traffic_simulator.position.*;
import traffic_simulator.road_system.road.Lane;


import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TrafficLights implements ActionListener {
    Orientation orientation;


    public TrafficLights() {
        orientation = Orientation.HORIZONTAL;
    }

    public void actionPerformed(ActionEvent e) {
        orientation = orientation.changeOrientation();
        ((JButton)e.getSource()).setText(orientation.toString());
    }


    public boolean canGo(Lane l) {
        return orientation.thisOrientacion(l.getDirection());
    }

    public boolean canGo(Orientation o) {
        return orientation.equals(o);
    }
}
