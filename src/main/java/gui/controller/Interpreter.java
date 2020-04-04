package gui.controller;

import gui.view.MainWindow;
import interpreter.component.Lexical;
import interpreter.component.Semantic;
import interpreter.component.Syntactic;
import interpreter.entity.Node;
import interpreter.entity.Token;
import midibuilder.entity.MidiFile;

import java.util.List;

public class Interpreter {

    private final Lexical lexical;
    private final Syntactic syntactic;
    private final Semantic semantic;

    private static final Interpreter instance = new Interpreter();

    public static Interpreter GetInstance() {
        return instance;
    }

    private Interpreter() {
        lexical = new Lexical();
        syntactic = new Syntactic();
        semantic = new Semantic();
    }

    // 词法分析
    public List<Token> runLex(String input, StringBuilder output) {
        List<Token> tokens = lexical.Lex(input);

        if (lexical.getError()) {
            output.append(lexical.getErrorInfo(tokens));
            output.append("检测到错误");
            MainWindow.GetInstance().outputTextArea.setText(output.toString());
            for (int line : lexical.getErrorLine()) {
                InputTexts.GetInstance().inputStyledDocument.setCharacterAttributes(
                        InputTexts.GetInstance().getIndexByLine(line),
                        InputTexts.GetInstance().getIndexByLine(line + 1) - InputTexts.GetInstance().getIndexByLine(line),
                        InputTexts.GetInstance().errorAttributeSet, true
                );
            }
            return null;
        }

        return tokens;
    }

    // 语法分析
    private Node runSyn(List<Token> tokens, StringBuilder output) {
        Node AbstractSyntaxTree = syntactic.Parse(tokens);

        if (syntactic.getIsError()) {
            output.append(syntactic.getErrors(AbstractSyntaxTree));
            output.append("检测到错误");
            MainWindow.GetInstance().outputTextArea.setText(output.toString());
            for (int line : syntactic.getErrorList()) {
                InputTexts.GetInstance().inputStyledDocument.setCharacterAttributes(
                        InputTexts.GetInstance().getIndexByLine(line),
                        InputTexts.GetInstance().getIndexByLine(line + 1) - InputTexts.GetInstance().getIndexByLine(line),
                        InputTexts.GetInstance().errorAttributeSet, true
                );
            }
            return null;
        }

        return AbstractSyntaxTree;
    }

    // 语义分析
    private String runMidiSem(Node abstractSyntaxTree, StringBuilder output) {
        String code = semantic.interpret(abstractSyntaxTree);

        if (semantic.getIsError()) {
            output.append(semantic.getErrors());
            output.append("检测到错误");
            MainWindow.GetInstance().outputTextArea.setText(output.toString());
            for (int line : semantic.getErrorLines()) {
                InputTexts.GetInstance().inputStyledDocument.setCharacterAttributes(
                        InputTexts.GetInstance().getIndexByLine(line),
                        InputTexts.GetInstance().getIndexByLine(line + 1) - InputTexts.GetInstance().getIndexByLine(line),
                        InputTexts.GetInstance().errorAttributeSet, true
                );
            }
            return null;
        } else {
            output.append(code);
        }

        return code;
    }

    // 执行解释过程
    public boolean runInterpret() {
        StringBuilder stringBuilder = new StringBuilder();

        if (MainWindow.GetInstance().inputTextPane.getText().isEmpty())
            return false;

        List<Token> tokens = runLex(MainWindow.GetInstance().inputTextPane.getText(), stringBuilder);

        if (tokens == null)
            return false;

        Node AbstractSyntaxTree = runSyn(tokens, stringBuilder);

        if (AbstractSyntaxTree == null)
            return false;

        String code = runMidiSem(AbstractSyntaxTree, stringBuilder);

        if (code == null)
            return false;

        MainWindow.GetInstance().outputTextArea.setText("==============================================\nMidi Successfully Generated");

        return true;
    }

    // 获取Midi文件
    public MidiFile getMidiFile(){
        return semantic.getMidiFile();
    }

}
