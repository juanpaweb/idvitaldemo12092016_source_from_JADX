package android.support.v4.net;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.NotificationCompat.WearableExtender;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.helper.ItemTouchHelper;
import idvital1.idvital1.C0239R;

class ConnectivityManagerCompatHoneycombMR2 {
    ConnectivityManagerCompatHoneycombMR2() {
    }

    public static boolean isActiveNetworkMetered(ConnectivityManager cm) {
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) {
            return true;
        }
        switch (info.getType()) {
            case ItemTouchHelper.ACTION_STATE_IDLE /*0*/:
            case ItemTouchHelper.DOWN /*2*/:
            case DrawerLayout.LOCK_MODE_UNDEFINED /*3*/:
            case ItemTouchHelper.LEFT /*4*/:
            case WearableExtender.SIZE_FULL_SCREEN /*5*/:
            case FragmentManagerImpl.ANIM_STYLE_FADE_EXIT /*6*/:
                return true;
            case ItemTouchHelper.UP /*1*/:
            case C0239R.styleable.Toolbar_contentInsetLeft /*7*/:
            case C0239R.styleable.Toolbar_contentInsetStartWithNavigation /*9*/:
                return false;
            default:
                return true;
        }
    }
}
