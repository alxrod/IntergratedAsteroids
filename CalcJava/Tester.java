import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.*;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;


class Tester implements Runnable { //2
	final int WIDTH = 800;
	final int HEIGHT = 800;

	JFrame frame;
	Canvas canvas;
	BufferStrategy bufferStrategy;

	public AlexCalc amath;
	public Polygon funcVis;

   public static void main(String[] args) { //3

      Tester ex = new Tester();
	  new Thread(ex).start();
   } 

   public Tester() {
   		// Testing:
   		amath = new AlexCalc();
	      // System.out.println(amath.testFunc(0.5));
	    ArrayList func = amath.generateFunc();
	    System.out.println(func);
	      // Evalutation:
	    System.out.println(amath.funcParser(func, (2*Math.PI/3)));

	      // Integral:
	    System.out.println(amath.calcIntegral(func, 0, 2*Math.PI));

	    System.out.println(amath.cartesianConvert(2*Math.PI/3, 10)[0]+","+amath.cartesianConvert(2*Math.PI/3, 10)[1]);



	    // Main:
	    funcVis = amath.funcVisualizer(func, 0, 2*Math.PI);
		frame = new JFrame("Basic Test Envirornment");

		JPanel panel = (JPanel) frame.getContentPane();
		panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		panel.setLayout(null);

		canvas = new Canvas();
		canvas.setBounds(0, 0, WIDTH, HEIGHT);
		canvas.setIgnoreRepaint(true);

		panel.add(canvas);


		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);


		canvas.createBufferStrategy(2);
		bufferStrategy = canvas.getBufferStrategy();

		canvas.requestFocus();
	}// BasicGameApp()

	public void run() {

		while (true) {
			// paint the graphics
			render();

			update();
			//sleep
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {

			}
		}
	}

	public void update() {
	
	}

	private void render() {
		Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();
		g.clearRect(0, 0, WIDTH, HEIGHT);
		g.draw(funcVis);
		g.dispose();

		bufferStrategy.show();
	}
} //6