package pianoroll.component;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.GLBuffers;
import pianoroll.component.renderer.BackgroundRenderer;
import pianoroll.component.renderer.ParticleRenderer;
import pianoroll.component.renderer.PianoRenderer;
import pianoroll.component.renderer.RollRenderer;
import pianoroll.entity.ColumnRow;
import pianoroll.entity.Key;
import pianoroll.entity.Particle;
import pianoroll.entity.Roll;
import uno.glsl.Program;

import java.nio.FloatBuffer;

import static com.jogamp.opengl.GL2ES3.*;
import static glm.GlmKt.glm;
import static uno.buffer.UtilKt.destroyBuffers;

public class PianorollCanvas implements GLEventListener {

    // 单例
    private static final PianorollCanvas instance = new PianorollCanvas();

    // 获取单例
    public static PianorollCanvas GetInstance() {
        return instance;
    }

    private static GLCanvas glcanvas;

    private final FloatBuffer clearColor;
    private final FloatBuffer clearDepth;
    private final FloatBuffer matBuffer;

    private long timeLastFrame;

    private final RollRenderer rollRenderer;
    private final PianoRenderer pianoRenderer;
    private final ParticleRenderer particleRenderer;
    private final BackgroundRenderer backgroundRenderer;

    private Program pianorollProgram;
    private Program particleProgram;

    private PianorollCanvas() {
        clearColor = GLBuffers.newDirectFloatBuffer(4);
        clearDepth = GLBuffers.newDirectFloatBuffer(1);
        matBuffer = GLBuffers.newDirectFloatBuffer(16);

        timeLastFrame = 0;

        rollRenderer = new RollRenderer();
        pianoRenderer = new PianoRenderer();
        backgroundRenderer = new BackgroundRenderer();
        particleRenderer = new ParticleRenderer();
    }

    public static void Setup() {
        GLProfile glProfile = GLProfile.get(GLProfile.GL3);
        GLCapabilities glCapabilities = new GLCapabilities(glProfile);

        glcanvas = new GLCanvas(glCapabilities);
        glcanvas.setBounds(0,0,1150,800);
        glcanvas.addGLEventListener(instance);

        Animator animator = new Animator(glcanvas);
        animator.start();
    }

    public void update(float deltaTime) {
        Pianoroll.GetInstance().getRollController().updateRolls(deltaTime);
        Pianoroll.GetInstance().getPianoController().updateKeys();
        Pianoroll.GetInstance().getBackgroundController().updateBackground(deltaTime);
        Pianoroll.GetInstance().getParticleController().updateParticles(deltaTime);
    }

    public void render(GL3 gl) {
        rollRenderer.drawRolls(gl, pianorollProgram);
        pianoRenderer.drawKeys(gl, pianorollProgram);
        backgroundRenderer.drawColumnRows(gl,pianorollProgram);
        particleRenderer.drawParticles(gl, particleProgram);
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();

        timeLastFrame = System.currentTimeMillis();

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

        glcanvas.addKeyListener(new InputProcessor());

        gl.glEnable(GL_DEPTH_TEST);
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        gl.glEnable(GL_MULTISAMPLE);

        gl.setSwapInterval(1);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();

        long timeCurrentFrame = System.currentTimeMillis();
        float deltaTime = (timeCurrentFrame - timeLastFrame) / 1_000f;
        timeLastFrame = timeCurrentFrame;

        update(deltaTime);

        gl.glClearBufferfv(GL_COLOR, 0, clearColor.put(0, .09f).put(1, .11f).put(2, .13f).put(3, 1f));
        gl.glClearBufferfv(GL_DEPTH, 0, clearDepth.put(0, 1f));

        render(gl);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL3 gl = drawable.getGL().getGL3();

        float ratio = (float) glcanvas.getSize().width / (float) glcanvas.getSize().height;
        glm.ortho(-ratio, ratio, -1.0f, 1.0f, -1f, 1f).scale(0.02365f).to(matBuffer);

        gl.glUseProgram(pianorollProgram.name);
        gl.glUniformMatrix4fv(pianorollProgram.get("proj"), 1, false, matBuffer);
        gl.glUseProgram(particleProgram.name);
        gl.glUniformMatrix4fv(particleProgram.get("proj"), 1, false, matBuffer);

        gl.glViewport(x, y, width, height);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();

        Pianoroll pianoroll= Pianoroll.GetInstance();

        gl.glDeleteProgram(pianorollProgram.name);
        gl.glDeleteProgram(particleProgram.name);

        for (Key key : pianoroll.getPianoController().getKeyList())
            gl.glDeleteVertexArrays(1, key.getVao());

        for (Roll roll : pianoroll.getRollController().getRollList())
            gl.glDeleteVertexArrays(1, roll.getVao());

        for (Particle particle : pianoroll.getParticleController().getParticleList())
            gl.glDeleteVertexArrays(1, particle.getVao());

        for(ColumnRow column:pianoroll.getBackgroundController().getColumnList())
            gl.glDeleteVertexArrays(1, column.getVao());

        for(ColumnRow row:pianoroll.getBackgroundController().getRowList())
            gl.glDeleteVertexArrays(1, row.getVao());

        destroyBuffers(matBuffer, clearColor, clearDepth);
    }

    public static GLCanvas GetGlcanvas() {
        return glcanvas;
    }

}
