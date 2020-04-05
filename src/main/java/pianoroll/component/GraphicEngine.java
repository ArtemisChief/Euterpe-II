package pianoroll.component;

import com.jogamp.opengl.GL3;
import pianoroll.component.controller.ParticleController;
import pianoroll.component.controller.PianoController;
import pianoroll.component.controller.RollController;
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

    private final PianoRenderer pianoRenderer;

    private final RollRenderer rollRenderer;

    private final ParticleRenderer particleRenderer;

    private Program pianorollProgram;
    private Program particleProgram;

    public GraphicEngine(PianoRoll pianoRoll) {
        this.pianoRoll=pianoRoll;

        pianoRenderer = new PianoRenderer(pianoRoll.getPianoController().getKeyList());

        rollRenderer = new RollRenderer(pianoRoll.getRollController().getAmount(), pianoRoll.getRollController().getRollList());

        particleRenderer = new ParticleRenderer(pianoRoll.getParticleController().getAmount(), pianoRoll.getParticleController().getParticleList());
    }

    public void init(GL3 gl) {
        pianoRenderer.init(gl);
        rollRenderer.init(gl);
        particleRenderer.init(gl);

        pianorollProgram = new Program(gl, getClass(),
                "shaders", "Pianoroll.vert", "Pianoroll.frag",
                "trackID", "scaleY", "offsetY", "proj", "colorID");

        particleProgram = new Program(gl, getClass(),
                "shaders", "Particle.vert", "Particle.frag",
                "trackID", "offset", "scale", "degrees", "proj", "colorID", "life");
    }

    public void update(float deltaTime) {

        pianoRoll.getParticleController().updateParticles(deltaTime);

        pianoRoll.getRollController().updateRolls(deltaTime);

    }

    public void render(GL3 gl) {
        pianoRenderer.drawKeys(gl, pianorollProgram);
        rollRenderer.drawRolls(gl, pianorollProgram);
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
