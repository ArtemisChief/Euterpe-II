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
        int VERTEX_PARTICLE = 0;
        int VERTEX_COLUMN = 0;
        int VERTEX_ROW = 1;
        int VERTEX_PIANO = 2;
    }

    public interface Piano {
        int KEY_MAX = 88;
    }

    public interface Pianoroll {
        int PARTICLE_AMOUNT = 5000;
        float DEFAULT_LENGTH_PER_SECOND = 30.0f;
        float LENGTH_PER_CROTCHET = 10.0f;
    }

    public interface Color {
        int HIGH_LIGHT = 100;
        int WHITE = 200;
        int BLACK = 201;
        int GREY = 202;
    }

}
