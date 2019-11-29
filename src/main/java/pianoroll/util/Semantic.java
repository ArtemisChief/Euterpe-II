package pianoroll.util;

public abstract class Semantic {

    public interface Attr {
        int POSITION = 0;
    }

    public interface Buffer {
        int VERTEX_KEYWHITE = 0;
        int VERTEX_KEYBLACK = 1;
//        int VERTEX_ROLLS = 2;
        int ELEMENT = 2;
        int MAX = 4;
    }

    public interface Piano {
        int KEY_MAX = 88;
    }

}
