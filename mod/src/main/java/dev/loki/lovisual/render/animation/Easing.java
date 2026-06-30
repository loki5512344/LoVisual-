package dev.loki.lovisual.render.animation;

public enum Easing {
    LINEAR {
        public float apply(float t) { return t; }
    },
    EASE_IN_QUAD {
        public float apply(float t) { return t * t; }
    },
    EASE_OUT_QUAD {
        public float apply(float t) { return t * (2 - t); }
    },
    EASE_IN_OUT_QUAD {
        public float apply(float t) { return t < 0.5f ? 2 * t * t : -1 + (4 - 2 * t) * t; }
    },
    EASE_IN_CUBIC {
        public float apply(float t) { return t * t * t; }
    },
    EASE_OUT_CUBIC {
        public float apply(float t) { return (--t) * t * t + 1; }
    },
    EASE_IN_OUT_CUBIC {
        public float apply(float t) { return t < 0.5f ? 4 * t * t * t : (t - 1) * (2 * t - 2) * (2 * t - 2) + 1; }
    },
    EASE_IN_EXPO {
        public float apply(float t) { return t == 0 ? 0 : (float) Math.pow(2, 10 * (t - 1)); }
    },
    EASE_OUT_EXPO {
        public float apply(float t) { return t == 1 ? 1 : (float) (1 - Math.pow(2, -10 * t)); }
    },
    EASE_IN_BOUNCE {
        public float apply(float t) { return 1 - EASE_OUT_BOUNCE.apply(1 - t); }
    },
    EASE_OUT_BOUNCE {
        public float apply(float t) {
            if (t < 1 / 2.75f) return 7.5625f * t * t;
            if (t < 2 / 2.75f) return 7.5625f * (t -= 1.5f / 2.75f) * t + 0.75f;
            if (t < 2.5f / 2.75f) return 7.5625f * (t -= 2.25f / 2.75f) * t + 0.9375f;
            return 7.5625f * (t -= 2.625f / 2.75f) * t + 0.984375f;
        }
    },
    EASE_IN_OUT_ELASTIC {
        public float apply(float t) {
            if (t == 0 || t == 1) return t;
            float p = 0.3f;
            float s = p / 4;
            return (t -= 0.5f) < 0
                ? (float) (-Math.pow(2, 10 * t) * Math.sin((t - s) * (2 * Math.PI) / p))
                : (float) (Math.pow(2, -10 * t) * Math.sin((t - s) * (2 * Math.PI) / p) + 1);
        }
    };

    public abstract float apply(float t);
}
