/**
 * 
 */
package unsw.graphics.geometry;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame3D;
import unsw.graphics.Point3DBuffer;
import unsw.graphics.Shader;

/**
 * A line in 3D space.
 * 
 * This class is immutable.
 * @author Robert Clifton-Everest
 *
 */
public class Line3D {
    private Point3D start, end;

    /** 
     * Construct a line from 'start' to 'end'
     * @param start
     * @param end
     */
    public Line3D(Point3D start, Point3D end) {
        this.start = start;
        this.end = end;
    }
    
    /**
     * Construct a line from (x0,y0,z0) to (x1,y1,z1)
     * @param x0
     * @param y0
     * @param z0
     * @param x1
     * @param y1
     * @param z1
     */
    public Line3D(float x0, float y0, float z0, float x1, float y1, float z1) {
        this(new Point3D(x0, y0, z0), new Point3D(x1, y1, z1));
    }

    public Point3D getStart() {
        return start;
    }

    public Point3D getEnd() {
        return end;
    }
    
    /**
     * Draw the line in the given coordinate frame.
     * @param gl
     */
    public void draw(GL3 gl, CoordFrame3D frame) {
        Point3DBuffer buffer = new Point3DBuffer(3);
        buffer.put(0, start);
        buffer.put(1, end);
        
        int[] names = new int[1];
        gl.glGenBuffers(1, names, 0);
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, names[0]);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, 2*3*Float.BYTES, buffer.getBuffer(), GL.GL_STATIC_DRAW);
        
        gl.glVertexAttribPointer(Shader.POSITION, 3, GL.GL_FLOAT, false, 0, 0);
        Shader.setModelMatrix(gl, frame.getMatrix());
        gl.glDrawArrays(GL.GL_LINES, 0, 2);
        
        gl.glDeleteBuffers(1, names, 0);
    }
    
    /**
     * Draw the line on the canvas.
     * @param gl
     */
    public void draw(GL3 gl) {
        draw(gl, CoordFrame3D.identity());
    }
}
