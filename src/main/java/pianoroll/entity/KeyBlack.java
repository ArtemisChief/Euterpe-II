package pianoroll.entity;

public class KeyBlack extends Key{

    private static float[] vertexData = {
            -0.65f,  0.0f,          // Left-Top
             0.65f,  0.0f,          // Right-Top
             0.65f, -8.0f,          // Right-Bottom
            -0.65f, -8.0f           // Left-Bottom
    };

    public KeyBlack(int trackID) {
        super(trackID, 1);
    }

    @Override
    public void press() {
        super.press();
        super.setColorID(3);
    }

    @Override
    public void release() {
        super.release();
        super.setColorID(1);
    }

    public static float[] GetVertexData() {
        return vertexData;
    }

}
