package gui.controller;

import gui.view.MainWindow;
import midibuilder.component.MidiFileBuilder;
import midiplayer.MidiPlayer;
import gui.entity.Status;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileIO {

    private static final FileIO instance = new FileIO();

    public static FileIO GetInstance() {
        return instance;
    }

    private final MainWindow mainWindow;

    private File tempMidiFile;

    private File file;

    private boolean isWritingInputText;

    private FileIO() {
        mainWindow = MainWindow.GetInstance();

        tempMidiFile = null;
        file = null;
        isWritingInputText = false;
    }

    public boolean openMuiFile() {
        if (Status.GetCurrentStatus().getIsEdited())
            if (!Diaglogs.GetInstance().askSaving())
                return false;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Euterpe File", "mui");
        fileChooser.setFileFilter(filter);
        int result = fileChooser.showOpenDialog(mainWindow);

        if (result != JFileChooser.CANCEL_OPTION) {
            file = fileChooser.getSelectedFile();

            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));

                StringBuilder stringBuilder = new StringBuilder();
                String content;
                while ((content = bufferedReader.readLine()) != null) {
                    stringBuilder.append(content);
                    stringBuilder.append(System.getProperty("line.separator"));
                }

                bufferedReader.close();

                isWritingInputText = true;
                mainWindow.inputTextPane.setText(stringBuilder.toString());
                mainWindow.inputTextPane.setCaretPosition(0);
                isWritingInputText = false;

                InputTexts.GetInstance().refreshColor();

                Status.SetCurrentStatus(Status.SAVED_FILE);

                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        return false;
    }

    public void saveMuiFile() {
        if (Status.GetCurrentStatus() == Status.SAVED_FILE) {
            try {
                if (!file.exists())
                    file.createNewFile();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
                bufferedWriter.write(mainWindow.inputTextPane.getText());
                bufferedWriter.close();

                Status.SetCurrentStatus(Status.SAVED_FILE);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            saveAsMuiFile();
        }
    }

    public void saveAsMuiFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Euterpe File", "mui");
        fileChooser.setFileFilter(filter);
        int result = fileChooser.showSaveDialog(mainWindow);

        if (result != JFileChooser.CANCEL_OPTION) {
            String fileStr = fileChooser.getSelectedFile().getAbsoluteFile().toString();

            if (fileStr.lastIndexOf(".mui") == -1)
                fileStr += ".mui";

            file = new File(fileStr);

            try {
                if (!file.exists())
                    file.createNewFile();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
                bufferedWriter.write(mainWindow.inputTextPane.getText());
                bufferedWriter.close();

                Status.SetCurrentStatus(Status.SAVED_FILE);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void exportMidiFile() {
        if (!Interpreter.GetInstance().runInterpret())
            return;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Midi File", "mid");
        fileChooser.setFileFilter(filter);
        int result = fileChooser.showSaveDialog(mainWindow);

        if (result != JFileChooser.CANCEL_OPTION) {
            String fileStr = fileChooser.getSelectedFile().getAbsoluteFile().toString();

            if (fileStr.lastIndexOf(".mid") == -1)
                fileStr += ".mid";

            File midiFile = new File(fileStr);

            if (!MidiFileBuilder.WriteToFile(Interpreter.GetInstance().getMidiFile(), midiFile))
                Diaglogs.GetInstance().showErrorInfo("目标文件被占用，无法导出");
        }
    }

    public boolean generateTempMidiFile() {
        if (!Interpreter.GetInstance().runInterpret())
            return false;

        if (tempMidiFile == null) {
            tempMidiFile = new File("tempMidi.mid");
        }

        if (!MidiFileBuilder.WriteToFile(Interpreter.GetInstance().getMidiFile(), tempMidiFile)) {
            Diaglogs.GetInstance().showErrorInfo("目标文件被占用，无法导出");
            return false;
        }

        return true;
    }

    public void loadSoundFont() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("SoundFont File", "sf2", "sf3");
        fileChooser.setFileFilter(filter);
        int value = fileChooser.showOpenDialog(mainWindow);
        if (value == JFileChooser.CANCEL_OPTION)
            return;
        File soundFontFile = fileChooser.getSelectedFile();
        MidiPlayer.GetInstance().loadSoundBank(soundFontFile);
    }

    public File openMidiFile() {
        if (Status.GetCurrentStatus().getIsEdited())
            if (!Diaglogs.GetInstance().askSaving())
                return null;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Midi File", "mid");
        fileChooser.setFileFilter(filter);
        int result = fileChooser.showOpenDialog(mainWindow);

        if (result == JFileChooser.CANCEL_OPTION)
            return null;

        return fileChooser.getSelectedFile();
    }

    public File getTempMidiFile() {
        return tempMidiFile;
    }

    public boolean isWritingInputText() {
        return isWritingInputText;
    }

    public void setWritingInputText(boolean writingInputText) {
        isWritingInputText = writingInputText;
    }

}
