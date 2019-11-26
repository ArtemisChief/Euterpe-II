package pianoroll.component;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.GLBuffers;
import glm.mat.Mat4x4;
import glm.vec._2.Vec2;
import glm.vec._3.Vec3;
import pianoroll.entity.KeyBlack;
import pianoroll.entity.KeyWhite;
import uno.glsl.Program;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
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
        int COLOR_INSTANCE_KEYWHITE = 2;
        int COLOR_INSTANCE_KEYBLACK = 3;
//        int COLOR_INSTANCE_ROLLS =6;
        int POSITION_INSTANCE_KEYWHITE = 4;
        int POSITION_INSTANCE_KEYBLACK = 5;
//        int POSITION_INSTANCE_ROLLS = 5;
        int ELEMENT = 6;
        int GLOBAL_MATRICES = 7;
        int MAX = 8;
    }

    private interface VertexArray {
        int KEYWHITE = 0;
        int KEYBLACK = 1;
//        int ROLLS = 2;
        int MAX = 2;
    }

    private IntBuffer bufferName = GLBuffers.newDirectIntBuffer(Buffer.MAX);
    private IntBuffer vertexArrayName = GLBuffers.newDirectIntBuffer(VertexArray.MAX);

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

        List<java.nio.Buffer> bufferList = new ArrayList<>();
        bufferList.add(GLBuffers.newDirectFloatBuffer(KeyWhite.GetVertexData()));
        bufferList.add(GLBuffers.newDirectFloatBuffer(KeyBlack.GetVertexData()));
        bufferList.add(GLBuffers.newDirectFloatBuffer(PianoKeys.GetColorsWhite()));
        bufferList.add(GLBuffers.newDirectFloatBuffer(PianoKeys.GetColorsBlack()));
        bufferList.add(GLBuffers.newDirectFloatBuffer(PianoKeys.GetAnchorsWhite()));
        bufferList.add(GLBuffers.newDirectFloatBuffer(PianoKeys.GetAnchorsBlack()));
        bufferList.add(GLBuffers.newDirectShortBuffer(elementData));

        gl.glGenBuffers(Buffer.MAX, bufferName);

        for (int buffer = 0; buffer < Buffer.GLOBAL_MATRICES; buffer++) {
            gl.glBindBuffer(GL_ARRAY_BUFFER, bufferName.get(buffer));
            gl.glBufferData(GL_ARRAY_BUFFER, bufferList.get(buffer).capacity() * Float.BYTES, bufferList.get(buffer), GL_STATIC_DRAW);
            gl.glBindBuffer(GL_ARRAY_BUFFER, 0);

            destroyBuffers(bufferList.get(buffer));
        }

        gl.glBindBuffer(GL_UNIFORM_BUFFER, bufferName.get(Buffer.GLOBAL_MATRICES));
        gl.glBufferData(GL_UNIFORM_BUFFER, Mat4x4.SIZE * 2, null, GL_STREAM_DRAW);
        gl.glBindBuffer(GL_UNIFORM_BUFFER, 0);

        gl.glBindBufferBase(GL_UNIFORM_BUFFER, 4, bufferName.get(Buffer.GLOBAL_MATRICES));

        checkError(gl, "initBuffers");
    }

    private void initVertexArray(GL3 gl) {

        gl.glGenVertexArrays(VertexArray.MAX, vertexArrayName);

        for(int vertex=0;vertex<VertexArray.MAX;vertex++) {
            gl.glBindVertexArray(vertexArrayName.get(vertex));
            {
                gl.glBindBuffer(GL_ARRAY_BUFFER, bufferName.get(vertex));
                {
                    gl.glEnableVertexAttribArray(0);
                    gl.glVertexAttribPointer(0, Vec2.length, GL_FLOAT, false, Vec2.SIZE, 0);

                    gl.glEnableVertexAttribArray(1);
                    gl.glBindBuffer(GL_ARRAY_BUFFER, bufferName.get(vertex + 2));
                    gl.glVertexAttribPointer(1, Vec3.length, GL_FLOAT, false, Vec3.SIZE, 0);
                    gl.glBindBuffer(GL_ARRAY_BUFFER, 0);
                    gl.glVertexAttribDivisor(1, 1);

                    gl.glEnableVertexAttribArray(2);
                    gl.glBindBuffer(GL_ARRAY_BUFFER, bufferName.get(vertex + 4));
                    gl.glVertexAttribPointer(2, Vec2.length, GL_FLOAT, false, Vec2.SIZE, 0);
                    gl.glBindBuffer(GL_ARRAY_BUFFER, 0);
                    gl.glVertexAttribDivisor(2, 1);
                }
                gl.glBindBuffer(GL_ARRAY_BUFFER, 0);

                gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferName.get(Buffer.ELEMENT));
            }
            gl.glBindVertexArray(vertex);
        }

        checkError(gl, "initVao");
    }

    private void initProgram(GL3 gl) {

        program = new Program(gl, getClass(), "shaders", "Euterpe.vert", "Euterpe.frag", "model");

        int globalMatricesBI = gl.glGetUniformBlockIndex(program.name, "GlobalMatrices");

        if (globalMatricesBI == -1) {
            System.err.println("block index 'GlobalMatrices' not found!");
        }
        gl.glUniformBlockBinding(program.name, globalMatricesBI, 4);

        checkError(gl, "initProgram");
    }

    @Override
    public void display(GLAutoDrawable drawable) {

        GL3 gl = drawable.getGL().getGL3();

        // view matrix
        {
            Mat4x4 view = new Mat4x4();
            view
                    .scale(15.3f)
                    .to(matBuffer);

            gl.glBindBuffer(GL_UNIFORM_BUFFER, bufferName.get(Buffer.GLOBAL_MATRICES));
            gl.glBufferSubData(GL_UNIFORM_BUFFER, Mat4x4.SIZE, Mat4x4.SIZE, matBuffer);
            gl.glBindBuffer(GL_UNIFORM_BUFFER, 0);
        }

        gl.glClearBufferfv(GL_COLOR, 0, clearColor.put(0, .266f).put(1, .266f).put(2, .266f).put(3, 1f));
        gl.glClearBufferfv(GL_DEPTH, 0, clearDepth.put(0, 1f));

        gl.glUseProgram(program.name);

        // model matrix
        {
            Mat4x4 model = new Mat4x4();
            model.to(matBuffer);

            gl.glUniformMatrix4fv(program.get("model"), 1, false, matBuffer);
        }

        gl.glBindVertexArray(vertexArrayName.get(VertexArray.KEYBLACK));
        gl.glDrawElementsInstanced(GL_TRIANGLES, 6, GL_UNSIGNED_SHORT, 0,36);

        gl.glBindVertexArray(vertexArrayName.get(VertexArray.KEYWHITE));
        gl.glBindBuffer(GL_UNIFORM_BUFFER, bufferName.get(Buffer.COLOR_INSTANCE_KEYWHITE));
        for(int keyId:PianoKeys.GetPressingWhiteList())
            gl.glBufferSubData(GL_UNIFORM_BUFFER, keyId*Vec3.SIZE, Vec3.SIZE, GLBuffers.newDirectFloatBuffer(KeyWhite.GetDownColorData()));
        for(int keyId:PianoKeys.GetReleasingWhiteList()) {
            gl.glBufferSubData(GL_UNIFORM_BUFFER, keyId * Vec3.SIZE, Vec3.SIZE, GLBuffers.newDirectFloatBuffer(KeyWhite.GetColorData()));
            glcanvas.invoke(false, drawable1 -> {
                PianoKeys.RemoveFromReleasingWhiteList(keyId);
                return true;
            });
        }
        gl.glBindBuffer(GL_UNIFORM_BUFFER, 0);
        gl.glDrawElementsInstanced(GL_TRIANGLES, 6, GL_UNSIGNED_SHORT, 0,52);

        gl.glUseProgram(0);
        gl.glBindVertexArray(0);

        checkError(gl, "display");
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

        GL3 gl = drawable.getGL().getGL3();

        float halfWidth = glcanvas.getSize().width / 2.0f;
        float halfHeight = glcanvas.getSize().height / 2.0f;
        glm.ortho(-halfWidth, halfWidth, -halfHeight, halfHeight, -5.0f, 5.0f).to(matBuffer);

        gl.glBindBuffer(GL_UNIFORM_BUFFER, bufferName.get(Buffer.GLOBAL_MATRICES));
        gl.glBufferSubData(GL_UNIFORM_BUFFER, 0, Mat4x4.SIZE, matBuffer);
        gl.glBindBuffer(GL_UNIFORM_BUFFER, 0);

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
