package pianoroll.component.controller;

import pianoroll.component.PianoRoll;
import pianoroll.entity.ColumnRow;
import pianoroll.util.Semantic;

import java.util.ArrayList;
import java.util.List;

public class BackgroundController {

    private final PianoRoll pianoRoll;

    private final List<ColumnRow> columnList;
    private final List<ColumnRow> rowList;

    public BackgroundController(PianoRoll pianoRoll) {
        this.pianoRoll = pianoRoll;
        columnList = new ArrayList<>();
        rowList = new ArrayList<>();
    }

    public void updateBackground(float deltaTime) {
        if (pianoRoll.isPlaying()) {
            for (ColumnRow row : rowList) {
                row.setOffsetY(row.getOffsetY() - pianoRoll.getLengthPerSecond() * deltaTime);

                if (row.getOffsetY() <= 0.0f) {
                    row.setOffsetY(2 * 4 * Semantic.Pianoroll.LENGTH_PER_CROTCHET - pianoRoll.getLengthPerSecond() * deltaTime);
                }
            }
        }
    }

    public List<ColumnRow> getColumnList() {
        return columnList;
    }

    public List<ColumnRow> getrowList() {
        return rowList;
    }

}
