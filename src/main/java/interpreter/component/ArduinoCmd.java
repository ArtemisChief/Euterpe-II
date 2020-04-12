package interpreter.component;

import java.io.IOException;
import java.io.InputStream;

public class ArduinoCmd {

    private static final String ArduinoPath = "F:\\Arduino\\arduino_debug.exe";

    public static InputStream error;
    public static InputStream output;

    public void compile(String filePath) {
        runCommand(ArduinoPath + " --verify " + filePath);
    }

    public void upload(String filePath) {
        runCommand(ArduinoPath + " --upload " + filePath);
    }

    private void runCommand(String cmd) {
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            error = process.getErrorStream();
            output = process.getInputStream();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
