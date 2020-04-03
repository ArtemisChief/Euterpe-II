package midipaser.util;

public class MidiParserUtil {

    public static int byteHighToDec(byte b) {
        return (b & 0xF0) >> 4;
    }

    public static int byteLowToDec(byte b) {
        return b & 0x0F;
    }

    public static float mptToBpm(int mpt) {
        return (60f / mpt * 1000000);
    }

    public static int bytesToInt(byte[] val) {
        int buffer = 0;

        for (int i = 0; i < val.length; ++i) {
            int dexToInt = val[val.length - i - 1];
            dexToInt = dexToInt > 0 ? dexToInt : 256 + dexToInt;

            buffer += dexToInt * Math.pow(16, i * 2);
        }

        return buffer;
    }

}
