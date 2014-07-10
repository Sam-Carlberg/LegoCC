package edu.wpi.lego.advancedcourse;

/**
 * Represents an object that determines when the send button should be enabled
 * or not.
 *
 * @author Paul Malmsten <pmalmsten@gmail.com>
 *
 */
public class SendEnableManager {

    /**
     * Represents an object that can handle changes in allowing sending of
     * commands.
     */
    public interface SendStatusHandler {

        /**
         * Called when it is determined that sending is allowed or disallowed.
         *
         * @param isAllowed True if sending commands to a remote device is
         * allowed; false otherwise.
         */
        public void sendingEnabledChanged(boolean isAllowed);
    }
    private boolean connectionEstablished = false;
    private boolean countdownRunning = false;
    private final SendStatusHandler statusHandler;

    public SendEnableManager(SendStatusHandler handler) {
        statusHandler = handler;
    }

    /**
     * Informs the send enable manager of the status of a remote connection.
     *
     * @param isConnected True if a connection is available; false otherwise.
     */
    public void updateConnectionStatus(boolean isConnected) {
        if (connectionEstablished != isConnected) {
            connectionEstablished = isConnected;
            informHandler();
        }
    }

    /**
     * Informs the send enable manager of the status of a countdown (i.e. if a
     * countdown is running, then sending should be locked out).
     *
     * @param isRunning True if sending should be prevented; false otherwise.
     */
    public void updateCountdownStatus(boolean isRunning) {
        if (countdownRunning != isRunning) {
            countdownRunning = isRunning;
            informHandler();
        }
    }

    /**
     * Informs the status handler of the new status.
     */
    private void informHandler() {
        statusHandler.sendingEnabledChanged(!countdownRunning && connectionEstablished);
    }
}
