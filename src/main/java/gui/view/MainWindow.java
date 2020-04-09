/*
 * Created by JFormDesigner on Fri Nov 22 14:26:02 CST 2019
 */

package gui.view;

import java.awt.event.*;

import java.awt.*;
import javax.swing.*;

import gui.controller.Diaglogs;
import gui.controller.FileIO;
import gui.controller.InputTexts;
import gui.controller.Menus;
import gui.entity.Status;
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

    Point pressedPoint;

    private MainWindow() {

        // 初始化组件
        initComponents();

        // 初始化钢琴卷帘组件
        PianorollCanvas.Setup();

        // 钢琴卷帘组件加入到窗口
        layeredPane.add(PianorollCanvas.GetGlcanvas(),new Integer(100));

        // 行号与滚动条
        StringBuilder lineStr = new StringBuilder();

        for (int i = 1; i < 1000; ++i)
            lineStr.append(i).append("\n");

        lineTextArea.setText(lineStr.toString());

        lineScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        lineScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        inputScrollPane.getVerticalScrollBar().addAdjustmentListener(
                e -> lineScrollPane.getVerticalScrollBar().setValue(inputScrollPane.getVerticalScrollBar().getValue()));

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
        playDirectMenuItem = new JMenuItem();
        stopDirectMenuItem = new JMenuItem();
        menu1 = new JMenu();
        convertToMuiMenuItem = new JMenuItem();
        convertToStaveMenuItem = new JMenuItem();
        convertToNmnMenuItem = new JMenuItem();
        JMenu toolMenu = new JMenu();
        transposerMenuItem = new JMenuItem();
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

        //======== this ========
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        setTitle("Euterpe II");
        setResizable(false);
        Container contentPane = getContentPane();
        contentPane.setLayout(new MigLayout(
            "insets 0,hidemode 3,gap 0 0",
            // columns
            "[450,fill]" +
            "[1150,fill]",
            // rows
            "[770,fill]0"));

        //======== leftPanel ========
        {
            leftPanel.setLayout(new MigLayout(
                "insets 0,hidemode 3,gap 0 0",
                // columns
                "[40,fill]" +
                "[410,fill]",
                // rows
                "0[30]0" +
                "[740,fill]0"));

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

                    //---- playDirectMenuItem ----
                    playDirectMenuItem.setText("Play");
                    playerMenu.add(playDirectMenuItem);

                    //---- stopDirectMenuItem ----
                    stopDirectMenuItem.setText("Stop");
                    playerMenu.add(stopDirectMenuItem);
                }
                menuBar2.add(playerMenu);

                //======== menu1 ========
                {
                    menu1.setText("Converter");

                    //---- convertToMuiMenuItem ----
                    convertToMuiMenuItem.setText("Convert Midi To Mui");
                    menu1.add(convertToMuiMenuItem);
                    menu1.addSeparator();

                    //---- convertToStaveMenuItem ----
                    convertToStaveMenuItem.setText("Convert To Stave");
                    menu1.add(convertToStaveMenuItem);

                    //---- convertToNmnMenuItem ----
                    convertToNmnMenuItem.setText("Convert To Numbered Musical Notation");
                    menu1.add(convertToNmnMenuItem);
                }
                menuBar2.add(menu1);

                //======== toolMenu ========
                {
                    toolMenu.setText("Tool");

                    //---- transposerMenuItem ----
                    transposerMenuItem.setText("Transposer");
                    toolMenu.add(transposerMenuItem);
                }
                menuBar2.add(toolMenu);

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
            leftPanel.add(menuBar2, "cell 0 0 2 1");

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
            leftPanel.add(lineScrollPane, "cell 0 1");

            //======== inputScrollPane ========
            {

                //---- inputTextPane ----
                inputTextPane.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
                inputTextPane.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
                inputTextPane.setBorder(null);
                inputTextPane.setDragEnabled(true);
                inputScrollPane.setViewportView(inputTextPane);
            }
            leftPanel.add(inputScrollPane, "cell 1 1");
        }
        contentPane.add(leftPanel, "cell 0 0");

        //======== layeredPane ========
        {

            //======== outputScrollPane ========
            {

                //---- outputTextArea ----
                outputTextArea.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
                outputTextArea.setEditable(false);
                outputTextArea.setBorder(null);
                outputScrollPane.setViewportView(outputTextArea);
            }
            layeredPane.add(outputScrollPane, JLayeredPane.POPUP_LAYER);
            outputScrollPane.setBounds(80, 185, 110, 185);
        }
        contentPane.add(layeredPane, "cell 1 0");
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
    public JMenuItem playDirectMenuItem;
    public JMenuItem stopDirectMenuItem;
    private JMenu menu1;
    public JMenuItem convertToMuiMenuItem;
    public JMenuItem convertToStaveMenuItem;
    public JMenuItem convertToNmnMenuItem;
    public JMenuItem transposerMenuItem;
    public JMenuItem instruMenuItem;
    public JMenuItem tipsMenuItem;
    public JMenuItem aboutMenuItem;
    private JScrollPane lineScrollPane;
    private JTextArea lineTextArea;
    private JScrollPane inputScrollPane;
    public JTextPane inputTextPane;
    private JLayeredPane layeredPane;
    private JScrollPane outputScrollPane;
    public JTextArea outputTextArea;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
