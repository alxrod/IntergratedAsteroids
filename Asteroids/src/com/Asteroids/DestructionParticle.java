package com.Asteroids;

import javafx.scene.transform.Affine;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

public class DestructionParticle extends Entity {
    public long creationTime;

    DestructionParticle(double xPos, double yPos) {
        super(xPos, yPos, 0, 0, new Polygon(), 4, 0);


        int[] xPoints = {0,0,1,1};
        int[] yPoints = {0,0,1,1};
        Polygon ParticleBody = new Polygon(xPoints,yPoints, 4);
        this.body = ParticleBody;

        Random rand = new Random();
        this.xVel = rand.nextDouble() * 3.5;
        this.yVel = rand.nextDouble() * 3.5;

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
        this.creationTime = System.currentTimeMillis();
    }

    public boolean updateB(ArrayList<Entity> entities) {
        super.update(entities);
        long now = System.currentTimeMillis();
        long tDelta = Math.abs(this.creationTime-now);
        if (tDelta > 500) {
            return false;
        } else {
            return true;
        }
    }
}