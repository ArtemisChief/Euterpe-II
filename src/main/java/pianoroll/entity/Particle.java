package pianoroll.entity;

public class Particle extends GraphicElement{

    private float offsetX;
    private float offsetY;

    private float velocityX;
    private float velocityY;

    private float scale;

    private float degrees;

    private float life;

    private float timeSum;
    private float lifeConst;
    private float scaleConst;

    public Particle() {
        super(-1);

        offsetX = 0.0f;
        offsetY = 0.0f;
        velocityX = 0.0f;
        velocityY = 0.0f;
        scale = 0.0f;
        degrees = 0.0f;
        life = 0.0f;
        timeSum = 0.0f;
        lifeConst = 0.0f;
        scaleConst = 0.0f;
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

    public float getVelocityX() {
        return velocityX;
    }

    public float getVelocityY() {
        return velocityY;
    }

    public void setVelocity(float velocityX, float velocityY) {
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
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

    public float getTimeSum() {
        return timeSum;
    }

    public void setTimeSum(float timeSum) {
        this.timeSum = timeSum;
    }

    public float getLifeConst() {
        return lifeConst;
    }

    public void setLifeConst(float lifeConst) {
        this.lifeConst = lifeConst;
    }

    public float getScaleConst() {
        return scaleConst;
    }

    public void setScaleConst(float scaleConst) {
        this.scaleConst = scaleConst;
    }

}
