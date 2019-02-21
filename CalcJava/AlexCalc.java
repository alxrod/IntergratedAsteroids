public class AlexCalc {
//    Notes:
//    area of sector of circle = 1/2 * theta * r^2
//    so to approximate the polar integral

//    you just make a really high n of sectors and add them up

    public double approxThreshhold = 10000;

    public double testFunc(double theta) {
//        r = 2 + 4 * cos(theta)
        // Code the function you want to intergrate here and run the program!
        return 4 - 2*Math.tan(theta);

    }

    public double sectorArea(double r, double theta) {
        return 0.5 * theta * Math.pow(r,2);
    }


    public double calcIntegral(double lowerBound, double upperBound) {
        double range = upperBound-lowerBound;
        double sum = 0;
        
        System.out.println("This is upper: " + upperBound + " this is lower: " + lowerBound);

        double theta = (range/approxThreshhold);

        for(double n=lowerBound; n<(upperBound-theta); n += theta ){
            // System.out.println()
            sum += sectorArea(testFunc(n), theta);
            
        }

        return sum;

    }
}