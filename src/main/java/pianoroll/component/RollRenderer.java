package pianoroll.component;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.GLBuffers;
import glm.vec._2.Vec2;
import pianoroll.entity.GraphicElement;
import pianoroll.entity.Roll;
import pianoroll.util.Semantic;
import uno.glsl.Program;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static com.jogamp.opengl.GL.*;
import static uno.buffer.UtilKt.destroyBuffers;

public class RollRenderer {

    private final int amount;

    private int lastUnusedRollWhite;
    private int lastUnusedRollBlack;

    private final List<Roll> rollList;

    public RollRenderer() {
        amount = 200;

        lastUnusedRollWhite = 0;
        lastUnusedRollBlack = amount / 2;

        rollList = new ArrayList<>();
    }

    public void init(GL3 gl) {
        final float[] vertexDataRollWhite = {
                -0.95f,  0.0f,           // Left-Top
                -0.95f, -1.0f,           // Left-Bottom
                 0.95f,  0.0f,           // Right-Top
                 0.95f, -1.0f            // Right-Bottom
        };

        final float[] vertexDataRollBlack = {
                -0.5f,  0.0f,            // Left-Top
                -0.5f, -1.0f,            // Left-Bottom
                 0.5f,  0.0f,            // Right-Top
                 0.5f, -1.0f             // Right-Bottom
        };

        IntBuffer buffer = GLBuffers.newDirectIntBuffer(2);

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

        for (int i = 0; i < amount / 2; ++i) {
            Roll roll = new Roll();

            roll.setVbo(buffer.get(Semantic.Buffer.VERTEX_ROLLWHITE));
            rollList.add(roll);
        }

        for (int i = amount / 2; i < amount; ++i) {
            Roll roll = new Roll();

            roll.setVbo(buffer.get(Semantic.Buffer.VERTEX_ROLLBLACK));
            rollList.add(roll);
        }

        for (Roll roll : rollList) {
            gl.glGenVertexArrays(1, roll.getVao());

            gl.glBindVertexArray(roll.getVao().get(0));
            {
                gl.glBindBuffer(GL_ARRAY_BUFFER, roll.getVbo());
                {
                    gl.glEnableVertexAttribArray(Semantic.Attr.POSITION);
                    gl.glVertexAttribPointer(Semantic.Attr.POSITION, Vec2.length, GL_FLOAT, false, Vec2.SIZE, 0);
                }
                gl.glBindBuffer(GL_ARRAY_BUFFER, 0);
            }
            gl.glBindVertexArray(0);
        }

        gl.glDeleteBuffers(2, buffer);
        destroyBuffers(buffer);
    }

    public void drawRolls(GL3 gl, Program program) {
        gl.glUseProgram(program.name);

        for (Roll roll : rollList) {
            if (!roll.isUnused()) {
                gl.glBindVertexArray(roll.getVao().get(0));
                gl.glUniform1i(program.get("trackID"), roll.getTrackID());
                gl.glUniform1i(program.get("colorID"), roll.getColorID());
                gl.glUniform1f(program.get("scaleY"), roll.getScaleY());
                gl.glUniform1f(program.get("offsetY"), roll.getOffsetY());
                gl.glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
            }
        }

        gl.glBindVertexArray(0);
    }

    public void newRoll(int trackID) {
        int unusedRoll;
        if (GraphicElement.IsWhite(trackID))
            unusedRoll = firstUnusedRollWhite();
        else
            unusedRoll = firstUnusedRollBlack();

        respawnRoll(rollList.get(unusedRoll), trackID);
    }

    private int firstUnusedRollWhite() {
        for (int i = lastUnusedRollWhite; i < amount / 2; ++i) {
            if (rollList.get(i).isUnused()) {
                lastUnusedRollWhite = i;
                return i;
            }
        }

        for (int i = 0; i < lastUnusedRollWhite; ++i) {
            if (rollList.get(i).isUnused()) {
                lastUnusedRollWhite = i;
                return i;
            }
        }

        lastUnusedRollWhite = 0;
        return 0;
    }

    private int firstUnusedRollBlack() {
        for (int i = lastUnusedRollBlack; i < amount / 2; ++i) {
            if (rollList.get(i).isUnused()) {
                lastUnusedRollBlack = i;
                return i;
            }
        }

        for (int i = amount / 2; i < lastUnusedRollBlack; ++i) {
            if (rollList.get(i).isUnused()) {
                lastUnusedRollBlack = i;
                return i;
            }
        }

        lastUnusedRollBlack = amount / 2;
        return amount / 2;
    }

    private void respawnRoll(Roll roll, int trackID) {
        roll.setTrackID(trackID);
        roll.setColorID(trackID + 100);
        roll.setOffsetY(0.0f);
        roll.setScaleY(1.0f);
        roll.setUpdatingScaleY(true);
        roll.setUnused(false);
    }

    public void stopUpdatingScaleY(int trackID) {
        for (Roll roll : rollList) {
            if (roll.getTrackID() == trackID && roll.isUpdatingScaleY()) {
                roll.setUpdatingScaleY(false);
                roll.setColorID(trackID);
            }
        }
    }

    public List<Roll> getRollList() {
        return rollList;
    }

}