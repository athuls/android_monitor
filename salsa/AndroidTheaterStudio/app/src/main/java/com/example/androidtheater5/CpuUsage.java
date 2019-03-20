package com.example.androidtheater5;

public class CpuUsage {
    public double totalCpuTime;
    public double totalLifeTime;

    public double getCpuUsage() {
        return totalCpuTime/totalLifeTime;
    }
}