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
import java.awt.geom.Point2D;
// import java.text.DecimalFormat;


class Tester implements Runnable { //2
	final int WIDTH = 800;
	final int HEIGHT = 800;

	JFrame frame;
	Canvas canvas;
	BufferStrategy bufferStrategy;

	public AlexCalc amath;
	public Polygon funcVis;
	public Point2D.Double center;
	public double angle=10;

   public static void main(String[] args) { //3

      Tester ex = new Tester();
	  new Thread(ex).start();
   } 

   public Tester() {
   		// Testing:
   		amath = new AlexCalc();
 
   		System.out.println(amath.testFuncCart(-4));
   		ArrayList func = amath.generateCartFunc();
   		System.out.println(func);
   		System.out.println(amath.funcCartParser(func, 0.5));

   		ArrayList<Double> xInts = amath.findBounds(func,-20.0,20.0);
   		System.out.println(xInts);
   		System.out.println(amath.riemannSumIntegral( func, ((double) xInts.get(0)), ((double) xInts.get(xInts.size()-1))));
   		funcVis = amath.funcCartVisualizer(func, ((double) xInts.get(0)), ((double) xInts.get(1)));
   		double[] dcenter = amath.calcCOMCart(func, ((double) xInts.get(0)), ((double) xInts.get(1)));
   		center = new Point2D.Double(dcenter[0], dcenter[1]);
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
		System.out.println("updating");
		funcVis = amath.rotateBody(20,center);
	
	}

	private void render() {
		Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();
		g.clearRect(0, 0, WIDTH, HEIGHT);
		g.draw(funcVis);
		g.setColor(Color.RED);
		g.fill(funcVis);
		g.setColor(Color.BLUE);
		g.fillRect((int) center.getX()-2, (int) center.getY()-2,4,4);
		
		g.setColor(Color.BLACK);
		g.setFont(new Font("TimesRoman", Font.PLAIN, 30));
		g.drawString("Intergrated Asteroids!", 75, 150);
		g.setFont(new Font("TimesRoman", Font.PLAIN, 15));
		g.drawString("~Sorry for the lag. Continous functions are hard to display.~", 75, 200);
		
		bufferStrategy.show();

		g.dispose();
	}


} //6