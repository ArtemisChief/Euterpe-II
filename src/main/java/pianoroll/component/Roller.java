package pianoroll.component;

import pianoroll.entity.Roll;

import java.util.ArrayList;
import java.util.List;

public class Roller {

    private float speed = 1.0f;

    private List<Roll> rollList;

    public Roller() {

        rollList = new ArrayList<>();

    }

    public void newRoll(int trackID) {

    }

    public void UpdateRolls(float deltaTime) {
        for (Roll roll : rollList) {
            roll.update(deltaTime * speed);
        }
    }

    public List<Roll> GetRollList() {
        return rollList;
    }

}
