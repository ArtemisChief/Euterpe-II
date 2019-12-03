package pianoroll.component;

import glm.vec._2.Vec2;
import pianoroll.entity.Particle;
import pianoroll.util.Semantic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParticleGenerator {

    private int amount;

    private int lastUsedParticle;

    private List<Particle> particleList;

    private Random random;

    public ParticleGenerator(int amount) {
        this.amount = amount;

        lastUsedParticle = 0;

        particleList = new ArrayList<>();

        random = new Random(System.currentTimeMillis());

        int vbo = PianorollCanvas.GetBufferName().get(Semantic.Buffer.PARTICLE);

        for (int i = 0; i < amount; ++i) {
            Particle particle = new Particle();

            particle.setVbo(vbo);
            PianorollCanvas.OfferGraphicElementQueue(particle);
            particleList.add(particle);
        }
    }

    public void newParticle(int trackID,int amount) {
        for (int i = 0; i < amount; ++i) {
            int unusedParticle = firstUnusedParticle();
            respawnParticle(particleList.get(unusedParticle), trackID);
        }
    }

    private int firstUnusedParticle() {
        for (int i = lastUsedParticle; i < amount; ++i) {
            if (particleList.get(i).getLife() <= 0.0f) {
                lastUsedParticle = i;
                return i;
            }
        }

        for (int i = 0; i < lastUsedParticle; ++i) {
            if (particleList.get(i).getLife() <= 0.0f) {
                lastUsedParticle = i;
                return i;
            }
        }

        lastUsedParticle = 0;
        return 0;
    }

    private void respawnParticle(Particle particle, int trackID) {
        particle.setTrackID(trackID);
        particle.setColorID(trackID);
        particle.setDegrees(random.nextFloat() * 90);
        particle.setOffset(random.nextFloat(), random.nextFloat());
        particle.setVelocity(random.nextFloat(), random.nextFloat() * 15.0f);
    }

}