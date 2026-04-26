package HMS.utils;

import javax.swing.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ClockThread — A background thread that continuously updates a JLabel with the
 * current time.
 * - Extends Thread class
 * - Uses run() method
 * - Uses Thread.sleep() for inter-thread timing
 * - Updates the Swing UI thread safely using SwingUtilities.invokeLater
 */
public class ClockThread extends Thread {

    private final JLabel clockLabel;
    private volatile boolean running = true;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ClockThread(JLabel clockLabel) {
        this.clockLabel = clockLabel;
        // Mark as daemon so it automatically stops when the main application exits
        this.setDaemon(true);
    }

    @Override
    public void run() {
        while (running) {
            String currentTime = LocalDateTime.now().format(formatter);

            // Safely update the Swing component from this background thread
            SwingUtilities.invokeLater(() -> clockLabel.setText(currentTime));

            try {
                // Pause for 1 second before updating again
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Logger.warn("Clock thread interrupted.");
                running = false;
            }
        }
    }

    public void stopClock() {
        this.running = false;
    }
}
