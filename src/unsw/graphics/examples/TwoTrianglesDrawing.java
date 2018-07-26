package unsw.graphics.examples;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL3;

import unsw.graphics.Application2D;
import unsw.graphics.geometry.LineStrip2D;
import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Triangle2D;

public class TwoTrianglesDrawing extends Application2D{
	
	private LineStrip2D currentStrip;
	private List<Triangle2D> triangles;

	// Constructor
	public TwoTrianglesDrawing() {
		super("Two Triangles", 600, 600);
        setBackground(new Color(1, 1, 0)); // set color yellow
        currentStrip = new LineStrip2D();
        triangles = new ArrayList<Triangle2D>();
	}
	
	public static void main(String[] args) {
		TwoTrianglesDrawing example = new TwoTrianglesDrawing();
		example.start();
	}
	
    @Override
    public void display(GL3 gl) {
    	super.display(gl);
    	
    	// top triangle
    	Point2D topright, topleft, center;
    	topright = new Point2D(1, 1);
    	topleft = new Point2D(-1, 1);
    	center = new Point2D(0, 0);
    	currentStrip.add(topright);
    	currentStrip.add(topleft);
    	currentStrip.add(center);
    	
    	Triangle2D topTriangle = new Triangle2D(currentStrip.getPoints());
    	topTriangle.draw(gl);
//        triangles.add(topTriangle);
        
        // reset currentStrip
        currentStrip = new LineStrip2D();
        
        Point2D bottomLeft, bottomRight;
        bottomLeft = new Point2D(-1, -1);
        bottomRight = new Point2D(1, -1);
        currentStrip.add(bottomLeft);
    	currentStrip.add(bottomRight);
    	currentStrip.add(center);
    	
    	Triangle2D bottomTriangle = new Triangle2D(currentStrip.getPoints());
    	bottomTriangle.draw(gl);
//    	triangles.add(bottomTriangle);
        
//        for (Triangle2D tri : triangles) 
//            tri.draw(gl);
        
    }
}
