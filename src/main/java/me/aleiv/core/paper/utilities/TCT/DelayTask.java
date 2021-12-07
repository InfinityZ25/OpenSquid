package me.aleiv.core.paper.utilities.TCT;

/**
 * Utility Runnable that eases the creation of delayed tasks.
 * 
 * @author jcedeno
 */
public class DelayTask implements Runnable {
    private long time;

    public DelayTask(long time) {
        this.time = time;
    }

    public static DelayTask of(long milliseconds) {
        return new DelayTask(milliseconds);
    }

    @Override
    public void run() {
        try {
            Thread.sleep(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}