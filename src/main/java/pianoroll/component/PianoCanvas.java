package pianoroll.component;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.GLBuffers;
import glm.mat.Mat4x4;
import glm.vec._2.Vec2;
import glm.vec._3.Vec3;
import pianoroll.entity.Key;
import pianoroll.entity.KeyBlack;
import pianoroll.entity.KeyWhite;
import uno.glsl.Program;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import static com.jogamp.opengl.GL.*;
import static com.jogamp.opengl.GL2ES2.GL_STREAM_DRAW;
import static com.jogamp.opengl.GL2ES3.*;
import static glm.GlmKt.glm;
import static uno.buffer.UtilKt.destroyBuffers;
import static uno.gl.GlErrorKt.checkError;

public class PianoCanvas implements GLEventListener {

    public static PianoCanvas instance = new PianoCanvas();

    public static PianoCanvas GetInstance() {
        return instance;
    }

    private static GLCanvas glcanvas;
    private static Animator animator;

    private static short[] elementData = {
            0, 1, 2,
            0, 2, 3
    };

    private interface Buffer {
        int VERTEX_KEYWHITE = 0;
        int VERTEX_KEYBLACK = 1;
//        int VERTEX_ROLLS = 2;
        int ELEMENT = 2;
        int MAX = 4;
    }

    private IntBuffer bufferName = GLBuffers.newDirectIntBuffer(Buffer.MAX);
    private IntBuffer vertexArrayName = GLBuffers.newDirectIntBuffer(88);

    private FloatBuffer clearColor = GLBuffers.newDirectFloatBuffer(4);
    private FloatBuffer clearDepth = GLBuffers.newDirectFloatBuffer(1);

    private FloatBuffer matBuffer = GLBuffers.newDirectFloatBuffer(16);

    private Program program;

    private PianoCanvas(){ }

    public static void Setup() {

        GLProfile glProfile = GLProfile.get(GLProfile.GL3);
        GLCapabilities glCapabilities = new GLCapabilities(glProfile);

        glcanvas = new GLCanvas(glCapabilities);
        glcanvas.addGLEventListener(instance);
        glcanvas.addKeyListener(new KeyListener() {

            List<Integer> keyDownList = new ArrayList<>();

            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (!keyDownList.contains(e.getKeyCode())) {
                    PianoKeys.GetInstance().pressKey(e);
                    keyDownList.add(e.getKeyCode());
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                PianoKeys.GetInstance().releasedKey(e);
                keyDownList.remove(keyDownList.indexOf(e.getKeyCode()));
            }
        });

        animator = new Animator(glcanvas);
        animator.start();
    }

    @Override
    public void init(GLAutoDrawable drawable) {

        GL3 gl = drawable.getGL().getGL3();

        initBuffers(gl);

        initVertexArray(gl);

        initProgram(gl);

        gl.glEnable(GL_DEPTH_TEST);

        gl.setSwapInterval(1);

    }

    private void initBuffers(GL3 gl) {

        FloatBuffer vertexBufferWhite = GLBuffers.newDirectFloatBuffer(KeyWhite.GetVertexData());
        FloatBuffer vertexBufferBlack = GLBuffers.newDirectFloatBuffer(KeyBlack.GetVertexData());
        ShortBuffer elementBuffer = GLBuffers.newDirectShortBuffer(elementData);

        gl.glGenBuffers(Buffer.MAX, bufferName);

        gl.glBindBuffer(GL_ARRAY_BUFFER, bufferName.get(Buffer.VERTEX_KEYWHITE));
        gl.glBufferData(GL_ARRAY_BUFFER, vertexBufferWhite.capacity() * Float.BYTES, vertexBufferWhite, GL_STATIC_DRAW);
        gl.glBindBuffer(GL_ARRAY_BUFFER, 0);

        gl.glBindBuffer(GL_ARRAY_BUFFER, bufferName.get(Buffer.VERTEX_KEYBLACK));
        gl.glBufferData(GL_ARRAY_BUFFER, vertexBufferBlack.capacity() * Float.BYTES, vertexBufferBlack, GL_STATIC_DRAW);
        gl.glBindBuffer(GL_ARRAY_BUFFER, 0);

        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferName.get(Buffer.ELEMENT));
        gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer.capacity() * Short.BYTES, elementBuffer, GL_STATIC_DRAW);
        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        destroyBuffers(vertexBufferWhite, vertexBufferBlack, elementBuffer);

        checkError(gl, "initBuffers");
    }

    private void initVertexArray(GL3 gl) {

        gl.glGenVertexArrays(88, vertexArrayName);

        for (int trackID = 0; trackID < 88; trackID++) {
            int buffer;
            if (PianoKeys.isWhiteKey(trackID))
                buffer = bufferName.get(Buffer.VERTEX_KEYWHITE);
            else
                buffer = bufferName.get(Buffer.VERTEX_KEYBLACK);

            gl.glBindVertexArray(vertexArrayName.get(trackID));
            {
                gl.glBindBuffer(GL_ARRAY_BUFFER, buffer);
                {
                    gl.glEnableVertexAttribArray(0);
                    gl.glVertexAttribPointer(0, Vec2.length, GL_FLOAT, false, Vec2.SIZE, 0);
                }
                gl.glBindBuffer(GL_ARRAY_BUFFER, 0);

                gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferName.get(Buffer.ELEMENT));
            }
            gl.glBindVertexArray(0);
        }

        checkError(gl, "initVao");

    }

    private void initProgram(GL3 gl) {

        program = new Program(gl, getClass(), "shaders", "Euterpe.vert", "Euterpe.frag", "trackID", "colorID", "model", "proj");

        checkError(gl, "initProgram");

    }

    @Override
    public void display(GLAutoDrawable drawable) {

        GL3 gl = drawable.getGL().getGL3();

        gl.glClearBufferfv(GL_COLOR, 0, clearColor.put(0, .266f).put(1, .266f).put(2, .266f).put(3, 1f));
        gl.glClearBufferfv(GL_DEPTH, 0, clearDepth.put(0, 1f));

        gl.glUseProgram(program.name);

        // model matrix
        {
            Mat4x4 model = new Mat4x4();
            model.to(matBuffer);

            gl.glUniformMatrix4fv(program.get("model"), 1, false, matBuffer);
        }

        for (Key key:PianoKeys.GetKeyList()) {
            gl.glBindVertexArray(vertexArrayName.get(key.getTrackID()));
            gl.glUniform1i(program.get("trackID"), key.getTrackID());
            gl.glUniform1i(program.get("colorID"), key.getColorID());
            gl.glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_SHORT, 0);
        }

        gl.glUseProgram(0);
        gl.glBindVertexArray(0);

        checkError(gl, "display");
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

        GL3 gl = drawable.getGL().getGL3();

        float ratio = (float)glcanvas.getSize().width / (float)glcanvas.getSize().height;
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
        gl.glDeleteVertexArrays(1, vertexArrayName);
        gl.glDeleteBuffers(Buffer.MAX, bufferName);

        destroyBuffers(vertexArrayName, bufferName, matBuffer, clearColor, clearDepth);
    }

    public static GLCanvas GetGlcanvas() {
        return glcanvas;
    }

}
