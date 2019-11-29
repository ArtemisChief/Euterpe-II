package pianoroll.entity;

public class RollWhite extends Roll{

    private static float[] vertexData = {
            -1.1f,  0.0f,           // Left-Top
             1.1f,  0.0f,           // Right-Top
             1.1f, -1.0f,           // Right-Bottom
            -1.1f, -1.0f            // Left-Bottom
    };

    public RollWhite(int keyID, int colorID) {
        super(keyID, colorID);
    }

    public static float[] GetVertexData() {
        return vertexData;
    }

}
