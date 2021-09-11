package converter.component.renderer;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.GLBuffers;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import converter.component.NmnConverter;
import converter.entity.GraphicElement;
import converter.entity.NmnNote;
import glm.vec._2.Vec2;
import uno.glsl.Program;

import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.jogamp.opengl.GL.*;
import static uno.buffer.UtilKt.destroyBuffers;
import com.jogamp.opengl.util.texture.TextureData;

public class NmnRenderer {
    private IntBuffer VBO;
    private List<GraphicElement> graphicElementList;
    private int section = 24;//每小节容量
    private List<GraphicElement> unbindNmnNoteList;

    public NmnRenderer() {
        unbindNmnNoteList = new ArrayList<>();
    }

    public void init(GL3 gl) {
        final float[] Vertices_Tonality = {
                //   ---- 位置 ----      - 纹理坐标 -
                -0.8f, 0.9f, 1.0f, 1.0f,   // 右上
                -0.8f, 0.75f, 1.0f, 0.0f,   // 右下
                -0.98f, 0.75f, 0.0f, 0.0f,   // 左下
                -0.98f, 0.9f, 0.0f, 1.0f    // 左上
        };
        final float[] Vertices_Normal = {
                //   ---- 位置 ----      - 纹理坐标 -
                -0.93f, 0.7f, 1.0f, 1.0f,   // 右上
                -0.93f, 0.55f, 1.0f, 0.0f,   // 右下
                -0.98f, 0.55f, 0.0f, 0.0f,   // 左下
                -0.98f, 0.7f, 0.0f, 1.0f    // 左上
        };
        final float[] Vertices_Underline = {
                //   ---- 位置 ----      - 纹理坐标 -
                -0.93f, 0.7f, 1.0f, 1.0f,   // 右上
                -0.93f, 0.65f, 1.0f, 0.0f,   // 右下
                -0.98f, 0.65f, 0.0f, 0.0f,   // 左下
                -0.98f, 0.7f, 0.0f, 1.0f    // 左上
        };
        VBO = GLBuffers.newDirectIntBuffer(3);

        FloatBuffer vertexBufferTonality = GLBuffers.newDirectFloatBuffer(Vertices_Tonality);
        FloatBuffer vertexBufferNormal = GLBuffers.newDirectFloatBuffer(Vertices_Normal);
        FloatBuffer vertexBufferUnderline = GLBuffers.newDirectFloatBuffer(Vertices_Underline);

        gl.glGenBuffers(3, VBO);

        //VBO序号，0为调（1=C），1为普通元素（音符、小节线），2为下划线（8、16、32分音符用）
        gl.glBindBuffer(GL_ARRAY_BUFFER, VBO.get(0));
        gl.glBufferData(GL_ARRAY_BUFFER, vertexBufferTonality.capacity() * Float.BYTES, vertexBufferTonality, GL_STATIC_DRAW);
        gl.glBindBuffer(GL_ARRAY_BUFFER, 0);

        gl.glBindBuffer(GL_ARRAY_BUFFER, VBO.get(1));
        gl.glBufferData(GL_ARRAY_BUFFER, vertexBufferNormal.capacity() * Float.BYTES, vertexBufferNormal, GL_STATIC_DRAW);
        gl.glBindBuffer(GL_ARRAY_BUFFER, 0);

        gl.glBindBuffer(GL_ARRAY_BUFFER, VBO.get(2));
        gl.glBufferData(GL_ARRAY_BUFFER, vertexBufferUnderline.capacity() * Float.BYTES, vertexBufferUnderline, GL_STATIC_DRAW);
        gl.glBindBuffer(GL_ARRAY_BUFFER, 0);

        destroyBuffers(vertexBufferTonality, vertexBufferNormal, vertexBufferUnderline);
    }

    public void drawNmn(GL3 gl, Program program) {
        if(graphicElementList == null){
            return;
        }
        gl.glUseProgram(program.name);

        for (GraphicElement element : graphicElementList) {
            gl.glBindTexture(GL_TEXTURE_2D, element.getTexture().get(0));
            gl.glBindVertexArray(element.getVao().get(0));

            gl.glUniform1f(program.get("offsetX"), element.getOffsetX());
            gl.glUniform1f(program.get("offsetY"), element.getOffsetY());

            gl.glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
        }
    }

    public void setGraphicElementList(List<GraphicElement> graphicElements){
        graphicElementList = graphicElements;
    }
    public void addToUnbindRollList(GraphicElement graphicElement){
        unbindNmnNoteList.add(graphicElement);
    }

    public void bindNotes(GL3 gl) {
        if (!unbindNmnNoteList.isEmpty()) {
            Iterator<GraphicElement> iterator = unbindNmnNoteList.iterator();
            while (iterator.hasNext()) {
                GraphicElement element = iterator.next();

                // set vbo
                int vbo;

                vbo = VBO.get(element.getShapeType());

                // bind vao
                gl.glGenVertexArrays(1, element.getVao());

                gl.glBindVertexArray(element.getVao().get(0));
                {
                    gl.glBindBuffer(GL_ARRAY_BUFFER, vbo);
                    {
                        //-----纹理-----
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
                            TextureData textureData = TextureIO.newTextureData(GLProfile.getDefault(), this.getClass().getResource("/symbols/" + element.getPicName() + ".jpg"), false, "JPG");
                            if (textureData != null) {
                                //System.out.println(textureData.getHeight());
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
                    }
                    gl.glBindBuffer(GL_ARRAY_BUFFER, 0);
                }
                gl.glBindVertexArray(0);

                iterator.remove();
            }
        }
    }




}