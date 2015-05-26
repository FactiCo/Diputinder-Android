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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import mx.com.factico.diputinder.adapters.DrawerAdapter;
import mx.com.factico.diputinder.beans.DrawerOption;
import mx.com.factico.diputinder.dialogues.Dialogues;
import mx.com.factico.diputinder.fragments.MainFragment;
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
        mDrawerOptions.add(new DrawerOption("Diputados"));
        mDrawerOptions.add(new DrawerOption("Gobernadores"));
        mDrawerList = (RecyclerView) findViewById(R.id.left_drawer_recycler);
        mDrawerList.setHasFixedSize(true);
        mDrawerList.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        mDrawerList.setItemAnimator(new DefaultItemAnimator());

        DrawerAdapter drawerAdapter = new DrawerAdapter(mDrawerOptions);
        drawerAdapter.setOnItemClickListener(new DrawerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Dialogues.Toast(getBaseContext(), "Click in position: " + position, Toast.LENGTH_LONG);
            }
        });

        // Set the adapter for the list view
        mDrawerList.setAdapter(new DrawerAdapter(mDrawerOptions));

        selectItem(0);
    }

    private class DrawerItemClickListener implements DrawerAdapter.OnItemClickListener {
        @Override
        public void onItemClick(View view, int position) {
            selectItem(position);
        }
    }

    /** Swaps fragments in the main content view */
    private void selectItem(int position) {
        // Create a new fragment and specify the planet to show based on position
        Fragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putInt(MainFragment.INDEX, position);
        fragment.setArguments(args);

        // Insert the fragment by replacing any existing fragment
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

    @Override
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

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        /*if (id == R.id.action_diputados) {
            if (candidatoType != CandidatoType.DIPUTADO) {
                candidatoType = CandidatoType.DIPUTADO;

                loadCandidatos(candidatoType);
            }
            return true;
        }

        if (id == R.id.action_gobernadores) {
            if (candidatoType != CandidatoType.GOBERNADOR) {
                candidatoType = CandidatoType.GOBERNADOR;

                loadCandidatos(candidatoType);
            }
            return true;
        }

        if (id == R.id.action_refresh) {
            showDialog("Obteniendo ciudad donde te encuentras...");
            clientListener.startLocationUpdates();
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }
}
