package pianoroll.component;

import com.jogamp.opengl.GL3;
import pianoroll.entity.Particle;
import pianoroll.util.Semantic;
import uno.glsl.Program;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.jogamp.opengl.GL.GL_TRIANGLE_STRIP;

public class ParticleRenderer {

    private final int amount;

    private int lastUnusedParticle;

    private final List<Particle> particleList;

    private final Random random;

    private final List<Integer> generateParticleTrackList;

    public ParticleRenderer() {
        amount = 500;

        lastUnusedParticle = 0;

        particleList = new ArrayList<>();

        random = new Random(System.currentTimeMillis());

        generateParticleTrackList = new ArrayList<>();

        for (int i = 0; i < amount; ++i) {
            Particle particle = new Particle();

            particle.setVbo(Canvas.GetBufferName().get(Semantic.Buffer.PARTICLE));
            Canvas.getGraphicElementQueue().offer(particle);
            particleList.add(particle);
        }
    }

    private void newParticle(int trackID) {
        int unusedParticle = firstUnusedParticle();
        respawnParticle(particleList.get(unusedParticle), trackID);
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

    private void respawnParticle(Particle particle, int trackID) {
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

    public void drawParticles(GL3 gl, Program program) {
        gl.glUseProgram(program.name);

        if (!generateParticleTrackList.isEmpty()) {
            for (int trackID : generateParticleTrackList) {
                newParticle(trackID);
            }
        }

        for (Particle particle : particleList) {
            if (particle.getLife() > 0.0f) {
                gl.glBindVertexArray(particle.getVao().get(0));
                gl.glUniform1i(program.get("trackID"), particle.getTrackID());
                gl.glUniform1i(program.get("colorID"), particle.getColorID());
                gl.glUniform2f(program.get("offset"), particle.getOffsetX(), particle.getOffsetY());
                gl.glUniform1f(program.get("scale"), particle.getScale());
                gl.glUniform1f(program.get("degrees"), particle.getDegrees());
                gl.glUniform1f(program.get("life"), particle.getLife());
                gl.glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
            }
        }

        gl.glBindVertexArray(0);
    }

    public void addParticlesToTrack(int trackID){
        generateParticleTrackList.add(trackID);
    }

    public void stopAddingParticlesToTrack(int trackID) {
        generateParticleTrackList.remove((Integer) trackID);
    }

    public List<Particle> getParticleList() {
        return particleList;
    }

}