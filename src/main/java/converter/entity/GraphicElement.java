package converter.entity;

import com.jogamp.opengl.util.GLBuffers;

import java.nio.IntBuffer;

public class GraphicElement {
    private final IntBuffer vao;
    private final IntBuffer texture;
    private float offsetX;
    private float offsetY;

    public GraphicElement() {
        vao = GLBuffers.newDirectIntBuffer(1);
        texture = GLBuffers.newDirectIntBuffer(1);
        offsetX = 0;
        offsetY = 0;
    }

    public IntBuffer getVao() {
        return vao;
    }
    public IntBuffer getTexture(){
        return texture;
    }

    public void setOffsetX(float offsetX){
        this.offsetX = offsetX;
    }
    public void setOffsetY(float offsetY){
        this.offsetY = offsetY;
    }
    public float getOffsetX(){
        return offsetX;
    }
    public float getOffsetY(){
        return offsetY;
    }
}
