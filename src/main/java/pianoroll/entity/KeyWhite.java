package pianoroll.entity;

public class KeyWhite extends Key{

    private static float[] vertexData = {
             0.0f,  0.0f,           // Left-Top
             2.2f,  0.0f,           // Right-Top
             2.2f, -13.3f,          // Right-Bottom
             0.0f, -13.3f           // Left-Bottom
    };

    private static float[] colorData = {
            0.94f, 0.94f, 0.94f
    };

    private static float[] downColorData = {
            0.90f, 0.47f, 0.04f
    };

    public KeyWhite(int pitch) {
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
