package com.mycompany.myapp;

public abstract class LabTest {

    protected String title;
    protected double cost;
    protected boolean isAvailable;

    public LabTest(String title, double cost, boolean isAvailable) {
        this.title = title;
        this.cost = cost;
        this.isAvailable = isAvailable;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public boolean isIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    @Override
    public String toString() {
        return "LabTest{" + "title=" + title + ", cost=" + cost + ", isAvailable=" + isAvailable + '}';
    }

}
