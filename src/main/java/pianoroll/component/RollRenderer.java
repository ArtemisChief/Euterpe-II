package pianoroll.component;

import com.jogamp.opengl.GL3;
import pianoroll.entity.*;
import pianoroll.util.Semantic;
import uno.glsl.Program;

import java.util.ArrayList;
import java.util.List;

import static com.jogamp.opengl.GL.GL_TRIANGLE_STRIP;

public class RollRenderer {

    private float speed = 30.0f;

    private int amount;

    private int lastUnusedRollWhite;
    private int lastUnusedRollBlack;

    private List<Roll> rollList;

    public RollRenderer() {
        amount = 200;

        lastUnusedRollWhite = 0;
        lastUnusedRollBlack = amount / 2;

        rollList = new ArrayList<>();

        for (int i = 0; i < amount / 2; ++i) {
            Roll roll = new RollWhite();

            roll.setVbo(Canvas.GetBufferName().get(Semantic.Buffer.VERTEX_ROLLWHITE));
            Canvas.OfferGraphicElementQueue(roll);
            rollList.add(roll);
        }

        for (int i = amount / 2; i < amount; ++i) {
            Roll roll = new RollBlack();

            roll.setVbo(Canvas.GetBufferName().get(Semantic.Buffer.VERTEX_ROLLBLACK));
            Canvas.OfferGraphicElementQueue(roll);
            rollList.add(roll);
        }
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
        for (int i = lastUnusedRollWhite; i < amount/2; ++i) {
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

    public float getSpeed() {
        return speed;
    }

    public List<Roll> getRollList() {
        return rollList;
    }

}
