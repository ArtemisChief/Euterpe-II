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

    private List<Integer> generateParticleTrackList;

    public ParticleGenerator() {
        this.amount = 500;

        lastUsedParticle = 0;

        particleList = new ArrayList<>();

        random = new Random(System.currentTimeMillis());

        generateParticleTrackList = new ArrayList<>();

        int vbo = PianorollCanvas.GetBufferName().get(Semantic.Buffer.PARTICLE);

        for (int i = 0; i < amount; ++i) {
            Particle particle = new Particle();

            particle.setVbo(vbo);
            PianorollCanvas.OfferGraphicElementQueue(particle);
            particleList.add(particle);
        }
    }

    public void newParticle(int trackID) {
        int unusedParticle = firstUnusedParticle();
        respawnParticle(particleList.get(unusedParticle), trackID);
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
        int randomColor = random.nextInt(4) - 2 + trackID;
        float randomScale = (random.nextFloat() + 0.9f) * 1.2f;
        float randomDegrees = random.nextFloat() * 90.0f;
        float randomX = (random.nextFloat() - 0.5f) * 2.0f;
        float randomY = random.nextFloat() + 0.1f;

        particle.setTrackID(trackID);
        particle.setColorID(randomColor);
        particle.setOffset(randomX * 1.1f, 0.0f);
        particle.setVelocity(randomX, randomY * 11.0f);
        particle.setScale(randomScale);
        particle.setDegrees(randomDegrees);
        particle.setLife(1.0f);
    }

    public void addParticlesToTrack(int trackID){
        generateParticleTrackList.add(trackID);
    }

    public void stopAddingParticlesToTrack(int trackID) {
        generateParticleTrackList.remove(generateParticleTrackList.indexOf(trackID));
    }

    public List<Particle> getParticleList() {
        return particleList;
    }

    public List<Integer> getGenerateParticleTrackList(){
        return generateParticleTrackList;
    }

}