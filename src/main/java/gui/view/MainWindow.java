/*
 * Created by JFormDesigner on Fri Nov 22 14:26:02 CST 2019
 */

package gui.view;

import java.awt.event.*;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.metal.MetalSliderUI;

import gui.controller.Diaglogs;
import gui.controller.FileIO;
import gui.controller.InputTexts;
import gui.controller.Menus;
import gui.entity.Status;
import pianoroll.component.Pianoroll;
import pianoroll.component.PianorollCanvas;
import midiplayer.component.MidiPlayer;
import net.miginfocom.swing.*;

/**
 * @author Chief
 */
public class MainWindow extends JFrame {

    // 单例
    private static final MainWindow instance = new MainWindow();

    // 获取单例
    public static MainWindow GetInstance() {
        return instance;
    }

    boolean playSliderPressed;

    private MainWindow() {

        playSliderPressed=false;

        // 初始化组件
        initComponents();

        // 初始化钢琴卷帘组件
        PianorollCanvas.Setup();

        // 钢琴卷帘组件加入到窗口
        layeredPane.add(PianorollCanvas.GetGlcanvas(), new Integer(100));

        // 设置调性下拉框
        keyComboBox.setSelectedIndex(4);
        keyComboBox.addActionListener(e -> {
            Pianoroll.GetInstance().getPianoController().setPitchOffset(keyComboBox.getSelectedIndex() - 4 + (octaveComboBox.getSelectedIndex() - 2) * 12);
        });

        // 设置音高下拉框
        octaveComboBox.setSelectedIndex(2);
        octaveComboBox.addActionListener(e -> {
            Pianoroll.GetInstance().getPianoController().setPitchOffset(keyComboBox.getSelectedIndex() - 4 + (octaveComboBox.getSelectedIndex() - 2) * 12);
        });

        // 开关延音
        sustainToggleBtn.addActionListener(e-> {
            if(sustainToggleBtn.isSelected())
                sustainToggleBtn.setText("Sustain On");
            else
                sustainToggleBtn.setText("Sustain Off");
            Pianoroll.GetInstance().getPianoController().setSustainEnable(sustainToggleBtn.isSelected());
        });

        // 进度条以及时间改变
        Timer timer=new Timer(50,e -> {
            if (playSlider.isEnabled() && !playSliderPressed) {
                playSlider.setValue((int) (MidiPlayer.GetInstance().getSequencer().getMicrosecondPosition() / (float)MidiPlayer.GetInstance().getSequencer().getMicrosecondLength() * 1000000));
            }
        });
        timer.start();

        // 播放进度条
        playSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (playSlider.isEnabled()) {
                    playSliderPressed = true;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (playSlider.isEnabled()) {
                    playSliderPressed = false;
                    long currentMicrosecond = (long) (playSlider.getValue() / 1000000.0f * MidiPlayer.GetInstance().getSequencer().getMicrosecondLength());
                    MidiPlayer.GetInstance().setMicrosecondPosition(currentMicrosecond);
                    Pianoroll.GetInstance().setCurrentTime(currentMicrosecond / 1_000_000f
                            , MidiPlayer.GetInstance().getSequencer().getTickPosition()
                            , MidiPlayer.GetInstance().getSequencer().getSequence().getResolution());
                }
            }
        });

        // 行号与滚动条
        StringBuilder lineStr = new StringBuilder();

        for (int i = 1; i < 10000; ++i)
            lineStr.append(i).append("\n");

        lineTextArea.setText(lineStr.toString());

        lineScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        lineScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        inputScrollPane.getVerticalScrollBar().addAdjustmentListener(
                e -> lineScrollPane.getVerticalScrollBar().setValue(inputScrollPane.getVerticalScrollBar().getValue()));

        // 单选按钮添加到组
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(pianorollRadioMenuItem);
        buttonGroup.add(outputTextRadioMenuItem);
        buttonGroup.add(staveRadioMenuItem);
        buttonGroup.add(nmnRadioMenuItem);

        layeredPane.setLayer(outputScrollPane, 100);

        // 关闭窗口
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (Status.GetCurrentStatus().getIsEdited())
                    if (!Diaglogs.GetInstance().askSaving())
                        return;

                if (FileIO.GetInstance().getTempMidiFile() != null && FileIO.GetInstance().getTempMidiFile().exists())
                    FileIO.GetInstance().getTempMidiFile().delete();

                MidiPlayer.GetInstance().close();
                System.exit(0);
            }
        });
    }

    public MainWindow init() {
        Menus.GetInstance().init();

        InputTexts.GetInstance().init();

        return instance;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        leftPanel = new JPanel();
        menuBar2 = new JMenuBar();
        JMenu fileMenu = new JMenu();
        newEmptyMenuItem = new JMenuItem();
        newTemplateMenuItem = new JMenuItem();
        separator2 = new JSeparator();
        openMenuItem = new JMenuItem();
        separator3 = new JSeparator();
        saveMenuItem = new JMenuItem();
        saveAsMenuItem = new JMenuItem();
        separator4 = new JSeparator();
        exportMidiMenuItem = new JMenuItem();
        exitMenuItem = new JMenuItem();
        JMenu playerMenu = new JMenu();
        loadSoundFontMenuItem = new JMenuItem();
        rebuildMenuItem = new JMenuItem();
        playDirectMenuItem = new JMenuItem();
        stopDirectMenuItem = new JMenuItem();
        playExternalMenuItem = new JMenuItem();
        arduinoMenu = new JMenu();
        converterMenu = new JMenu();
        convertToMuiMenuItem = new JMenuItem();
        convertToStaveMenuItem = new JMenuItem();
        convertToNmnMenuItem = new JMenuItem();
        rightPanelMenu = new JMenu();
        pianorollRadioMenuItem = new JRadioButtonMenuItem();
        outputTextRadioMenuItem = new JRadioButtonMenuItem();
        staveRadioMenuItem = new JRadioButtonMenuItem();
        nmnRadioMenuItem = new JRadioButtonMenuItem();
        JMenu helpMenu = new JMenu();
        instruMenuItem = new JMenuItem();
        tipsMenuItem = new JMenuItem();
        aboutMenuItem = new JMenuItem();
        lineScrollPane = new JScrollPane();
        lineTextArea = new JTextArea();
        inputScrollPane = new JScrollPane();
        inputTextPane = new JTextPane();
        layeredPane = new JLayeredPane();
        outputScrollPane = new JScrollPane();
        outputTextArea = new JTextArea();
        panel1 = new JPanel();
        playSlider = new JSlider();
        keyComboBox = new JComboBox<>();
        octaveComboBox = new JComboBox<>();
        sustainToggleBtn = new JToggleButton();
        timeLength = new JLabel();
        currTime = new JLabel();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        setTitle("Euterpe II");
        setResizable(false);
        Container contentPane = getContentPane();
        contentPane.setLayout(null);

        //======== leftPanel ========
        {
            leftPanel.setLayout(null);

            //======== menuBar2 ========
            {

                //======== fileMenu ========
                {
                    fileMenu.setText("File");

                    //---- newEmptyMenuItem ----
                    newEmptyMenuItem.setText("New - Empty");
                    fileMenu.add(newEmptyMenuItem);

                    //---- newTemplateMenuItem ----
                    newTemplateMenuItem.setText("New - Template");
                    fileMenu.add(newTemplateMenuItem);
                    fileMenu.add(separator2);

                    //---- openMenuItem ----
                    openMenuItem.setText("Open");
                    fileMenu.add(openMenuItem);
                    fileMenu.add(separator3);

                    //---- saveMenuItem ----
                    saveMenuItem.setText("Save");
                    fileMenu.add(saveMenuItem);

                    //---- saveAsMenuItem ----
                    saveAsMenuItem.setText("Save As...");
                    fileMenu.add(saveAsMenuItem);
                    fileMenu.add(separator4);

                    //---- exportMidiMenuItem ----
                    exportMidiMenuItem.setText("Export Midi File");
                    fileMenu.add(exportMidiMenuItem);
                    fileMenu.addSeparator();

                    //---- exitMenuItem ----
                    exitMenuItem.setText("Exit");
                    fileMenu.add(exitMenuItem);
                }
                menuBar2.add(fileMenu);

                //======== playerMenu ========
                {
                    playerMenu.setText("MidiPlayer");

                    //---- loadSoundFontMenuItem ----
                    loadSoundFontMenuItem.setText("Load SoundFont");
                    playerMenu.add(loadSoundFontMenuItem);
                    playerMenu.addSeparator();

                    //---- rebuildMenuItem ----
                    rebuildMenuItem.setText("Rebuild Midi");
                    playerMenu.add(rebuildMenuItem);
                    playerMenu.addSeparator();

                    //---- playDirectMenuItem ----
                    playDirectMenuItem.setText("Play");
                    playerMenu.add(playDirectMenuItem);

                    //---- stopDirectMenuItem ----
                    stopDirectMenuItem.setText("Stop");
                    playerMenu.add(stopDirectMenuItem);
                    playerMenu.addSeparator();

                    //---- playExternalMenuItem ----
                    playExternalMenuItem.setText("Play From External ");
                    playerMenu.add(playExternalMenuItem);
                }
                menuBar2.add(playerMenu);

                //======== arduinoMenu ========
                {
                    arduinoMenu.setText("Arduino");
                }
                menuBar2.add(arduinoMenu);

                //======== converterMenu ========
                {
                    converterMenu.setText("Converter");

                    //---- convertToMuiMenuItem ----
                    convertToMuiMenuItem.setText("Convert Midi To Mui");
                    converterMenu.add(convertToMuiMenuItem);
                    converterMenu.addSeparator();

                    //---- convertToStaveMenuItem ----
                    convertToStaveMenuItem.setText("Convert To Stave");
                    converterMenu.add(convertToStaveMenuItem);

                    //---- convertToNmnMenuItem ----
                    convertToNmnMenuItem.setText("Convert To Numbered Musical Notation");
                    converterMenu.add(convertToNmnMenuItem);
                }
                menuBar2.add(converterMenu);

                //======== rightPanelMenu ========
                {
                    rightPanelMenu.setText("Switch Right Panel");

                    //---- pianorollRadioMenuItem ----
                    pianorollRadioMenuItem.setText("Pianoroll Panel");
                    pianorollRadioMenuItem.setSelected(true);
                    rightPanelMenu.add(pianorollRadioMenuItem);
                    rightPanelMenu.addSeparator();

                    //---- outputTextRadioMenuItem ----
                    outputTextRadioMenuItem.setText("Output Text Panel");
                    rightPanelMenu.add(outputTextRadioMenuItem);
                    rightPanelMenu.addSeparator();

                    //---- staveRadioMenuItem ----
                    staveRadioMenuItem.setText("Stave Panel");
                    rightPanelMenu.add(staveRadioMenuItem);

                    //---- nmnRadioMenuItem ----
                    nmnRadioMenuItem.setText("Numbered Musical Notaion Panel");
                    rightPanelMenu.add(nmnRadioMenuItem);
                }
                menuBar2.add(rightPanelMenu);

                //======== helpMenu ========
                {
                    helpMenu.setText("Help");

                    //---- instruMenuItem ----
                    instruMenuItem.setText("Instruments");
                    helpMenu.add(instruMenuItem);

                    //---- tipsMenuItem ----
                    tipsMenuItem.setText("Tips");
                    helpMenu.add(tipsMenuItem);

                    //---- aboutMenuItem ----
                    aboutMenuItem.setText("About");
                    helpMenu.add(aboutMenuItem);
                }
                menuBar2.add(helpMenu);
            }
            leftPanel.add(menuBar2);
            menuBar2.setBounds(0, 0, 450, 30);

            //======== lineScrollPane ========
            {

                //---- lineTextArea ----
                lineTextArea.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
                lineTextArea.setEnabled(false);
                lineTextArea.setEditable(false);
                lineTextArea.setBorder(null);
                lineTextArea.setBackground(Color.white);
                lineTextArea.setForeground(new Color(153, 153, 153));
                lineScrollPane.setViewportView(lineTextArea);
            }
            leftPanel.add(lineScrollPane);
            lineScrollPane.setBounds(0, 31, 40, 742);

            //======== inputScrollPane ========
            {

                //---- inputTextPane ----
                inputTextPane.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
                inputTextPane.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
                inputTextPane.setBorder(null);
                inputTextPane.setDragEnabled(true);
                inputScrollPane.setViewportView(inputTextPane);
            }
            leftPanel.add(inputScrollPane);
            inputScrollPane.setBounds(39, 31, 411, 742);

            {
                // compute preferred size
                Dimension preferredSize = new Dimension();
                for(int i = 0; i < leftPanel.getComponentCount(); i++) {
                    Rectangle bounds = leftPanel.getComponent(i).getBounds();
                    preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                    preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                }
                Insets insets = leftPanel.getInsets();
                preferredSize.width += insets.right;
                preferredSize.height += insets.bottom;
                leftPanel.setMinimumSize(preferredSize);
                leftPanel.setPreferredSize(preferredSize);
            }
        }
        contentPane.add(leftPanel);
        leftPanel.setBounds(0, 0, 449, 768);

        //======== layeredPane ========
        {

            //======== outputScrollPane ========
            {
                outputScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                outputScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

                //---- outputTextArea ----
                outputTextArea.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
                outputTextArea.setEditable(false);
                outputTextArea.setBorder(null);
                outputScrollPane.setViewportView(outputTextArea);
            }
            layeredPane.add(outputScrollPane, JLayeredPane.DEFAULT_LAYER);
            outputScrollPane.setBounds(0, 0, 1145, 773);

            //======== panel1 ========
            {
                panel1.setLayout(null);

                //---- playSlider ----
                playSlider.setBorder(null);
                playSlider.setFocusable(false);
                playSlider.setValue(0);
                playSlider.setMaximum(1000000);
                playSlider.setEnabled(false);
                panel1.add(playSlider);
                playSlider.setBounds(45, 5, 670, 20);

                //---- keyComboBox ----
                keyComboBox.setModel(new DefaultComboBoxModel<>(new String[] {
                    "1 = Ab (-4)",
                    "1 = A (-3)",
                    "1 = Bb (-2)",
                    "1 = B (-1)",
                    "1 = C (0)",
                    "1 = Db (+1)",
                    "1 = D (+2)",
                    "1 = Eb (+3)",
                    "1 = E (+4)",
                    "1 = F (+5)",
                    "1 = F# (+6)",
                    "1 = G (+7)"
                }));
                keyComboBox.setMaximumRowCount(12);
                keyComboBox.setFocusable(false);
                keyComboBox.setSelectedIndex(4);
                panel1.add(keyComboBox);
                keyComboBox.setBounds(880, 5, 120, 20);

                //---- octaveComboBox ----
                octaveComboBox.setMaximumRowCount(5);
                octaveComboBox.setModel(new DefaultComboBoxModel<>(new String[] {
                    "Octave -2",
                    "Octave -1",
                    "Octave  0",
                    "Octave  1",
                    "Octave  2"
                }));
                octaveComboBox.setFocusable(false);
                octaveComboBox.setSelectedIndex(2);
                panel1.add(octaveComboBox);
                octaveComboBox.setBounds(1010, 5, 120, 20);

                //---- sustainToggleBtn ----
                sustainToggleBtn.setText("Sustain Off");
                sustainToggleBtn.setFocusable(false);
                panel1.add(sustainToggleBtn);
                sustainToggleBtn.setBounds(760, 5, 105, 20);

                //---- timeLength ----
                timeLength.setText("1:21");
                panel1.add(timeLength);
                timeLength.setBounds(715, 0, 40, 30);

                //---- currTime ----
                currTime.setText("00:00");
                panel1.add(currTime);
                currTime.setBounds(5, 0, 40, 30);
            }
            layeredPane.add(panel1, JLayeredPane.POPUP_LAYER);
            panel1.setBounds(0, 0, 1145, 30);
        }
        contentPane.add(layeredPane);
        layeredPane.setBounds(450, 0, 1150, 770);

        {
            // compute preferred size
            Dimension preferredSize = new Dimension();
            for(int i = 0; i < contentPane.getComponentCount(); i++) {
                Rectangle bounds = contentPane.getComponent(i).getBounds();
                preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
            }
            Insets insets = contentPane.getInsets();
            preferredSize.width += insets.right;
            preferredSize.height += insets.bottom;
            contentPane.setMinimumSize(preferredSize);
            contentPane.setPreferredSize(preferredSize);
        }
        setSize(1600, 800);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel leftPanel;
    private JMenuBar menuBar2;
    public JMenuItem newEmptyMenuItem;
    public JMenuItem newTemplateMenuItem;
    private JSeparator separator2;
    public JMenuItem openMenuItem;
    private JSeparator separator3;
    public JMenuItem saveMenuItem;
    public JMenuItem saveAsMenuItem;
    private JSeparator separator4;
    public JMenuItem exportMidiMenuItem;
    public JMenuItem exitMenuItem;
    public JMenuItem loadSoundFontMenuItem;
    public JMenuItem rebuildMenuItem;
    public JMenuItem playDirectMenuItem;
    public JMenuItem stopDirectMenuItem;
    public JMenuItem playExternalMenuItem;
    private JMenu arduinoMenu;
    private JMenu converterMenu;
    public JMenuItem convertToMuiMenuItem;
    public JMenuItem convertToStaveMenuItem;
    public JMenuItem convertToNmnMenuItem;
    private JMenu rightPanelMenu;
    public JRadioButtonMenuItem pianorollRadioMenuItem;
    public JRadioButtonMenuItem outputTextRadioMenuItem;
    public JRadioButtonMenuItem staveRadioMenuItem;
    public JRadioButtonMenuItem nmnRadioMenuItem;
    public JMenuItem instruMenuItem;
    public JMenuItem tipsMenuItem;
    public JMenuItem aboutMenuItem;
    private JScrollPane lineScrollPane;
    private JTextArea lineTextArea;
    private JScrollPane inputScrollPane;
    public JTextPane inputTextPane;
    public JLayeredPane layeredPane;
    public JScrollPane outputScrollPane;
    public JTextArea outputTextArea;
    private JPanel panel1;
    public JSlider playSlider;
    private JComboBox<String> keyComboBox;
    private JComboBox<String> octaveComboBox;
    private JToggleButton sustainToggleBtn;
    private JLabel timeLength;
    private JLabel currTime;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
