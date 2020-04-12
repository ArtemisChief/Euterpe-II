package pianoroll.component.renderer;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.GLBuffers;
import glm.vec._2.Vec2;
import pianoroll.component.Pianoroll;
import pianoroll.entity.GraphicElement;
import pianoroll.entity.Key;
import pianoroll.util.Semantic;
import uno.glsl.Program;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import static com.jogamp.opengl.GL.*;
import static uno.buffer.UtilKt.destroyBuffers;

public class PianoRenderer {

    private final List<Key> keyList;

    public PianoRenderer() {
        keyList = Pianoroll.GetInstance().getPianoController().getKeyList();
    }

    public void init(GL3 gl) {
        final float[] vertexDataKeyWhite = {
                -1.113f,  0.0f,          // Left-Top
                -1.113f, -12.1f,         // Left-Bottom
                 1.113f, -12.1f,         // Right-Bottom
                 1.113f,  0.0f           // Right-Top
        };

        final float[] vertexDataKeyBlack = {
                -0.68f,  0.0f,           // Left-Top
                -0.68f, -8.3f,           // Left-Bottom
                 0.68f, -8.3f,           // Right-Bottom
                 0.68f,  0.0f            // Right-Top
        };

        IntBuffer buffer = GLBuffers.newDirectIntBuffer(2);

        FloatBuffer vertexBufferKeyWhite = GLBuffers.newDirectFloatBuffer(vertexDataKeyWhite);
        FloatBuffer vertexBufferKeyBlack = GLBuffers.newDirectFloatBuffer(vertexDataKeyBlack);

        gl.glGenBuffers(2, buffer);

        gl.glBindBuffer(GL_ARRAY_BUFFER, buffer.get(Semantic.Buffer.VERTEX_KEYWHITE));
        gl.glBufferData(GL_ARRAY_BUFFER, vertexBufferKeyWhite.capacity() * Float.BYTES, vertexBufferKeyWhite, GL_STATIC_DRAW);
        gl.glBindBuffer(GL_ARRAY_BUFFER, 0);

        gl.glBindBuffer(GL_ARRAY_BUFFER, buffer.get(Semantic.Buffer.VERTEX_KEYBLACK));
        gl.glBufferData(GL_ARRAY_BUFFER, vertexBufferKeyBlack.capacity() * Float.BYTES, vertexBufferKeyBlack, GL_STATIC_DRAW);
        gl.glBindBuffer(GL_ARRAY_BUFFER, 0);

        destroyBuffers(vertexBufferKeyWhite, vertexBufferKeyBlack);

        for (int trackID = 0; trackID < Semantic.Piano.KEY_MAX; ++trackID) {
            Key key = new Key(trackID);
            int vbo;

            if (Key.IsWhite(trackID))
                vbo = buffer.get(Semantic.Buffer.VERTEX_KEYWHITE);
            else
                vbo = buffer.get(Semantic.Buffer.VERTEX_KEYBLACK);

            gl.glGenVertexArrays(1, key.getVao());

            gl.glBindVertexArray(key.getVao().get(0));
            {
                gl.glBindBuffer(GL_ARRAY_BUFFER, vbo);
                {
                    gl.glEnableVertexAttribArray(Semantic.Attr.POSITION);
                    gl.glVertexAttribPointer(Semantic.Attr.POSITION, Vec2.length, GL_FLOAT, false, Vec2.SIZE, 0);
                }
                gl.glBindBuffer(GL_ARRAY_BUFFER, 0);
            }
            gl.glBindVertexArray(0);

            keyList.add(key);
        }

        gl.glDeleteBuffers(2, buffer);
        destroyBuffers(buffer);
    }

    public void drawKeys(GL3 gl, Program program) {
        gl.glUseProgram(program.name);

        gl.glUniform1f(program.get("scaleY"), 1.0f);
        gl.glUniform1f(program.get("offsetY"), 0.0f);

        for (Key key : keyList) {
            gl.glBindVertexArray(key.getVao().get(0));
            gl.glUniform1i(program.get("trackID"), key.getTrackID());
            gl.glUniform1i(program.get("colorID"), key.getColorID());

            if (GraphicElement.IsWhite(key.getTrackID()))
                gl.glUniform1f(program.get("posZ"), 0.5f);
            else
                gl.glUniform1f(program.get("posZ"), 0.7f);

            gl.glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
        }

        gl.glBindVertexArray(0);
    }

    public void dispose(GL3 gl) {
        for (Key key : keyList)
            gl.glDeleteVertexArrays(1, key.getVao());
    }

}