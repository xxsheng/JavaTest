package lottery.web.helper.speed;

import java.io.Serializable;

/**
 * Created by Nick on 2017-09-16.
 */
public class AccessTime implements Serializable{
    private static final long serialVersionUID = 5242656746382103980L;

    private long[] accessTimes; // 访问时间戳记录，按先后顺序存储
    private int inMilliSeconds; // 多少秒内
    private int times = 10; // 多少次

    public AccessTime(int inMilliSeconds, int times) {
        this.inMilliSeconds = inMilliSeconds;
        this.times = times;
        init();
    }

    public void init() {
        accessTimes = new long[times];
    }

    public int getInMilliSeconds() {
        return inMilliSeconds;
    }

    public void setInMilliSeconds(int inMilliSeconds) {
        this.inMilliSeconds = inMilliSeconds;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public long getLast() {
        return this.accessTimes[times - 1];
    }

    public long getFirst() {
        return this.accessTimes[0];
    }

    public long getElement(int i) {
        return accessTimes[i];
    }

    public void insert(long nextTime) {
        if (this.getLast() != 0) {
            for (int i = 0; i < this.times - 1; i++) {
                accessTimes[i] = accessTimes[i + 1];
            }
            this.accessTimes[times - 1] = nextTime;
        } else {
            int j = 0;
            while (accessTimes[j] != 0) {
                j++;
            }
            accessTimes[j] = nextTime;
        }
    }
}
