package unsw.graphics.examples;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

import unsw.graphics.Application3D;
import unsw.graphics.CoordFrame3D;
import unsw.graphics.Matrix4;
import unsw.graphics.Shader;
import unsw.graphics.Texture;
import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;

/**
 * A simple cylinder mesh. Solution to week 8 tute exercise.
 * 
 * @author Robert Clifton-Everest
 */

public class CylinderExample extends Application3D implements MouseListener{
	

    private static final int NUM_SLICES = 32;
    private float rotateX = 0;
    private float rotateY = 0;
    private Point2D myMousePoint = null;
    private static final int ROTATION_SCALE = 1;
    
    private List<TriangleMesh> meshes =  new ArrayList<>();
    
    private Texture canTop;
    private Texture canLabel;
    
    public CylinderExample() {
        super("Cylinder example", 800, 800);
    }

    public static void main(String[] args) {
        CylinderExample example = new CylinderExample();
        example.start();
    }
    
    @Override
    public void reshape(GL3 gl, int width, int height) {
        super.reshape(gl, width, height);
        Shader.setProjMatrix(gl, Matrix4.perspective(60, 1, 1, 100));
    }
    
    @Override
    public void init(GL3 gl) {
        super.init(gl);
        getWindow().addMouseListener(this);
        
        Shader shader = new Shader(gl, "shaders/vertex_tex_phong.glsl",
                "shaders/fragment_tex_phong.glsl");
        shader.use(gl);
        
        // Set the lighting properties
        Shader.setPoint3D(gl, "lightPos", new Point3D(0, 0, 5));
        Shader.setColor(gl, "lightIntensity", Color.WHITE);
        Shader.setColor(gl, "ambientIntensity", new Color(0.2f, 0.2f, 0.2f));
        
        // Set the material properties
        Shader.setColor(gl, "ambientCoeff", Color.WHITE);
        Shader.setColor(gl, "diffuseCoeff", new Color(0.5f, 0.5f, 0.5f));
        Shader.setColor(gl, "specularCoeff", new Color(0.8f, 0.8f, 0.8f));
        Shader.setFloat(gl, "phongExp", 16f);
        
        makeCylinder(gl);
                
        canLabel = new Texture(gl, "res/textures/canLabel.bmp", "bmp", true);
        canTop = new Texture(gl, "res/textures/canTop.bmp", "bmp", true);
        
        gl.glBindTexture(GL.GL_TEXTURE_2D, canLabel.getId());
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL3.GL_CLAMP_TO_BORDER);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL3.GL_CLAMP_TO_BORDER);
        
        float[] white = {1, 1, 1, 1};
        gl.glTexParameterfv(GL.GL_TEXTURE_2D, GL3.GL_TEXTURE_BORDER_COLOR, white, 0);
    }   
    
    @Override
    public void display(GL3 gl) {
        super.display(gl);
        
        Shader.setInt(gl, "tex", 0);
        
        gl.glActiveTexture(GL.GL_TEXTURE0);

        CoordFrame3D frame = CoordFrame3D.identity()
                .translate(0, 0, -10)
                .rotateX(rotateX)
                .rotateY(rotateY);
        
        Shader.setPenColor(gl, Color.WHITE);

        gl.glBindTexture(GL.GL_TEXTURE_2D, canTop.getId());
        meshes.get(0).draw(gl, frame);
        meshes.get(1).draw(gl, frame);
        gl.glBindTexture(GL.GL_TEXTURE_2D, canLabel.getId());
        meshes.get(2).draw(gl, frame);
    }

    private void makeCylinder(GL3 gl) {        
        //An front circle
        List<Point3D> frontcircle = new ArrayList<>();
        List<Integer> frontIndices = new ArrayList<>();
        List<Point2D> circleTexCoords = new ArrayList<>();
        float angleIncrement = (float) (2*Math.PI/NUM_SLICES);
        for (int i = 0; i < NUM_SLICES; i++) {
            float angle = i * angleIncrement;
            float x = (float) Math.cos(angle);
            float y = (float) Math.sin(angle);
            frontcircle.add(new Point3D(x, y, -1));
            circleTexCoords.add(new Point2D(x/2 + 0.5f, y/2 + 0.5f));
            
            //Generate indices that effectively create a triangle fan.
            frontIndices.add(i);
            if (i > 2) {
                frontIndices.add(0);
                frontIndices.add(i-1);
            }
        }


        TriangleMesh front = new TriangleMesh(frontcircle, frontIndices, true, circleTexCoords);
        
        // The back circle
        List<Point3D> backCircle = new ArrayList<>();
        for (Point3D p : frontcircle)
            backCircle.add(p.translate(0,0,-2));
        List<Integer> backIndices = new ArrayList<>(frontIndices);
        Collections.reverse(backIndices);
        
        //Note that the texture on the back circle is mirrored
        TriangleMesh back = new TriangleMesh(backCircle, backIndices, true, circleTexCoords);
        
        // We want the sides to be smooth, so make sure the vertices are shared.
        List<Point3D> sides = new ArrayList<>();        
        List<Integer> sideIndices = new ArrayList<>();
        List<Point2D> sideTexCoords = new ArrayList<>();
        for (int i = 0; i <= NUM_SLICES; i++) {
            //The corners of the quad we will draw as triangles
            Point3D f = frontcircle.get(i % NUM_SLICES);
            Point3D b = backCircle.get(i % NUM_SLICES);

            sides.add(f);
            sides.add(b);
            
            float s = 1f*i/NUM_SLICES;
            
            //Wraps the texture all the way around
            sideTexCoords.add(new Point2D(s,0));
            sideTexCoords.add(new Point2D(s,1));
            
            //Only on one third
//            sideTexCoords.add(new Point2D(s*3,0));
//            sideTexCoords.add(new Point2D(s*3,1));
            
            if (i == NUM_SLICES) break;
            
            //Indices
            int j = i + 1;
            sideIndices.add(2*i);
            sideIndices.add(2*i + 1);
            sideIndices.add(2*j + 1);
            
            sideIndices.add(2*i);
            sideIndices.add(2*j + 1);
            sideIndices.add(2*j);
        }
        
        TriangleMesh sidesMesh = new TriangleMesh(sides, sideIndices, true, sideTexCoords);
        
        front.init(gl);
        back.init(gl);
        sidesMesh.init(gl);
        meshes.add(front);
        meshes.add(back);
        meshes.add(sidesMesh);
    }

   
	@Override
	public void mouseDragged(MouseEvent e) {
		Point2D p = new Point2D(e.getX(), e.getY());

        if (myMousePoint != null) {
            float dx = p.getX() - myMousePoint.getX();
            float dy = p.getY() - myMousePoint.getY();

            // Note: dragging in the x dir rotates about y
            //       dragging in the y dir rotates about x
            rotateY += dx * ROTATION_SCALE;
            rotateX += dy * ROTATION_SCALE;

        }
        myMousePoint = p;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		 myMousePoint = new Point2D(e.getX(), e.getY());
	}

    @Override
    public void mouseClicked(MouseEvent e) { }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) { }

    @Override
    public void mousePressed(MouseEvent e) { }

    @Override
    public void mouseReleased(MouseEvent e) { }

    @Override
    public void mouseWheelMoved(MouseEvent e) { }
}
