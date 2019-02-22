import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Random;
import java.applet.*;
import javax.swing.*;

public class AlexCalc {
//    Notes:
//    area of sector of circle = 1/2 * theta * r^2
//    so to approximate the polar integral

//    you just make a really high n of sectors and add them up
    public double approxThreshhold = 10000;
    public double visualizeThreshhold = 10000000;

    public double testFunc(double theta) {
//        r = 2 + 4 * cos(theta)
        // Code the function you want to intergrate here and run the program!
        return Math.tan(theta);

    }

    public double sectorArea(double r, double theta) {
        return 0.5 * theta * Math.pow(r,2);
    }  

    // We're going to have fixed operations with random variables

    // So this is the parser of the function we'll use
    public double funcParser(ArrayList parameters, double theta) {
        // This is not concise code but I use it to make the equation more readable to the mathmatician
        double a = (double)(parameters.get(0));
        double b = (double)(parameters.get(1));
        double c = (double)(parameters.get(2));
        double d = (double)(parameters.get(3));
        double f = (double)(parameters.get(4));
        return (1.0/a) * Math.sin(b*theta + c) + d + f*Math.pow((theta-Math.PI), 2);

    }

    public Polygon funcVisualizer(ArrayList parameters, double lowerBound, double upperBound) {
        int[] xPoints = new int[(int)visualizeThreshhold];
        int[] yPoints = new int[(int)visualizeThreshhold];

        double range = upperBound-lowerBound;
        double jumpSize = range/visualizeThreshhold;
        int count = 0;
        for (double theta=lowerBound; theta<upperBound-jumpSize; theta+=jumpSize) {
            double r = funcParser(parameters, theta);
            double[] cartCoords = cartesianConvert(theta, r);
            xPoints[count] = (int)Math.round(cartCoords[0]*10)+350;
            yPoints[count] = (int)Math.round(cartCoords[1]*10)+350;
            count++;
        }

        Polygon visualized = new Polygon(xPoints, yPoints, (int) visualizeThreshhold);
        ArrayList<Double> bodies = new ArrayList<Double>();
        return visualized;




    }

    public double[] cartesianConvert(double theta, double r) {
        double[] cartCoords = new double[2];
        cartCoords[0] = Math.cos(theta) * r;
        cartCoords[1] = Math.sin(theta) * r;
        return cartCoords;

    }

    public ArrayList generateFunc() {
        ArrayList<Double> parameters = new ArrayList<Double>();
        Random rand = new Random();
        parameters.add(new Double(rand.nextInt(5)+1));
        parameters.add(new Double(rand.nextInt(21)-10));
        parameters.add(new Double(Math.PI).doubleValue());
        parameters.add(new Double(rand.nextInt(10)+5));
        parameters.add(Math.random()+0.01);
        return parameters;


    }


    public double calcIntegral(ArrayList parameters, double lowerBound, double upperBound) {
        double range = upperBound-lowerBound;
        double sum = 0;
        
        System.out.println("This is upper: " + upperBound + " this is lower: " + lowerBound);

        double theta = (range/approxThreshhold);

        for(double n=lowerBound; n<(upperBound-theta); n += theta ){
            // System.out.println()
            sum += sectorArea(funcParser(parameters, n), theta);

        }

        return sum;

    }
}