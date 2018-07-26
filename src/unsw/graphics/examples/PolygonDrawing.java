/**
 * 
 */
package unsw.graphics.examples;


import java.util.ArrayList;
import java.util.List;

import com.jogamp.newt.event.MouseAdapter;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.opengl.GL3;

import unsw.graphics.Application2D;
import unsw.graphics.geometry.Line2D;
import unsw.graphics.geometry.LineStrip2D;
import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Polygon2D;

/**
 * Use the mouse to draw line strips.
 * 
 * Left-click to place a point, right-click to complete the current strip.
 * 
 * @author Robert Clifton-Everest
 *
 */
public class PolygonDrawing extends Application2D {
    
    private LineStrip2D currentStrip;
    
    private Point2D currentPoint;

    private List<Polygon2D> finishedPolygons;

    public PolygonDrawing() {
        super("Polygon Drawing", 600, 600);
        currentStrip = new LineStrip2D();
        currentPoint = new Point2D(0,0);
        finishedPolygons = new ArrayList<Polygon2D>();
    }

    public static void main(String[] args) {
        PolygonDrawing example = new PolygonDrawing();
        example.start();
    }
    
    @Override
    public void init(GL3 gl) {
        super.init(gl);
        getWindow().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent ev) {
                if (ev.getButton() == MouseEvent.BUTTON1)
                    currentStrip.add(currentPoint);
                else if (ev.getButton() == MouseEvent.BUTTON3) {

                    if (currentStrip.getPoints().size() < 3) return;
                    Polygon2D poly = new Polygon2D(currentStrip.getPoints());
                    finishedPolygons.add(poly);
                    currentStrip = new LineStrip2D();
                }
            }
            
            @Override
            public void mouseMoved(MouseEvent ev) {
                currentPoint = fromScreenCoords(ev);
            }
        });
    }
    
    @Override
    public void display(GL3 gl) {
        super.display(gl);
        
        currentStrip.draw(gl);
        currentPoint.draw(gl);
        
        for (Polygon2D poly : finishedPolygons) 
            poly.draw(gl);

        
        if (!currentStrip.getPoints().isEmpty()) {
            Line2D incomplete = new Line2D(currentPoint, currentStrip.getLast());
            incomplete.draw(gl);
        }
    }

    private Point2D fromScreenCoords(MouseEvent ev) {
        //We need to map from pixel coordinates to coordinates on the canvas
        float x = 2f*ev.getX()/getWindow().getSurfaceWidth() - 1;
        float y = -2f*ev.getY()/getWindow().getSurfaceHeight() + 1;
        return new Point2D(x, y);
    }

}
