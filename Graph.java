// This program will read a solution to the CVRP problem from a file called best-solution.txt
// It requires the CVRPData.java file which is available on the unit web page.

// To run, ensure that the Graph.java file, the CVRPData.java file and the best-solution.txt file are all in the current directory.
// Compile using the command:
// javac Graph.java CVRPData.java
// Run using the command:
// java Graph

// This program assumes that there is only one depot, which is index 1 in CVRPData's data structure.
// It also assumes that best-solution.txt is formatted correctly (with login, cost etc)

// This code has a hack to fix a problem with coordinates. The CVRP data may have negative coordinates but this
// code will only diplay positive coordinates on screen. By adding SHIFT to each coodinate we can make them positive,
// but the value of SHIFT needs to be set for each problem. (We could set it automatically by looking at the coordinates
// but I haven't bothered.

// @author Thomas Pickering
// @author Tim Kovacs

import java.awt.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;
import java.io.*;

// Class Description:
// Draws a graph of a given solution.

public class Graph extends JPanel {

  // ******************* Constants ************************

  public static int WIDTH;
  public static int HEIGHT;
  public static final int PADDING        = 40;
  public static final int SCALE_FACTOR   = 5;
  public static final int CROSS_SIZE     = 3;
  public static final int LINE_THICKNESS = 3;
  public static final Color AXIS_COLOUR  = Color.BLACK;
  public static final Color DEPOT_COLOUR = Color.RED;
  public static final Color POINT_COLOUR = Color.BLACK;
  public static final int SHIFT = 100; // add this to all coordinates to make them positive

  // ******************** Fields **************************
  
  // Window
  JFrame w;

  ArrayList<ArrayList<Integer>> trucks;
  ArrayList<Color> truckColours;

  FileInputStream inStream; // = new FileInputStream("best-solution.txt");
  DataInputStream dStream;
  BufferedReader reader;

  // ***************** Constructors ***********************


  public Graph() {
    CVRPData.readFile("fruitybun250.vrp");
    trucks = new ArrayList<ArrayList<Integer>>();

    setWidthAndHeight();
    truckColours = new ArrayList<Color>();
    setupWindow();
    readFile();
    generateColours();
    repaint();
  }

  // ******************* Methods **************************

    /** Return a Point representing the coordinates of the given Node
     * and add SHIFT to each dimension in order to make them positive */
    private Point getLocation(int node) {
	Point aPoint = CVRPData.getLocation(node);
	int x = (int) aPoint.getX() + SHIFT;
	int y = (int) aPoint.getY() + SHIFT;
	aPoint.setLocation(x,y);
	return aPoint;
    }

  // Sets the WIDTH and HEIGHT constants.
  private void setWidthAndHeight() {
    int maxX = (int) Double.NEGATIVE_INFINITY;
    int maxY = (int) Double.NEGATIVE_INFINITY;

    int xPos, yPos;

    for (int i = 1; i < CVRPData.NUM_NODES; i++) {
      xPos = (int) getLocation(i).getX();
      yPos = (int) getLocation(i).getY();

      if (xPos > maxX) maxX = xPos;
      if (yPos > maxY) maxY = yPos;
    }

    WIDTH  = maxX * SCALE_FACTOR;
    HEIGHT = maxY * SCALE_FACTOR;
  }


  private void readFile() {
    ArrayList<Integer> route = new ArrayList<Integer>();

    try {
      inStream = new FileInputStream("best-solution.txt");
      dStream = new DataInputStream(inStream);
      reader = new BufferedReader(new InputStreamReader(inStream));

      String line;
      String delim = "->";

      while ((line = reader.readLine()) != null) {
        route = new ArrayList<Integer>();

	if (line.startsWith("login") || line.startsWith("name") || line.startsWith("algorithm") || line.startsWith("cost"))
	    continue;

	String[] tokens = line.split(delim);
	for (int i = 0; i < tokens.length; i++) {
	    route.add(new Integer(tokens[i]));	
	}
	  trucks.add(route);
      }
      
    } catch (FileNotFoundException e) {
      System.err.println("Error: best-solution.txt not found.");
    } catch (IOException e) {
      System.err.println("Error whilst reading from best-solution.txt. Is it formatted correctly?");
    }
  }


  // Entry point
  public static void main(String[] args) {
    new Graph();
  }


  // Sets up the graph window
  private void setupWindow() {
    w = new JFrame("Best Solution");
    w.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    w.setLocationByPlatform(true);
    w.add(this);
    w.setSize(new Dimension(WIDTH + (2 * PADDING), HEIGHT + (2 * PADDING)));
    w.setVisible(true);
  }


  private void generateColours() {
    Random g = new Random();

    for (int i = 0; i < trucks.size(); i++) {
      truckColours.add(new Color(g.nextInt(255), g.nextInt(255), g.nextInt(255)));
    }
  }

  
  protected void paintComponent(Graphics g) {
    //g.super();
    Graphics2D g2d = (Graphics2D) g;

    g2d.setStroke(new BasicStroke(LINE_THICKNESS));
    //drawAxes(g2d);

    // Draw trucks
    for (int i = 0; i < trucks.size(); i++) {
      for (int j = 0; j < trucks.get(i).size() - 1; j++) {
	int x1 = ((int) getLocation(trucks.get(i).get(j).intValue()).getX()     * SCALE_FACTOR) + PADDING;
	int y1 = HEIGHT - ((int) getLocation(trucks.get(i).get(j).intValue()).getY()     * SCALE_FACTOR) + PADDING;
	int x2 = ((int) getLocation(trucks.get(i).get(j + 1).intValue()).getX() * SCALE_FACTOR) + PADDING;
	int y2 = HEIGHT - ((int) getLocation(trucks.get(i).get(j + 1).intValue()).getY() * SCALE_FACTOR) + PADDING;

	g2d.setColor(truckColours.get(i));
        g2d.drawLine(x1, y1, x2, y2);
      }
    }

    // Draw depot
    drawX(g2d, DEPOT_COLOUR, getLocation(1));

    // Draw customers
    for (int i = 2; i < CVRPData.NUM_NODES; i++) {
      drawX(g2d, POINT_COLOUR, getLocation(i));
    }
  }


  // Draws the graph axes
  private void drawAxes(Graphics2D g) {
    g.setColor(AXIS_COLOUR);

    g.drawLine(PADDING, PADDING, PADDING, PADDING + HEIGHT);
    g.drawLine(PADDING, PADDING + HEIGHT, PADDING + WIDTH, PADDING + HEIGHT);
  }


  // Draws an 'x' at a given point in a given colour.
  protected void drawX(Graphics2D g, Color c, Point p) {
    int x = (((int) p.getX()) * SCALE_FACTOR) + PADDING;
    int y = PADDING + HEIGHT - (((int) p.getY()) * SCALE_FACTOR);

    g.setColor(c);

    g.drawLine(x - CROSS_SIZE, y - CROSS_SIZE, x + CROSS_SIZE, y + CROSS_SIZE);
    g.drawLine(x - CROSS_SIZE, y + CROSS_SIZE, x + CROSS_SIZE, y - CROSS_SIZE); 
  }
}
