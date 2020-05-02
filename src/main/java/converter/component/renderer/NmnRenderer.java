package converter.component.renderer;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.GLBuffers;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import glm.vec._2.Vec2;
import glm.vec._3.Vec3;
import uno.glsl.Program;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static com.jogamp.opengl.GL.*;

import java.io.*;
import javax.imageio.*;
import java.awt.image.BufferedImage;


public class NmnRenderer {
    IntBuffer VBO;
    IntBuffer VAO;
    IntBuffer EBO;
    IntBuffer TEXTURE;

    public NmnRenderer(){

    }

    public void init(GL3 gl) {

        final float[] triVertices = {
                //     ---- 位置 ----       ---- 颜色 ----     - 纹理坐标 -
                0.3f,  0.6f, 0.0f,   1.0f, 0.0f, 0.0f,   1.0f, 0.0f,   // 右上
                0.3f, -0.3f, 0.0f,   0.0f, 1.0f, 0.0f,   1.0f, 1.0f,   // 右下
                -0.3f, -0.3f, 0.0f,   0.0f, 0.0f, 1.0f,   0.0f, 1.0f,   // 左下
                -0.3f,  0.6f, 0.0f,   1.0f, 1.0f, 1.0f,   0.0f, 0.0f    // 左上
        };

        /*
        final float[] triVertices = {
                //     ---- 位置 ----
                0.3f,  0.6f, 0.0f,   // 右上
                0.3f, -0.3f, 0.0f,   // 右下
                -0.3f, -0.3f, 0.0f,   // 左下
                -0.3f,  0.6f, 0.0f    // 左上
        };
        */
        int[] indices = { // 注意索引从0开始!
                0, 1, 3, // 第一个三角形
                1, 2, 3  // 第二个三角形
        };

        //生成VBO
        VBO = GLBuffers.newDirectIntBuffer(1);
        FloatBuffer vertexBufferTriangle = GLBuffers.newDirectFloatBuffer(triVertices);
        gl.glGenBuffers(1, VBO);
        //生成VAO
        VAO = GLBuffers.newDirectIntBuffer(1);
        gl.glGenVertexArrays(1, VAO);
        //生成EBO
        EBO = GLBuffers.newDirectIntBuffer(1);
        IntBuffer indicesBufferTriangle = GLBuffers.newDirectIntBuffer(indices);
        gl.glGenBuffers(1,EBO);

        //绑定VAO
        gl.glBindVertexArray(VAO.get(0));
        //绑定VBO
        gl.glBindBuffer(GL_ARRAY_BUFFER, VBO.get(0));
        gl.glBufferData(GL_ARRAY_BUFFER, vertexBufferTriangle.capacity() * Float.BYTES, vertexBufferTriangle, GL_STATIC_DRAW);
        //绑定EBO
        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO.get(0));
        gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBufferTriangle.capacity() * Integer.BYTES, indicesBufferTriangle, GL_STATIC_DRAW);

        //纹理
        //生成纹理
        TEXTURE = GLBuffers.newDirectIntBuffer(1);
        gl.glGenTextures(1,TEXTURE);

        //加载图片
        try {
            TextureData textureData = TextureIO.newTextureData(GLProfile.getDefault(), new File("D:\\contain.jpg"), false, "JPG");
            if(textureData!=null){
                System.out.println(textureData.getHeight());
                gl.glTexImage2D(GL_TEXTURE_2D,0, GL_RG8,textureData.getWidth(), textureData.getHeight(),0, GL_RGB, GL_UNSIGNED_BYTE, textureData.getBuffer());
                gl.glGenerateMipmap(GL_TEXTURE_2D);
            }else{
                System.out.println("failed to load picture");
            }

        }catch (IOException e) {
            System.out.println(e.getMessage());
        }

        //绑定纹理
        //gl.glActiveTexture(GL_TEXTURE0);
        gl.glBindTexture(GL_TEXTURE_2D, TEXTURE.get(0));
        // 为当前绑定的纹理对象设置环绕、过滤方式
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);


        //-----链接顶点属性-----
        //告诉OpenGL该如何解析顶点数据（应用到逐个顶点属性上）
        //每一行前三个是顶点位置
        gl.glVertexAttribPointer(0, Vec3.length, GL_FLOAT, false, Vec3.SIZE + Vec3.SIZE + Vec2.SIZE, 0);
        gl.glEnableVertexAttribArray(0);

        //4-6是颜色
        gl.glVertexAttribPointer(1, Vec3.length, GL_FLOAT, false, Vec3.SIZE + Vec3.SIZE + Vec2.SIZE, Vec3.SIZE);
        gl.glEnableVertexAttribArray(1);
        //8-9是纹理
        gl.glVertexAttribPointer(2, Vec2.length, GL_FLOAT, false, Vec3.SIZE + Vec3.SIZE + Vec2.SIZE, Vec3.SIZE + Vec3.SIZE);
        gl.glEnableVertexAttribArray(2);


    }

    public void drawNmn(GL3 gl, Program program) {
        gl.glUseProgram(program.name);
        gl.glBindTexture(GL_TEXTURE_2D,TEXTURE.get(0));
        gl.glBindVertexArray(VAO.get(0));
        gl.glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);

    }


}
