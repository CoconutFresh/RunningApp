package com.example.runningapp;

import java.util.List;

public class RunSession {

    private long totalTime = -1;
    private float totalDist = -1;
    private int avgPaceMin = -1;
    private int avgPaceSec = -1;
    private String avgPace;
    List<CharSequence> segments;

    public RunSession(String type, long time, float dist, int min, int sec) {
        setTime(time);
        setDist(dist);
        setPace(min, sec);
    }

    public void setTime(long inputTime) {
        totalTime = inputTime / -1000;
    }

    public void setDist(float inputDist) {
        totalDist = inputDist;
    }

    public void setPace(int min, int sec) {
        avgPaceMin = min;
        avgPaceSec = sec;

        avgPace = avgPaceMin + ":" + avgPaceSec;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public float getTotalDist() {
        return totalDist;
    }

    public String getAvgPace() {
        return avgPace;
    }
}
