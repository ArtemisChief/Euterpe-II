package pianoroll.entity;

public class RollBlack extends Roll{

    private static float[] vertexData = {
            -0.65f,  0.0f,          // Left-Top
             0.65f,  0.0f,          // Right-Top
             0.65f, -8.0f,          // Right-Bottom
            -0.65f, -8.0f           // Left-Bottom
    };

    public RollBlack(int keyID, int colorID) {
        super(keyID, colorID);
    }

    public static float[] GetVertexData() {
        return vertexData;
    }

}
