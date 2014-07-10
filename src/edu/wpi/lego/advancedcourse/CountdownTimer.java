package edu.wpi.lego.advancedcourse;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Represents an object that counts down, calls a status handler, and calls a
 * completion handler.
 *
 * @author Paul Malmsten <pmalmsten@gmail.com>
 *
 */
public class CountdownTimer {

    /**
     * Represents an object that can handle countdown status.
     */
    public interface CountdownHandler {

        /**
         * Called when a countdown is started.
         */
        public void onStart();

        /**
         * Called on every tick of the timer.
         *
         * @param remainingSeconds The number of seconds remaining on the clock.
         */
        public void onTick(int remainingSeconds);

        /**
         * Called when the countdown expires.
         */
        public void onTimeout();
    }

    private final int m_initialSeconds;
    private final CountdownHandler m_hander;
    private int m_remainingSeconds;
    private Timer m_timer;

    /**
     * Creates a new countdown timer with the given period in seconds.
     *
     * @param durationSeconds The duration of the timer.
     * @param countdownHandler The object to inform of countdown status.
     */
    public CountdownTimer(int durationSeconds, CountdownHandler countdownHandler) {
        m_initialSeconds = durationSeconds;
        m_hander = countdownHandler;
    }

    /**
     * Resets the countdown to the inital value and starts it
     */
    public void resetAndStart() {
        if (m_timer == null) {
            m_remainingSeconds = m_initialSeconds;

            m_timer = new Timer();
            m_timer.scheduleAtFixedRate(new TimerTask() {

                @Override
                public void run() {
                    m_remainingSeconds--;
                    m_hander.onTick(m_remainingSeconds);

                    if (m_remainingSeconds <= 0) {
                        // On complete, clean up the timer
                        this.cancel();
                        m_timer = null;
                        m_hander.onTimeout();
                    }
                }
            }, 1000, 1000);

            // First tick
            m_hander.onStart();
            m_hander.onTick(m_remainingSeconds);
        }
    }
}
