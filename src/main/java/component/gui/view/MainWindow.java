/*
 * Created by JFormDesigner on Fri Nov 22 14:26:02 CST 2019
 */

package component.gui.view;

import java.awt.event.*;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;
import com.jogamp.newt.event.KeyListener;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;

import component.gui.controller.InputTexts;
import component.gui.controller.Menus;
import component.gui.controller.PianoCanvas;
import net.miginfocom.swing.*;

/**
 * @author Chief
 */
public class MainWindow extends JFrame implements KeyListener {

    // 单例
    private static MainWindow instance = new MainWindow();

    // 获取单例
    public static MainWindow GetInstance() {
        return instance;
    }

    Point pressedPoint;

    private static Animator animator;

    private MainWindow() {

        // 取消Windows自带顶边框
        setUndecorated(true);

        // 初始化组件
        initComponents();

        // 创建OpenGL3画板
        PianoCanvas pianoCanvas = new PianoCanvas();

        GLProfile glProfile = GLProfile.get(GLProfile.GL3);
        GLCapabilities glCapabilities = new GLCapabilities(glProfile);

        final GLCanvas glcanvas = new GLCanvas(glCapabilities);
        glcanvas.setSize(canvasPanel.getSize());
        glcanvas.addGLEventListener(pianoCanvas);

        canvasPanel.add(glcanvas);

        animator = new Animator(glcanvas);
        animator.start();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                animator.stop();
                System.exit(0);
            }
        });

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

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {

        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    // 最小化窗口
    private void minimizeButtonActionPerformed(ActionEvent e) {
        setExtendedState(JFrame.ICONIFIED);
    }

    // 关闭窗口
    private void closeButtonActionPerformed(ActionEvent e) {
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

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        menuBar1 = new JMenuBar();
        fileMenu = new JMenu();
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
        playerMenu = new JMenu();
        loadSoundFontMenuItem = new JMenuItem();
        playDirectMenuItem = new JMenuItem();
        stopDirectMenuItem = new JMenuItem();
        toolMenu = new JMenu();
        transposerMenuItem = new JMenuItem();
        helpMenu = new JMenu();
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
        canvasPanel = new JPanel();
        outputScrollPane = new JScrollPane();
        outputTextArea = new JTextArea();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        setTitle("Euterpe 2");
        Container contentPane = getContentPane();
        contentPane.setLayout(new MigLayout(
            "insets 0,hidemode 3,gap 0 0",
            // columns
            "[40,fill]" +
            "[430,fill]" +
            "[810,fill]",
            // rows
            "0[690,fill]0" +
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

        //======== canvasPanel ========
        {
            canvasPanel.setLayout(null);
        }
        contentPane.add(canvasPanel, "cell 2 0");

        //======== outputScrollPane ========
        {

            //---- outputTextArea ----
            outputTextArea.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
            outputTextArea.setEditable(false);
            outputScrollPane.setViewportView(outputTextArea);
        }
        contentPane.add(outputScrollPane, "cell 0 1 2 1");
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JMenuBar menuBar1;
    private JMenu fileMenu;
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
    private JMenu playerMenu;
    public JMenuItem loadSoundFontMenuItem;
    public JMenuItem playDirectMenuItem;
    public JMenuItem stopDirectMenuItem;
    private JMenu toolMenu;
    public JMenuItem transposerMenuItem;
    private JMenu helpMenu;
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
    public JPanel canvasPanel;
    private JScrollPane outputScrollPane;
    public JTextArea outputTextArea;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
