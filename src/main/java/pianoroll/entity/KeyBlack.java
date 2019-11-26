package pianoroll.entity;

public class KeyBlack extends Key{

    private static float[] vertexData = {
            -0.63f,  0.0f,          // Left-Top
             0.63f,  0.0f,          // Right-Top
             0.63f, -9.0f,          // Right-Bottom
            -0.63f, -9.0f           // Left-Bottom
    };

    private static float[] colorData = {
            0.22f, 0.22f, 0.22f
    };

    private static float[] downColorData = {
            0.22f, 0.22f, 0.22f
    };

    public KeyBlack(int pitch) {
        super(pitch);
    }

    public static float[] GetVertexData() {
        return vertexData;
    }

    public static float[] GetColorData() {
        return colorData;
    }

    public static float[] GetDownColorData() {
        return downColorData;
    }

}
