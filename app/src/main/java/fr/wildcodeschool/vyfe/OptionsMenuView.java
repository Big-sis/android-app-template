package fr.wildcodeschool.vyfe;

import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;

public class OptionsMenuView {
    public static void invisibleLogout(Menu menu,  Activity activity){
        MenuItem itemLogout = menu.findItem(R.id.logout);
        itemLogout.setVisible(false);
        activity.invalidateOptionsMenu();

    }
}
