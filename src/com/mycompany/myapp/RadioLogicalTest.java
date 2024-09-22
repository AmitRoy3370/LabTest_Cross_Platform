package com.mycompany.myapp;

public class RadioLogicalTest extends LabTest {

    String plateDimention;

    public RadioLogicalTest(String title, double cost, boolean isAvailable, String plateDimention) {
        super(title, cost, isAvailable);

        this.plateDimention = plateDimention;

    }

    public String getPlateDimention() {
        return plateDimention;
    }

    public void setPlateDimention(String plateDimention) {
        this.plateDimention = plateDimention;
    }

    @Override
    public String toString() {
        return "RadioLogicalTest{" + "plateDimention=" + plateDimention + '}';
    }

}
