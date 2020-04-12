package pianoroll.component.controller;

import pianoroll.component.Pianoroll;
import pianoroll.component.PianorollCanvas;
import pianoroll.entity.ColumnRow;
import pianoroll.entity.Roll;
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

    public void createRow(float offsetY) {
        ColumnRow row = new ColumnRow(Semantic.Piano.KEY_MAX / 2, offsetY);
        PianorollCanvas.GetInstance().getBackgroundRenderer().addToUnbindRowList(row);
        rowList.add(row);
    }

    public void updateBackground(float distance) {
        for (ColumnRow row : rowList)
            row.setOffsetY(row.getOffsetY() - distance);
    }

    public List<ColumnRow> getColumnList() {
        return columnList;
    }

    public List<ColumnRow> getRowList() {
        return rowList;
    }

}
