package traffic_simulator.road_system.data;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class Settings extends JPanel {
    private Color background = new Color(255,255,204);
    private static final int MIN_CARS_NUM = 1;
    private static final int MAX_CARS_NUM = 10;
    private static final int INIT_CARS_NUM = 6;
    private static int CARS_NUM;

    private static final int MAX_SIMULATION_SPEED = 100;
    private static final int MIN_SIMULATION_SPEED = 0;
    private static final int INIT_SIMULATION_SPEED = 50;
    private static int SIMULATION_SPEED;
    private static final int MIN_REFRESH_RATE = 15;

    public Settings() {
        setBackground(background);
        setLayout(new GridLayout(4,1));

        this.add(new JLabel("Simulation speed", SwingConstants.CENTER));

        JSlider simSpeed = new JSlider(JSlider.HORIZONTAL,MIN_SIMULATION_SPEED, MAX_SIMULATION_SPEED, INIT_SIMULATION_SPEED);
        simSpeed.setMajorTickSpacing(25);
        simSpeed.setMinorTickSpacing(5);
        simSpeed.setPaintTicks(true);
        simSpeed.setPaintLabels(true);
        simSpeed.setBackground(background);
        simSpeed.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e) {
                SIMULATION_SPEED = simSpeed.getValue();
            }
        });
        this.add(simSpeed);


        this.add(new JLabel("Number of vehicles", SwingConstants.CENTER));

        JSlider vehNum = new JSlider(JSlider.HORIZONTAL,MIN_CARS_NUM, MAX_CARS_NUM, INIT_CARS_NUM);
        vehNum.setMajorTickSpacing(9);
        vehNum.setMinorTickSpacing(1);
        vehNum.setPaintTicks(true);
        vehNum.setPaintLabels(true);
        vehNum.setBackground(background);
        vehNum.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e) {
                CARS_NUM = vehNum.getValue();
            }
        });
        this.add(vehNum);
    }

    public static int timeBetweenVehicles() {
        return MAX_CARS_NUM - CARS_NUM + 4;
    }

    public static int threadSleep() {
        return (int) MIN_REFRESH_RATE - SIMULATION_SPEED / 10;
    }
}
