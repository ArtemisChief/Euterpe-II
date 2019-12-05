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

    private Piano piano;
    private Roller roller;
    private ParticleGenerator particleGenerator;

    private FloatBuffer clearColor;
    private FloatBuffer clearDepth;
    private FloatBuffer matBuffer;

    private Program pianorollProgram;
    private Program particleProgram;

    private long timeLastFrame;
    private float deltaTime;

    private PianorollCanvas() {
        clearColor = GLBuffers.newDirectFloatBuffer(4);
        clearDepth = GLBuffers.newDirectFloatBuffer(1);
        matBuffer = GLBuffers.newDirectFloatBuffer(16);
    }

    private void drawKeys(GL3 gl) {
        gl.glUniform1f(pianorollProgram.get("scaleY"), 1f);
        gl.glUniform1f(pianorollProgram.get("offsetY"), 0);

        for (Key key : piano.getKeyList()) {
            gl.glBindVertexArray(key.getVao().get(0));
            gl.glUniform1i(pianorollProgram.get("trackID"), key.getTrackID());
            gl.glUniform1i(pianorollProgram.get("colorID"), key.getColorID());
            gl.glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
        }

        gl.glBindVertexArray(0);
    }

    private void drawRolls(GL3 gl) {

        for (Roll roll : roller.getRollList()) {
            if (roll.getOffsetY() - roll.getScaleY() > 80.0f) {
                glcanvas.invoke(false, drawable -> {
                    roller.getRollList().remove(roll);
                    return true;
                });
                gl.glDeleteVertexArrays(1,roll.getVao());
            } else {
                roll.update(deltaTime * roller.getSpeed());

                gl.glBindVertexArray(roll.getVao().get(0));
                gl.glUniform1i(pianorollProgram.get("trackID"), roll.getTrackID());
                gl.glUniform1i(pianorollProgram.get("colorID"), roll.getColorID());
                gl.glUniform1f(pianorollProgram.get("scaleY"), roll.getScaleY());
                gl.glUniform1f(pianorollProgram.get("offsetY"), roll.getOffsetY());
                gl.glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
            }
        }

        gl.glBindVertexArray(0);
    }

    private void drawParticles(GL3 gl) {
        if (!particleGenerator.getGenerateParticleTrackList().isEmpty()) {
            for (int trackID : particleGenerator.getGenerateParticleTrackList()) {
                particleGenerator.newParticle(trackID);
            }
        }

        for (Particle particle : particleGenerator.getParticleList()) {
            particle.update(deltaTime);

            if (particle.getLife() > 0.0f) {
                gl.glBindVertexArray(particle.getVao().get(0));
                gl.glUniform1i(particleProgram.get("trackID"), particle.getTrackID());
                gl.glUniform1i(particleProgram.get("colorID"), particle.getColorID());
                gl.glUniform2f(particleProgram.get("offset"), particle.getOffsetX(), particle.getOffsetY());
                gl.glUniform1f(particleProgram.get("scale"), particle.getScale());
                gl.glUniform1f(particleProgram.get("degrees"), particle.getDegrees());
                gl.glUniform1f(particleProgram.get("life"), particle.getLife());
                gl.glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
            }
        }

        gl.glBindVertexArray(0);
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
        pianorollProgram = new Program(gl, getClass(), "shaders", "Pianoroll.vert", "Pianoroll.frag", "trackID", "scaleY", "offsetY", "proj", "colorID");
        particleProgram = new Program(gl, getClass(), "shaders", "Particle.vert", "Particle.frag", "trackID", "offset", "scale", "degrees", "proj", "colorID", "life");
        checkError(gl, "initProgram");
    }

    private void initBuffers(GL3 gl) {
        FloatBuffer vertexBufferKeyWhite = GLBuffers.newDirectFloatBuffer(KeyWhite.GetVertexData());
        FloatBuffer vertexBufferKeyBlack = GLBuffers.newDirectFloatBuffer(KeyBlack.GetVertexData());
        FloatBuffer vertexBufferRollWhite = GLBuffers.newDirectFloatBuffer(RollWhite.GetVertexData());
        FloatBuffer vertexBufferRollBlack = GLBuffers.newDirectFloatBuffer(RollBlack.GetVertexData());
        FloatBuffer vertexBufferParticle = GLBuffers.newDirectFloatBuffer(Particle.GetVertexData());

        gl.glGenBuffers(Semantic.Buffer.MAX, bufferName);

        gl.glBindBuffer(GL_ARRAY_BUFFER, bufferName.get(Semantic.Buffer.VERTEX_KEYWHITE));
        gl.glBufferData(GL_ARRAY_BUFFER, vertexBufferKeyWhite.capacity() * Float.BYTES, vertexBufferKeyWhite, GL_STATIC_DRAW);
        gl.glBindBuffer(GL_ARRAY_BUFFER, 0);

        gl.glBindBuffer(GL_ARRAY_BUFFER, bufferName.get(Semantic.Buffer.VERTEX_KEYBLACK));
        gl.glBufferData(GL_ARRAY_BUFFER, vertexBufferKeyBlack.capacity() * Float.BYTES, vertexBufferKeyBlack, GL_STATIC_DRAW);
        gl.glBindBuffer(GL_ARRAY_BUFFER, 0);

        gl.glBindBuffer(GL_ARRAY_BUFFER, bufferName.get(Semantic.Buffer.VERTEX_ROLLWHITE));
        gl.glBufferData(GL_ARRAY_BUFFER, vertexBufferRollWhite.capacity() * Float.BYTES, vertexBufferRollWhite, GL_STATIC_DRAW);
        gl.glBindBuffer(GL_ARRAY_BUFFER, 0);

        gl.glBindBuffer(GL_ARRAY_BUFFER, bufferName.get(Semantic.Buffer.VERTEX_ROLLBLACK));
        gl.glBufferData(GL_ARRAY_BUFFER, vertexBufferRollBlack.capacity() * Float.BYTES, vertexBufferRollBlack, GL_STATIC_DRAW);
        gl.glBindBuffer(GL_ARRAY_BUFFER, 0);

        gl.glBindBuffer(GL_ARRAY_BUFFER, bufferName.get(Semantic.Buffer.PARTICLE));
        gl.glBufferData(GL_ARRAY_BUFFER, vertexBufferParticle.capacity() * Float.BYTES, vertexBufferParticle, GL_STATIC_DRAW);
        gl.glBindBuffer(GL_ARRAY_BUFFER, 0);

        destroyBuffers(vertexBufferKeyWhite, vertexBufferKeyBlack, vertexBufferRollWhite, vertexBufferRollBlack, vertexBufferParticle);

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
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);
        gl.glEnable(GL_MULTISAMPLE);

        gl.setSwapInterval(1);

        timeLastFrame = System.currentTimeMillis();

        piano = new Piano();
        roller = new Roller();
        particleGenerator = new ParticleGenerator();
        glcanvas.addKeyListener(new InputProcessor(piano, roller, particleGenerator));
    }

    @Override
    public void display(GLAutoDrawable drawable) {

        long timeCurrentFrame = System.currentTimeMillis();
        deltaTime = (float) (timeCurrentFrame - timeLastFrame) / 1_000f;
        timeLastFrame = timeCurrentFrame;

        GL3 gl = drawable.getGL().getGL3();

        while (!graphicElementQueue.isEmpty())
            initVertexArrays(gl);

        gl.glClearBufferfv(GL_COLOR, 0, clearColor.put(0, .266f).put(1, .266f).put(2, .266f).put(3, 1f));
        gl.glClearBufferfv(GL_DEPTH, 0, clearDepth.put(0, 1f));

        gl.glUseProgram(pianorollProgram.name);

        drawKeys(gl);

        drawRolls(gl);

        gl.glUseProgram(particleProgram.name);

        drawParticles(gl);

        gl.glUseProgram(0);

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
        for(Key key:piano.getKeyList())
        gl.glDeleteVertexArrays(1, key.getVao());
        gl.glDeleteBuffers(Semantic.Buffer.MAX, bufferName);

        destroyBuffers(bufferName, matBuffer, clearColor, clearDepth);
    }

}
