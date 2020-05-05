package converter.component.renderer;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.GLBuffers;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import converter.component.NmnConverter;
import converter.entity.GraphicElement;
import converter.entity.MuiNote;
import converter.entity.NmnNote;
import glm.vec._2.Vec2;
import glm.vec._3.Vec3;
import uno.glsl.Program;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static com.jogamp.opengl.GL.*;
import static uno.buffer.UtilKt.destroyBuffers;

import java.io.*;
import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;


public class NmnRenderer {
    IntBuffer VBO;

    private NmnConverter nmnConverter;
    List<NmnNote> nmnNoteList;
    List<GraphicElement> elements;

    public NmnRenderer(){
        elements = new ArrayList<>();

    }
    public NmnRenderer(File midiFile) {
        nmnConverter = NmnConverter.GetInstance();
        nmnNoteList = nmnConverter.getNmnNoteList(midiFile);
    }

    public void init(GL3 gl) {

        //生成VBO
        VBO = GLBuffers.newDirectIntBuffer(2);
        gl.glGenBuffers(2, VBO);

        //TODO
        initTonality(gl);
        initNotes(gl);


        //gl.glDeleteBuffers(1, VBO);
        //destroyBuffers(VBO);
    }

    public void drawNmn(GL3 gl, Program program) {
        gl.glUseProgram(program.name);

        for(GraphicElement element: elements){
            gl.glBindTexture(GL_TEXTURE_2D, element.getTexture().get(0));
            gl.glBindVertexArray(element.getVao().get(0));

            gl.glUniform1f(program.get("offsetX"), element.getOffsetX());
            gl.glUniform1f(program.get("offsetY"), element.getOffsetY());

            gl.glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
        }


    }

    private void initTonality(GL3 gl){
        final float[] triVertices = {
                //   ---- 位置 ----      - 纹理坐标 -
                -0.8f,  0.9f,             1.0f, 1.0f,   // 右上
                -0.8f, 0.75f,             1.0f, 0.0f,   // 右下
                -0.98f, 0.75f,             0.0f, 0.0f,   // 左下
                -0.98f,  0.9f,             0.0f, 1.0f    // 左上
        };
        FloatBuffer vertexBufferTriangle = GLBuffers.newDirectFloatBuffer(triVertices);

        GraphicElement element = new GraphicElement();
        //生成VAO
        gl.glGenVertexArrays(1, element.getVao());

        //绑定VAO
        gl.glBindVertexArray(element.getVao().get(0));
        //绑定VBO
        gl.glBindBuffer(GL_ARRAY_BUFFER, VBO.get(0));
        gl.glBufferData(GL_ARRAY_BUFFER, vertexBufferTriangle.capacity() * Float.BYTES, vertexBufferTriangle, GL_STATIC_DRAW);

        //纹理
        //生成纹理
        gl.glGenTextures(1, element.getTexture());

        //绑定纹理
        gl.glBindTexture(GL_TEXTURE_2D, element.getTexture().get(0));
        // 为当前绑定的纹理对象设置环绕、过滤方式
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        //加载图片
        try {
            TextureData textureData = TextureIO.newTextureData(GLProfile.getDefault(), new File("src/main/resources/symbols/tonality.jpg"), false, "JPG");
            if (textureData != null) {
                System.out.println(textureData.getHeight());
                gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, textureData.getWidth(), textureData.getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, textureData.getBuffer());
                gl.glGenerateMipmap(GL_TEXTURE_2D);
            } else {
                System.out.println("failed to load picture");
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        //-----链接顶点属性-----
        //告诉OpenGL该如何解析顶点数据（应用到逐个顶点属性上）
        //0-1是坐标
        gl.glVertexAttribPointer(0, Vec2.length, GL_FLOAT, false, Vec2.SIZE + Vec2.SIZE, 0);
        gl.glEnableVertexAttribArray(0);

        //2-3是纹理
        gl.glVertexAttribPointer(1, Vec2.length, GL_FLOAT, false, Vec2.SIZE + Vec2.SIZE, Vec2.SIZE);
        gl.glEnableVertexAttribArray(1);

        elements.add(element);
    }

    private void initNotes(GL3 gl){
        File midiFile = new File("src/main/resources/symbols/AWM.mid");
        nmnConverter = NmnConverter.GetInstance();
        nmnNoteList = nmnConverter.getNmnNoteList(midiFile);
        /*
        notes = new ArrayList<>();

        for(int trackID = 0; trackID < nmnNoteList.size(); trackID++){
            GraphicElement note = new GraphicElement(trackID);


        }
        */
        float[] Vertices = {
                //   ---- 位置 ----      - 纹理坐标 -
                -0.93f,  0.7f,             1.0f, 1.0f,   // 右上
                -0.93f, 0.55f,             1.0f, 0.0f,   // 右下
                -0.98f, 0.55f,             0.0f, 0.0f,   // 左下
                -0.98f,  0.7f,             0.0f, 1.0f    // 左上
        };
        FloatBuffer vertexBufferTriangle = GLBuffers.newDirectFloatBuffer(Vertices);

        float offsetX = -0.05f;
        float offsetY = 0;
        for(int i = 0; i<nmnNoteList.size();i++){
            GraphicElement element = new GraphicElement();
            offsetX +=0.05;
            if(i%30 == 0 && i>=30){
                offsetX = 0f;
                offsetY -= 0.18f;
            }
            element.setOffsetX(offsetX);
            element.setOffsetY(offsetY);


            //生成VAO
            gl.glGenVertexArrays(1, element.getVao());

            //绑定VAO
            gl.glBindVertexArray(element.getVao().get(0));
            //绑定VBO
            gl.glBindBuffer(GL_ARRAY_BUFFER, VBO.get(1));
            gl.glBufferData(GL_ARRAY_BUFFER, vertexBufferTriangle.capacity() * Float.BYTES, vertexBufferTriangle, GL_STATIC_DRAW);

            //纹理
            //生成纹理
            gl.glGenTextures(1, element.getTexture());

            //绑定纹理
            gl.glBindTexture(GL_TEXTURE_2D, element.getTexture().get(0));
            // 为当前绑定的纹理对象设置环绕、过滤方式
            gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

            //加载图片
            try {
                TextureData textureData = TextureIO.newTextureData(GLProfile.getDefault(), new File("src/main/resources/symbols/"+nmnNoteList.get(i).getPitch()+".jpg"), false, "JPG");
                if (textureData != null) {
                    System.out.println(textureData.getHeight());
                    gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, textureData.getWidth(), textureData.getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, textureData.getBuffer());
                    gl.glGenerateMipmap(GL_TEXTURE_2D);
                } else {
                    System.out.println("failed to load picture");
                }

            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

            //-----链接顶点属性-----
            //告诉OpenGL该如何解析顶点数据（应用到逐个顶点属性上）
            //0-1是坐标
            gl.glVertexAttribPointer(0, Vec2.length, GL_FLOAT, false, Vec2.SIZE + Vec2.SIZE, 0);
            gl.glEnableVertexAttribArray(0);

            //2-3是纹理
            gl.glVertexAttribPointer(1, Vec2.length, GL_FLOAT, false, Vec2.SIZE + Vec2.SIZE, Vec2.SIZE);
            gl.glEnableVertexAttribArray(1);

            elements.add(element);
        }



    }

}