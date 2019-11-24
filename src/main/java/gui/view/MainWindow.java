/*
 * Created by JFormDesigner on Fri Nov 22 14:26:02 CST 2019
 */

package gui.view;

import java.awt.event.*;
import javax.swing.border.*;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

import gui.controller.InputTexts;
import gui.controller.Menus;
import gui.controller.PianoCanvas;
import midi.component.MidiPiano;
import midi.component.MidiPlayer;
import net.miginfocom.swing.*;

/**
 * @author Chief
 */
public class MainWindow extends JFrame {

    // 单例
    private static MainWindow instance = new MainWindow();

    // 获取单例
    public static MainWindow GetInstance() {
        return instance;
    }

    Point pressedPoint;

    private MainWindow() {

        // 取消Windows自带顶边框
        setUndecorated(true);

        // 初始化组件
        initComponents();

        // 初始化钢琴卷帘组件并加入窗口
        PianoCanvas.Setup();
        getContentPane().add(PianoCanvas.GetGlcanvas(), "cell 2 0");

        // 行号与滚动条
        StringBuilder lineStr = new StringBuilder();

        for (int i = 1; i < 1000; i++)
            lineStr.append(i).append("\n");

        lineTextArea.setText(lineStr.toString());

        lineScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        lineScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        inputScrollPane.getVerticalScrollBar().addAdjustmentListener(
                e -> lineScrollPane.getVerticalScrollBar().setValue(inputScrollPane.getVerticalScrollBar().getValue()));
    }

    public MainWindow init() {
        Menus.GetInstance().init();

        InputTexts.GetInstance().init();

        return instance;
    }

    // 最小化窗口
    private void minimizeButtonActionPerformed(ActionEvent e) {
        setExtendedState(JFrame.ICONIFIED);
    }

    // 关闭窗口
    private void closeButtonActionPerformed(ActionEvent e) {
        MidiPlayer.GetInstance().close();
        System.exit(0);
    }

    // 拖动窗口-按下鼠标
    private void titleMenuMousePressed(MouseEvent e) {
        pressedPoint = e.getPoint();
    }

    // 拖动窗口-移动位置
    private void titleMenuMouseDragged(MouseEvent e) {
        Point point = e.getPoint();
        Point locationPoint = getLocation();
        int x = locationPoint.x + point.x - pressedPoint.x;
        int y = locationPoint.y + point.y - pressedPoint.y;
        setLocation(x, y);
    }

    private void keyLabelMousePressed(MouseEvent e) {
        // TODO add your code here
    }

    private void sustainLabelMousePressed(MouseEvent e) {
        // TODO add your code here
    }

    private void OctaveLabelMousePressed(MouseEvent e) {
        // TODO add your code here
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        menuBar1 = new JMenuBar();
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
        JMenu toolMenu = new JMenu();
        transposerMenuItem = new JMenuItem();
        JMenu helpMenu = new JMenu();
        instruMenuItem = new JMenuItem();
        tipsMenuItem = new JMenuItem();
        aboutMenuItem = new JMenuItem();
        titleMenu = new JMenu();
        minimizeButton = new JButton();
        closeButton = new JButton();
        lineScrollPane = new JScrollPane();
        lineTextArea = new JTextArea();
        inputScrollPane = new JScrollPane();
        inputTextPane = new JTextPane();
        button1 = new JButton();
        outputScrollPane = new JScrollPane();
        outputTextArea = new JTextArea();
        pianoControllPanel = new JPanel();
        progressBar1 = new JProgressBar();
        panel1 = new JPanel();
        JLabel label1 = new JLabel();
        keyLabel = new JLabel();
        panel2 = new JPanel();
        JLabel label2 = new JLabel();
        sustainLabel = new JLabel();
        panel3 = new JPanel();
        JLabel label3 = new JLabel();
        OctaveLabel = new JLabel();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        setTitle("Euterpe 2");
        setResizable(false);
        Container contentPane = getContentPane();
        contentPane.setLayout(new MigLayout(
            "insets 0,hidemode 3,gap 0 0",
            // columns
            "[40,fill]" +
            "[430,fill]" +
            "[810,fill]",
            // rows
            "[690,fill]0" +
            "[45,fill]0"));

        //======== menuBar1 ========
        {
            menuBar1.setMinimumSize(new Dimension(100, 30));
            menuBar1.setPreferredSize(new Dimension(100, 30));
            menuBar1.setMaximumSize(new Dimension(100, 32768));

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
            menuBar1.add(fileMenu);

            //======== playerMenu ========
            {
                playerMenu.setText("Midi Player");

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
            menuBar1.add(playerMenu);

            //======== toolMenu ========
            {
                toolMenu.setText("Tool");

                //---- transposerMenuItem ----
                transposerMenuItem.setText("Transposer");
                toolMenu.add(transposerMenuItem);
            }
            menuBar1.add(toolMenu);

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
            menuBar1.add(helpMenu);

            //======== titleMenu ========
            {
                titleMenu.setText("                                                                                               Euterpe 2                                                                                                                                             ");
                titleMenu.setOpaque(false);
                titleMenu.setBorderPainted(false);
                titleMenu.setEnabled(false);
                titleMenu.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        titleMenuMousePressed(e);
                    }
                });
                titleMenu.addMouseMotionListener(new MouseMotionAdapter() {
                    @Override
                    public void mouseDragged(MouseEvent e) {
                        titleMenuMouseDragged(e);
                    }
                });
            }
            menuBar1.add(titleMenu);

            //---- minimizeButton ----
            minimizeButton.setBorderPainted(false);
            minimizeButton.setFont(new Font("Consolas", Font.PLAIN, 12));
            minimizeButton.setIcon(new ImageIcon(getClass().getResource("/icons/minimize0.png")));
            minimizeButton.setRolloverIcon(new ImageIcon(getClass().getResource("/icons/minimize1.png")));
            minimizeButton.setBorder(null);
            minimizeButton.setRolloverSelectedIcon(new ImageIcon(getClass().getResource("/icons/minimize1.png")));
            minimizeButton.addActionListener(e -> minimizeButtonActionPerformed(e));
            menuBar1.add(minimizeButton);

            //---- closeButton ----
            closeButton.setBorderPainted(false);
            closeButton.setFont(new Font("Consolas", Font.PLAIN, 12));
            closeButton.setIcon(new ImageIcon(getClass().getResource("/icons/close0.png")));
            closeButton.setRolloverIcon(new ImageIcon(getClass().getResource("/icons/close1.png")));
            closeButton.setBorder(null);
            closeButton.setRolloverSelectedIcon(new ImageIcon(getClass().getResource("/icons/close1.png")));
            closeButton.addActionListener(e -> closeButtonActionPerformed(e));
            menuBar1.add(closeButton);
        }
        setJMenuBar(menuBar1);

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
        contentPane.add(lineScrollPane, "cell 0 0");

        //======== inputScrollPane ========
        {

            //---- inputTextPane ----
            inputTextPane.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
            inputTextPane.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
            inputTextPane.setBorder(null);
            inputTextPane.setDragEnabled(true);
            inputScrollPane.setViewportView(inputTextPane);
        }
        contentPane.add(inputScrollPane, "cell 1 0");

        //---- button1 ----
        button1.setText("Play");
        button1.setPreferredSize(new Dimension(40, 40));
        button1.setMinimumSize(new Dimension(40, 40));
        button1.setMaximumSize(new Dimension(40, 40));
        contentPane.add(button1, "cell 0 1,grow");

        //======== outputScrollPane ========
        {

            //---- outputTextArea ----
            outputTextArea.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
            outputTextArea.setEditable(false);
            outputScrollPane.setViewportView(outputTextArea);
        }
        contentPane.add(outputScrollPane, "cell 1 1");

        //======== pianoControllPanel ========
        {
            pianoControllPanel.setLayout(new MigLayout(
                "hidemode 3",
                // columns
                "[400,fill]unrel" +
                "[120,fill]rel" +
                "[120,fill]rel" +
                "[120,fill]0",
                // rows
                "rel[30,fill]rel"));
            pianoControllPanel.add(progressBar1, "cell 0 0,grow");

            //======== panel1 ========
            {
                panel1.setBackground(new Color(58, 60, 66));
                panel1.setBorder(LineBorder.createBlackLineBorder());
                panel1.setLayout(new MigLayout(
                    "hidemode 3",
                    // columns
                    "[50,fill]" +
                    "[50,fill]",
                    // rows
                    "0[45]0"));

                //---- label1 ----
                label1.setText("Key 1 =");
                label1.setHorizontalAlignment(SwingConstants.CENTER);
                label1.setForeground(new Color(184, 179, 178));
                panel1.add(label1, "cell 0 0");

                //---- keyLabel ----
                keyLabel.setText("C(0)");
                keyLabel.setHorizontalAlignment(SwingConstants.CENTER);
                keyLabel.setBackground(new Color(229, 228, 233));
                keyLabel.setForeground(new Color(116, 116, 116));
                keyLabel.setOpaque(true);
                keyLabel.setBorder(LineBorder.createBlackLineBorder());
                keyLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        keyLabelMousePressed(e);
                    }
                });
                panel1.add(keyLabel, "cell 1 0");
            }
            pianoControllPanel.add(panel1, "cell 1 0,grow");

            //======== panel2 ========
            {
                panel2.setBackground(new Color(58, 60, 66));
                panel2.setBorder(LineBorder.createBlackLineBorder());
                panel2.setLayout(new MigLayout(
                    "hidemode 3",
                    // columns
                    "[50,fill]" +
                    "[50,fill]",
                    // rows
                    "0[45]0"));

                //---- label2 ----
                label2.setText("Sustain");
                label2.setHorizontalAlignment(SwingConstants.CENTER);
                label2.setForeground(new Color(184, 179, 178));
                panel2.add(label2, "cell 0 0");

                //---- sustainLabel ----
                sustainLabel.setText("127");
                sustainLabel.setHorizontalAlignment(SwingConstants.CENTER);
                sustainLabel.setBackground(new Color(229, 228, 233));
                sustainLabel.setForeground(new Color(116, 116, 116));
                sustainLabel.setOpaque(true);
                sustainLabel.setBorder(LineBorder.createBlackLineBorder());
                sustainLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        sustainLabelMousePressed(e);
                    }
                });
                panel2.add(sustainLabel, "cell 1 0");
            }
            pianoControllPanel.add(panel2, "cell 2 0,grow");

            //======== panel3 ========
            {
                panel3.setBackground(new Color(58, 60, 66));
                panel3.setBorder(LineBorder.createBlackLineBorder());
                panel3.setLayout(new MigLayout(
                    "hidemode 3",
                    // columns
                    "[50,fill]" +
                    "[50,fill]",
                    // rows
                    "0[45]0"));

                //---- label3 ----
                label3.setText("Octave");
                label3.setHorizontalAlignment(SwingConstants.CENTER);
                label3.setForeground(new Color(184, 179, 178));
                panel3.add(label3, "cell 0 0");

                //---- OctaveLabel ----
                OctaveLabel.setText("0");
                OctaveLabel.setHorizontalAlignment(SwingConstants.CENTER);
                OctaveLabel.setBackground(new Color(229, 228, 233));
                OctaveLabel.setForeground(new Color(116, 116, 116));
                OctaveLabel.setOpaque(true);
                OctaveLabel.setBorder(LineBorder.createBlackLineBorder());
                OctaveLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        OctaveLabelMousePressed(e);
                    }
                });
                panel3.add(OctaveLabel, "cell 1 0");
            }
            pianoControllPanel.add(panel3, "cell 3 0");
        }
        contentPane.add(pianoControllPanel, "cell 2 1,grow");
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JMenuBar menuBar1;
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
    public JMenuItem transposerMenuItem;
    public JMenuItem instruMenuItem;
    public JMenuItem tipsMenuItem;
    public JMenuItem aboutMenuItem;
    private JMenu titleMenu;
    private JButton minimizeButton;
    private JButton closeButton;
    private JScrollPane lineScrollPane;
    private JTextArea lineTextArea;
    private JScrollPane inputScrollPane;
    public JTextPane inputTextPane;
    private JButton button1;
    private JScrollPane outputScrollPane;
    public JTextArea outputTextArea;
    private JPanel pianoControllPanel;
    private JProgressBar progressBar1;
    private JPanel panel1;
    private JLabel keyLabel;
    private JPanel panel2;
    private JLabel sustainLabel;
    private JPanel panel3;
    private JLabel OctaveLabel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
