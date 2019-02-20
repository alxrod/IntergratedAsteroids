package com.Asteroids;

import javafx.scene.transform.Affine;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Bullet extends Entity {
    public long creationTime;

    Bullet(double xPos, double yPos, double xVel, double yVel, Polygon rectBod) {
        super(xPos, yPos, xVel, yVel, rectBod, 3, 0);
        this.creationTime = System.currentTimeMillis();
    }

    public boolean updateB(ArrayList<Entity> entities) {
        long now = System.currentTimeMillis();
        long tDelta = Math.abs(this.creationTime-now);
        if (tDelta > 2000) {
            return false;
        } else {
            this.collision(entities);
            this.move();
            this.fixCrossOver();
            this.approxShape();

            if (this.isAlive == false) {
                return false;
            }

            return true;
        }

    }
}