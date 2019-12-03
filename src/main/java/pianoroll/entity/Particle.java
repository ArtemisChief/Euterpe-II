package pianoroll.entity;

import glm.vec._2.Vec2;

public class Particle extends GraphicElement{

    private static float[] vertexData = {
            -0.2f,   0.2f,          // Left-Top
            -0.2f,  -0.2f,          // Left-Bottom
             0.2f,   0.2f,          // Right-Top
             0.2f,  -0.2f           // Right-Bottom

    };

    private Vec2 offset;

    private Vec2 velocity;

    private float degrees;

    private float life;

    public Particle() {
        super(0, 0);
        offset = new Vec2(0.0f, 0.0f);
        velocity = new Vec2(0.0f, 0.0f);
        degrees = 0.0f;
        life = 0.0f;
    }

    public void update(float deltaTime) {
        life -= deltaTime;
        if (life > 0.0f) {
            degrees += deltaTime;
            velocity.minus(deltaTime);
            offset.plus(velocity.times(deltaTime));
        }
    }

    public static float[] GetVertexData() {
        return vertexData;
    }

    public Vec2 getOffset() {
        return offset;
    }

    public void setOffset(float x,float y) {
        offset.x = x;
        offset.y = y;
    }

    public Vec2 getVelocity() {
        return velocity;
    }

    public void setVelocity(float x,float y) {
        velocity.x = x;
        velocity.y = y;
    }

    public float getDegrees() {
        return degrees;
    }

    public void setDegrees(float degrees) {
        this.degrees = degrees;
    }

    public float getLife() {
        return life;
    }

    public void setLife(float life) {
        this.life = life;
    }

}
