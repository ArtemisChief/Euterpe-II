package interpreter.component;

import interpreter.entity.ArduinoNote;
import interpreter.entity.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SemanticArduino {

    private Node AbstractSyntaxTree;

    private List<Integer> errorLines;
    private StringBuilder code;
    private StringBuilder errorInfo;
    private Map<String, Integer> paragraphCount;
    private int count;
    private int scoreLength;

    public String ConvertToArduino(Node abstractSyntaxTree) {
        AbstractSyntaxTree = abstractSyntaxTree;

        errorLines = new ArrayList<>();

        code = new StringBuilder();
        errorInfo = new StringBuilder();

        paragraphCount = new HashMap<>();

        count = 0;

        code.append("#include <Tone.h>\n" +
                "#include <SCoop.h>\n\n" +
                "int tonePin1=2;\n" +
                "int tonePin2=3;\n\n" +
                "Tone tone1;\n" +
                "Tone tone2;\n\n\n");

        DFS_Arduino(AbstractSyntaxTree);

        if(getIsError())
            return null;

        return code.toString();
    }

    private void DFS_Arduino(Node curNode) {
        double speedFactor;
        int noteCount = 0;
        int rhythmCount = 0;

        for (Node child : curNode.getChildren()) {
            switch (child.getType()) {
                case "score":
                    count++;
                    scoreLength = 0;
                    code.append("const int length" + count + ";\n\n" +
                            "double speedFactor" + count + ";\n\n" +
                            "double tonalityFactor" + count + ";\n\n");
                    DFS_Arduino(child);
                    break;

                case "execution":
                    code.append("void play(int *paragragh, int *duration, int paragraphLength, Tone tonePlayer, double tonalityFactor, double speedFactor){\n" +
                            "  for(int i=0;i<paragraphLength;i++){\n" +
                            "    if(paragragh[i]!=0)\n" +
                            "      tonePlayer.play(paragragh[i] * tonalityFactor);\n" +
                            "    if(duration[i]>0){\n" +
                            "      delay(duration[i] * speedFactor-1);\n" +
                            "      tonePlayer.stop();\n" +
                            "      delay(1);\n" +
                            "    }else{\n" +
                            "      delay(-duration[i] * speedFactor-1);\n" +
                            "      delay(1);\n" +
                            "      tonePlayer.stop();\n" +
                            "    }\n" +
                            "  }\n" +
                            "}\n\n" +
                            "defineTaskLoop(Task1){};//task1\n\n" +
                            "defineTaskLoop(Task2){};//task2\n\n" + "" +
                            "void setup() {\n" +
                            "  tone1.begin(tonePin1);\n" +
                            "  tone2.begin(tonePin2);\n" +
                            "  mySCoop.start();\n" +
                            "}\n\n" +
                            "void loop() {\n" +
                            "  yield();\n" +
                            "}");
                    DFS_Arduino(child);
                    break;

                case "statement":
                    if (paragraphCount.containsKey(child.getChild(0).getContent())) {
                        errorInfo.append("Line: " + child.getChild(0).getLine() + "\t重复声明的段落名" + child.getChild(0).getContent() + "\n");
                        errorLines.add(child.getChild(0).getLine());
                        count--;
                    }
                    paragraphCount.put(child.getChild(0).getContent(), count);
                    code.append("int *" + child.getChild(0).getContent() + "=new int[length" + count + "]\n{};//Notes\n\n");
                    code.append("int *" + child.getChild(0).getContent() + "Duration=new int[length" + count + "]\n{};//Duration\n\n");
                    break;

                case "speed":
                    speedFactor = 60 / Double.parseDouble(child.getChild(0).getContent()) / 4 * 1000;
                    code.insert(code.indexOf("speedFactor" + count) + ("speedFactor" + count).length(), "=" + speedFactor);
                    break;

                case "tonality":
                    for (Node tonality : child.getChildren()) {
                        double halfTone = 1;
                        switch (tonality.getContent()) {
                            case "#":
                                halfTone = 1.059463;
                                break;
                            case "b":
                                halfTone = 0.943874;
                                break;
                            case "C":
                                code.insert(code.indexOf("tonalityFactor" + count) + ("tonalityFactor" + count).length(), "=" + Double.parseDouble(ArduinoNote.Tonality.C) * halfTone);
                                break;
                            case "D":
                                code.insert(code.indexOf("tonalityFactor" + count) + ("tonalityFactor" + count).length(), "=" + Double.parseDouble(ArduinoNote.Tonality.D) * halfTone);
                                break;
                            case "E":
                                code.insert(code.indexOf("tonalityFactor" + count) + ("tonalityFactor" + count).length(), "=" + Double.parseDouble(ArduinoNote.Tonality.E) * halfTone);
                                break;
                            case "F":
                                code.insert(code.indexOf("tonalityFactor" + count) + ("tonalityFactor" + count).length(), "=" + Double.parseDouble(ArduinoNote.Tonality.F) * halfTone);
                                break;
                            case "G":
                                code.insert(code.indexOf("tonalityFactor" + count) + ("tonalityFactor" + count).length(), "=" + Double.parseDouble(ArduinoNote.Tonality.G) * halfTone);
                                break;
                            case "A":
                                code.insert(code.indexOf("tonalityFactor" + count) + ("tonalityFactor" + count).length(), "=" + Double.parseDouble(ArduinoNote.Tonality.A) * halfTone);
                                break;
                            case "B":
                                code.insert(code.indexOf("tonalityFactor" + count) + ("tonalityFactor" + count).length(), "=" + Double.parseDouble(ArduinoNote.Tonality.B) * halfTone);
                                break;
                        }
                    }
                    break;

                case "sentence":
                    DFS_Arduino(child);
                    break;

                case "end paragraph":
                    code.insert(code.indexOf("length" + count) + ("length" + count).length(), "=" + scoreLength);
                    code.delete(code.indexOf("};//Notes") - 3, code.indexOf("};//Notes"));
                    code.delete(code.indexOf("};//Duration") - 3, code.indexOf("};//Duration"));
                    code.delete(code.indexOf("//Notes"), code.indexOf("//Notes") + 7);
                    code.delete(code.indexOf("//Duration"), code.indexOf("//Duration") + 10);
                    break;

                case "melody":
                    double pitchFactor = 1;
                    double halfTone = 1;
                    Integer pitch;

                    for (Node tone : child.getChildren()) {
                        switch (tone.getContent()) {
                            case "(":
                                pitchFactor *= 0.5;
                                break;
                            case ")":
                                pitchFactor *= 2;
                                break;
                            case "[":
                                pitchFactor *= 2;
                                break;
                            case "]":
                                pitchFactor *= 0.5;
                                break;
                            case "#":
                                halfTone *= 1.059463;
                                break;
                            case "b":
                                halfTone *= 0.943874;
                                break;
                            case "0":
                                noteCount++;
                                pitch = 0;
                                code.insert(code.indexOf("};//Notes"), pitch + ", ");
                                break;
                            case "1":
                                noteCount++;
                                pitch = (int) ((Double.parseDouble(ArduinoNote.Pitch.C) * pitchFactor * halfTone));
                                code.insert(code.indexOf("};//Notes"), pitch + ", ");
                                halfTone = 1;
                                break;
                            case "2":
                                noteCount++;
                                pitch = (int) ((Double.parseDouble(ArduinoNote.Pitch.D) * pitchFactor * halfTone));
                                code.insert(code.indexOf("};//Notes"), pitch + ", ");
                                halfTone = 1;
                                break;
                            case "3":
                                noteCount++;
                                pitch = (int) ((Double.parseDouble(ArduinoNote.Pitch.E) * pitchFactor * halfTone));
                                code.insert(code.indexOf("};//Notes"), pitch + ", ");
                                halfTone = 1;
                                break;
                            case "4":
                                noteCount++;
                                pitch = (int) ((Double.parseDouble(ArduinoNote.Pitch.F) * pitchFactor * halfTone));
                                code.insert(code.indexOf("};//Notes"), pitch + ", ");
                                halfTone = 1;
                                break;
                            case "5":
                                noteCount++;
                                pitch = (int) ((Double.parseDouble(ArduinoNote.Pitch.G) * pitchFactor * halfTone));
                                code.insert(code.indexOf("};//Notes"), pitch + ", ");
                                halfTone = 1;
                                break;
                            case "6":
                                noteCount++;
                                pitch = (int) ((Double.parseDouble(ArduinoNote.Pitch.A) * pitchFactor * halfTone));
                                code.insert(code.indexOf("};//Notes"), pitch + ", ");
                                halfTone = 1;
                                break;
                            case "7":
                                noteCount++;
                                pitch = (int) ((Double.parseDouble(ArduinoNote.Pitch.B) * pitchFactor * halfTone));
                                code.insert(code.indexOf("};//Notes"), pitch + ", ");
                                halfTone = 1;
                                break;
                        }
                    }
                    code.insert(code.indexOf("};//Notes"), "\n");
                    break;

                case "rhythm":
                    Integer legato = 1;
                    Integer line = child.getChild(0).getLine();
                    for (Node rhythm : child.getChildren()) {
                        switch (rhythm.getContent()) {
                            case "{":
                                legato = -1;
                                break;
                            case "}":
                                code.deleteCharAt(code.lastIndexOf("-"));
                                legato = 1;
                                break;
                            case "1":
                                rhythmCount++;
                                code.insert(code.indexOf("};//Duration"), 16 * legato + ", ");
                                break;
                            case "1*":
                                rhythmCount++;
                                code.insert(code.indexOf("};//Duration"), 24 * legato + ", ");
                                break;
                            case "2":
                                rhythmCount++;
                                code.insert(code.indexOf("};//Duration"), 8 * legato + ", ");
                                break;
                            case "2*":
                                rhythmCount++;
                                code.insert(code.indexOf("};//Duration"), 12 * legato + ", ");
                                break;
                            case "4":
                                rhythmCount++;
                                code.insert(code.indexOf("};//Duration"), 4 * legato + ", ");
                                break;
                            case "4*":
                                rhythmCount++;
                                code.insert(code.indexOf("};//Duration"), 6 * legato + ", ");
                                break;
                            case "8":
                                rhythmCount++;
                                code.insert(code.indexOf("};//Duration"), 2 * legato + ", ");
                                break;
                            case "8*":
                                rhythmCount++;
                                code.insert(code.indexOf("};//Duration"), 3 * legato + ", ");
                                break;
                            case "g":
                                rhythmCount++;
                                code.insert(code.indexOf("};//Duration"), 1 * legato + ", ");
                                break;
                            case "g*":
                                rhythmCount++;
                                errorInfo.append("Line: " + line + "\t不支持16分附点音符，即g*\n");
                                errorLines.add(line);
                                break;
                            case "w":
                                rhythmCount++;
                                errorInfo.append("Line: " + line + "\t不支持32分音符，即w\n");
                                errorLines.add(line);
                                break;
                            case "w*":
                                rhythmCount++;
                                errorInfo.append("Line: " + line + "\t不支持32分附点音符，即w*\n");
                                errorLines.add(line);
                                break;
                        }
                    }
                    code.insert(code.indexOf("};//Duration"), "\n");

                    if (noteCount != rhythmCount) {
                        errorInfo.append("Line: " + line + "\t该句音符与时值数量不相同\n");
                        errorLines.add(line);
                    }
                    scoreLength += noteCount;
                    break;

                case "playlist":
                    String paraName = "";
                    int tonePlayed = 0;         //要使用的蜂鸣器编号
                    boolean andOp = false;
                    for (Node playList : child.getChildren()) {
                        switch (playList.getContent()) {
                            case "&":
                                if (tonePlayed > 1) {
                                    errorInfo.append("Line: " + playList.getLine() + "\t不支持两个以上蜂鸣器同时播放，请减少play中同时播放的数量\n");
                                    errorLines.add(playList.getLine());
                                }
                                andOp = true;
                                break;
                            case ",":
                                if (tonePlayed == 1) {
                                    code.insert(code.indexOf("};//task2"), "\n  play(" + paraName + ", " + paraName + "Duration, length" + paragraphCount.get(paraName) + ",tone2, tonalityFactor" + paragraphCount.get(paraName) + "*0.5, speedFactor" + paragraphCount.get(paraName) + ");\n");
                                }
                                tonePlayed = 0;
                                andOp = false;
                                break;
                            default:
                                paraName = playList.getContent();
                                if (!paragraphCount.containsKey(paraName)) {
                                    errorInfo.append("Line: " + playList.getLine() + "\t未声明的段落名" + paraName + "\n");
                                    errorLines.add(playList.getLine());
                                }
                                tonePlayed++;
                                if (!andOp) {
                                    code.insert(code.indexOf("};//task1"), "\n  play(" + paraName + ", " + paraName + "Duration, length" + paragraphCount.get(paraName) + ",tone1, tonalityFactor" + paragraphCount.get(paraName) + ", speedFactor" + paragraphCount.get(paraName) + ");\n");
                                } else {
                                    code.insert(code.indexOf("};//task2"), "\n  play(" + paraName + ", " + paraName + "Duration, length" + paragraphCount.get(paraName) + ",tone2, tonalityFactor" + paragraphCount.get(paraName) + ", speedFactor" + paragraphCount.get(paraName) + ");\n");
                                }
                                break;
                        }
                    }
                    if (tonePlayed == 1) {
                        code.insert(code.indexOf("};//task2"), "\n  play(" + paraName + ", " + paraName + "Duration, length" + paragraphCount.get(paraName) + ",tone2, tonalityFactor" + paragraphCount.get(paraName) + "*0.5, speedFactor" + paragraphCount.get(paraName) + ");\n");
                    }
                    code.delete(code.indexOf("//task1"), code.indexOf("//task1") + 7);
                    code.delete(code.indexOf("//task2"), code.indexOf("//task2") + 7);
                    break;
            }
        }
    }

    public boolean getIsError() {
        return !errorLines.isEmpty();
    }

    public List<Integer> getErrorLines() {
        return errorLines;
    }

    public String getErrors() {
        return errorInfo.toString();
    }
}