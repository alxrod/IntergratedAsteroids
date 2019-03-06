import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Random;
import java.applet.*;
import javax.swing.*;
import java.awt.geom.Area;
import java.awt.geom.Point2D;

public class AlexCalc {
//    Notes:
//    area of sector of circle = 1/2 * theta * r^2
//    so to approximate the polar integral

//    you just make a really high n of sectors and add them up


    // This is the variable for the approximations in the intergrals. this is n in rienman sums or how many rectangles were going to have.
    public double approxThreshhold = 10000;
    // This is the variable for the number of veritcies used in the approximation polygon.
    public double visualizeThreshhold = 10000000;
    // This are the double(0.0 vs int which is 0) points that represent the vertices of the approx. shape.
    public double[] xDPoints = new double[(int)visualizeThreshhold];
    public double[] yDPoints = new double[(int)visualizeThreshhold];

    // These two functions are just the test functions I used to make sure my integration functions were actually working.
    // The Pol version is a polar function while the Cart one is a cartesian one. this is nomenclature I use in all my method names in this document.
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


    // So my strategy was to have a fixed function which we would then randomize the coefficients off to generate different lookign asteroids.
    // The way that I pass "uniquely generated functions" is throuhg a standardized list called parameters
    // Parameters holds 5 different coefficients. In both the polar funcPolParser and cartesian funcCartParser, the methods take in a value to evaluate and the unique 
    // set of coefficients and plugs both through the equation.

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

    // This is how we calculated the numerator function for calculating the x center of mass equation. As you can see it is just x * f(x) which we will then take the integral of.
    public double funcCartParserXBar(ArrayList parameters, double x) {
        // This is not concise code but I use it to make the equation more readable to the mathmatician
        double a = (double)(parameters.get(0));
        double b = (double)(parameters.get(1));
        double c = (double)(parameters.get(2));
        double d = (double)(parameters.get(3));
        double f = (double)(parameters.get(4));
        return x * Math.sqrt( a*(b-c*Math.cos(d*x) - Math.pow(x,2)/f ) );

    }
    // The same applies here where we calculated the numerator function for the y center of mass equation. This time it is just 1/2 * f(x)^2.
    public double funcCartParserYBar(ArrayList parameters, double x) {
        // This is not concise code but I use it to make the equation more readable to the mathmatician
        double a = (double)(parameters.get(0));
        double b = (double)(parameters.get(1));
        double c = (double)(parameters.get(2));
        double d = (double)(parameters.get(3));
        double f = (double)(parameters.get(4));
        return 1/2*Math.pow( Math.sqrt( a*(b-c*Math.cos(d*x) - Math.pow(x,2)/f )), 2 );

    }


    // These two methods are by far the most technically and generally just not super mathy.
    // What I'm doing is iterating through from the lowerbound to the upper bound, adding by the total range/ the number of vertices I want and evaluating the function at that point
    // the outcome is 10000000 points evaluated through the function i've set with the "parameters" list. I can then feed those points into a java class called Polygon which will visualize them for me, playing connect the dots
    // Also I scale all of the poitns by 15 then add 400 just to make the asteroid bigger and center it in the screen.
    public Polygon funcPolVisualizer(ArrayList parameters, double lowerBound, double upperBound) {
        int[] xPoints = new int[(int)visualizeThreshhold];
        int[] yPoints = new int[(int)visualizeThreshhold];

        double range = upperBound-lowerBound;
        double jumpSize = range/visualizeThreshhold;
        int count = 0;
        for (double theta=lowerBound; theta<upperBound-jumpSize; theta+=jumpSize) {
            double r = funcPolParser(parameters, theta);
            // Notice that I have to convert the polar coordinates with this version of the method into cartesian coordinates cause thats what java works in.
            // See my function below for how I'm doing that.
            double[] cartCoords = cartesianConvert(theta, r);

            // I store the points in these lists which are integers(meaning they round the precise decimals) so that they are in the form the Polygon Java class likes
            xPoints[count] = (int)Math.round(cartCoords[0]*10)+350;
            yPoints[count] = (int)Math.round(cartCoords[1]*10)+350;

            // But I keep these points as doubles so that I can apply the fancy rotations to them later with all my trig!
            xDPoints[count] = (cartCoords[0]*10)+350;
            yDPoints[count] = (cartCoords[1]*10)+350;

            count++;
        }


        // Here is where I actually create the polygon object.
        Polygon visualized = new Polygon(xPoints, yPoints, (int) visualizeThreshhold);
        ArrayList<Double> bodies = new ArrayList<Double>();
        return visualized;

    }

    // THis is just a simpiler version of the polar method above where I don't have to worry about converting polar points to cartesian because they are already polar.
    public Polygon funcCartVisualizer(ArrayList parameters, double lowerBound, double upperBound) {
        int[] xPoints = new int[(int)visualizeThreshhold];
        int[] yPoints = new int[(int)visualizeThreshhold];

        double range = upperBound-lowerBound;
        double jumpSize = range/(visualizeThreshhold/2);
        int count = 0;
        for (double x=lowerBound; x<upperBound-jumpSize; x+=jumpSize) {
            double y = funcCartParser(parameters, x);
            xPoints[count] = (int)Math.round(x*15)+400;
            yPoints[count] = (int)Math.round(y*15)+400;

            xDPoints[count] = (x*15)+400;
            yDPoints[count] = (y*15)+400;

            count++;
        } 
        for (double x=upperBound; x>(lowerBound+jumpSize); x-=jumpSize) {
            double y = funcCartParser(parameters, x);
            xPoints[count] = (int)Math.round(x*15)+400;
            yPoints[count] = (int)Math.round(y*-15)+400;

            xDPoints[count] = (x*15)+400;
            yDPoints[count] = (y*-15)+400;

            count++;
        } 
        

        // I had a couple values where if I a y value that was undefined, then the Polygon class would glitch and make random points at 0,0
        // The quick programmy hacky fix that wasn't related to math was to just replace these points with the same point as before it and it would all look the same!
        for (int x = 0; x<visualizeThreshhold; x++) {
            if (xPoints[x] == 0) {
                xPoints[x] = xPoints[x-1];
            }
        }
        for (int y = 0; y<visualizeThreshhold; y++) {
            if (yPoints[y] == 0) {
                yPoints[y] = yPoints[y-1];
            }
        }


         for (int x = 0; x<visualizeThreshhold; x++) {
            if (xDPoints[x] == 0) {
                xDPoints[x] = xDPoints[x-1];
            }
        }
        for (int y = 0; y<visualizeThreshhold; y++) {
            if (yDPoints[y] == 0) {
                yDPoints[y] = yDPoints[y-1];
            }
        }

        Polygon visualized = new Polygon(xPoints, yPoints, (int) visualizeThreshhold);
        ArrayList<Double> bodies = new ArrayList<Double>();
        return visualized;

    } 

    // I'm just using basic trig here to convert the angle and magnitude into a height and length.
    public double[] cartesianConvert(double theta, double r) {
        double[] cartCoords = new double[2];
        cartCoords[0] = Math.cos(theta) * r;
        cartCoords[1] = Math.sin(theta) * r;
        return cartCoords;

    }

    // As I explained before, I use a list of 5 randomly generated coeefficients. These two functions below generate the random coefficients we need for unique asteroids.
    // You can see we have used our desmos links to determine what ranges for each coefficient produces the prettiest asteroids.
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

    // This is our numerical intergation method for calculating center of mass
    // In 2d shapes with equally distributed mass across area, the centroid is the center of mass
    // So I take a guess at the centroid/center of mass by just adding up all my approximate polygon's vertices and averaging them.
    // This was so that I could check against the the integral functions I was writing to make sure they were right.
    // I knew this would work.
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


    // So this was my integral function for polar functions. It uses the sectorArea function I wrote which just takes an angle and gives the sector area as a function of the formula we found online. 
    // It finds the range and then chooses an angle(this is not the position of the sector but rather kinda an equivalent to the width in the rienman sums) for each of the sectors as the range / the number of sectors I want. 
    // I then increase by that angle and calculate the area at each point. I add them all up and I have a sum of all the little sectors: a numerically intergrated polar integral.
    public double calcPolIntegral(ArrayList parameters, double lowerBound, double upperBound, int mode) {
        double range = upperBound-lowerBound;
        double sum = 0;
        
        System.out.println("This is upper: " + upperBound + " this is lower: " + lowerBound);

        double theta = (range/approxThreshhold);

        for(double n=lowerBound; n<(upperBound-theta); n += theta ){
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

    // This is just our rienman sum strategy we've talked about so much. specifically you can see im doing left hand sum because thats easier witht he for loops in java 
    // And doing it this many times is good enough no matter how you do it. 
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

    // This just returns the evalutated formula for a sector with a given theta(kinda equivalent to a rectangles width)
    public double sectorArea(double r, double theta) {
        return 0.5 * theta * Math.pow(r,2);
    }  

    // these are failed attempts to do the polar center of mass, these represent the numerator functions in the formula.
    public double xCenterTop(double r, double theta) {
        return theta * 0.5 * Math.pow(r,2) * theta;
    }
    public double yCenterTop(double r, double theta) {
        return 0.125 * Math.pow(r,4) * theta;
    }

    // So we know that these functions are going to be undefined until they reach the asteroid and undefined afterwards right?
    // So a super easy way to find the x intercepts is to take a guess at them then iterate through them and take
    // the first and last values that are defined. These will approximately be our x intercepts.
    // I add them to a list to give back.
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

    // This is my utterly failed attempt at polar center of mass calculation using the flawed equations we derived. 
    public double[] calcCOMPol(ArrayList parameters) {
        double area = calcPolIntegral(parameters, 0, 2*Math.PI, 0);
        System.out.println("area: " + area);
        double xBar = calcPolIntegral(parameters,0, 2*Math.PI, 1)/area;
        double yBar = calcPolIntegral(parameters,0, 2*Math.PI, 2)/area;
        System.out.println("Center of Mass: " + xBar + " " + yBar);
        double[] center = {xBar*15+400,yBar*15+400};
        return center;
    }

    // This is the money equation. What I do is I use the regular integral function which gives me the area of the asteroid
    // Then I repeat the code but evaluating with the xBar and yBar methods I wrote above to find there integrals. then I easily divde xBar by area and yBar by area and that gives me the center of mass!!! Finally!
    // I return them in a quick little list.
    // Also I scale all of the poitns by 15 then add 400 just to make the asteroid bigger and center it in the screen.
    public double[] calcCOMCart(ArrayList parameters, double lowerBound, double upperBound) {
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
        double[] center = {xCenter*15+400,yCenter*15+400};
        return center;
    }

    // This is just a stupid method that I made to just run all the code sequentially to make a polygon looking like an asteroid so I could try to implement it into the game.
    // That was before I realized it would be too lagy. 
    public ArrayList generateAsteroid() {
        ArrayList<Object> body_center = new ArrayList<Object>();


        ArrayList func = generateCartFunc();

        ArrayList<Double> xInts = findBounds(func,-20.0,20.0);
       
        body_center.add( funcCartVisualizer(func, ((double) xInts.get(0)), ((double) xInts.get(1)) ) );
        body_center.add( calcCOMCart(func, ((double) xInts.get(0)), ((double) xInts.get(1))) );

        return body_center;
    }

    // These methods I snagged from my original game:

    // THis is a litle trig method that takes a single point and moves it a fixed angle around a center point.
    public Point2D.Double rotatePoint(Point2D.Double p, double angle, Point2D.Double center) {
        double radAngle = angle / 180 * Math.PI;

        double x1 = p.x - center.x;
        double y1 = p.y - center.y;

        double tempX1 = x1 * Math.cos(radAngle) - y1 * Math.sin(radAngle);
        double tempY1 = x1 * Math.sin(radAngle) + y1 * Math.cos(radAngle);

        Point2D.Double finalPoint = new Point2D.Double(tempX1 + center.x, tempY1 + center.y);

        return finalPoint;


    }


    // This rotation just applies the point rotation to every vertex in the approximate polygon.
    public Polygon rotateBody(double angle, Point2D.Double center) {
        // Point2D.Double center = calculateCenter();
        for (int p = 0; p < visualizeThreshhold; p++) {
            Point2D.Double prePoint = new Point2D.Double(xDPoints[p], yDPoints[p]);
            Point2D.Double rotatedPoint = this.rotatePoint(prePoint, angle, center);
            this.xDPoints[p] = rotatedPoint.getX();
            this.yDPoints[p] = rotatedPoint.getY();
        }

        int[] xPoints = new int[(int)visualizeThreshhold];
        int[] yPoints = new int[(int)visualizeThreshhold];

        // This just cleans out any bugs with undefined y values like I did above!
        for (int xp = 0; xp < xPoints.length; xp++) {
            Integer integerTransX = (int) this.xDPoints[xp];
            xPoints[xp] = integerTransX.intValue();
        }
        for (int yp = 0; yp < yPoints.length; yp++) {
            Integer integerTransY = (int) this.yDPoints[yp];
            yPoints[yp] = integerTransY.intValue();
        }

        for (int x = 0; x<visualizeThreshhold; x++) {
            if (xPoints[x] == 0) {
                xPoints[x] = xPoints[x-1];
            }
        }
        for (int y = 0; y<visualizeThreshhold; y++) {
            if (yPoints[y] == 0) {
                yPoints[y] = yPoints[y-1];
            }
        }

        return new Polygon(xPoints, yPoints, (int) visualizeThreshhold);
    }
}

// And there we go!