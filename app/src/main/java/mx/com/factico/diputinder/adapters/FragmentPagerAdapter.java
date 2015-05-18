package mx.com.factico.diputinder.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zace3d on 18/05/15.
 */
public class FragmentPagerAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> listFragments;

    public FragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        this.listFragments = new ArrayList<>();
    }

    public void clear() {
        listFragments.clear();
    }

    /**
     * Add a new fragment in the list.
     *
     * @param fragment a new fragment
     */
    public void addFragment(Fragment fragment) {
        this.listFragments.add(fragment);
    }

    @Override
    public Fragment getItem(int position) {
        return listFragments != null ? listFragments.get(position) : null;
    }

    @Override
    public int getCount() {
        return this.listFragments.size();
    }
}