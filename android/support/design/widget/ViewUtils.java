package android.support.design.widget;

import android.os.Build.VERSION;

class ViewUtils {
    static final Creator DEFAULT_ANIMATOR_CREATOR;

    /* renamed from: android.support.design.widget.ViewUtils.1 */
    static class C02791 implements Creator {
        C02791() {
        }

        public ValueAnimatorCompat createAnimator() {
            return new ValueAnimatorCompat(VERSION.SDK_INT >= 12 ? new ValueAnimatorCompatImplHoneycombMr1() : new ValueAnimatorCompatImplEclairMr1());
        }
    }

    ViewUtils() {
    }

    static {
        DEFAULT_ANIMATOR_CREATOR = new C02791();
    }

    static ValueAnimatorCompat createAnimator() {
        return DEFAULT_ANIMATOR_CREATOR.createAnimator();
    }
}
