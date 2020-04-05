package pianoroll.component;

import pianoroll.entity.Particle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParticleManager {

    private final int amount;

    private final List<Particle> particleList;

    private final List<Integer> triggeredTrackList;

    private int lastUnusedParticle;

    private final Random random;

    public ParticleManager() {
        amount = 500;
        particleList = new ArrayList<>();
        triggeredTrackList = new ArrayList<>();
        lastUnusedParticle = 0;
        random = new Random(System.currentTimeMillis());
    }

    public void triggerTrack(int trackID) {
        triggeredTrackList.add(trackID);
    }

    public void suspendTrack(int trackID) {
        triggeredTrackList.remove((Integer) trackID);
    }

    public void respawnParticle() {
        if (!triggeredTrackList.isEmpty()) {
            for (int trackID : triggeredTrackList) {
                Particle particle = particleList.get(firstUnusedParticle());
                int randomColor = random.nextInt(4) - 2 + trackID;
                float randomScale = (random.nextFloat() + 0.6f) * 1.2f;
                float randomDegrees = random.nextFloat() * 90.0f;
                float randomX = (random.nextFloat() - 0.5f) * 2.0f;
                float randomY = random.nextFloat() + 0.1f;

                particle.setTrackID(trackID);
                particle.setColorID(randomColor);
                particle.setOffset(randomX, 0.0f);
                particle.setVelocity(randomX, randomY * 10.0f);
                particle.setScale(randomScale);
                particle.setDegrees(randomDegrees);
                particle.setLife(1.0f);
            }
        }
    }

    private int firstUnusedParticle() {
        for (int i = lastUnusedParticle; i < amount; ++i) {
            if (particleList.get(i).getLife() <= 0.0f) {
                lastUnusedParticle = i;
                return i;
            }
        }

        for (int i = 0; i < lastUnusedParticle; ++i) {
            if (particleList.get(i).getLife() <= 0.0f) {
                lastUnusedParticle = i;
                return i;
            }
        }

        lastUnusedParticle = 0;
        return 0;
    }

    public int getAmount() {
        return amount;
    }

    public List<Particle> getParticleList() {
        return particleList;
    }

}
