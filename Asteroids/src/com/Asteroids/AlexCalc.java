package com.Asteroids;

public class AlexCalc {
//    Notes:
//    area of sector of circle = 1/2 * theta * r^2
//    so to approximate the polar integral

//    you just make a really high n of sectors and add them up

    public double approxThreshhold = 1000;

    public double testFunc(double theta) {
//        r = 2 + 4 * cos(theta)
        return 2 + 4 * Math.cos(theta);

    }

    public double sectorArea(double r, double theta) {
        return 0.5 * theta * Math.pow(r,2);
    }


    public double calcIntegral(double lowerBound, double upperBound) {
        double range = upperBound-lowerBound;
        double sum = 0;
        for(int n=0; n<approxThreshhold;n += (range/approxThreshhold) ){
            sum += sectorArea(testFunc(n), n);
        }

        System.out.println("Finished Lopop");
        return sum;

    }
}