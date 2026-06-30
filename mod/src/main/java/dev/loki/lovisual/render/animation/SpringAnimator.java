package dev.loki.lovisual.render.animation;

public class SpringAnimator {
    private float value;
    private float velocity;
    private float target;
    private float stiffness = 180f;
    private float damping = 12f;
    private float precision = 0.01f;

    public SpringAnimator() {
        this(0);
    }

    public SpringAnimator(float initial) {
        this.value = initial;
        this.target = initial;
    }

    public void setTarget(float target) {
        this.target = target;
    }

    public void setStiffness(float stiffness) {
        this.stiffness = stiffness;
    }

    public void setDamping(float damping) {
        this.damping = damping;
    }

    public void setPrecision(float precision) {
        this.precision = precision;
    }

    public void update(float delta) {
        float force = (target - value) * stiffness;
        velocity += force * delta;
        velocity *= (float) Math.exp(-damping * delta);
        value += velocity * delta;
    }

    public float getValue() {
        return value;
    }

    public float getTarget() {
        return target;
    }

    public boolean isSettled() {
        return Math.abs(value - target) < precision && Math.abs(velocity) < precision;
    }
}
