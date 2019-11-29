package pianoroll.component;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.GLBuffers;
import glm.vec._2.Vec2;
import pianoroll.entity.GraphicElement;
import pianoroll.entity.Key;
import pianoroll.entity.KeyBlack;
import pianoroll.entity.KeyWhite;
import pianoroll.util.Semantic;
import uno.glsl.Program;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.LinkedList;
import java.util.Queue;

import static com.jogamp.opengl.GL.*;
import static com.jogamp.opengl.GL2ES3.*;
import static glm.GlmKt.glm;
import static uno.buffer.UtilKt.destroyBuffers;
import static uno.gl.GlErrorKt.checkError;

public class PianorollCanvas implements GLEventListener {

    // 单例
    private static PianorollCanvas instance = new PianorollCanvas();

    // 获取单例
    public static PianorollCanvas GetInstance() {
        return instance;
    }

    private static GLCanvas glcanvas;
    private static Animator animator;

    private static IntBuffer bufferName = GLBuffers.newDirectIntBuffer(Semantic.Buffer.MAX);

    private static Queue<GraphicElement> graphicElementQueue = new LinkedList<>();

    private static short[] elementData = new short[]{
            0, 1, 2,
            0, 2, 3
    };

    private Piano piano;
    private Roller roller;

    private FloatBuffer clearColor;
    private FloatBuffer clearDepth;
    private FloatBuffer matBuffer;

    private Program program;

    private long timeLastFrame;

    private PianorollCanvas() {
        clearColor = GLBuffers.newDirectFloatBuffer(4);
        clearDepth = GLBuffers.newDirectFloatBuffer(1);
        matBuffer = GLBuffers.newDirectFloatBuffer(16);
    }

    private void drawKeys(GL3 gl) {

        gl.glUniform1f(program.get("scaleY"), 1f);
        gl.glUniform1f(program.get("offsetY"), 0);

        for (Key key : piano.getKeyList()) {
            gl.glBindVertexArray(key.getVao().get(0));
            gl.glUniform1i(program.get("trackID"), key.getTrackID());
            gl.glUniform1i(program.get("colorID"), key.getColorID());
            gl.glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_SHORT, 0);
        }

        gl.glBindVertexArray(0);

    }

    private void drawRolls(GL3 gl, float deltaTime) {


    }

    public static void Setup() {
        GLProfile glProfile = GLProfile.get(GLProfile.GL3);
        GLCapabilities glCapabilities = new GLCapabilities(glProfile);

        glcanvas = new GLCanvas(glCapabilities);
        glcanvas.addGLEventListener(instance);

        animator = new Animator(glcanvas);
        animator.start();
    }

    private void initProgram(GL3 gl) {
        program = new Program(gl, getClass(), "shaders", "Euterpe.vert", "Euterpe.frag", "trackID", "colorID", "scaleY", "offsetY", "proj");
        checkError(gl, "initProgram");
    }

    private void initBuffers(GL3 gl) {
        FloatBuffer vertexBufferWhite = GLBuffers.newDirectFloatBuffer(KeyWhite.GetVertexData());
        FloatBuffer vertexBufferBlack = GLBuffers.newDirectFloatBuffer(KeyBlack.GetVertexData());
        ShortBuffer elementBuffer = GLBuffers.newDirectShortBuffer(elementData);

        gl.glGenBuffers(Semantic.Buffer.MAX, bufferName);

        gl.glBindBuffer(GL_ARRAY_BUFFER, bufferName.get(Semantic.Buffer.VERTEX_KEYWHITE));
        gl.glBufferData(GL_ARRAY_BUFFER, vertexBufferWhite.capacity() * Float.BYTES, vertexBufferWhite, GL_STATIC_DRAW);
        gl.glBindBuffer(GL_ARRAY_BUFFER, 0);

        gl.glBindBuffer(GL_ARRAY_BUFFER, bufferName.get(Semantic.Buffer.VERTEX_KEYBLACK));
        gl.glBufferData(GL_ARRAY_BUFFER, vertexBufferBlack.capacity() * Float.BYTES, vertexBufferBlack, GL_STATIC_DRAW);
        gl.glBindBuffer(GL_ARRAY_BUFFER, 0);

        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferName.get(Semantic.Buffer.ELEMENT));
        gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer.capacity() * Short.BYTES, elementBuffer, GL_STATIC_DRAW);
        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        destroyBuffers(vertexBufferWhite, vertexBufferBlack, elementBuffer);

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

            gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferName.get(Semantic.Buffer.ELEMENT));
        }
        gl.glBindVertexArray(0);

        checkError(gl, "initVao");
    }

    public static void OfferGraphicElementQueue(GraphicElement graphicElement) {
        graphicElementQueue.offer(graphicElement);
    }

    public static GLCanvas GetGlcanvas() {
        return glcanvas;
    }

    public static IntBuffer GetBufferName() {
        return bufferName;
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();

        initBuffers(gl);

        initProgram(gl);

        gl.glEnable(GL_DEPTH_TEST);

        gl.setSwapInterval(1);

        timeLastFrame = System.currentTimeMillis();

        piano=new Piano();
        roller=new Roller();
        glcanvas.addKeyListener(new InputProcessor(piano, roller));
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();

        while (!graphicElementQueue.isEmpty())
            initVertexArrays(gl);

        gl.glClearBufferfv(GL_COLOR, 0, clearColor.put(0, .266f).put(1, .266f).put(2, .266f).put(3, 1f));
        gl.glClearBufferfv(GL_DEPTH, 0, clearDepth.put(0, 1f));

        gl.glUseProgram(program.name);

        drawKeys(gl);

        long timeCurrentFrame = System.currentTimeMillis();
        float deltaTime = (float) (timeCurrentFrame - timeLastFrame) / 1_000f;
        timeLastFrame = timeCurrentFrame;

        drawRolls(gl, deltaTime);

        gl.glUseProgram(0);

        checkError(gl, "display");
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL3 gl = drawable.getGL().getGL3();

        float ratio = (float) glcanvas.getSize().width / (float) glcanvas.getSize().height;
        glm.ortho(-ratio, ratio, -1.0f, 1.0f, -1f, 1f).scale(0.0222f).to(matBuffer);

        gl.glUseProgram(program.name);
        gl.glUniformMatrix4fv(program.get("proj"), 1, false, matBuffer);
        gl.glUseProgram(0);

        gl.glViewport(x, y, width, height);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();

        gl.glDeleteProgram(program.name);
        for(Key key:piano.getKeyList())
        gl.glDeleteVertexArrays(1, key.getVao());
        gl.glDeleteBuffers(Semantic.Buffer.MAX, bufferName);

        destroyBuffers(bufferName, matBuffer, clearColor, clearDepth);
    }

}
