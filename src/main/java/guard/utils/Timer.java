package guard.utils;

public class Timer {

    private static long lastMS = 1L;

    public boolean isDelayComplete(float f) {
        if(System.currentTimeMillis() - lastMS >= f) {
            return true;
        }
        return false;
    }
    public long getDelay() {
        return System.currentTimeMillis() - lastMS;
    }

    public void setLastMS(long lastMS) {
        this.lastMS = lastMS;
    }

    public long getCurrentMS() {
        return System.nanoTime() / 1000000L;
    }

    public void setLastMS() {
        this.lastMS = System.currentTimeMillis();
    }

    public int convertToMS(int perSecond) {
        return 1000 / perSecond;
    }

    public void reset() {
        lastMS = getCurrentMS();
    }

}
