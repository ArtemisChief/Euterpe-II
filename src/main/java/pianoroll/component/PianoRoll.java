package pianoroll.component;

import pianoroll.component.controller.ParticleController;
import pianoroll.component.controller.PianoController;
import pianoroll.component.controller.RollController;

import java.util.ArrayList;
import java.util.List;

public class PianoRoll {

    private final List<Integer> triggeredTrackList;

    private final PianoController pianoController;
    private final RollController rollController;
    private final ParticleController particleController;

    private final GraphicEngine graphicEngine;
    private final InputProcessor inputProcessor;

    public PianoRoll() {
        triggeredTrackList=new ArrayList<>();

        pianoController = new PianoController(triggeredTrackList);
        rollController = new RollController(triggeredTrackList);
        particleController = new ParticleController(triggeredTrackList);

        graphicEngine = new GraphicEngine(this);
        inputProcessor = new InputProcessor(this);
    }

    public void trigger(Integer trackID) {
        triggeredTrackList.add(trackID);
    }

    public void suspend(Integer trackID) {
        triggeredTrackList.remove(trackID);
    }

    public PianoController getPianoController() {
        return pianoController;
    }

    public RollController getRollController() {
        return rollController;
    }

    public ParticleController getParticleController() {
        return particleController;
    }

    public GraphicEngine getGraphicEngine() {
        return graphicEngine;
    }

    public InputProcessor getInputProcessor() {
        return inputProcessor;
    }

}
