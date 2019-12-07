package pianoroll.entity;

public class RollWhite extends Roll{

    private static final float[] vertexData = {
            -0.95f,  0.0f,           // Left-Top
            -0.95f, -1.0f,           // Left-Bottom
             0.95f,  0.0f,           // Right-Top
             0.95f, -1.0f            // Right-Bottom

    };

    public static float[] GetVertexData() {
        return vertexData;
    }

}
