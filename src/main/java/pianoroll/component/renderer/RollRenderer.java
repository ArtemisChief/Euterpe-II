package pianoroll.component.renderer;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.GLBuffers;
import glm.vec._2.Vec2;
import pianoroll.component.Pianoroll;
import pianoroll.entity.GraphicElement;
import pianoroll.entity.Roll;
import pianoroll.util.Semantic;
import uno.glsl.Program;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.jogamp.opengl.GL.*;
import static uno.buffer.UtilKt.destroyBuffers;

public class RollRenderer {

    private final List<Roll> rollList;
    private final List<Roll> unbindRollList;

    private IntBuffer buffer;

    public RollRenderer() {
        rollList = Pianoroll.GetInstance().getRollController().getRollList();
        unbindRollList=new ArrayList<>();
    }

    public void init(GL3 gl) {
        final float[] vertexDataRollWhite = {
                -1.02f, 0.0f,           // Left-Top
                -1.02f, -1.0f,           // Left-Bottom
                1.02f, -1.0f,           // Right-Bottom
                1.02f, 0.0f            // Right-Top
        };

        final float[] vertexDataRollBlack = {
                -0.65f, 0.0f,           // Left-Top
                -0.65f, -1.0f,           // Left-Bottom
                0.65f, -1.0f,           // Right-Bottom
                0.65f, 0.0f            // Right-Top
        };

        buffer = GLBuffers.newDirectIntBuffer(2);

        FloatBuffer vertexBufferRollWhite = GLBuffers.newDirectFloatBuffer(vertexDataRollWhite);
        FloatBuffer vertexBufferRollBlack = GLBuffers.newDirectFloatBuffer(vertexDataRollBlack);

        gl.glGenBuffers(2, buffer);

        gl.glBindBuffer(GL_ARRAY_BUFFER, buffer.get(Semantic.Buffer.VERTEX_ROLLWHITE));
        gl.glBufferData(GL_ARRAY_BUFFER, vertexBufferRollWhite.capacity() * Float.BYTES, vertexBufferRollWhite, GL_STATIC_DRAW);
        gl.glBindBuffer(GL_ARRAY_BUFFER, 0);

        gl.glBindBuffer(GL_ARRAY_BUFFER, buffer.get(Semantic.Buffer.VERTEX_ROLLBLACK));
        gl.glBufferData(GL_ARRAY_BUFFER, vertexBufferRollBlack.capacity() * Float.BYTES, vertexBufferRollBlack, GL_STATIC_DRAW);
        gl.glBindBuffer(GL_ARRAY_BUFFER, 0);

        destroyBuffers(vertexBufferRollWhite, vertexBufferRollBlack);
    }

    public void addToUnbindRollList(Roll roll) {
        unbindRollList.add(roll);
    }

    public void bindBuffer(GL3 gl) {
        if(!unbindRollList.isEmpty()) {
            Iterator<Roll> iterator = unbindRollList.iterator();
            while (iterator.hasNext()) {
                Roll roll = iterator.next();

                // set vbo
                int vbo;

                if (GraphicElement.IsWhite(roll.getTrackID()))
                    vbo = buffer.get(Semantic.Buffer.VERTEX_ROLLWHITE);
                else
                    vbo = buffer.get(Semantic.Buffer.VERTEX_ROLLBLACK);

                // bind vao
                gl.glGenVertexArrays(1, roll.getVao());

                gl.glBindVertexArray(roll.getVao().get(0));
                {
                    gl.glBindBuffer(GL_ARRAY_BUFFER, vbo);
                    {
                        gl.glEnableVertexAttribArray(Semantic.Attr.POSITION);
                        gl.glVertexAttribPointer(Semantic.Attr.POSITION, Vec2.length, GL_FLOAT, false, Vec2.SIZE, 0);
                    }
                    gl.glBindBuffer(GL_ARRAY_BUFFER, 0);
                }
                gl.glBindVertexArray(0);

                iterator.remove();
            }
        }
    }

    public void drawRolls(GL3 gl, Program program) {
        gl.glUseProgram(program.name);

        gl.glUniform1f(program.get("posZ"), 0.3f);

        for (Roll roll : rollList) {
            gl.glBindVertexArray(roll.getVao().get(0));
            gl.glUniform1i(program.get("trackID"), roll.getTrackID());
            gl.glUniform1i(program.get("colorID"), roll.getColorID());
            gl.glUniform1f(program.get("scaleY"), roll.getScaleY());
            gl.glUniform1f(program.get("offsetY"), roll.getOffsetY());
            gl.glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
        }

        gl.glBindVertexArray(0);
    }

    public void dispose(GL3 gl) {
        for (Roll roll : rollList)
            gl.glDeleteVertexArrays(1, roll.getVao());

        gl.glDeleteBuffers(2, buffer);
        destroyBuffers(buffer);
    }

}