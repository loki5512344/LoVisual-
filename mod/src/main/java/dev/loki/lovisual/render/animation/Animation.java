package dev.loki.lovisual.render.animation;

public class Animation {
    private final Easing easing;
    private final float duration;
    private float elapsed;
    private float from;
    private float to;
    private boolean forward;

    public Animation(Easing easing, float duration) {
        this.easing = easing;
        this.duration = duration;
        this.elapsed = 0;
        this.from = 0;
        this.to = 1;
        this.forward = true;
    }

    public Animation(Easing easing, float duration, float from, float to) {
        this(easing, duration);
        this.from = from;
        this.to = to;
    }

    public void reset() {
        elapsed = 0;
    }

    public void setDirection(boolean forward) {
        if (this.forward != forward) {
            elapsed = duration - elapsed;
            this.forward = forward;
        }
    }

    public void update(float delta) {
        elapsed = Math.min(duration, elapsed + delta);
    }

    public float getProgress() {
        float t = Math.min(1, elapsed / duration);
        return easing.apply(t);
    }

    public float getValue() {
        float t = getProgress();
        return from + (to - from) * t;
    }

    public boolean isFinished() {
        return elapsed >= duration;
    }
}
