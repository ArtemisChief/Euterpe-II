package pianoroll.component;

import com.jogamp.opengl.GL3;
import midi.component.MidiPlayer;
import pianoroll.entity.Key;
import pianoroll.entity.KeyBlack;
import pianoroll.entity.KeyWhite;
import pianoroll.util.Semantic;
import uno.glsl.Program;

import javax.sound.midi.ShortMessage;
import java.util.ArrayList;
import java.util.List;

import static com.jogamp.opengl.GL.GL_TRIANGLE_STRIP;

public class PianoRenderer {

    private final List<Key> keyList;

    private int pitchOffset;

    public PianoRenderer() {

        pitchOffset = 0;

        keyList = new ArrayList<>();

        Key key;
        int vbo;

        for (int trackID = 0; trackID < Semantic.Piano.KEY_MAX; ++trackID) {
            if (Key.IsWhite(trackID)) {
                key = new KeyWhite(trackID);
                vbo = Canvas.GetBufferName().get(Semantic.Buffer.VERTEX_KEYWHITE);
            } else {
                key = new KeyBlack(trackID);
                vbo = Canvas.GetBufferName().get(Semantic.Buffer.VERTEX_KEYBLACK);
            }

            key.setVbo(vbo);
            Canvas.getGraphicElementQueue().offer(key);
            keyList.add(key);
        }

        try {
            //todo sustain
            ShortMessage shortMessage = new ShortMessage(176, 0, 64, 127);
            MidiPlayer.GetInstance().getSynthesizer().getReceiver().send(shortMessage, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public void addHalfPitch(){
        pitchOffset++;
    }

    public void reduceHalfPitch(){
        pitchOffset--;
    }

    public List<Key> getKeyList() {
        return keyList;
    }

    public int getPitchOffset() {
        return pitchOffset;
    }

}
