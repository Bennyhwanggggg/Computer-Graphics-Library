package unsw.graphics;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.awt.ImageUtil;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

public class Texture {

    private boolean mipMapEnabled;

    private int id;

    /**
     * Create a texture from a file. The file must have dimensions that are a 
     * power of 2.
     * @param gl
     * @param fileName
     * @param extension
     * @param mipmaps
     */
    public Texture(GL3 gl, String fileName, String extension, boolean mipmaps) {
        mipMapEnabled = mipmaps;
        TextureData data = null;
        try {
            File file = new File(fileName);
            BufferedImage img = ImageIO.read(file); // read file into
                                                    // BufferedImage
            ImageUtil.flipImageVertically(img);

            data = AWTTextureIO.newTextureData(gl.getGLProfile(), img,
                    false);

        } catch (IOException exc) {
            System.err.println(fileName);
            exc.printStackTrace();
            System.exit(1);
        }

        int[] ids = new int[1];

        gl.glGenTextures(1, ids, 0);

        id = ids[0];

        gl.glBindTexture(GL.GL_TEXTURE_2D, id);

        // Build texture initialised with image data.
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, data.getInternalFormat(),
                data.getWidth(), data.getHeight(), 0, data.getPixelFormat(),
                data.getPixelType(), data.getBuffer());

        setFilters(gl);

    }

    private void setFilters(GL3 gl) {
        // Build the texture from data.
        if (mipMapEnabled) {
            // TODO Set texture parameters to enable automatic mipmap generation
            // and bilinear/trilinear filtering

        } else {
            // Set texture parameters to enable bilinear filtering.
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
                    GL.GL_NEAREST);
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
                    GL.GL_NEAREST);
        }

    }

    /**
     * Create a texture from the given ByteBuffer. Buffer is assumed to be RGBA
     * format.
     * @param gl
     * @param buffer
     * @param size
     * @param mipmaps
     */
    public Texture(GL3 gl, ByteBuffer buffer, int size, boolean mipmaps) {
        mipMapEnabled = mipmaps;
        int[] ids = new int[1];
        gl.glGenTextures(1, ids, 0);
        
        id = ids[0];
        
        gl.glBindTexture(GL.GL_TEXTURE_2D, id);

        // Specify image data for currently active texture object.
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, size, size, 0,
                GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, buffer);

        setFilters(gl);

    }
    
    /**
     * Create a texture with NO associated buffer.
     * @param gl
     * @param buffer
     * @param size
     * @param mipmaps
     */
    public Texture(GL3 gl, boolean mipmaps) {
        mipMapEnabled = mipmaps;
        int[] ids = new int[1];
        gl.glGenTextures(1, ids, 0);
        
        id = ids[0];
        
        gl.glBindTexture(GL.GL_TEXTURE_2D, id);

        setFilters(gl);

    }

    public int getId() {
        return id;
    }

    public void destroy(GL3 gl) {
        gl.glDeleteTextures(1, new int[] {id}, 0);
    }
}
