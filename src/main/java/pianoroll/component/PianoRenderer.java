package pianoroll.component;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.GLBuffers;
import glm.vec._2.Vec2;
import midi.component.MidiPlayer;
import pianoroll.entity.Key;
import pianoroll.entity.KeyBlack;
import pianoroll.entity.KeyWhite;
import pianoroll.util.Semantic;
import uno.glsl.Program;

import javax.sound.midi.ShortMessage;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static com.jogamp.opengl.GL.*;
import static uno.buffer.UtilKt.destroyBuffers;

public class PianoRenderer {

    private final List<Key> keyList;

    private int pitchOffset;

    public PianoRenderer() {

        pitchOffset = 0;

        keyList = new ArrayList<>();

        try {
            //todo sustain
            ShortMessage shortMessage = new ShortMessage(176, 0, 64, 127);
            MidiPlayer.GetInstance().getSynthesizer().getReceiver().send(shortMessage, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init(GL3 gl) {
        IntBuffer buffer = GLBuffers.newDirectIntBuffer(2);

        FloatBuffer vertexBufferKeyWhite = GLBuffers.newDirectFloatBuffer(KeyWhite.GetVertexData());
        FloatBuffer vertexBufferKeyBlack = GLBuffers.newDirectFloatBuffer(KeyBlack.GetVertexData());

        gl.glGenBuffers(2, buffer);

        gl.glBindBuffer(GL_ARRAY_BUFFER, buffer.get(Semantic.Buffer.VERTEX_KEYWHITE));
        gl.glBufferData(GL_ARRAY_BUFFER, vertexBufferKeyWhite.capacity() * Float.BYTES, vertexBufferKeyWhite, GL_STATIC_DRAW);
        gl.glBindBuffer(GL_ARRAY_BUFFER, 0);

        gl.glBindBuffer(GL_ARRAY_BUFFER, buffer.get(Semantic.Buffer.VERTEX_KEYBLACK));
        gl.glBufferData(GL_ARRAY_BUFFER, vertexBufferKeyBlack.capacity() * Float.BYTES, vertexBufferKeyBlack, GL_STATIC_DRAW);
        gl.glBindBuffer(GL_ARRAY_BUFFER, 0);

        destroyBuffers(vertexBufferKeyWhite, vertexBufferKeyBlack);

        for (int trackID = 0; trackID < Semantic.Piano.KEY_MAX; ++trackID) {
            Key key;
            int vbo;

            if (Key.IsWhite(trackID)) {
                key = new KeyWhite(trackID);
                vbo = buffer.get(Semantic.Buffer.VERTEX_KEYWHITE);
            } else {
                key = new KeyBlack(trackID);
                vbo = buffer.get(Semantic.Buffer.VERTEX_KEYBLACK);
            }

            key.setVbo(vbo);
            keyList.add(key);
        }

        for (Key key : keyList) {
            gl.glGenVertexArrays(1, key.getVao());

            gl.glBindVertexArray(key.getVao().get(0));
            {
                gl.glBindBuffer(GL_ARRAY_BUFFER, key.getVbo());
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

    public void drawKeys(GL3 gl, Program program) {
        gl.glUseProgram(program.name);

        gl.glUniform1f(program.get("scaleY"), 1f);
        gl.glUniform1f(program.get("offsetY"), 0);

        for (Key key : keyList) {
            gl.glBindVertexArray(key.getVao().get(0));
            gl.glUniform1i(program.get("trackID"), key.getTrackID());
            gl.glUniform1i(program.get("colorID"), key.getColorID());
            gl.glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
        }

        gl.glBindVertexArray(0);
    }

    public void addHalfPitch() {
        pitchOffset++;
    }

    public void reduceHalfPitch() {
        pitchOffset--;
    }

    public List<Key> getKeyList() {
        return keyList;
    }

    public int getPitchOffset() {
        return pitchOffset;
    }

}