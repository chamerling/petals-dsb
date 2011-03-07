package org.ow2.petals.monitoring.router;

public class MonitoringThread extends Thread {

    /**
     * the frequency of actions to repeat in milliseconds. Default value is set
     * to 10000 (10 seconds)
     */
    private int SENDING_FREQUENCY = 10000;

    /**
     * The Monitoring used to notify
     */
    private Monitoring monitor;

    /**
     * Flag to skip the next wake up
     */
    private boolean skipNextWakeUp;

    /**
     * Creates a new daemon thread
     * 
     */
    public MonitoringThread(Monitoring monitor) {
        this.monitor = monitor;
        SENDING_FREQUENCY = monitor.getTimeSendLimit();
        this.skipNextWakeUp = false;
        this.setDaemon(true);
    }

    /**
     * Returns the time to wait before notifying.
     * 
     * @return an int representation of the time to wait between too calls to
     *         the run() method, in milliseconds
     */
    public int getSendingFrequency() {
        return SENDING_FREQUENCY;
    }

    /**
     * @see Thread#run()
     */
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                sleep(SENDING_FREQUENCY);
                if (!skipNextWakeUp) {
                    monitor.notifyMessages();
                } else {
                    skipNextWakeUp = false;
                }
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Sets the time to wait before notifying.
     * 
     * @param sending_frequency
     *            the time to wait in milliseconds
     */
    public void setSendingFrequency(int sending_frequency) {
        SENDING_FREQUENCY = sending_frequency;
    }

    /**
     * @return the skipNextWakeUp
     */
    public boolean isSkipNextWakeUp() {
        return skipNextWakeUp;
    }

    /**
     * @param skipNextWakeUp
     *            the skipNextWakeUp to set
     */
    public void setSkipNextWakeUp(boolean skipNextWakeUp) {
        this.skipNextWakeUp = skipNextWakeUp;
    }
}
