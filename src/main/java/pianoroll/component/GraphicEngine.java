package pianoroll.component;

import com.jogamp.opengl.GL3;
import pianoroll.entity.Key;
import pianoroll.entity.Particle;
import pianoroll.entity.Roll;
import pianoroll.util.Semantic;
import uno.glsl.Program;

import java.nio.FloatBuffer;

public class GraphicEngine {

    private PianoManager pianoManager;
    private PianoRenderer pianoRenderer;

    private RollManager rollManager;
    private RollRenderer rollRenderer;

    private ParticleManager particleManager;
    private ParticleRenderer particleRenderer;

    private Program pianorollProgram;
    private Program particleProgram;

    public GraphicEngine() {
        pianoManager = new PianoManager();
        pianoRenderer = new PianoRenderer(pianoManager.getKeyList());

        rollManager = new RollManager();
        rollRenderer = new RollRenderer();

        particleManager = new ParticleManager();
        particleRenderer = new ParticleRenderer(particleManager.getAmount(), particleManager.getParticleList());
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

        particleManager.respawnParticle();

        for (Roll roll : rollRenderer.getRollList())
            roll.update(deltaTime * Semantic.Roll.SPEED);

        for (Particle particle : particleManager.getParticleList())
            particle.update(deltaTime);
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

    public void dispose(GL3 gl) {
        gl.glDeleteProgram(pianorollProgram.name);
        gl.glDeleteProgram(particleProgram.name);

        for (Key key : pianoManager.getKeyList())
            gl.glDeleteVertexArrays(1, key.getVao());

        for (Roll roll : rollRenderer.getRollList())
            gl.glDeleteVertexArrays(1, roll.getVao());

        for (Particle particle : particleManager.getParticleList())
            gl.glDeleteVertexArrays(1, particle.getVao());
    }

    public PianoManager getPianoManager() {
        return pianoManager;
    }

    public RollManager getRollManager() {
        return rollManager;
    }

    public RollRenderer getRollRenderer() {
        return rollRenderer;
    }

    public ParticleManager getParticleManager() {
        return particleManager;
    }

}
