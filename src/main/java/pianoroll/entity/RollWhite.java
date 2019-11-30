package pianoroll.entity;

public class RollWhite extends Roll{

    private static float[] vertexData = {
            -0.95f,  0.0f,           // Left-Top
             0.95f,  0.0f,           // Right-Top
             0.95f, -1.0f,           // Right-Bottom
            -0.95f, -1.0f            // Left-Bottom
    };

    public RollWhite(int keyID, int colorID) {
        super(keyID, colorID);
    }

    public static float[] GetVertexData() {
        return vertexData;
    }

}
