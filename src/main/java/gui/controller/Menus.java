package gui.controller;

import gui.view.MainWindow;
import midiplayer.component.MidiPlayer;
import gui.entity.Status;
import pianoroll.component.Pianoroll;
import pianoroll.component.PianorollCanvas;
import pianoroll.util.Semantic;

import java.io.IOException;

public class Menus {

    private static final Menus instance = new Menus();

    public static Menus GetInstance() {
        return instance;
    }

    private Menus() {
    }

    public void init() {

        MidiPlayer.GetInstance().getSequencer().addMetaEventListener(meta -> {
            if (meta.getType() == 47) {
                MainWindow.GetInstance().playDirectMenuItem.setText("Play");
                MidiPlayer.GetInstance().stop();
                Pianoroll.GetInstance().reset();
                Pianoroll.GetInstance().loadMidiFile(FileIO.GetInstance().getTempMidiFile());
            }
        });

        // 新建空文件
        MainWindow.GetInstance().newEmptyMenuItem.addActionListener(e -> {
            if (Status.GetCurrentStatus().getIsEdited())
                if (!Diaglogs.GetInstance().askSaving())
                    return;

            Pianoroll.GetInstance().reset();

            MainWindow.GetInstance().inputTextPane.setText("");
            MainWindow.GetInstance().tipsMenuItem.doClick();
            MainWindow.GetInstance().playDirectMenuItem.setText("Play");
            MidiPlayer.GetInstance().stop();
            Status.SetCurrentStatus(Status.NEW_FILE);
        });

        // 新建模板文件
        MainWindow.GetInstance().newTemplateMenuItem.addActionListener(e -> {
            if (Status.GetCurrentStatus().getIsEdited())
                if (!Diaglogs.GetInstance().askSaving())
                    return;

            Pianoroll.GetInstance().reset();

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
            MainWindow.GetInstance().tipsMenuItem.doClick();
            MainWindow.GetInstance().inputTextPane.setCaretPosition(0);
            MainWindow.GetInstance().playDirectMenuItem.setText("Play");
            MidiPlayer.GetInstance().stop();
            Status.SetCurrentStatus(Status.NEW_FILE);
        });

        // 打开文件
        MainWindow.GetInstance().openMenuItem.addActionListener(e -> {
            if(!FileIO.GetInstance().openMuiFile())
                return;

            Pianoroll.GetInstance().reset();

            MainWindow.GetInstance().outputTextArea.setText("");
            MainWindow.GetInstance().playDirectMenuItem.setText("Play");
            MidiPlayer.GetInstance().stop();

            if (!MidiPlayer.GetInstance().getIsLoadedMidiFile()) {
                if (!FileIO.GetInstance().generateTempMidiFile())
                    return;

                MidiPlayer.GetInstance().loadMidiFile(FileIO.GetInstance().getTempMidiFile());
                Pianoroll.GetInstance().reset();
                Pianoroll.GetInstance().loadMidiFile(FileIO.GetInstance().getTempMidiFile());
            }
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

        // 重新编译Midi文件
        MainWindow.GetInstance().rebuildMenuItem.addActionListener(e -> {
            if (!MidiPlayer.GetInstance().getIsLoadedMidiFile()) {
                if (!FileIO.GetInstance().generateTempMidiFile())
                    return;

                MidiPlayer.GetInstance().loadMidiFile(FileIO.GetInstance().getTempMidiFile());
                Pianoroll.GetInstance().reset();
                Pianoroll.GetInstance().loadMidiFile(FileIO.GetInstance().getTempMidiFile());
            }
        });

        // 播放
        MainWindow.GetInstance().playDirectMenuItem.addActionListener(e -> {
            if (MidiPlayer.GetInstance().getSequencer().isRunning()) {
                MidiPlayer.GetInstance().pause();
                MainWindow.GetInstance().playDirectMenuItem.setText("Resume");
                Pianoroll.GetInstance().setPlaying(false);
            } else {
                MidiPlayer.GetInstance().play();
                MainWindow.GetInstance().playDirectMenuItem.setText("Pause");
                Pianoroll.GetInstance().setPlaying(true);
            }
        });

        // 停止播放
        MainWindow.GetInstance().stopDirectMenuItem.addActionListener(e -> {
            MainWindow.GetInstance().playDirectMenuItem.setText("Play");
            MidiPlayer.GetInstance().stop();
            Pianoroll.GetInstance().reset();
            Pianoroll.GetInstance().loadMidiFile(FileIO.GetInstance().getTempMidiFile());
        });

        // 从外部播放Midi文件
        MainWindow.GetInstance().playExternalMenuItem.addActionListener(e-> {
            if (!FileIO.GetInstance().generateTempMidiFile())
                return;

            try {
                Runtime.getRuntime().exec("rundll32 url.dll FileProtocolHandler file://" + FileIO.GetInstance().getTempMidiFile().getAbsolutePath().replace("\\", "\\\\"));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        // 转换Midi到Mui
        MainWindow.GetInstance().convertToMuiMenuItem.addActionListener(e -> {
            FileIO.GetInstance().convertMidiFile();
            MainWindow.GetInstance().outputTextArea.setText("");
            MainWindow.GetInstance().playDirectMenuItem.setText("Play");
            MidiPlayer.GetInstance().stop();

            if (!MidiPlayer.GetInstance().getIsLoadedMidiFile()) {
                if (!FileIO.GetInstance().generateTempMidiFile())
                    return;

                MidiPlayer.GetInstance().loadMidiFile(FileIO.GetInstance().getTempMidiFile());
                Pianoroll.GetInstance().reset();
                Pianoroll.GetInstance().loadMidiFile(FileIO.GetInstance().getTempMidiFile());
            }
        });

        // 转换Mui到五线谱
        MainWindow.GetInstance().convertToStaveMenuItem.addActionListener(e -> {

        });

        // 转换Mui到简谱
        MainWindow.GetInstance().convertToNmnMenuItem.addActionListener(e -> {

        });

        // 打开钢琴卷帘面板
        MainWindow.GetInstance().pianorollRadioMenuItem.addActionListener(e -> {
            MainWindow.GetInstance().layeredPane.moveToFront(PianorollCanvas.GetGlcanvas());
        });

        // 打开输出文字面板
        MainWindow.GetInstance().outputTextRadioMenuItem.addActionListener(e -> {
            MainWindow.GetInstance().layeredPane.moveToFront(MainWindow.GetInstance().outputScrollPane);
        });

        // 打开五线谱面板
        MainWindow.GetInstance().staveRadioMenuItem.addActionListener(e -> {

        });

        // 打开简谱面板
        MainWindow.GetInstance().nmnRadioMenuItem.addActionListener(e -> {

        });

        // 打开转调器面板
        MainWindow.GetInstance().transposerRadioMenuItem.addActionListener(e -> {

        });

        // 打开乐器菜单
        MainWindow.GetInstance().instruMenuItem.addActionListener(e -> {
            String str = "=========================================================\n" +
                    "                                                             Instrument\n" +
                    "----------------------------------------------------------------------------------------------\n" +
                    "音色号\t乐器名\t                |\t音色号\t乐器名\n" +
                    "----------------------------------------------------------------------------------------------\n" +
                    "钢琴类\t\t                |\t簧乐器\n" +
                    "0\t大钢琴\t                |\t64\t高音萨克斯\n" +
                    "1\t亮音钢琴\t                |\t65\t中音萨克斯\n" +
                    "2\t电子大钢琴\t                |\t66\t次中音萨克斯\n" +
                    "3\t酒吧钢琴\t                |\t67\t上低音萨克斯\n" +
                    "4\t电钢琴1\t                |\t68\t双簧管\n" +
                    "5\t电钢琴2\t                |\t69\t英国管\n" +
                    "6\t大键琴\t                |\t70\t巴颂管\n" +
                    "7\t电翼琴\t                |\t71\t单簧管\n" +
                    "----------------------------------------------------------------------------------------------\n" +
                    "固定音高敲击乐器\t                |\t吹管乐器\n" +
                    "8\t钢片琴\t                |\t72\t短笛\n" +
                    "9\t钟琴\t                |\t73\t长笛\n" +
                    "10\t音乐盒\t                |\t74\t竖笛\n" +
                    "11\t颤音琴\t                |\t75\t牧笛\n" +
                    "12\t马林巴琴\t                |\t76\t瓶笛\n" +
                    "13\t木琴\t                |\t77\t尺八\n" +
                    "14\t管钟\t                |\t78\t哨子\n" +
                    "15\t洋琴\t                |\t79\t陶笛\n" +
                    "----------------------------------------------------------------------------------------------\n" +
                    "风琴\t\t                |\t合成音主旋律\n" +
                    "16\t音栓风琴\t                |\t80\t方波\n" +
                    "17\t敲击风琴\t                |\t81\t锯齿波\n" +
                    "18\t摇滚风琴\t                |\t82\t汽笛风琴\n" +
                    "19\t教堂管风琴\t                |\t83\t合成吹管\n" +
                    "20\t簧风琴\t                |\t84\t合成电吉他\n" +
                    "21\t手风琴\t                |\t85\t人声键\n" +
                    "22\t口琴\t                |\t86\t五度音\n" +
                    "23\t探戈手风琴\t                |\t87\t贝斯吉他合奏\n" +
                    "----------------------------------------------------------------------------------------------\n" +
                    "吉他\t\t                |\t合成音和弦衬底\n" +
                    "24\t木吉他(尼龙弦)\t                |\t88\t新时代\n" +
                    "25\t木吉他(钢弦)\t                |\t89\t温暖的\n" +
                    "26\t电吉他(爵士)\t                |\t90\t多重和音\n" +
                    "27\t电吉他(清音)\t                |\t91\t唱诗班\n" +
                    "28\t电吉他(闷音)\t                |\t92\t弓弦音色\n" +
                    "29\t电吉他(驱动音效)               |\t93\t金属的\n" +
                    "30\t电吉他(失真音效)               |\t94\t光华\n" +
                    "31\t吉他泛音\t                |\t95\t宽阔的\n" +
                    "----------------------------------------------------------------------------------------------\n" +
                    "贝斯\t\t                |\t合成音效果\n" +
                    "32\t贝斯\t                |\t96\t雨声\n" +
                    "33\t电贝斯(指弹)\t                |\t97\t电影音效\n" +
                    "34\t电贝斯(拨片)\t                |\t98\t水晶\n" +
                    "35\t无品贝斯\t                |\t99\t气氛\n" +
                    "36\t打弦贝斯1\t                |\t100\t明亮\n" +
                    "37\t打弦贝斯2\t                |\t101\t魅影\n" +
                    "38\t合成贝斯1\t                |\t102\t回音\n" +
                    "39\t合成贝斯2\t                |\t103\t科幻\n" +
                    "----------------------------------------------------------------------------------------------\n" +
                    "弦乐器\t\t                |\t民族乐器\n" +
                    "40\t小提琴\t                |\t104\t西塔琴\n" +
                    "41\t中提琴\t                |\t105\t斑鸠琴\n" +
                    "42\t大提琴\t                |\t106\t三味线\n" +
                    "43\t低音提琴\t                |\t107\t古筝\n" +
                    "44\t颤弓弦乐\t                |\t108\t卡林巴铁片琴\n" +
                    "45\t弹拨弦乐\t                |\t109\t苏格兰风琴\n" +
                    "46\t竖琴\t                |\t110\t古提亲\n" +
                    "47\t定音鼓\t                |\t111\t兽笛\n" +
                    "----------------------------------------------------------------------------------------------\n" +
                    "合奏\t\t                |\t打击乐器\n" +
                    "48\t弦乐合奏1\t                |\t112\t叮当铃\n" +
                    "49\t弦乐合奏2\t                |\t113\t阿果果鼓\n" +
                    "50\t合成弦乐1\t                |\t114\t钢鼓\n" +
                    "51\t合成弦乐2\t                |\t115\t木鱼\n" +
                    "52\t唱诗班\"啊\"\t                |\t116\t太鼓\n" +
                    "53\t合唱\"喔\"\t                |\t117\t定音筒鼓\n" +
                    "54\t合成人声\t                |\t118\t合成鼓\n" +
                    "55\t交响打击乐\t                |\t119\t反钹\n" +
                    "----------------------------------------------------------------------------------------------\n" +
                    "铜管乐器\t\t                |\t特殊音效\n" +
                    "56\t小号\t                |\t120\t吉他滑弦杂音\n" +
                    "57\t长号\t                |\t121\t呼吸杂音\n" +
                    "58\t大号\t                |\t122\t海浪\n" +
                    "59\t闷音小号\t                |\t123\t鸟鸣\n" +
                    "60\t法国圆号\t                |\t124\t电话铃声\n" +
                    "61\t铜管乐\t                |\t125\t直升机\n" +
                    "62\t合成铜管1\t                |\t126\t鼓掌\n" +
                    "63\t合成铜管2\t                |\t127\t枪声\n" +
                    "=========================================================";
            MainWindow.GetInstance().outputTextArea.setText(str);
            MainWindow.GetInstance().outputTextArea.setCaretPosition(0);
            MainWindow.GetInstance().outputTextRadioMenuItem.doClick();
        });

        // 打开提示
        MainWindow.GetInstance().tipsMenuItem.addActionListener(e -> {
            String str = "============================================\n" +
                    "                                                  Tips\n" +
                    "-------------------------------------------------------------------------\n" +
                    "* 你可以在“Help-Tips”中随时打开Tips\n" +
                    "\n" +
                    "1. 构成乐谱的成分：\n" +
                    "\t1）paragraph Name  段落声明，以下各属性独立\n" +
                    "\t2）instrument= 0      \t演奏的乐器（非必要 默认钢琴）\n" +
                    "\t3）volume= 127        该段落的音量（非必要 默认127）\n" +
                    "\t4）speed= 90\t该段落演奏速度（非必要 默认90）\n" +
                    "\t5）1= C\t\t该段落调性（非必要 默认C调）\n" +
                    "\t6）((1))(2)|34|[55]\t音符的音名，即音高\n" +
                    "\t7）<1248{gw*}>\t音符的时值，即持续时间\n" +
                    "\t8）end\t\t段落声明结束\n" +
                    "\n" +
                    "2. 乐谱成分的解释：\n" +
                    "\t1）声部声明：标识符须以字母开头，后跟字母或数字\n" +
                    "\t2）乐器音色：见“Help-Instrument”中具体说明\n" +
                    "\t3）声部音量：最小值0（禁音）最大值127（最大音量）\n" +
                    "\t4）声部速度：每分钟四分音符个数，即BPM\n" +
                    "\t5）声部调性：CDEFGAB加上b（降号）与#（升号）\n" +
                    "\t6）“( )”内为低八度，可叠加“[ ]”内为高八度，同上\n" +
                    "\t7）“< >”内为全、2、4、6、8、16、32分音符与附点\n" +
                    "\t8）“| |”内为同时音，该符号不可叠加，意为同时演奏的音\n" +
                    "\t9）“{ }”内为连音，若音高相同则会合并成一个音\n" +
                    "\t10）声明结束：须用end结束声明，对应paragraph\n" +
                    "\n" +
                    "3. 播放乐谱的方法：\n" +
                    "\t1）通过“play( )”进行播放，( )”内为声部的标识符\n" +
                    "\t2）“&”左右的声部将同时播放，\n" +
                    "\t3）“ , ”左右的声部将先后播放\n" +
                    "===========================================";
            MainWindow.GetInstance().outputTextArea.setText(str);
            MainWindow.GetInstance().outputTextArea.setCaretPosition(0);
            MainWindow.GetInstance().outputTextRadioMenuItem.doClick();
        });

        // 打开关于
        MainWindow.GetInstance().aboutMenuItem.addActionListener(e -> {
            String str = "============================================\n" +
                    "\t\t          Euterpe II\n" +
                    "--------------------------------------------------------------------------\n" +
                    "* 名称来源于希腊神话中司管抒情诗与音乐的缪斯——欧忒耳佩\n" +
                    "   意为“令人快乐”（原Euterpe）\n" +
                    "\n" +
                    "1.简介\n" +
                    "\t通过设计原创的音乐语言，运用解释器原理设计\n" +
                    "\t以便于键盘输入为特点的，数字乐谱——Midi解释器\n" +
                    "\t包含完整的词法分析、语法分析与语义分析\n" +
                    "\t可通过内置人机良好的数字乐谱编辑器谱写乐谱\n" +
                    "\t并通过内置实时Midi播放器，加载SoundFont2音源播放\n" +
                    "\t同时包含便于扒谱的工具与生成Midi文件等功能\n" +
                    "\n" +
                    "2.项目成员\n" +
                    "\t1）项目组长，语义分析，钢琴卷帘，转调器，用户界面：Chief\n" +
                    "\t2）词法分析，Midi转Mui：yyzih\n" +
                    "\t3）语法分析，语言设计，Midi转五线谱、简谱：AsrielMao\n" +
                    "\n" +
                    "3.当前版本\n" +
                    "\t0.0.1\n" +
                    "\n" +
                    "\t\t\t\t   All Rights Reserved. \n" +
                    "    \t    Copyright © 2018-2021 Chief, yyzih and AsrielMao.\n" +
                    "============================================";
            MainWindow.GetInstance().outputTextArea.setText(str);
            MainWindow.GetInstance().outputTextArea.setCaretPosition(0);
            MainWindow.GetInstance().outputTextRadioMenuItem.doClick();
        });

    }

}
