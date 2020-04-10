package pianoroll.component.controller;

import pianoroll.component.Pianoroll;
import pianoroll.entity.ColumnRow;
import pianoroll.util.Semantic;

import java.util.ArrayList;
import java.util.List;

public class BackgroundController {

    private final List<ColumnRow> columnList;
    private final List<ColumnRow> rowList;

    public BackgroundController() {
        columnList = new ArrayList<>();
        rowList = new ArrayList<>();
    }

    public void updateBackground(float deltaTime) {
        if (Pianoroll.GetInstance().isPlaying()) {
            for (ColumnRow row : rowList) {
                row.setOffsetY(row.getOffsetY() - Pianoroll.GetInstance().getLengthPerSecond() * deltaTime);

                if (row.getOffsetY() <= 0.0f) {
                    row.setOffsetY(2 * 4 * Semantic.Pianoroll.LENGTH_PER_CROTCHET - Pianoroll.GetInstance().getLengthPerSecond() * deltaTime);
                }
            }
        }
    }

    public void reset() {
        for (int i = 0; i < 2; i++)
            rowList.get(i).setOffsetY(i * 4 * Semantic.Pianoroll.LENGTH_PER_CROTCHET);
    }

    public List<ColumnRow> getColumnList() {
        return columnList;
    }

    public List<ColumnRow> getRowList() {
        return rowList;
    }

}
