package com.Asteroids;

import com.sun.tools.javac.util.ArrayUtils;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.applet.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import javax.swing.*;

public class AlexCalc {
//    Notes:
//    area of sector of circle = 1/2 * theta * r^2
//    so to approximate the polar integral

//    you just make a really high n of sectors and add them up

    public double approxThreshhold = 10000;
    public double visualizeThreshhold = 10000000;
    public double[] xDPoints = new double[(int)visualizeThreshhold];
    public double[] yDPoints = new double[(int)visualizeThreshhold];

    public double testFuncPol(double theta) {
//        r = 2 + 4 * cos(theta)
        // Code the function you want to intergrate here and run the program!
        return Math.tan(theta);

    }

    public double testFuncCart(double x) {
//        r = 2 + 4 * cos(theta)
        // Code the function you want to intergrate here and run the program!
        return Math.sqrt( 2*(5-1*Math.cos(2.3*x + 2) - Math.pow(x,2)/4 ) );

    }


    // We're going to have fixed operations with random variables

    // So this is the parser of the function we'll use
    public double funcPolParser(ArrayList parameters, double theta) {
        // This is not concise code but I use it to make the equation more readable to the mathmatician
        double a = (double)(parameters.get(0));
        double b = (double)(parameters.get(1));
        double c = (double)(parameters.get(2));
        double d = (double)(parameters.get(3));
        double f = (double)(parameters.get(4));
        return (1.0/a) * Math.sin(b*theta + c) + d + f*Math.pow((theta-Math.PI), 2);

    }

    public double funcCartParser(ArrayList parameters, double x) {
        // This is not concise code but I use it to make the equation more readable to the mathmatician
        double a = (double)(parameters.get(0));
        double b = (double)(parameters.get(1));
        double c = (double)(parameters.get(2));
        double d = (double)(parameters.get(3));
        double f = (double)(parameters.get(4));
        return Math.sqrt( a*(b-c*Math.cos(d*x) - Math.pow(x,2)/f ) );

    }

    public double funcCartParserXBar(ArrayList parameters, double x) {
        // This is not concise code but I use it to make the equation more readable to the mathmatician
        double a = (double)(parameters.get(0));
        double b = (double)(parameters.get(1));
        double c = (double)(parameters.get(2));
        double d = (double)(parameters.get(3));
        double f = (double)(parameters.get(4));
        return x * Math.sqrt( a*(b-c*Math.cos(d*x) - Math.pow(x,2)/f ) );

    }

    public double funcCartParserYBar(ArrayList parameters, double x) {
        // This is not concise code but I use it to make the equation more readable to the mathmatician
        double a = (double)(parameters.get(0));
        double b = (double)(parameters.get(1));
        double c = (double)(parameters.get(2));
        double d = (double)(parameters.get(3));
        double f = (double)(parameters.get(4));
        return 1/2*Math.pow( Math.sqrt( a*(b-c*Math.cos(d*x) - Math.pow(x,2)/f )), 2 );

    }


    public Polygon funcPolVisualizer(ArrayList parameters, double lowerBound, double upperBound) {
        int[] xPoints = new int[(int)visualizeThreshhold];
        int[] yPoints = new int[(int)visualizeThreshhold];

        double range = upperBound-lowerBound;
        double jumpSize = range/visualizeThreshhold;
        int count = 0;
        for (double theta=lowerBound; theta<upperBound-jumpSize; theta+=jumpSize) {
            double r = funcPolParser(parameters, theta);
            double[] cartCoords = cartesianConvert(theta, r);
            xPoints[count] = (int)Math.round(cartCoords[0]*10)+350;
            yPoints[count] = (int)Math.round(cartCoords[1]*10)+350;

            xDPoints[count] = (cartCoords[0]*10)+350;
            yDPoints[count] = (cartCoords[1]*10)+350;

            count++;
        }

        Polygon visualized = new Polygon(xPoints, yPoints, (int) visualizeThreshhold);
        ArrayList<Double> bodies = new ArrayList<Double>();
        return visualized;

    }

    public ArrayList funcCartVisualizer(ArrayList parameters, double lowerBound, double upperBound, boolean inGame) {
        int[] xPoints = new int[(int)visualizeThreshhold];
        int[] yPoints = new int[(int)visualizeThreshhold];

        double range = upperBound-lowerBound;
        double jumpSize = range/(visualizeThreshhold/2);
        int count = 0;
        for (double x=lowerBound; x<upperBound-jumpSize; x+=jumpSize) {
            double y = funcCartParser(parameters, x);
            xPoints[count] = (int)Math.round(x*10);
            yPoints[count] = (int)Math.round(y*10);

            xDPoints[count] = (x*10);
            yDPoints[count] = (y*10);

            count++;
        }
        for (double x=upperBound; x>(lowerBound+jumpSize); x-=jumpSize) {
            double y = funcCartParser(parameters, x);
            xPoints[count] = (int)Math.round(x*10);
            yPoints[count] = (int)Math.round(y*-10);

            xDPoints[count] = (x*10);
            yDPoints[count] = (y*-10);

            count++;
        }



        for (int x = 0; x<visualizeThreshhold; x++) {
            if (xPoints[x] == 0 && x!=0) {
                xPoints[x] = xPoints[x-1];
            } else if (xPoints[x] == 0) {
                xPoints[x] = xPoints[x+1];
            }
        }
        for (int y = 0; y<visualizeThreshhold; y++) {
            if (yPoints[y] == 0 && y!=0) {
                yPoints[y] = yPoints[y-1];
            } else if (xPoints[y] == 0) {
                xPoints[y] = xPoints[y+1];
            }
        }
        if (inGame) {
            ArrayList<Object> points = new ArrayList<Object>();
            points.add(xDPoints);
            points.add(yDPoints);
            return points;

        } else {
            ArrayList<Object> poly = new ArrayList<Object>();
            Polygon visualized = new Polygon(xPoints, yPoints, (int) visualizeThreshhold);
            ArrayList<Double> bodies = new ArrayList<Double>();
            poly.add(visualized);
            return poly;
        }


    }

    public double[] cartesianConvert(double theta, double r) {
        double[] cartCoords = new double[2];
        cartCoords[0] = Math.cos(theta) * r;
        cartCoords[1] = Math.sin(theta) * r;
        return cartCoords;

    }

    public ArrayList generatePolFunc() {
        ArrayList<Double> parameters = new ArrayList<Double>();
        Random rand = new Random();
        parameters.add(new Double(rand.nextInt(5)+1));
        parameters.add(new Double(rand.nextInt(10)+5));
        parameters.add(new Double(rand.nextInt(10)));
        parameters.add(new Double(rand.nextInt(10)+5));
        parameters.add(Math.random()+0.01);
        return parameters;

    }

    public ArrayList generateCartFunc() {
        ArrayList<Double> parameters = new ArrayList<Double>();
        Random rand = new Random();
        parameters.add(new Double(rand.nextInt(9)+2));
        parameters.add(new Double(rand.nextInt(5)+1));
        parameters.add(new Double(rand.nextInt(9))/10);
        parameters.add(new Double(rand.nextInt(9)-4));
        parameters.add(new Double(rand.nextInt(2)+4));
        return parameters;

    }

    public double[] numIntegratedCOM() {
        double[] centerOfMass = new double[2];
        for (int i=0;i<visualizeThreshhold;i++) {
            centerOfMass[0] += xDPoints[i];
            centerOfMass[1] += yDPoints[i];
        }
        centerOfMass[0] = centerOfMass[0] / visualizeThreshhold;
        centerOfMass[1] = centerOfMass[1] / visualizeThreshhold;
        return centerOfMass;

    }



    public double calcPolIntegral(ArrayList parameters, double lowerBound, double upperBound, int mode) {
        double range = upperBound-lowerBound;
        double sum = 0;

        System.out.println("This is upper: " + upperBound + " this is lower: " + lowerBound);

        double theta = (range/approxThreshhold);

        for(double n=lowerBound; n<(upperBound-theta); n += theta ){
            // System.out.println()
            if (mode == 0) {
                sum += sectorArea(funcPolParser(parameters, n), theta);
            }
            if (mode == 1) {
                sum += xCenterTop(funcPolParser(parameters, n), theta);
            }
            if (mode == 2) {
                sum += yCenterTop(funcPolParser(parameters, n), theta);
            }

        }

        return sum;

    }

    public double riemannSumIntegral(ArrayList parameters, double lowerBound, double upperBound) {
        double n = approxThreshhold;
        double range = upperBound-lowerBound;
        // So the rectangles have a width of:
        double rectWidth = range/n;
        double sum = 0;

        for (double i=lowerBound;i<upperBound-rectWidth;i+=rectWidth) {
            sum += rectWidth*funcCartParser(parameters, i);
        }

        return sum;
    }


    public double sectorArea(double r, double theta) {
        return 0.5 * theta * Math.pow(r,2);
    }

    public double xCenterTop(double r, double theta) {
        return theta * 0.5 * Math.pow(r,2) * theta;
    }

    public double yCenterTop(double r, double theta) {
        return 0.125 * Math.pow(r,4) * theta;
    }

    public ArrayList<Double> findBounds(ArrayList parameters, double lowerGuess, double upperGuess) {
        ArrayList<Double> xInts = new ArrayList<Double>();

        boolean noneFoundYet = true;
        for (double i=lowerGuess;i<upperGuess;i+=0.001) {
            if (Double.isFinite(funcCartParser(parameters, i))) {
                if (noneFoundYet) {
                    xInts.add(((double) Math.round(i*1000))/1000);
                } else if (Double.isFinite(funcCartParser(parameters, i+0.001)) == false) {
                    xInts.add(((double) Math.round(i*1000))/1000);
                }
                noneFoundYet = false;
            }
        }
        return xInts;
    }

    // This seems like a kinda failed attempt
    public double[] calcCOMPol(ArrayList parameters) {
        double area = calcPolIntegral(parameters, 0, 2*Math.PI, 0);
        System.out.println("area: " + area);
        double xBar = calcPolIntegral(parameters,0, 2*Math.PI, 1)/area;
        double yBar = calcPolIntegral(parameters,0, 2*Math.PI, 2)/area;
        System.out.println("Center of Mass: " + xBar + " " + yBar);
        double[] center = {xBar*10+350,yBar*10+350};
        return center;
    }


    public ArrayList<Double> calcCOMCart(ArrayList parameters, double lowerBound, double upperBound) {
        double area = riemannSumIntegral(parameters, lowerBound, upperBound);

        // xBar top:
        double n = approxThreshhold;
        double range = upperBound-lowerBound;
        // So the rectangles have a width of:
        double rectWidth = range/n;
        double sum = 0;

        for (double i=lowerBound;i<upperBound-rectWidth;i+=rectWidth) {
            sum += rectWidth*funcCartParserXBar(parameters, i);
        }

        double xCenter = sum/area;

        sum = 0;

        for (double i=lowerBound;i<upperBound-rectWidth;i+=rectWidth) {
            sum += rectWidth*funcCartParserYBar(parameters, i);
        }

        double yCenter = sum/area;

        System.out.println("Center of Mass: " + xCenter + " " + yCenter);
        ArrayList<Double> center = new ArrayList<Double>();
        center.add(xCenter*10);
        center.add(yCenter*10);
        return center;
    }

    public ArrayList generateAsteroid() {
        ArrayList<Object> body_center = new ArrayList<Object>();


        ArrayList func = generateCartFunc();

        ArrayList<Double> xInts = findBounds(func,-20.0,20.0);

        ArrayList<Double> vis = funcCartVisualizer(func, ((double) xInts.get(0)), ((double) xInts.get(1)), true );
        body_center.add(   DoubleStream.of(xDPoints).boxed().collect(Collectors.toCollection(ArrayList::new))  );
        body_center.add(   DoubleStream.of(yDPoints).boxed().collect(Collectors.toCollection(ArrayList::new))   );
        body_center.add( calcCOMCart(func, ((double) xInts.get(0)), ((double) xInts.get(1))) );

        return body_center;
    }
}