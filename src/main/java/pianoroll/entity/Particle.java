package pianoroll.entity;

public class Particle extends GraphicElement{

    private static float[] vertexData = {
            -0.3f,   0.3f,          // Left-Top
            -0.3f,  -0.3f,          // Left-Bottom
             0.3f,   0.3f,          // Right-Top
             0.3f,  -0.3f           // Right-Bottom
    };

    private float offsetX;
    private float offsetY;

    private float velocityX;
    private float velocityY;

    private float degrees;

    private float life;

    public Particle() {
        super(-1, -1);
        offsetX = 0.0f;
        offsetY = 0.0f;
        velocityX = 0.0f;
        velocityY = 0.0f;
        degrees = 0.0f;
        life = 0.0f;
    }

    public void update(float deltaTime) {
        life -= deltaTime;
        if (life > 0.0f) {
            degrees += deltaTime * 500.0f;
            offsetX += velocityX * deltaTime;
            offsetY += velocityY * deltaTime;
        }
    }

    public static float[] GetVertexData() {
        return vertexData;
    }

    public float getOffsetX() {
        return offsetX;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public void setOffset(float offsetX, float offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public void setVelocity(float velocityX, float velocityY) {
        this.velocityX = velocityX;
        this.velocityY = velocityY;
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
