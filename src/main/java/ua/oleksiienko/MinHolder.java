package ua.oleksiienko;

public class MinHolder {
    private volatile int min;

    public MinHolder() {
        this.min = Integer.MAX_VALUE;
    }

    public int get() {
        return min;
    }

    public synchronized void update(int val) {
        min = Math.min(min, val);
    }
}
