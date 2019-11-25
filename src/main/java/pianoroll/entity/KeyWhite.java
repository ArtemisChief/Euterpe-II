package pianoroll.entity;

public class KeyWhite extends Key{

    private static float[] vertexData = {
             // positions    // colors
             0.0f,  0.0f,    0.94f, 0.94f, 0.94f,    // Left-Top
             2.2f,  0.0f,    0.94f, 0.94f, 0.94f,    // Right-Top
             2.2f, -13.3f,   0.89f, 0.89f, 0.89f,    // Right-Bottom
             0.0f, -13.3f,   0.89f, 0.89f, 0.89f,    // Left-Bottom
    };

    public KeyWhite(int pitch) {
        super(pitch);
    }

    public static float[] getVertexData() {
        return vertexData;
    }

}
