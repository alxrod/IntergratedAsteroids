package com.Asteroids;

import javafx.scene.transform.Affine;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Exhaust extends Entity {
    public long creationTime;

    Exhaust(double xPos, double yPos, Polygon rectBod) {
        super(xPos, yPos, 0, 0, rectBod, 4, 0);
        this.creationTime = System.currentTimeMillis();
    }

    public boolean update() {
        long now = System.currentTimeMillis();
        long tDelta = Math.abs(this.creationTime-now);
        if (tDelta > 100) {
            return false;
        } else {
            return true;
        }
    }
}