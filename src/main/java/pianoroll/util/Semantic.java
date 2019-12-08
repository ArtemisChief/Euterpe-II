package pianoroll.util;

public abstract class Semantic {

    public interface Attr {
        int POSITION = 0;
    }

    public interface Buffer {
        int VERTEX_KEYWHITE = 0;
        int VERTEX_KEYBLACK = 1;
        int VERTEX_ROLLWHITE = 0;
        int VERTEX_ROLLBLACK = 1;
        int PARTICLE = 0;
    }

    public interface Piano {
        int KEY_MAX = 88;
    }

    public interface Roll {
        float SPEED = 30.0f;
    }

}
