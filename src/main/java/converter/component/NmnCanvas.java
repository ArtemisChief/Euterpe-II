package converter.component;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;

public class NmnCanvas implements GLEventListener {

    // 单例
    private static final NmnCanvas instance = new NmnCanvas();

    // 获取单例
    public static NmnCanvas GetInstance() {
        return instance;
    }

    private static GLCanvas glcanvas;

    private NmnCanvas() {

    }

    public static void Setup() {

    }

    @Override
    public void init(GLAutoDrawable drawable) {

    }

    @Override
    public void display(GLAutoDrawable drawable) {

    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

    }

    @Override
    public void dispose(GLAutoDrawable drawable) {

    }

}
