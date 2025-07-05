// RaceConditionSemaphoreFix.java

import java.util.concurrent.Semaphore;

public class RaceConditionSemaphoreFix {

    // ANSI color codes for readability in console output
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String BLUE = "\u001B[34m";
    public static final String CYAN = "\u001B[36m";

    public static void main(String[] args) {
        Servo sharedServo = new Servo();

        Thread soilTask = new ReadSoilMoistureTask(sharedServo);
        Thread rainTask = new ReadRainSensorTask(sharedServo);

        soilTask.start();
        rainTask.start();
    }

    // Shared actuator with mutual exclusion
    static class Servo {
        private String state = "OFF";
        private final Semaphore lock = new Semaphore(1);

        public void setState(String newState) {
            try {
                lock.acquire(); // Enforce mutual exclusion

                System.out.println(CYAN + "\n------------------------\nCYCLE - " + Thread.currentThread().getName() + RESET);
                System.out.println(getColor() + "[" + Thread.currentThread().getName() + "] wants to SET servo to: " + newState + RESET);

                Thread.sleep(200); // Simulate control delay
                state = newState;

                System.out.println(getColor() + "[" + Thread.currentThread().getName() + "] finished SETTING servo to: " + state + RESET);
            } catch (InterruptedException e) {
                System.out.println("Thread interrupted: " + e.getMessage());
            } finally {
                lock.release(); // Always release the lock
            }
        }

        public String getState() {
            return state;
        }

        private String getColor() {
            String name = Thread.currentThread().getName();
            if (name.contains("Soil")) return BLUE;
            if (name.contains("Rain")) return RED;
            return RESET;
        }
    }

    // Soil Task with mutual exclusion
    static class ReadSoilMoistureTask extends Thread {
        private final Servo servo;

        public ReadSoilMoistureTask(Servo servo) {
            this.servo = servo;
            this.setName("SoilMoistureTask");
        }

        @Override
        public void run() {
            // Cycle through setting the servo state to ON
            for (int i = 1; i <= 5; i++) {
                servo.setState("ON");
            }
        }
    }

    // Rain Task with mutual exclusion
    static class ReadRainSensorTask extends Thread {
        private final Servo servo;

        public ReadRainSensorTask(Servo servo) {
            this.servo = servo;
            this.setName("RainSensorTask");
        }

        @Override
        public void run() {
            // Cycle through setting the servo state to OFF
            for (int i = 1; i <= 5; i++) {
                servo.setState("OFF");
            }
        }
    }
}
