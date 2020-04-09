package converter.component;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;

public class StaveCanvas implements GLEventListener {

    // 单例
    private static final StaveCanvas instance = new StaveCanvas();

    // 获取单例
    public static StaveCanvas GetInstance() {
        return instance;
    }

    private static GLCanvas glcanvas;

    private StaveCanvas() {

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
