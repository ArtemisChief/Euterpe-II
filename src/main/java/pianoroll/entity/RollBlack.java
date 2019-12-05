package pianoroll.entity;

public class RollBlack extends Roll{

    private static float[] vertexData = {
            -0.5f,  0.0f,            // Left-Top
            -0.5f, -1.0f,            // Left-Bottom
             0.5f,  0.0f,            // Right-Top
             0.5f, -1.0f             // Right-Bottom

    };

    public RollBlack(int keyID) {
        super(keyID);
    }

    public static float[] GetVertexData() {
        return vertexData;
    }

}