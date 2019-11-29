package pianoroll.util;

public abstract class Semantic {

    public interface Attr {
        int POSITION = 0;
    }

    public interface Buffer {
        int VERTEX_KEYWHITE = 0;
        int VERTEX_KEYBLACK = 1;
        int VERTEX_ROLLWHITE = 2;
        int VERTEX_ROLLBLACK = 3;
        int ELEMENT = 4;
        int MAX = 5;
    }

    public interface Piano {
        int KEY_MAX = 88;
    }

}
