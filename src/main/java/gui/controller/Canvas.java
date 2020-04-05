package gui.controller;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.GLBuffers;
import pianoroll.component.GraphicEngine;
import pianoroll.component.InputProcessor;
import pianoroll.component.PianoRoll;

import java.nio.FloatBuffer;

import static com.jogamp.opengl.GL2ES3.*;
import static glm.GlmKt.glm;
import static uno.buffer.UtilKt.destroyBuffers;

public class Canvas implements GLEventListener {

    // 单例
    private static final Canvas instance = new Canvas();

    // 获取单例
    public static Canvas GetInstance() {
        return instance;
    }

    private static GLCanvas glcanvas;

    private final FloatBuffer clearColor;
    private final FloatBuffer clearDepth;
    private final FloatBuffer matBuffer;

    private long timeLastFrame;

    private PianoRoll pianoRoll;
    private GraphicEngine graphicEngine;

    private Canvas() {
        clearColor = GLBuffers.newDirectFloatBuffer(4);
        clearDepth = GLBuffers.newDirectFloatBuffer(1);
        matBuffer = GLBuffers.newDirectFloatBuffer(16);
    }

    public static void Setup() {
        GLProfile glProfile = GLProfile.get(GLProfile.GL3);
        GLCapabilities glCapabilities = new GLCapabilities(glProfile);

        glcanvas = new GLCanvas(glCapabilities);
        glcanvas.addGLEventListener(instance);

        Animator animator = new Animator(glcanvas);
        animator.start();
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();

        timeLastFrame = System.currentTimeMillis();

        pianoRoll = new PianoRoll();
        graphicEngine = pianoRoll.getGraphicEngine();
        graphicEngine.init(gl);

        glcanvas.addKeyListener(pianoRoll.getInputProcessor());

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
        float deltaTime = (float) (timeCurrentFrame - timeLastFrame) / 1_000f;
        timeLastFrame = timeCurrentFrame;

        graphicEngine.update(deltaTime);

        gl.glClearBufferfv(GL_COLOR, 0, clearColor.put(0, .266f).put(1, .266f).put(2, .266f).put(3, 1f));
        gl.glClearBufferfv(GL_DEPTH, 0, clearDepth.put(0, 1f));

        graphicEngine.render(gl);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL3 gl = drawable.getGL().getGL3();

        float ratio = (float) glcanvas.getSize().width / (float) glcanvas.getSize().height;
        glm.ortho(-ratio, ratio, -1.0f, 1.0f, -1f, 1f).scale(0.0222f).to(matBuffer);

        graphicEngine.reshape(gl, matBuffer);

        gl.glViewport(x, y, width, height);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();

        graphicEngine.dispose(gl, pianoRoll.getPianoController().getKeyList(), pianoRoll.getRollController().getRollList(), pianoRoll.getParticleController().getParticleList());

        destroyBuffers(matBuffer, clearColor, clearDepth);
    }

    public static GLCanvas GetGlcanvas() {
        return glcanvas;
    }

}
