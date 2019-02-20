package com.Asteroids;

import javafx.scene.transform.Affine;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.util.ArrayList;


public class Entity {
    public Polygon body;
    public double xPos;
    public double yPos;
    public double xVel;
    public double yVel;
    public int physicsCategory;
    public boolean isAlive;
    public ArrayList<Double> dXPoints;
    public ArrayList<Double> dYPoints;
    public double rotationVel;

    public Entity(double xPos, double yPos, double xVel, double yVel, Polygon body, int phyCat, double rotationVel) {
        this.body = body;
        this.xPos = xPos;
        this.yPos = yPos;
        this.xVel = xVel;
        this.yVel = yVel;
        this.physicsCategory = phyCat;
        this.isAlive = true;
        this.dYPoints = new ArrayList<Double>();
        this.dXPoints = new ArrayList<Double>();
        this.rotationVel = rotationVel;
        for (int xp : this.body.xpoints) {
            Integer xpInteger = new Integer(xp);
            this.dXPoints.add(xpInteger.doubleValue());

        }
        for (int yp : this.body.ypoints) {
            Integer ypInteger = new Integer(yp);
            this.dYPoints.add(ypInteger.doubleValue());
        }

        this.updatePostion(this.xPos, this.yPos);
        this.approxShape();

    }

    public void update(ArrayList<Entity> entities) {
        this.collision(entities);
        this.move();
        this.fixCrossOver();
        this.approxShape();

        this.rotateBody(this.rotationVel);
    }

    public void fixCrossOver() {
        if (this.xPos > 900) {
            teleport(0, this.yPos);
        } else if (this.xPos < 0) {
            teleport(900, this.yPos);
        } else if (this.yPos > 600) {
            teleport(this.xPos, 0);
        } else if (this.yPos < 0) {
            teleport(this.xPos, 600);
        }

    }

    public void approxShape() {
        for (int xp = 0; xp < this.body.xpoints.length; xp++) {
            Integer integerTransX = this.dXPoints.get(xp).intValue();
            this.body.xpoints[xp] = integerTransX.intValue();
        }
        for (int yp = 0; yp < this.body.ypoints.length; yp++) {
            Integer integerTransY = this.dYPoints.get(yp).intValue();
            this.body.ypoints[yp] = integerTransY.intValue();
        }
    }


    public void draw(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.draw(this.body);

//        g2d.fillRect((int) calculateCenter().getX(), (int) calculateCenter().getY(), 1, 1);

    }

    public void updatePostion(double xChange, double yChange) {
        for(int xp = 0; xp < this.dXPoints.size(); xp++) {
            Double dXP = this.dXPoints.get(xp);
            Double newValue = dXP += new Double(xChange);
            this.dXPoints.set(xp, newValue);
        }

        for(int yp = 0; yp < this.dYPoints.size(); yp++) {
            Double dYP = this.dYPoints.get(yp);
            Double newValue = dYP += new Double(yChange);
            this.dYPoints.set(yp, newValue);
        }


    }


    public void collision(ArrayList<Entity> entities) {

//      Player: 1
//      Asteroid: 2
//      Bullet: 3
        for ( Entity entity : entities ) {
            if (entity != this) {
                if (this.testIntersect(this.body, entity.body)) {
                    if (this.physicsCategory == 1 && entity.physicsCategory == 2) {
                        this.isAlive = false;
                        entity.isAlive = false;

                    }
                    if (this.physicsCategory == 3 && entity.physicsCategory == 2) {
                        this.isAlive = false;
                        entity.isAlive = false;
                    }


                }

            }

        }
    }

    public void move() {
        double oldXpos = this.xPos;
        double oldYpos = this.yPos;
        this.xPos += this.xVel;
        this.yPos += this.yVel;
        double xChange = this.xPos - oldXpos;
        double yChange = this.yPos - oldYpos;
        updatePostion(xChange, yChange);


    }


    public void teleport(double x, double y) {
        double xChange = x-this.xPos;
        double yChange = y-this.yPos;
        this.xPos = x;
        this.yPos = y;
        updatePostion(xChange, yChange);
    }

    public void rotate(double angle) {
        Point2D.Double[] bodyPoints = new Point2D.Double[this.body.npoints];
        for (int p = 0; p<bodyPoints.length; p++) {
            bodyPoints[p] = new Point2D.Double(this.body.xpoints[p], this.body.ypoints[p]);
        }

    }

    public boolean testIntersect(Shape a, Shape b) {
        Area areaA = new Area(a);
        areaA.intersect(new Area(b));
        return !areaA.isEmpty();

    }

    public Point2D.Double calculateCenter() {
        Double x1 = new Double(0.0);
        Double y1 = new Double(0.0);
        int numOfPoints = this.body.npoints;
        for (int xp=0; xp<this.dXPoints.size(); xp++) {
            x1 += this.dXPoints.get(xp);
        }

        for (int yp=0; yp<this.dYPoints.size(); yp++) {
            y1 += this.dYPoints.get(yp);
        }

        double xF = x1 / numOfPoints;
        double yF = y1 / numOfPoints;

        return new Point2D.Double(xF,yF);

    }

    public void rotateBody(double angle) {
        Point2D.Double center = calculateCenter();
        for (int p = 0; p < this.body.npoints; p++) {
            Point2D.Double prePoint = new Point2D.Double(this.dXPoints.get(p), this.dYPoints.get(p));
            Point2D.Double rotatedPoint = this.rotatePoint(prePoint, angle, center);
            this.dXPoints.set(p, rotatedPoint.getX());
            this.dYPoints.set(p, rotatedPoint.getY());
        }
    }

    public Point2D.Double rotatePoint(Point2D.Double p, double angle, Point2D.Double center) {
        double radAngle = angle / 180 * Math.PI;

        double x1 = p.x - center.x;
        double y1 = p.y - center.y;

        double tempX1 = x1 * Math.cos(radAngle) - y1 * Math.sin(radAngle);
        double tempY1 = x1 * Math.sin(radAngle) + y1 * Math.cos(radAngle);

        Point2D.Double finalPoint = new Point2D.Double(tempX1 + center.x, tempY1 + center.y);

        return finalPoint;


    }





}
