package pianoroll.entity;

public class KeyWhite extends Key{

    private static float[] vertexData = {
            -1.1f,  0.0f,           // Left-Top
             1.1f,  0.0f,           // Right-Top
             1.1f, -11.5f,          // Right-Bottom
            -1.1f, -11.5f           // Left-Bottom
    };

    public KeyWhite(int trackID) {
        super(trackID, 0);
    }

    @Override
    public void press() {
        super.press();
        super.setColorID(2);
    }

    @Override
    public void release() {
        super.release();
        super.setColorID(0);
    }

    public static float[] GetVertexData() {
        return vertexData;
    }

}
