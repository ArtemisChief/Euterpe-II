package pianoroll.component;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.GLBuffers;
import pianoroll.component.inputprocessor.KeyboardProcessor;
import pianoroll.component.inputprocessor.MouseProcessor;
import pianoroll.component.renderer.BackgroundRenderer;
import pianoroll.component.renderer.ParticleRenderer;
import pianoroll.component.renderer.PianoRenderer;
import pianoroll.component.renderer.RollRenderer;
import pianoroll.util.Semantic;
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

    private volatile double timeLastFrame;

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

        timeLastFrame =  0;

        rollRenderer = new RollRenderer();
        pianoRenderer = new PianoRenderer();
        backgroundRenderer = new BackgroundRenderer();
        particleRenderer = new ParticleRenderer();
    }

    public static void Setup() {
        GLProfile glProfile = GLProfile.get(GLProfile.GL3);
        GLCapabilities glCapabilities = new GLCapabilities(glProfile);

        glcanvas = new GLCanvas(glCapabilities);
        glcanvas.setBounds(0, 0, Semantic.Canvas.WIDTH, Semantic.Canvas.HEIGHT);
        glcanvas.addGLEventListener(instance);

        Animator animator = new Animator(glcanvas);
        animator.start();
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();

        timeLastFrame =  com.jogamp.common.os.Platform.currentTimeMicros();

        rollRenderer.init(gl);
        pianoRenderer.init(gl);
        backgroundRenderer.init(gl);
        particleRenderer.init(gl);

        pianorollProgram = new Program(gl, getClass(),
                "/shaders", "Pianoroll.vert", "Pianoroll.frag",
                "trackID", "scaleY", "offsetY", "proj", "posZ", "colorID");

        particleProgram = new Program(gl, getClass(),
                "/shaders", "Particle.vert", "Particle.frag",
                "trackID", "offset", "scale", "degrees", "proj", "colorID", "life");

        KeyboardProcessor keyboardProcessor = new KeyboardProcessor();
        MouseProcessor mouseProcessor = new MouseProcessor();

        glcanvas.addKeyListener(keyboardProcessor);
        glcanvas.addMouseListener(mouseProcessor);
        glcanvas.addMouseMotionListener(mouseProcessor);

        gl.glEnable(GL_DEPTH_TEST);
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        gl.glEnable(GL_MULTISAMPLE);

        gl.setSwapInterval(1);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();

        double timeCurrentFrame = com.jogamp.common.os.Platform.currentTimeMicros();
        float deltaTime = (float) (timeCurrentFrame - timeLastFrame) / 1_000_000f;
        timeLastFrame = timeCurrentFrame;

        Pianoroll.GetInstance().update(deltaTime);

        rollRenderer.bindBuffer(gl);
        backgroundRenderer.bindBuffer(gl);

        gl.glClearBufferfv(GL_COLOR, 0, clearColor.put(0, .09f).put(1, .11f).put(2, .13f).put(3, 1f));
        gl.glClearBufferfv(GL_DEPTH, 0, clearDepth.put(0, 1f));

        rollRenderer.drawRolls(gl, pianorollProgram);
        pianoRenderer.drawKeys(gl, pianorollProgram);
        backgroundRenderer.drawColumnRows(gl, pianorollProgram);
        particleRenderer.drawParticles(gl, particleProgram);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL3 gl = drawable.getGL().getGL3();

        glm.ortho(-Semantic.Canvas.RATIO, Semantic.Canvas.RATIO, -1.0f, 1.0f, -1f, 1f).scale(Semantic.Canvas.SCALE_FACTOR).to(matBuffer);

        gl.glUseProgram(pianorollProgram.name);
        gl.glUniformMatrix4fv(pianorollProgram.get("proj"), 1, false, matBuffer);
        gl.glUseProgram(particleProgram.name);
        gl.glUniformMatrix4fv(particleProgram.get("proj"), 1, false, matBuffer);

        gl.glViewport(x, y, width, height);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();

        gl.glDeleteProgram(pianorollProgram.name);
        gl.glDeleteProgram(particleProgram.name);

        backgroundRenderer.dispose(gl);
        particleRenderer.dispose(gl);
        pianoRenderer.dispose(gl);
        rollRenderer.dispose(gl);

        destroyBuffers(matBuffer, clearColor, clearDepth);
    }

    public static GLCanvas GetGlcanvas() {
        return glcanvas;
    }

    public RollRenderer getRollRenderer() {
        return rollRenderer;
    }

    public BackgroundRenderer getBackgroundRenderer() {
        return backgroundRenderer;
    }
}
