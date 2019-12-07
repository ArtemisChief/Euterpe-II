package pianoroll.entity;

public class RollBlack extends Roll{

    private static final float[] vertexData = {
            -0.5f,  0.0f,            // Left-Top
            -0.5f, -1.0f,            // Left-Bottom
             0.5f,  0.0f,            // Right-Top
             0.5f, -1.0f             // Right-Bottom

    };

    public static float[] GetVertexData() {
        return vertexData;
    }

}
