package pianoroll.component;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.GLBuffers;
import glm.vec._2.Vec2;
import pianoroll.entity.*;
import pianoroll.util.Semantic;
import uno.glsl.Program;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;
import java.util.Queue;

import static com.jogamp.opengl.GL2ES3.*;
import static glm.GlmKt.glm;
import static uno.buffer.UtilKt.destroyBuffers;
import static uno.gl.GlErrorKt.checkError;

public class Canvas implements GLEventListener {

    // 单例
    private static final Canvas instance = new Canvas();

    // 获取单例
    public static Canvas GetInstance() {
        return instance;
    }

    private static GLCanvas glcanvas;

    private static final IntBuffer bufferName = GLBuffers.newDirectIntBuffer(Semantic.Buffer.MAX);

    private static final Queue<GraphicElement> graphicElementQueue = new LinkedList<>();

    private PianoRenderer pianoRenderer;
    private RollRenderer rollRenderer;
    private ParticleRenderer particleRenderer;

    private final FloatBuffer clearColor;
    private final FloatBuffer clearDepth;
    private final FloatBuffer matBuffer;

    private Program pianorollProgram;
    private Program particleProgram;

    private long timeLastFrame;

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

    private void initProgram(GL3 gl) {
        pianorollProgram = new Program(gl, getClass(),
                "shaders", "Pianoroll.vert", "Pianoroll.frag",
                "trackID", "scaleY", "offsetY", "proj", "colorID");

        particleProgram = new Program(gl, getClass(),
                "shaders", "Particle.vert", "Particle.frag",
                "trackID", "offset", "scale", "degrees", "proj", "colorID", "life");

        checkError(gl, "initProgram");
    }

    private void initBuffers(GL3 gl) {
        FloatBuffer[] floatBuffers = {
                GLBuffers.newDirectFloatBuffer(KeyWhite.GetVertexData()),
                GLBuffers.newDirectFloatBuffer(KeyBlack.GetVertexData()),
                GLBuffers.newDirectFloatBuffer(RollWhite.GetVertexData()),
                GLBuffers.newDirectFloatBuffer(RollBlack.GetVertexData()),
                GLBuffers.newDirectFloatBuffer(Particle.GetVertexData())
        };

        gl.glGenBuffers(Semantic.Buffer.MAX, bufferName);

        for (int buffer = Semantic.Buffer.VERTEX_KEYWHITE; buffer < Semantic.Buffer.MAX; ++buffer) {
            gl.glBindBuffer(GL_ARRAY_BUFFER, bufferName.get(buffer));
            gl.glBufferData(GL_ARRAY_BUFFER, floatBuffers[buffer].capacity() * Float.BYTES, floatBuffers[buffer], GL_STATIC_DRAW);
            gl.glBindBuffer(GL_ARRAY_BUFFER, 0);

            destroyBuffers(floatBuffers[buffer]);
        }

        checkError(gl, "initBuffers");
    }

    private void initVertexArrays(GL3 gl) {
        GraphicElement graphicElement = graphicElementQueue.poll();

        gl.glGenVertexArrays(1,graphicElement.getVao());

        int vbo = graphicElement.getVbo();

        gl.glBindVertexArray(graphicElement.getVao().get(0));
        {
            gl.glBindBuffer(GL_ARRAY_BUFFER, vbo);
            {
                gl.glEnableVertexAttribArray(Semantic.Attr.POSITION);
                gl.glVertexAttribPointer(Semantic.Attr.POSITION, Vec2.length, GL_FLOAT, false, Vec2.SIZE, 0);
            }
            gl.glBindBuffer(GL_ARRAY_BUFFER, 0);
        }
        gl.glBindVertexArray(0);

        checkError(gl, "initVao");
    }

    private void update(GL3 gl, float deltaTime) {
        while (!graphicElementQueue.isEmpty())
            initVertexArrays(gl);

        for (Roll roll : rollRenderer.getRollList())
            roll.update(deltaTime * rollRenderer.getSpeed());

        for (Particle particle : particleRenderer.getParticleList())
            particle.update(deltaTime);
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();

        initBuffers(gl);

        initProgram(gl);

        gl.glEnable(GL_DEPTH_TEST);
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);
        gl.glEnable(GL_MULTISAMPLE);

        gl.setSwapInterval(1);

        timeLastFrame = System.currentTimeMillis();

        pianoRenderer = new PianoRenderer();
        rollRenderer = new RollRenderer();
        particleRenderer = new ParticleRenderer();

        glcanvas.addKeyListener(new InputProcessor(pianoRenderer, rollRenderer, particleRenderer));
    }

    @Override
    public void display(GLAutoDrawable drawable) {

        GL3 gl = drawable.getGL().getGL3();

        long timeCurrentFrame = System.currentTimeMillis();
        float deltaTime = (float) (timeCurrentFrame - timeLastFrame) / 1_000f;
        timeLastFrame = timeCurrentFrame;

        update(gl, deltaTime);

        gl.glClearBufferfv(GL_COLOR, 0, clearColor.put(0, .266f).put(1, .266f).put(2, .266f).put(3, 1f));
        gl.glClearBufferfv(GL_DEPTH, 0, clearDepth.put(0, 1f));

        pianoRenderer.drawKeys(gl, pianorollProgram);
        rollRenderer.drawRolls(gl, pianorollProgram);
        particleRenderer.drawParticles(gl, particleProgram);

        checkError(gl, "display");
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL3 gl = drawable.getGL().getGL3();

        float ratio = (float) glcanvas.getSize().width / (float) glcanvas.getSize().height;
        glm.ortho(-ratio, ratio, -1.0f, 1.0f, -1f, 1f).scale(0.0222f).to(matBuffer);

        gl.glUseProgram(pianorollProgram.name);
        gl.glUniformMatrix4fv(pianorollProgram.get("proj"), 1, false, matBuffer);
        gl.glUseProgram(particleProgram.name);
        gl.glUniformMatrix4fv(particleProgram.get("proj"), 1, false, matBuffer);
        gl.glUseProgram(0);

        gl.glViewport(x, y, width, height);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();

        gl.glDeleteProgram(pianorollProgram.name);
        gl.glDeleteProgram(particleProgram.name);
        for(Key key: pianoRenderer.getKeyList())
        gl.glDeleteVertexArrays(1, key.getVao());
        gl.glDeleteBuffers(Semantic.Buffer.MAX, bufferName);

        destroyBuffers(bufferName, matBuffer, clearColor, clearDepth);
    }

    public static GLCanvas GetGlcanvas() {
        return glcanvas;
    }

    public static IntBuffer GetBufferName() {
        return bufferName;
    }

    public static Queue<GraphicElement> getGraphicElementQueue() {
        return graphicElementQueue;
    }

}
