package mx.com.factico.diputinder;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import mx.com.factico.diputinder.adapters.DrawerAdapter;
import mx.com.factico.diputinder.beans.CandidatoType;
import mx.com.factico.diputinder.beans.DrawerOption;
import mx.com.factico.diputinder.beans.StateType;
import mx.com.factico.diputinder.dialogues.Dialogues;
import mx.com.factico.diputinder.fragments.MainFragment;
import mx.com.factico.diputinder.location.LocationUtils;
import mx.com.factico.diputinder.views.CustomTextView;

/**
 * Created by zace3d on 18/05/15.
 */
public class MainActivity extends ActionBarActivity {
    public static final String TAG_CLASS = MainActivity.class.getSimpleName();

    private DrawerLayout mDrawerLayout;
    private RecyclerView mDrawerList;
    private List<DrawerOption> mDrawerOptions;
    private CustomTextView actionbarTitle;
    private Toolbar mToolbar;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar();
        initUI();

        //LocationUtils.getStateFromLatLong(getBaseContext());
    }

    protected void setSupportActionBar() {
        mToolbar = (Toolbar) findViewById(R.id.actionbar);
        mToolbar.setTitle("");
        mToolbar.getBackground().setAlpha(255);
        actionbarTitle = (CustomTextView) mToolbar.findViewById(R.id.actionbar_title);
        actionbarTitle.setText(getResources().getString(R.string.app_name));

        setSupportActionBar(mToolbar);
    }

    protected void initUI() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        /****/
        mDrawerOptions = new ArrayList<>();
        mDrawerOptions.add(new DrawerOption("Ver diputados"));
        mDrawerOptions.add(new DrawerOption("Ver gobernadores"));
        mDrawerOptions.add(new DrawerOption("Ver alcaldes"));
        mDrawerList = (RecyclerView) findViewById(R.id.left_drawer_recycler);
        mDrawerList.setHasFixedSize(true);
        mDrawerList.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        mDrawerList.setItemAnimator(new DefaultItemAnimator());

        DrawerAdapter drawerAdapter = new DrawerAdapter(mDrawerOptions);
        drawerAdapter.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerList.setAdapter(drawerAdapter);

        selectItem(1);
    }

    private class DrawerItemClickListener implements DrawerAdapter.OnItemClickListener {
        @Override
        public void onItemClick(View view, int position) {
            // Dialogues.Toast(getBaseContext(), "Position: " + position, Toast.LENGTH_SHORT);
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        Fragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putInt(MainFragment.INDEX, position);
        args.putSerializable(MainFragment.CANDIDATO_TYPE, position);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();

        // Highlight the selected item, update the title, and close the drawer
        // mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        actionbarTitle.setText(title);
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            //showDialog("Obteniendo ciudad donde te encuentras...");
            //clientListener.startLocationUpdates();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
}
