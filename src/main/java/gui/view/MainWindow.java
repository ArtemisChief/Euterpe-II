/*
 * Created by JFormDesigner on Fri Nov 22 14:26:02 CST 2019
 */

package gui.view;

import java.awt.event.*;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;

import gui.controller.Diaglogs;
import gui.controller.FileIO;
import gui.controller.InputTexts;
import gui.controller.Menus;
import gui.entity.Status;
import pianoroll.component.Pianoroll;
import pianoroll.component.PianorollCanvas;
import midiplayer.MidiPlayer;
import converter.component.NmnCanvas;

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

    private MainWindow() {

        // 初始化组件
        initComponents();

        // 初始化钢琴卷帘组件
        PianorollCanvas.Setup();

        // 钢琴卷帘组件加入到窗口
        layeredPane.add(PianorollCanvas.GetGlcanvas(), new Integer(100));

        // 初始化简谱转换器
        NmnCanvas.Setup();

        // 简谱转换器加入到窗口
        layeredPane.add(NmnCanvas.GetGlcanvas(), new Integer(100));

        // 行号与滚动条
        StringBuilder lineStr = new StringBuilder();

        for (int i = 1; i < 100000; ++i)
            lineStr.append(i).append("\n");

        lineTextArea.setText(lineStr.toString());

        lineScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        lineScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        inputScrollPane.getVerticalScrollBar().addAdjustmentListener(
                e -> lineScrollPane.getVerticalScrollBar().setValue(inputScrollPane.getVerticalScrollBar().getValue()));

        // 设置层叠面板中的文字面板层次
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
        exitMenuItem = new JMenuItem();
        JMenu playerMenu = new JMenu();
        loadSoundFontMenuItem = new JMenuItem();
        rebuildMenuItem = new JMenuItem();
        playExternalMenuItem = new JMenuItem();
        exportMidiMenuItem = new JMenuItem();
        loadMidiFileMenuItem = new JMenuItem();
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
        sustainToggleBtn = new JToggleButton();
        timeLength = new JLabel();
        currTime = new JLabel();
        playBtn = new JButton();
        tonalityTextField = new JTextField();
        octaveTextField = new JTextField();

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
                    fileMenu.addSeparator();

                    //---- exitMenuItem ----
                    exitMenuItem.setText("Exit");
                    fileMenu.add(exitMenuItem);
                }
                menuBar2.add(fileMenu);

                //======== playerMenu ========
                {
                    playerMenu.setText("Midi");

                    //---- loadSoundFontMenuItem ----
                    loadSoundFontMenuItem.setText("Load SoundFont");
                    playerMenu.add(loadSoundFontMenuItem);
                    playerMenu.addSeparator();

                    //---- rebuildMenuItem ----
                    rebuildMenuItem.setText("Rebuild Midi");
                    playerMenu.add(rebuildMenuItem);
                    playerMenu.addSeparator();

                    //---- playExternalMenuItem ----
                    playExternalMenuItem.setText("Play From External ");
                    playerMenu.add(playExternalMenuItem);
                    playerMenu.addSeparator();

                    //---- exportMidiMenuItem ----
                    exportMidiMenuItem.setText("Export Midi File");
                    playerMenu.add(exportMidiMenuItem);

                    //---- loadMidiFileMenuItem ----
                    loadMidiFileMenuItem.setText("Load Midi File");
                    playerMenu.add(loadMidiFileMenuItem);
                }
                menuBar2.add(playerMenu);

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
            lineScrollPane.setBounds(0, 31, 50, 742);

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
            inputScrollPane.setBounds(50, 31, 400, 742);

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

                //---- outputTextArea ----
                outputTextArea.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
                outputTextArea.setEditable(false);
                outputTextArea.setBorder(null);
                outputScrollPane.setViewportView(outputTextArea);
            }
            layeredPane.add(outputScrollPane, JLayeredPane.DEFAULT_LAYER);
            outputScrollPane.setBounds(0, 31, 1145, 742);

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
                playSlider.setBounds(105, 5, 730, 20);

                //---- sustainToggleBtn ----
                sustainToggleBtn.setText("Sustain Off");
                sustainToggleBtn.setFocusable(false);
                panel1.add(sustainToggleBtn);
                sustainToggleBtn.setBounds(880, 5, 90, 20);

                //---- timeLength ----
                timeLength.setText("00:00");
                panel1.add(timeLength);
                timeLength.setBounds(835, 0, 40, 30);

                //---- currTime ----
                currTime.setText("00:00");
                panel1.add(currTime);
                currTime.setBounds(65, 0, 40, 30);

                //---- playBtn ----
                playBtn.setText("\u25b6");
                playBtn.setForeground(new Color(128, 185, 226));
                playBtn.setFocusable(false);
                panel1.add(playBtn);
                playBtn.setBounds(5, 5, 50, 20);

                //---- tonalityTextField ----
                tonalityTextField.setText("1 = C (0)");
                tonalityTextField.setEditable(false);
                tonalityTextField.setFocusable(false);
                tonalityTextField.setRequestFocusEnabled(false);
                tonalityTextField.setVerifyInputWhenFocusTarget(false);
                tonalityTextField.setHorizontalAlignment(SwingConstants.LEFT);
                panel1.add(tonalityTextField);
                tonalityTextField.setBounds(980, 5, 75, 20);

                //---- octaveTextField ----
                octaveTextField.setEditable(false);
                octaveTextField.setFocusable(false);
                octaveTextField.setRequestFocusEnabled(false);
                octaveTextField.setVerifyInputWhenFocusTarget(false);
                octaveTextField.setForeground(new Color(51, 59, 66));
                octaveTextField.setText("Octave  0");
                octaveTextField.setHorizontalAlignment(SwingConstants.CENTER);
                panel1.add(octaveTextField);
                octaveTextField.setBounds(1065, 5, 70, 20);
            }
            layeredPane.add(panel1, JLayeredPane.POPUP_LAYER);
            panel1.setBounds(0, 0, 1145, 31);
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
    public JMenuItem exitMenuItem;
    public JMenuItem loadSoundFontMenuItem;
    public JMenuItem rebuildMenuItem;
    public JMenuItem playExternalMenuItem;
    public JMenuItem exportMidiMenuItem;
    public JMenuItem loadMidiFileMenuItem;
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
    public JToggleButton sustainToggleBtn;
    public JLabel timeLength;
    public JLabel currTime;
    public JButton playBtn;
    public JTextField tonalityTextField;
    public JTextField octaveTextField;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
