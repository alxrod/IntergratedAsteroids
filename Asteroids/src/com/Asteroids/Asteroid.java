package com.Asteroids;

import javafx.scene.transform.Affine;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

public class Asteroid extends Entity {

    public double width;
    public double height;
    public double size;

    Asteroid(double xPos, double yPos, double xVel, double yVel, double size) {
        super(xPos, yPos, xVel, yVel, new Polygon(), 2, 0);
        this.size = size;

        Random rand = new Random();
        double randRotationVel = rand.nextDouble() * 5;
        this.rotationVel = randRotationVel;

        int bound = (int) Math.pow(2.5, size);
        int separation = 2*bound;
        if (size == 1) {
            bound = 3;
            separation = 6;
        }

        int[] xPoints = {
                1*separation+rand.nextInt(bound),
                2*separation+rand.nextInt(bound),
                3*separation+rand.nextInt(bound),
                3*separation+rand.nextInt(bound),
                2*separation+rand.nextInt(bound),
                1*separation+rand.nextInt(bound),
                0*separation+rand.nextInt(bound),
                0*separation+rand.nextInt(bound),};
        this.width = xPoints[3]-xPoints[0];
        int[] yPoints = {
                0*separation+rand.nextInt(bound),
                0*separation+rand.nextInt(bound),
                1*separation+rand.nextInt(bound),
                2*separation+rand.nextInt(bound),
                3*separation+rand.nextInt(bound),
                3*separation+rand.nextInt(bound),
                2*separation+rand.nextInt(bound),
                1*separation+rand.nextInt(bound),};
        this.height = yPoints[4]-yPoints[0];

        Polygon asteroidBody = new Polygon(xPoints,yPoints, 8);
        this.body = asteroidBody;

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

    public Integer update(ArrayList<Entity> entities, ArrayList<Asteroid> asteroids, ArrayList<DestructionParticle> particles, Integer score) {
        super.update(entities);
        if (this.isAlive == false) {
            entities.remove(this);
            asteroids.remove(this);

            Random rand = new Random();
            double newXPos;
            double newYPos;

            for (int i = 0; i<4*this.size; i++) {
                newXPos = this.xPos + rand.nextDouble()*this.width;
                newYPos = this.yPos + rand.nextDouble()*this.height;
                DestructionParticle particle = new DestructionParticle(newXPos, newYPos);
                entities.add(particle);
                particles.add(particle);
            }
            if (size != 1) {
                for (int i = 0; i < 2; i++) {
                    newXPos = this.xPos + rand.nextDouble()*this.width;
                    newYPos = this.yPos + rand.nextDouble()*this.height;
                    double newXVel = 1 + rand.nextDouble() * 3.5;
                    double newYVel = 1 + rand.nextDouble() * 3.5;
                    Asteroid miniAsteroid = new Asteroid(newXPos, newYPos, newXVel, newYVel, this.size - 1);
                    entities.add(miniAsteroid);
                    asteroids.add(miniAsteroid);

                }
            }

            if (size == 3) {
                return 20;
            }
            if (size == 2) {
                return 50;
            }
            if (size == 1) {
                return 100;
            }

        }
        return 0;
    }
}