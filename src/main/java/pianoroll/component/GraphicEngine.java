package pianoroll.component;

import com.jogamp.opengl.GL3;
import pianoroll.component.renderer.BackgroundRenderer;
import pianoroll.component.renderer.ParticleRenderer;
import pianoroll.component.renderer.PianoRenderer;
import pianoroll.component.renderer.RollRenderer;
import pianoroll.entity.Key;
import pianoroll.entity.Particle;
import pianoroll.entity.Roll;
import uno.glsl.Program;

import java.nio.FloatBuffer;
import java.util.List;

public class GraphicEngine {

    private final PianoRoll pianoRoll;

    private final RollRenderer rollRenderer;
    private final PianoRenderer pianoRenderer;
    private final ParticleRenderer particleRenderer;
    private final BackgroundRenderer backgroundRenderer;

    private Program pianorollProgram;
    private Program particleProgram;

    public GraphicEngine(PianoRoll pianoRoll) {
        this.pianoRoll = pianoRoll;

        rollRenderer = new RollRenderer(pianoRoll.getRollController().getAmount(), pianoRoll.getRollController().getRollList());
        pianoRenderer = new PianoRenderer(pianoRoll.getPianoController().getKeyList());
        backgroundRenderer = new BackgroundRenderer(pianoRoll.getBackgroundController().getColumnList(),pianoRoll.getBackgroundController().getrowList());
        particleRenderer = new ParticleRenderer(pianoRoll.getParticleController().getAmount(), pianoRoll.getParticleController().getParticleList());
    }

    public void init(GL3 gl) {
        rollRenderer.init(gl);
        pianoRenderer.init(gl);
        backgroundRenderer.init(gl);
        particleRenderer.init(gl);

        pianorollProgram = new Program(gl, getClass(),
                "shaders", "Pianoroll.vert", "Pianoroll.frag",
                "trackID", "scaleY", "offsetY", "proj", "colorID");

        particleProgram = new Program(gl, getClass(),
                "shaders", "Particle.vert", "Particle.frag",
                "trackID", "offset", "scale", "degrees", "proj", "colorID", "life");
    }

    public void update(float deltaTime) {
        pianoRoll.getRollController().updateRolls(deltaTime);
        pianoRoll.getPianoController().updateKeys();
        pianoRoll.getBackgroundController().updateBackground(deltaTime);
        pianoRoll.getParticleController().updateParticles(deltaTime);
    }

    public void render(GL3 gl) {
        rollRenderer.drawRolls(gl, pianorollProgram);
        pianoRenderer.drawKeys(gl, pianorollProgram);
        backgroundRenderer.drawColumnRows(gl,pianorollProgram);
        particleRenderer.drawParticles(gl, particleProgram);
    }

    public void reshape(GL3 gl, FloatBuffer buffer) {
        gl.glUseProgram(pianorollProgram.name);
        gl.glUniformMatrix4fv(pianorollProgram.get("proj"), 1, false, buffer);
        gl.glUseProgram(particleProgram.name);
        gl.glUniformMatrix4fv(particleProgram.get("proj"), 1, false, buffer);
    }

    public void dispose(GL3 gl, List<Key> keyList, List<Roll> rollList, List<Particle> particleList) {
        gl.glDeleteProgram(pianorollProgram.name);
        gl.glDeleteProgram(particleProgram.name);

        for (Key key : keyList)
            gl.glDeleteVertexArrays(1, key.getVao());

        for (Roll roll : rollList)
            gl.glDeleteVertexArrays(1, roll.getVao());

        for (Particle particle : particleList)
            gl.glDeleteVertexArrays(1, particle.getVao());
    }

}
