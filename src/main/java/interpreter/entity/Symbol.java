package interpreter.entity;

/**
 * 符号类
 * 用于记录特殊符号及其位置
 * 以便对特殊符号进行处理
 *
 * 0    连音左括号
 * 1    同时音符
 * 2    连音右括号
 */

public class Symbol {

    private final int symbol;

    private final int position;

    private final int line;

    public Symbol(int symbol, int position, int line) {
        this.symbol = symbol;
        this.position = position;
        this.line = line;
    }

    public int getSymbol() {
        return symbol;
    }

    public int getPosition() {
        return position;
    }

    public int getLine() {
        return line;
    }

}
