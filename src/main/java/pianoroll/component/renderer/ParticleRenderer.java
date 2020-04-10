package pianoroll.component.renderer;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.GLBuffers;
import glm.vec._2.Vec2;
import pianoroll.component.Pianoroll;
import pianoroll.entity.Particle;
import pianoroll.util.Semantic;
import uno.glsl.Program;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import static com.jogamp.opengl.GL.*;
import static uno.buffer.UtilKt.destroyBuffers;

public class ParticleRenderer {

    private final List<Particle> particleList;

    public ParticleRenderer() {
        this.particleList = Pianoroll.GetInstance().getParticleController().getParticleList();
    }

    public void init(GL3 gl) {
        final float[] vertexDataParticle = {
                -0.3f, 0.3f,          // Left-Top
                -0.3f, -0.3f,          // Left-Bottom
                0.3f, 0.3f,          // Right-Top
                0.3f, -0.3f           // Right-Bottom
        };

        IntBuffer buffer = GLBuffers.newDirectIntBuffer(1);

        FloatBuffer vertexBufferParticle = GLBuffers.newDirectFloatBuffer(vertexDataParticle);

        gl.glGenBuffers(1, buffer);

        gl.glBindBuffer(GL_ARRAY_BUFFER, buffer.get(Semantic.Buffer.VERTEX_PARTICLE));
        gl.glBufferData(GL_ARRAY_BUFFER, vertexBufferParticle.capacity() * Float.BYTES, vertexBufferParticle, GL_STATIC_DRAW);
        gl.glBindBuffer(GL_ARRAY_BUFFER, 0);

        destroyBuffers(vertexBufferParticle);

        for (int i = 0; i < Semantic.Pianoroll.PARTICLE_AMOUNT; ++i) {
            Particle particle = new Particle();

            particle.setVbo(buffer.get(Semantic.Buffer.VERTEX_PARTICLE));
            particleList.add(particle);
        }

        for (Particle particle : particleList) {
            gl.glGenVertexArrays(1, particle.getVao());

            gl.glBindVertexArray(particle.getVao().get(0));
            {
                gl.glBindBuffer(GL_ARRAY_BUFFER, particle.getVbo());
                {
                    gl.glEnableVertexAttribArray(Semantic.Attr.POSITION);
                    gl.glVertexAttribPointer(Semantic.Attr.POSITION, Vec2.length, GL_FLOAT, false, Vec2.SIZE, 0);
                }
                gl.glBindBuffer(GL_ARRAY_BUFFER, 0);
            }
            gl.glBindVertexArray(0);
        }

        gl.glDeleteBuffers(1, buffer);
        destroyBuffers(buffer);
    }

    public void drawParticles(GL3 gl, Program program) {
        gl.glUseProgram(program.name);

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

}