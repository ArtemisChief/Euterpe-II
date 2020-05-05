package converter.component;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.GLBuffers;
import converter.component.renderer.NmnRenderer;
import uno.glsl.Program;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL2ES3.GL_COLOR;
import static com.jogamp.opengl.GL2ES3.GL_DEPTH;

public class NmnCanvas implements GLEventListener {

    // 单例
    private static final NmnCanvas instance = new NmnCanvas();

    // 获取单例
    public static NmnCanvas GetInstance() {
        return instance;
    }

    private static GLCanvas glcanvas;

    //renderer
    private final NmnRenderer nmnRenderer;

    private Program nmnProgram;

    private final FloatBuffer clearColor;
    private final FloatBuffer clearDepth;

    private NmnCanvas() {
        //背景
        clearColor = GLBuffers.newDirectFloatBuffer(4);
        clearDepth = GLBuffers.newDirectFloatBuffer(1);

        nmnRenderer = new NmnRenderer();
    }

    public static void Setup() {
        GLProfile glProfile = GLProfile.get(GLProfile.GL3);
        GLCapabilities glCapabilities = new GLCapabilities(glProfile);

        glcanvas = new GLCanvas(glCapabilities);
        glcanvas.setBounds(0, 0, 1150,800);
        glcanvas.addGLEventListener(instance);

        Animator animator = new Animator(glcanvas);
        animator.start();
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();

        //将renderer初始化
        nmnRenderer.init(gl);

        nmnProgram = new Program(gl, getClass(),
                "/shaders", "Nmn.vert", "Nmn.frag",
                "offsetX", "offsetY");
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        //gl.glClearBufferfv(GL_COLOR, 0, clearColor.put(0, .2f).put(1, .3f).put(2, .4f).put(3, 1f));
        gl.glClearBufferfv(GL_COLOR, 0, clearColor.put(0, 1f).put(1, 1f).put(2, 1f).put(3, 1f));
        gl.glClearBufferfv(GL_DEPTH, 0, clearDepth.put(0, 1f));

        //绘制
        nmnRenderer.drawNmn(gl, nmnProgram);




    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

    }

    @Override
    public void dispose(GLAutoDrawable drawable) {

    }

    public static GLCanvas GetGlcanvas() {
        return glcanvas;
    }
}
