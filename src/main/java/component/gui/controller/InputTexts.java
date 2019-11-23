package component.gui.controller;

import component.gui.view.MainWindow;
import entity.gui.Status;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputTexts {

    private static InputTexts instance = new InputTexts();

    public static InputTexts GetInstance() {
        return instance;
    }

    private InputTexts(){ }

    public SimpleAttributeSet attributeSet;
    public SimpleAttributeSet statementAttributeSet;
    public SimpleAttributeSet durationAttributeSet;
    public SimpleAttributeSet normalAttributeSet;
    public SimpleAttributeSet commentAttributeSet;
    public SimpleAttributeSet errorAttributeSet;
    public SimpleAttributeSet sameTimeNoteAttributeSet;

    private Pattern statementPattern;
    private Pattern keywordPattern;
    private Pattern parenPattern;
    private Pattern sameNotePattern;

    private JTextPane inputTextPane;

    public class MyDocument extends DefaultStyledDocument {
        @Override
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            boolean isComment = false;
            boolean isAutoComplete = false;

            //处理自动补全
            String text = inputTextPane.getText().replace("\r", "");
            char b;
            if (offs == text.length() || (b = text.charAt(offs)) == '\n' || b == ' ' || b == ')' || b == ']' || b == '|' || (offs > 0 && text.charAt(offs - 1) == '/')) {
                switch (str) {
                    case "(":
                        isAutoComplete = true;
                        str += ")";
                        break;
                    case "[":
                        isAutoComplete = true;
                        str += "]";
                        break;
                    case "{":
                        isAutoComplete = true;
                        str += "}";
                        break;
                    case "<":
                        isAutoComplete = true;
                        str += ">";
                        break;
                    case "|":
                        isAutoComplete = true;
                        str += "|";
                        break;
                    case "*":
                        str += "\n\n*/";
                        isComment = true;
                        break;
                }
            }

            if (offs < text.length() && ((b = text.charAt(offs)) == ')' && str.equals(")") || str.equals("]") && b == ']' || str.equals("}") && b == '}' || str.equals(">") && b == '>' || str.equals("|") && b == '|')) {
                str = "";
                isAutoComplete = true;
            }


            super.insertString(offs, str, a);

            if (isAutoComplete)
                inputTextPane.setCaretPosition(offs + 1);
            if (isComment)
                inputTextPane.setCaretPosition(offs + 2);

            refreshColor();
            Status.GetCurrentStatus().setIsEdited(true);
        }

        @Override
        public void remove(int offs, int len) throws BadLocationException {
            //自动删除界符
            if (offs < inputTextPane.getText().replace("\r", "").length() - 1) {
                char a = inputTextPane.getText().replace("\r", "").charAt(offs);
                char b = inputTextPane.getText().replace("\r", "").charAt(offs + 1);
                if ((a == '(' && b == ')') || (a == '[' && b == ']') || (a == '{' && b == '}') || (a == '<' && b == '>') || (a == '|' && b == '|')) {
                    len++;
                }
            }

            super.remove(offs, len);

            refreshColor();
            Status.GetCurrentStatus().setIsEdited(true);
        }
    }

    public MyDocument inputStyledDocument;

    public void init() {

        inputTextPane = MainWindow.GetInstance().inputTextPane;

        //样式
        attributeSet = new SimpleAttributeSet();
        statementAttributeSet = new SimpleAttributeSet();
        durationAttributeSet = new SimpleAttributeSet();
        normalAttributeSet = new SimpleAttributeSet();
        commentAttributeSet = new SimpleAttributeSet();
        errorAttributeSet = new SimpleAttributeSet();
        sameTimeNoteAttributeSet = new SimpleAttributeSet();

        StyleConstants.setForeground(attributeSet, new Color(100, 158, 220));
        StyleConstants.setBold(attributeSet, true);
        StyleConstants.setForeground(statementAttributeSet, new Color(179, 210, 235));
        StyleConstants.setBold(statementAttributeSet, true);
        StyleConstants.setForeground(durationAttributeSet, new Color(70, 170, 155));
        StyleConstants.setForeground(commentAttributeSet, new Color(190, 190, 190));
        StyleConstants.setForeground(errorAttributeSet, new Color(238, 0, 1));
        StyleConstants.setBackground(sameTimeNoteAttributeSet, new Color(130, 130, 130));

        inputStyledDocument = new MyDocument();
        inputTextPane.setDocument(inputStyledDocument);
        statementPattern = Pattern.compile("\\bparagraph\\b|\\bend\\b|\\bplay");
        keywordPattern = Pattern.compile("\\bspeed=|\\binstrument=|\\bvolume=|\\b1=");
        parenPattern = Pattern.compile("<(\\s*\\{?\\s*(1|2|4|8|g|w|\\*)+\\s*\\}?\\s*)+>");
        sameNotePattern = Pattern.compile("\\|");


    }

    //代码着色
    private void refreshColor() {
        String input = inputTextPane.getText().replace("\r", "");

        inputStyledDocument.setCharacterAttributes(
                0,
                input.length(),
                normalAttributeSet, true
        );

        //声明着色
        Matcher statementMatcher = statementPattern.matcher(input);
        while (statementMatcher.find()) {
            inputStyledDocument.setCharacterAttributes(
                    statementMatcher.start(),
                    statementMatcher.end() - statementMatcher.start(),
                    statementAttributeSet, true
            );
        }

        //关键字着色
        Matcher inputMatcher = keywordPattern.matcher(input);
        while (inputMatcher.find()) {
            inputStyledDocument.setCharacterAttributes(
                    inputMatcher.start(),
                    inputMatcher.end() - inputMatcher.start(),
                    attributeSet, true
            );
        }

        //节奏片段着色
        Matcher parenMatcher = parenPattern.matcher(input);
        while (parenMatcher.find()) {
            inputStyledDocument.setCharacterAttributes(
                    parenMatcher.start(),
                    parenMatcher.end() - parenMatcher.start(),
                    durationAttributeSet, true
            );
        }

        //注释着色
        for (int i = 0; i < input.length(); i++) {
            //单行注释
            if (i + 1 < input.length())
                if (input.charAt(i) == '/' && input.charAt(i + 1) == '/')
                    while (i + 1 < input.length() && input.charAt(i) != '\n') {
                        i++;
                        inputStyledDocument.setCharacterAttributes(
                                i - 1,
                                2,
                                commentAttributeSet, true
                        );
                    }

            //多行注释
            if (i + 1 < input.length() && input.charAt(i) == '/' && input.charAt(i + 1) == '*')
                while (i + 1 < input.length() && (input.charAt(i) != '*' || input.charAt(i + 1) != '/')) {
                    i++;
                    inputStyledDocument.setCharacterAttributes(
                            i - 1,
                            3,
                            commentAttributeSet, true
                    );
                }
        }

        //同时音着色
        int count = 0;
        int last = 0;
        Matcher noteMatcher = sameNotePattern.matcher(input);
        while (noteMatcher.find()) {
            count++;

            if (count % 2 == 0) {
                inputStyledDocument.setCharacterAttributes(
                        last,
                        noteMatcher.end() - last,
                        sameTimeNoteAttributeSet, true
                );
            } else
                last = noteMatcher.start();
        }
    }

    //通过行号找到该行第一个字符在输入字符串中的位置
    public int getIndexByLine(int line) {
        int index = 0;
        String input = inputTextPane.getText().replace("\r", "") + "\n";

        for (int i = 0; i < line - 1; i++) {
            index = input.indexOf("\n", index + 1);
        }
        return index;
    }

}