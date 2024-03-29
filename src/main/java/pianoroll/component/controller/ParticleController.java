package pianoroll.component.controller;

import pianoroll.component.Pianoroll;
import pianoroll.entity.Particle;
import pianoroll.util.Semantic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParticleController {

    private final List<Particle> particleList;

    private final Random random;

    private int lastUnusedParticle;

    public ParticleController() {
        particleList = new ArrayList<>();
        random = new Random(System.currentTimeMillis());
        lastUnusedParticle = 0;
    }

    public void updateParticles(float deltaTime) {
        // spawn a new particle if needed
        for (int trackID : Pianoroll.GetInstance().getTriggeredTrackList()) {
            Particle particle = particleList.get(firstUnusedParticle());
            int randomColor = random.nextInt(6) - 3 + trackID;
            float randomScale = (random.nextFloat() + 0.6f) * 1.2f;
            float randomDegrees = random.nextFloat() * 90.0f;
            float randomX = (random.nextFloat() - 0.5f) * 2.0f;
            float randomY = random.nextFloat() + 0.1f;

            particle.setTrackID(trackID);
            particle.setColorID(randomColor);
            particle.setOffset(randomX, 0.0f);
            particle.setVelocity(randomX * 2.0f, randomY * 20.0f);
            particle.setScale(randomScale);
            particle.setScaleConst(randomScale);
            particle.setDegrees(randomDegrees);
            particle.setLife(0.7f);
            particle.setLifeConst(0.7f);
            particle.setTimeSum(0.0f);
        }

        // update all particles
        for (Particle particle : particleList) {
            if (particle.getLife() > 0.0f) {
                particle.setLife(particle.getLife() - deltaTime);
                particle.setDegrees(particle.getDegrees() + deltaTime * 300.0f);
                particle.setOffset(particle.getOffsetX() + particle.getVelocityX() * deltaTime, particle.getOffsetY() + particle.getVelocityY() * deltaTime);
                particle.setTimeSum(particle.getTimeSum() + deltaTime);
                particle.setScale((float) Math.sqrt((particle.getLifeConst() - particle.getTimeSum()) / particle.getLifeConst()) * particle.getScaleConst());
            }
        }
    }

    private int firstUnusedParticle() {
        for (int i = lastUnusedParticle; i < Semantic.Pianoroll.PARTICLE_AMOUNT; ++i) {
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

    public List<Particle> getParticleList() {
        return particleList;
    }

}
