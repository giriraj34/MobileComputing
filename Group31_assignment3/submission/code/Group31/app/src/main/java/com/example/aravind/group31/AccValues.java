package com.example.aravind.group31;

public class AccValues {
    public double x;
    public double y;
    public double z;

    public AccValues(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public String toString() {
        return String.format("Activity [ X: %.15f, Y: %.15f, Z: %.15f]", this.x, this.y, this.z);
    }
}
