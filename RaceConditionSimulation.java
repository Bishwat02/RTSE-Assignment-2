// RaceConditionSimulation.java

public class RaceConditionSimulation {

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

    // Shared actuator class
    static class Servo {
        private String state = "OFF";

        public void setState(String newState) {
            System.out.println(CYAN + "\n------------------------\nCYCLE - " + Thread.currentThread().getName() + RESET);
            System.out.println(getColor() + "[" + Thread.currentThread().getName() + "] SETTING servo to: " + newState + RESET);

            try {
                Thread.sleep(100); // simulate actuator delay
            } catch (InterruptedException e) {
                System.out.println("Thread interrupted: " + e.getMessage());
            }

            state = newState;

            System.out.println(getColor() + "[" + Thread.currentThread().getName() + "] SERVO is now: " + state + RESET);
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

    // Soil Task thread
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

    // Rain Task thread
    static class ReadRainSensorTask extends Thread {
        private final Servo servo;

        public ReadRainSensorTask(Servo servo) {
            this.servo = servo;
            this.setName("RainSensorTask");
        }

        @Override
        public void run() {
            //Cycle through setting the servo state to OFF
            for (int i = 1; i <= 5; i++) {
                servo.setState("OFF");
            }
        }
    }
}
