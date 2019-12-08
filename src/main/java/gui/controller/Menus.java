package gui.controller;

import gui.view.MainWindow;
import midi.component.MidiPlayer;
import gui.entity.Status;

public class Menus {

    private static final Menus instance = new Menus();

    public static Menus GetInstance() {
        return instance;
    }

    private Menus(){ }

    public void init(){

        MidiPlayer.GetInstance().getSequencer().addMetaEventListener(meta -> {
            if (meta.getType() == 47) {
                MainWindow.GetInstance().playDirectMenuItem.setText("Play");
                MidiPlayer.GetInstance().stop();
            }
        });

        // 新建空文件
        MainWindow.GetInstance().newEmptyMenuItem.addActionListener(e -> {
            if (Status.GetCurrentStatus().getIsEdited())
                if (!Diaglogs.GetInstance().askSaving())
                    return;

            MainWindow.GetInstance().inputTextPane.setText("");
            MainWindow.GetInstance().outputTextArea.setText("");
            MainWindow.GetInstance().playDirectMenuItem.setText("Play");
            MidiPlayer.GetInstance().stop();
            Status.SetCurrentStatus(Status.NEW_FILE);
        });

        // 新建模板文件
        MainWindow.GetInstance().newTemplateMenuItem.addActionListener(e -> {
            if (Status.GetCurrentStatus().getIsEdited())
                if (!Diaglogs.GetInstance().askSaving())
                    return;

            String str = "/*\n" +
                    " 数字乐谱模板\n" +
                    " 声部1 + 声部2\n" +
                    " 双声部 Version\n" +
                    " */\n" +
                    "\n" +
                    "//声部1\n" +
                    "paragraph Name1\n" +
                    "instrument= 0\n" +
                    "volume= 127\n" +
                    "speed= 90\n" +
                    "1= C\n" +
                    "1234  567[1]  <4444 4444>\n" +
                    "[1]765  4321  <4444 4444>\n" +
                    "\n" +
                    "1324    3546  <8888 8888>\n" +
                    "576[1] 7[21]  <8888 884>\n" +
                    "\n" +
                    "[1]675  6453  <gggg gggg>\n" +
                    "4231   2(7)1  <gggg gg8>\n" +
                    "end\n" +
                    "\n" +
                    "//声部2\n" +
                    "paragraph Name2\n" +
                    "instrument= 0\n" +
                    "volume= 127\n" +
                    "speed= 90\n" +
                    "1= C\n" +
                    "(1234  567)1  <4444 4444>\n" +
                    "1(765  4321)  <4444 4444>\n" +
                    "\n" +
                    "(1324  3546)  <8888 8888>\n" +
                    "(576)1 (7)21  <8888 884>\n" +
                    "\n" +
                    "1(675  6453)  <gggg gggg>\n" +
                    "(4231 2(7)1)  <gggg gg8>\n" +
                    "end\n" +
                    "\n" +
                    "//添加更多声部......\n" +
                    "\n" +
                    "//多声部同时播放\n" +
                    "play(Name1&Name2)";

            MainWindow.GetInstance().inputTextPane.setText(str);
            MainWindow.GetInstance().outputTextArea.setText("");
            MainWindow.GetInstance().inputTextPane.setCaretPosition(0);
            MainWindow.GetInstance().playDirectMenuItem.setText("Play");
            MidiPlayer.GetInstance().stop();
            Status.SetCurrentStatus(Status.NEW_FILE);
        });

        // 打开文件
        MainWindow.GetInstance().openMenuItem.addActionListener(e -> {
            FileIO.GetInstance().openMuiFile();
            MainWindow.GetInstance().outputTextArea.setText("");
            MainWindow.GetInstance().playDirectMenuItem.setText("Play");
            MidiPlayer.GetInstance().stop();
        });

        // 保存文件
        MainWindow.GetInstance().saveMenuItem.addActionListener(e -> {
            FileIO.GetInstance().saveMuiFile();
            MainWindow.GetInstance().playDirectMenuItem.setText("Play");
            MidiPlayer.GetInstance().stop();
        });

        // 另存为文件
        MainWindow.GetInstance().saveAsMenuItem.addActionListener(e -> {
            FileIO.GetInstance().saveAsMuiFile();
            MainWindow.GetInstance().playDirectMenuItem.setText("Play");
            MidiPlayer.GetInstance().stop();
        });

        // 导出Midi文件
        MainWindow.GetInstance().exportMidiMenuItem.addActionListener(e -> {
            FileIO.GetInstance().exportMidiFile();
        });

        // 退出
        MainWindow.GetInstance().exitMenuItem.addActionListener(e -> {
            if (Status.GetCurrentStatus().getIsEdited())
                if (!Diaglogs.GetInstance().askSaving())
                    return;

            if (FileIO.GetInstance().getTempMidiFile() != null && FileIO.GetInstance().getTempMidiFile().exists())
                FileIO.GetInstance().getTempMidiFile().delete();

            MidiPlayer.GetInstance().close();
            System.exit(0);
        });

        // 加载SoundFont文件
        MainWindow.GetInstance().loadSoundFontMenuItem.addActionListener(e -> {
            FileIO.GetInstance().loadSoundFont();
        });

        // 播放
        MainWindow.GetInstance().playDirectMenuItem.addActionListener(e -> {
            if (!MidiPlayer.GetInstance().getIsLoadedMidiFile()) {
                if (!FileIO.GetInstance().generateTempMidiFile())
                    return;

                MidiPlayer.GetInstance().loadMidiFile(FileIO.GetInstance().getTempMidiFile());
            }

            if (MidiPlayer.GetInstance().getSequencer().isRunning()) {
                MidiPlayer.GetInstance().pause();
                MainWindow.GetInstance().playDirectMenuItem.setText("Resume");
            } else {
                MidiPlayer.GetInstance().play();
                MainWindow.GetInstance().playDirectMenuItem.setText("Pause");
            }
        });

        // 停止播放
        MainWindow.GetInstance().stopDirectMenuItem.addActionListener(e -> {
            MainWindow.GetInstance().playDirectMenuItem.setText("Play");
            MidiPlayer.GetInstance().stop();
        });

        // 打开转调器窗口
        MainWindow.GetInstance().transposerMenuItem.addActionListener(e -> {

        });

        // 打开乐器菜单窗口
        MainWindow.GetInstance().instruMenuItem.addActionListener(e -> {

        });

        // 打开提示窗口
        MainWindow.GetInstance().tipsMenuItem.addActionListener(e -> {

        });

        // 打开关于窗口
        MainWindow.GetInstance().aboutMenuItem.addActionListener(e -> {

        });

    }

}
