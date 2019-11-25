package pianoroll.entity;

public class KeyBlack extends Key{

    private static float[] vertexData = {
             // positions    // colors
            -0.63f,  0.0f,   0.22f, 0.22f, 0.22f,    // Left-Top
             0.63f,  0.0f,   0.22f, 0.22f, 0.22f,    // Right-Top
             0.63f, -9.0f,   0.06f, 0.06f, 0.06f,    // Right-Bottom
            -0.63f, -9.0f,   0.06f, 0.06f, 0.06f,    // Left-Bottom
    };

    public KeyBlack(int pitch) {
        super(pitch);
    }

    public static float[] getVertexData() {
        return vertexData;
    }

}
