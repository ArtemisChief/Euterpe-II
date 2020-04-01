package pianoroll.component;

import com.jogamp.opengl.GL3;
import midi.component.MidiParser;
import pianoroll.entity.Key;
import pianoroll.entity.Particle;
import pianoroll.entity.Roll;
import pianoroll.util.Semantic;
import uno.glsl.Program;

import java.io.File;
import java.nio.FloatBuffer;

public class GraphicEngine {

    private PianoRenderer pianoRenderer;
    private RollRenderer rollRenderer;
    private ParticleRenderer particleRenderer;
    private MidiParser midiParser;

    private Program pianorollProgram;
    private Program particleProgram;

    public GraphicEngine() {
        pianoRenderer = new PianoRenderer();
        rollRenderer = new RollRenderer();
        particleRenderer = new ParticleRenderer();
        midiParser =new MidiParser();
        midiParser.convert(new File("River Flows In You.mid"));
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
        for (Roll roll : rollRenderer.getRollList())
            roll.update(deltaTime * Semantic.Roll.SPEED);

        for (Particle particle : particleRenderer.getParticleList())
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

        for (Key key : pianoRenderer.getKeyList())
            gl.glDeleteVertexArrays(1, key.getVao());

        for(Roll roll:rollRenderer.getRollList())
            gl.glDeleteVertexArrays(1, roll.getVao());

        for(Particle particle:particleRenderer.getParticleList())
            gl.glDeleteVertexArrays(1, particle.getVao());
    }

    public PianoRenderer getPianoRenderer() {
        return pianoRenderer;
    }

    public RollRenderer getRollRenderer() {
        return rollRenderer;
    }

    public ParticleRenderer getParticleRenderer() {
        return particleRenderer;
    }

}
