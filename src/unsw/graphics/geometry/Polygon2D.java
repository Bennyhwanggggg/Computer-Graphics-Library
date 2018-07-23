package unsw.graphics.geometry;

import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

import unsw.graphics.Point2DBuffer;
import unsw.graphics.Shader;

public class Polygon2D {
	
	private List<Point2D> points;
	
	public Polygon2D(List<Point2D> points) {
		this.points = points;
	}
	
	public void draw(GL3 gl) {
        Point2DBuffer buffer = new Point2DBuffer(points);
        
        int[] names = new int[1];
        gl.glGenBuffers(1, names, 0);
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, names[0]);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, points.size()*2*Float.BYTES, buffer.getBuffer(), GL.GL_STATIC_DRAW);
        
        gl.glVertexAttribPointer(Shader.POSITION, 2, GL.GL_FLOAT, false, 0, 0);
        gl.glDrawArrays(GL.GL_TRIANGLE_FAN, 0, points.size());
        
        gl.glDeleteBuffers(1, names, 0);
    }

}
