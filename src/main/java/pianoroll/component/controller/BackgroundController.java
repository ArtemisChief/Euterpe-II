package pianoroll.component.controller;

import pianoroll.component.PianorollCanvas;
import pianoroll.entity.Background;
import pianoroll.util.Semantic;

import java.util.ArrayList;
import java.util.List;

public class BackgroundController {

    private final List<Background> columnList;
    private final List<Background> rowList;

    public BackgroundController() {
        columnList = new ArrayList<>();
        rowList = new ArrayList<>();
    }

    public void createRow(float offsetY) {
        Background row = new Background(Semantic.Piano.KEY_MAX / 2, offsetY);
        PianorollCanvas.GetInstance().getBackgroundRenderer().addToUnbindRowList(row);
        rowList.add(row);
    }

    public void updateBackground(float distance) {
        for (Background row : rowList)
            row.setOffsetY(row.getOffsetY() - distance);
    }

    public List<Background> getColumnList() {
        return columnList;
    }

    public List<Background> getRowList() {
        return rowList;
    }

}
