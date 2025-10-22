package com.example.gymtrackerweb.dto;

import java.util.Objects;

public class EjercicioMiniKpis {
    double bestE1rm;
    double bestSetKg;
    int bestSetReps;
    double vol4w;
    Double deltaE1rm;

    public EjercicioMiniKpis(double bestE1rm, double bestSetKg, int bestSetReps,double vol4w,Double deltaE1rm ) {
        this.bestE1rm = bestE1rm;
        this.bestSetKg = bestSetKg;
        this.bestSetReps = bestSetReps;
        this.vol4w = vol4w;
        this.deltaE1rm = deltaE1rm;
    }

    public double getBestE1rm() {
        return bestE1rm;
    }
    public void setBestE1rm(double bestE1rm) {
        this.bestE1rm = bestE1rm;
    }
    public double getBestSetKg() {
        return bestSetKg;
    }
    public void setBestSetKg(double bestSetKg) {
        this.bestSetKg = bestSetKg;
    }
    public int getBestSetReps() {
        return bestSetReps;
    }
    public void setBestSetReps(int bestSetReps) {
        this.bestSetReps = bestSetReps;
    }
    public double getVol4w() {
        return vol4w;
    }
    public void setVol4w(double vol4w) {
        this.vol4w = vol4w;
    }
    public Double getDeltaE1rm() {
        return deltaE1rm;
    }
    public void setDeltaE1rm(Double deltaE1rm) {
        this.deltaE1rm = deltaE1rm;
    }
}
