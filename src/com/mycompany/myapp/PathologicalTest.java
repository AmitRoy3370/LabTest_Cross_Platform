package com.mycompany.myapp;

public class PathologicalTest extends LabTest {

    String reagent;

    public PathologicalTest(String title, double cost, boolean isAvailable, String reagent) {
        super(title, cost, isAvailable);

        this.reagent = reagent;

    }

    public String getReagent() {
        return reagent;
    }

    public void setReagent(String reagent) {
        this.reagent = reagent;
    }

    @Override
    public String toString() {
        return super.toString() + "\nPathologicalTest{" + "reagent=" + reagent + '}';
    }

}
