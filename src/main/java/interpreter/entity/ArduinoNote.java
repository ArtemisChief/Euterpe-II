package interpreter.entity;

/**
 * 音符类
 * 相当于一个字典
 * 用查表的方法得到基础音的频率与音调的倍率关系
 */

public class ArduinoNote {
    public class Pitch {
        public static final String C = "523";
        public static final String D = "587";
        public static final String E = "659";
        public static final String F = "698";
        public static final String G = "784";
        public static final String A = "880";
        public static final String B = "988";

//        public static final String C = "262";
//        public static final String D = "294";
//        public static final String E = "330";
//        public static final String F = "349";
//        public static final String G = "392";
//        public static final String A = "440";
//        public static final String B = "494";
    }

    public class Tonality {
        public static final String C = "1";
        public static final String D = "1.1225";
        public static final String E = "1.2599";
        public static final String F = "1.3348";
        public static final String G = "1.4983";
        public static final String A = "1.6818";
        public static final String B = "1.8877";
    }
}
