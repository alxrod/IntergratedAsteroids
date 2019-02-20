package com.Asteroids;

import javafx.scene.transform.Affine;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

public class Player extends Entity {

    public double directionSlope;
    public Point2D.Double lastSpawnedParticle;
    public boolean spaceReleased;
    public boolean shielding;
    public long lastShield;
    public boolean shieldFlash;

    public double speed;

    Player(double xPos, double yPos, Polygon body) {
        super(xPos, yPos, 0, 0, body, 1, 0);

//      Cause always going to spawn vertical so undefined slope
        this.directionSlope = -1000000000;
        this.speed = 0.4;
        this.lastSpawnedParticle = new Point2D.Double(this.body.xpoints[3], this.body.ypoints[3]);
        this.spaceReleased = true;
        this.shielding = false;
        this.lastShield = System.currentTimeMillis()-9000;
        this.shieldFlash = true;
    }

    public void update(ArrayList<Entity> entities, boolean thrusterPressed, ArrayList<Exhaust> exhaustParticles, boolean firing, ArrayList<Bullet> bullets, ArrayList<DestructionParticle> destructionParticles, ArrayList<DestructionPlank> destructionPlanks, boolean shielding) {


        double yChange = this.body.ypoints[3]-this.body.ypoints[1];
        double xChange = this.body.xpoints[3]-this.body.xpoints[1];
        if (xChange == 0) {
            this.directionSlope = -1000000000;
        } else {
            this.directionSlope = (yChange)/(xChange);
        }


        if (thrusterPressed) {
            double combinedVelocity = Math.sqrt( Math.pow( this.xVel ,2)+Math.pow( this.yVel ,2) );
            if (this.directionSlope == -1000000000) {
                if (this.body.ypoints[3]>this.body.ypoints[1]) {
                    this.yVel -= this.speed;
                } else {
                    this.yVel += this.speed;
                }

            } else if (combinedVelocity < 10){
                double hypo = Math.sqrt(Math.pow(xChange, 2) + Math.pow(yChange, 2));
                double ratio = this.speed / hypo * -1;
                this.xVel += xChange * ratio;
                this.yVel += yChange * ratio;
            }
            
            double distance = Math.sqrt( Math.pow( this.lastSpawnedParticle.getX()-this.body.xpoints[3] ,2)+Math.pow( this.lastSpawnedParticle.getY()-this.body.ypoints[3] ,2) );
            if (distance > 8) {
                Random rand = new Random();

                double randomXValue = (this.dXPoints.get(3) - 5) + (6) * rand.nextDouble();
                double randomYValue = (this.dYPoints.get(3) - 5) + (6) * rand.nextDouble();

                int[] xPoints = {0, 1, 1, 0};
                int[] yPoints = {0, 0, 1, 1};
                Polygon partBod = new Polygon(xPoints,yPoints, 4);
                Exhaust particle = new Exhaust(randomXValue, randomYValue, partBod);
                entities.add(particle);
                exhaustParticles.add(particle);

                this.lastSpawnedParticle = new Point2D.Double(this.body.xpoints[3], this.body.ypoints[3]);
            }
        }


        if (firing && this.spaceReleased == true) {
            double bulletSpeed = 10;
            double hypo = Math.sqrt(Math.pow(xChange, 2) + Math.pow(yChange, 2));
            double ratio = bulletSpeed / hypo * -1;
            double bulletXVel;
            double bulletYVel;

            if (this.directionSlope == -1000000000) {
                double direction;
                if (this.dYPoints.get(3)>this.dYPoints.get(1)) {
                    direction = -1;
                } else {
                    direction = 1;
                }
                bulletXVel = 0;
                bulletYVel = bulletSpeed * direction;
            } else {
                bulletXVel = xChange * ratio;
                bulletYVel = yChange * ratio;
            }

            int[] xPoints = {0, 2, 2, 0};
            int[] yPoints = {0, 0, 2, 1};
            Polygon bulletBod = new Polygon(xPoints,yPoints, 4);
            Bullet bullet = new Bullet(dXPoints.get(1), dYPoints.get(1), bulletXVel, bulletYVel, bulletBod);
            entities.add(bullet);
            bullets.add(bullet);
            this.spaceReleased = false;

        } else if (firing == false) {
            this.spaceReleased = true;
        }


        if (this.xVel > 0) {
            this.xVel -= 0.02;
        } else {
            this.xVel += 0.02;
        }

        if (this.yVel > 0) {
            this.yVel -= 0.02;
        } else {
            this.yVel += 0.02;
        }



        this.move();
        this.fixCrossOver();
        this.approxShape();

        this.rotateBody(this.rotationVel);
        this.collision(entities);



        long now = System.currentTimeMillis();
        long tDelta = Math.abs(this.lastShield-now);
        if (this.shielding == true) {
            if (tDelta > 2000) {
                this.lastShield = now;
                this.shielding = false;
            }

        } else {
            if (shielding == true && tDelta > 7000) {
                System.out.println("Shielding");
                this.lastShield = now;
                this.shielding = true;
            }
        }

        if (this.shielding==true) {
            this.isAlive = true;
        }

        if (this.isAlive == false) {
            Random rand = new Random();
            for (int i = 0; i<5; i++) {
                double newXPos = this.xPos + rand.nextDouble()*30;
                double newYPos = this.yPos + rand.nextDouble()*30;
                DestructionParticle particle = new DestructionParticle(newXPos, newYPos);
                entities.add(particle);
                destructionParticles.add(particle);
            }
            for (int i = 0; i<3; i++) {
                double newXPos = this.xPos + rand.nextDouble()*30;
                double newYPos = this.yPos + rand.nextDouble()*30;
                DestructionPlank plank = new DestructionPlank(newXPos, newYPos);
                entities.add(plank);
                destructionPlanks.add(plank);
            }
        }



    }

    public void draw(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.draw(this.body);
        g2d.setColor(Color.BLACK);
        g2d.fill(this.body);

        if (this.shielding == true) {
            if (this.shieldFlash == true) {
                g2d.setColor(Color.WHITE);
                Point2D.Double center = this.calculateCenter();
                Double x = center.getX();
                Double y = center.getY();
                int rX = x.intValue()-(72/2);
                int rY = y.intValue()-(72/2);

                g2d.drawOval(rX, rY, 72, 72);
                this.shieldFlash = false;
            } else {
                this.shieldFlash = true;
            }

        }

        if(this.shielding == false) {
            long now = System.currentTimeMillis();
            long tDelta = Math.abs(now-this.lastShield);
            int width = (int) (10 * tDelta/1000);
            if (tDelta > 7000) {
                width = 70;
            }
            int height = 15;
            g2d.setColor(Color.WHITE);
            g2d.fillRect(800, 20, width, height);
            g2d.drawRect(799, 19, 72, 17);
        }




    }
}