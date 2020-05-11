package converter.component.controller;

import converter.component.NmnCanvas;
import converter.entity.GraphicElement;

import java.util.List;

public class NmnController {

    public NmnController(){

    }

    public void updateNmn(List<GraphicElement> graphicElements){
        NmnCanvas.GetInstance().getNmnRenderer().setGraphicElementList(graphicElements);
        for(GraphicElement element: graphicElements){
            NmnCanvas.GetInstance().getNmnRenderer().addToUnbindRollList(element);
        }
    }
}
