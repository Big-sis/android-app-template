package fr.vyfe;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;
//TODO: classe qui sera dans lavenir supprimer av les nouveauw fragments
public class PreparedSessionAdapter extends FragmentPagerAdapter {
    private final List fragments;

    //On fournit à l'adapter la liste des fragments à afficher
    public PreparedSessionAdapter(FragmentManager fm, List fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return (Fragment) this.fragments.get(position);
    }

    @Override
    public int getCount() {
        return this.fragments.size();
    }
}
