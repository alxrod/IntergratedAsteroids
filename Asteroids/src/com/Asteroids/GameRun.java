package com.Asteroids;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Random;
import java.applet.*;
import javax.swing.*;

public class GameRun extends Applet implements Runnable, KeyListener {

    public Player player;

    public boolean gameRunning;
    public Thread mainThread;
    public ArrayList<Entity> entities = new ArrayList<Entity>();
    public ArrayList<Exhaust> exhaustParticles = new ArrayList<Exhaust>();
    public ArrayList<Bullet> bullets = new ArrayList<Bullet>();
    public ArrayList<Asteroid> asteroids = new ArrayList<Asteroid>();
    public ArrayList<DestructionParticle> destructionParticles = new ArrayList<DestructionParticle>();
    public ArrayList<DestructionPlank> destructionPlanks = new ArrayList<DestructionPlank>();
    public boolean thrusterPressed = false;
    public double rotationVel;
    public boolean firing = false;
    public Integer score;
    public boolean shielding = false;
    public KiranCalc kMath;
    public AlexCalc aMath;


    public void init() {
        setSize(900, 600);
        setBackground(Color.BLACK);

        gameRunning = true;

        sceneSetup();

        addKeyListener(this);
        setFocusable(true);
        requestFocusInWindow();

        aMath = new AlexCalc();
        kMath = new KiranCalc();

        Thread mainThread = new Thread(this);
        mainThread.start();
    }

    public void sceneSetup() {
        int[] xPoints = {0, 15, 30, 15};
        int[] yPoints = {0, 40, 0, 7};
        Polygon playerBod = new Polygon(xPoints,yPoints, 4);
        this.player = new Player(getWidth()/2,getHeight()/2, playerBod);
        this.entities.add(this.player);


        for (int a = 0; a < 4; a++) {
            genAsteroid();
        }

        this.score = 0;
    }


    public void update() {
        this.player.rotationVel = this.rotationVel;
        if (this.player.isAlive) {
            this.player.update(this.entities, this.thrusterPressed, this.exhaustParticles, this.firing, this.bullets, this.destructionParticles, this.destructionPlanks, this.shielding);
        }

        for (int pP = 0; pP < this.exhaustParticles.size(); pP++) {
            if (this.exhaustParticles.get(pP).update() == false) {
                this.entities.remove(this.entities.indexOf(this.exhaustParticles.get(pP)));
                this.exhaustParticles.remove(pP);
            }
        }

        for (int pP = 0; pP < this.destructionParticles.size(); pP++) {
            if (this.destructionParticles.get(pP).updateB(this.entities) == false) {
                this.entities.remove(this.entities.indexOf(this.destructionParticles.get(pP)));
                this.destructionParticles.remove(pP);
            }
        }
        for (int pP = 0; pP < this.destructionPlanks.size(); pP++) {
            if (this.destructionPlanks.get(pP).updateB(this.entities) == false) {
                this.entities.remove(entities.indexOf(this.destructionPlanks.get(pP)));
                this.destructionPlanks.remove(pP);
            }
        }



        for (int b = 0; b < this.bullets.size(); b++) {
            if (this.bullets.get(b).updateB(entities) == false) {
                this.entities.remove(entities.indexOf(bullets.get(b)));
                this.bullets.remove(b);
            }
        }

        for (int a = 0; a < this.asteroids.size(); a++) {
            this.score += this.asteroids.get(a).update(this.entities, this.asteroids, this.destructionParticles, this.score);
        }

        if (this.asteroids.size() < 4) {
            System.out.println("Spawning new asteroid");
            genAsteroid();
        }



    }

    public void genAsteroid() {
        Random rand = new Random();
        double randXpos = rand.nextDouble() * getWidth();
        double randYpos = rand.nextDouble() * getHeight();
        double newXVel = 1 + rand.nextDouble() * 3.5;
        double newYVel = 1 + rand.nextDouble() * 3.5;
        if (Math.abs(randXpos-player.xPos)<100 || Math.abs(randYpos-player.yPos)<100) {
            System.out.println("Almost spawned asteroid on player");
            genAsteroid();
        } else {
            Asteroid starter = new Asteroid(randXpos, randYpos, newXVel, newYVel, 3);
            this.entities.add(starter);
            this.asteroids.add(starter);
        }

    }

    public void draw(Graphics g) {
        Graphics offgc;
        Image offscreen = null;
        Dimension d = size();

        offscreen = createImage(d.width, d.height);
        offgc = offscreen.getGraphics();
        offgc.setColor(getBackground());
        offgc.fillRect(0, 0, d.width, d.height);
        offgc.setColor(getForeground());

        paint(offgc);

        g.drawImage(offscreen, 0, 0, this);

    }

    public void run() {
        while (gameRunning) {
            this.update();
            this.draw(getGraphics());
            try {
                mainThread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public void paint (Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        for (int pP = 0; pP < this.exhaustParticles.size(); pP++) {
            this.exhaustParticles.get(pP).draw(g2d);
        }

        for (int pP = 0; pP < this.destructionParticles.size(); pP++) {
            this.destructionParticles.get(pP).draw(g2d);
        }

        for (int pP = 0; pP < this.destructionPlanks.size(); pP++) {
            this.destructionPlanks.get(pP).draw(g2d);
        }


        for (int b = 0; b < this.bullets.size(); b++) {
            this.bullets.get(b).draw(g2d);
        }

        for (int a = 0; a < this.asteroids.size(); a++) {
            this.asteroids.get(a).draw(g2d);
        }

        if (this.player.isAlive) {
            this.player.draw(g2d);
        }

        g2d.setFont(new Font("Monospaced", Font.PLAIN, 32));
        g2d.setColor(Color.white);
        g2d.drawString(score.toString(), 20, 40);

        if (this.player.isAlive == false) {
//          In the future I'd like to programmatically calculate this text but since I am not going to do applets after this and its such a short phrase its not 100% worth it now..
            g2d.setFont(new Font("Monospaced", Font.PLAIN, 64));
            g2d.setColor(Color.white);
            g2d.drawString("GAME OVER" , getWidth()/2-165, getHeight()/2);
            g2d.setFont(new Font("Monospaced", Font.PLAIN, 32));
            g2d.drawString("Press R to Restart" , getWidth()/2-155, getHeight()/2+32);

        }




    }

    public void killEntities() {
        this.entities = new ArrayList<Entity>();
        this.exhaustParticles = new ArrayList<Exhaust>();
        this.bullets = new ArrayList<Bullet>();
        this.asteroids = new ArrayList<Asteroid>();
        this.destructionParticles = new ArrayList<DestructionParticle>();
        this.destructionPlanks = new ArrayList<DestructionPlank>();

        this.player.lastShield = System.currentTimeMillis()-9000;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        switch( keyCode ) {
            case KeyEvent.VK_UP:
                this.thrusterPressed = true;
                break;
            case KeyEvent.VK_DOWN:
                // handle down
                break;
            case KeyEvent.VK_LEFT:
                this.rotationVel = -5;
                break;
            case KeyEvent.VK_RIGHT :
                this.rotationVel = 5;
                break;
            case KeyEvent.VK_SPACE :
                this.firing = true;
                break;

            case KeyEvent.VK_S :
                this.shielding = true;
                break;


            case KeyEvent.VK_R :
                if (this.player.isAlive == false) {
                    this.killEntities();
                    this.sceneSetup();

                }
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        switch( keyCode ) {
            case KeyEvent.VK_UP:
                this.thrusterPressed = false;
                break;
            case KeyEvent.VK_DOWN:
                // handle down
                break;
            case KeyEvent.VK_LEFT:
                this.rotationVel = 0;
                break;
            case KeyEvent.VK_RIGHT :
                this.rotationVel = 0;
                break;
            case KeyEvent.VK_SPACE :
                this.firing = false;
                break;
            case KeyEvent.VK_S :
                this.shielding = false;
                break;
        }

    }


}
