package pianoroll.entity;

public class RollWhite extends Roll{

    private static float[] vertexData = {
            -1.1f,  0.0f,           // Left-Top
             1.1f,  0.0f,           // Right-Top
             1.1f, -11.5f,          // Right-Bottom
            -1.1f, -11.5f           // Left-Bottom
    };

    public RollWhite(int keyID, int colorID) {
        super(keyID, colorID);
    }

    public static float[] GetVertexData() {
        return vertexData;
    }

}
