package guard.utils;

import java.util.Collection;
import java.util.LinkedList;

public final class SampleList<T> extends LinkedList<T> {

    private final int sampleSize;
    private final boolean update;

    public SampleList(int sampleSize) {
        this.sampleSize = sampleSize;
        this.update = false;
    }
    public SampleList(int sampleSize, boolean update) {
        this.sampleSize = sampleSize;
        this.update = update;
    }

    @Override
    public boolean add(T t) {
        if(isCollected()) {
            if(this.update) {
                super.removeFirst();
            } else {
                super.clear();
            }
        }
        return super.add(t);
    }

    public int getMaxSize() {
        return sampleSize;
    }


    public boolean isCollected() {
        return super.size() >= this.sampleSize;
    }

    public double getAverageDouble(SampleList<Double> list) {
        double n = 0;
        int n2 = 0;

        for(double l : list) {
            n += l;
            n2++;
        }
        if(n != 0 && n2 != 0) {
            return n / n2;
        }
        return 0;
    }
    public int getAverageInt(SampleList<Integer> list) {
        int n = 0;
        int n2 = 0;

        for(int l : list) {
            n += l;
            n2++;
        }
        if(n != 0 && n2 != 0) {
            return n / n2;
        }
        return 0;
    }
    public float getAverageFloat(SampleList<Float> list) {
        float n = 0f;
        int n2 = 0;

        for(float l : list) {
            n += l;
            n2++;
        }
        if(n != 0 && n2 != 0) {
            return n / n2;
        }
        return 0f;
    }

    public double getStandardDeviation(final Collection<? extends Number> data) {
        final double variance = getVariance(data);
        return Math.sqrt(variance);
    }

    public double getVariance(final Collection<? extends Number> data) {
        int count = 0;
        double sum = 0.0;
        double variance = 0.0;
        double average;
        for (final Number number : data) {
            sum += number.doubleValue();
            ++count;
        }
        average = sum / count;
        for (final Number number : data) {
            variance += Math.pow(number.doubleValue() - average, 2.0);
        }
        return variance;
    }

    public long getAverageLong(SampleList<Long> list) {
        long n = 0L;
        int n2 = 0;

        for(long l : list) {
            n += l;
            n2++;
        }
        if(n != 0 && n2 != 0) {
            return n / n2;
        }
        return 0L;
    }

    public int getMode(Collection<? extends Number> list) {
        int mode = (int) list.toArray()[0];
        int maxCount = 0;
        for (Number value : list) {
            int count = 1;
            for (Number value2 : list) {
                if (value2.equals(value))
                    count++;
                if (count > maxCount) {
                    mode = (int) value;
                    maxCount = count;
                }
            }
        }
        return mode;
    }

}
