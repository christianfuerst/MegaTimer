package de.yourdot.megatimer.model;

import com.orm.SugarRecord;

public class MegaTimer extends SugarRecord {

    private String title;
    private String status;
    private long length;
    private int color;
    private long start;
    private long stop;

    public MegaTimer() {
    }

    public MegaTimer(String title, String status, long length, int color, long start, long stop) {
        this.title = title;
        this.status = status;
        this.length = length;
        this.color = color;
        this.start = start;
        this.stop = stop;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getStatus() {
        return status;
    }

    public void setLength(long length) {
        this.length = length;
    }
    public long getLength() {
        return length;
    }

    public void setColor(int color) { this.color = color; }
    public int getColor() { return color; }

    public void setStart(long start) {
        this.start = start;
    }
    public long getStart() {
        return start;
    }

    public void setStop(long stop) {
        this.stop = stop;
    }
    public long getStop() {
        return stop;
    }

    public long getProgress() {
        return length - (stop - start);
    }

    public long getElapsedTime() {
        return (stop - start) / 1000;
    }
}