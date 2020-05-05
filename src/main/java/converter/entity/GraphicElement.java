package converter.entity;

import com.jogamp.opengl.util.GLBuffers;

import java.nio.IntBuffer;

public class GraphicElement {
    private final IntBuffer vao;
    private final IntBuffer texture;

    public GraphicElement() {
        vao = GLBuffers.newDirectIntBuffer(1);
        texture = GLBuffers.newDirectIntBuffer(1);
    }

    public IntBuffer getVao() {
        return vao;
    }
    public IntBuffer getTexture(){
        return texture;
    }

}
