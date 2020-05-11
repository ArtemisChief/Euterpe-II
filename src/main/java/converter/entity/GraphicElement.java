package converter.entity;

import com.jogamp.opengl.util.GLBuffers;

import java.nio.IntBuffer;

public class GraphicElement {
    private final IntBuffer vao;
    private final IntBuffer texture;
    private float offsetX;
    private float offsetY;

    private String picName;

    //shape类型
    //0，调号（1=C）
    //1，普通音符、小节线
    //2，下划线（用于表示8分音符等）
    private int shapeType;

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
    public void setPicName(String picName){
        this.picName = picName;
    }
    public void setShapeType(int shapeType){
        this.shapeType = shapeType;
    }
    public float getOffsetX(){
        return offsetX;
    }
    public float getOffsetY(){
        return offsetY;
    }
    public String getPicName(){
        return picName;
    }
    public int getShapeType(){
        return shapeType;
    }

}
