package mx.com.factico.diputinder;

import android.app.ProgressDialog;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.maps.model.LatLng;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.List;

import mx.com.factico.diputinder.adapters.MyArrayAdapter;
import mx.com.factico.diputinder.beans.Address;
import mx.com.factico.diputinder.beans.Diputado;
import mx.com.factico.diputinder.dialogues.Dialogues;
import mx.com.factico.diputinder.httpconnection.HttpConnection;
import mx.com.factico.diputinder.location.LocationClientListener;
import mx.com.factico.diputinder.location.LocationUtils;
import mx.com.factico.diputinder.parser.GsonParser;

public class MainActivity extends ActionBarActivity implements View.OnClickListener {
    public static final String TAG_CLASS = MainActivity.class.getSimpleName();

    private List<Diputado> diputados = new ArrayList<>();
    private List<Diputado> auxDiputados = new ArrayList<>();
    private MyArrayAdapter arrayAdapter;
    private int i;

    private SwipeFlingAdapterView flingContainer;

    private LocationClientListener clientListener;
    private LatLng userLocation;
    private Address address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar();
        initLocationClientListener();
        // initUI();
    }

    protected void initLocationClientListener() {
        clientListener = new LocationClientListener(MainActivity.this);
        clientListener.setOnLocationClientListener(new LocationClientListener.OnLocationClientListener() {
            @Override
            public void onLocationChanged(Location location) {
                userLocation = LocationUtils.getLatLngFromLocation(location);
                //Dialogues.Toast(getBaseContext(), "Find location: " + location.getLatitude() + ", " + location.getLongitude(), Toast.LENGTH_LONG);

                address = LocationUtils.getAdressFromLatLong(getBaseContext(), location.getLatitude(), location.getLongitude());

                if (address != null) {
                    Dialogues.Log(TAG_CLASS, "Address: " + address.getAddress(), Log.ERROR);
                    Dialogues.Log(TAG_CLASS, "City: " + address.getCity(), Log.ERROR);
                    Dialogues.Log(TAG_CLASS, "State: " + address.getState(), Log.ERROR);
                    Dialogues.Log(TAG_CLASS, "Country: " + address.getCountry(), Log.ERROR);
                    Dialogues.Log(TAG_CLASS, "PostalCode: " + address.getPostalCode(), Log.ERROR);
                    Dialogues.Log(TAG_CLASS, "KnownName: " + address.getKnownName(), Log.ERROR);

                    loadDiputados();

                    clientListener.stopLocationUpdates();
                }
            }
        });
    }

    public void startLocationListener() {
        if (clientListener != null)
            clientListener.connect();
    }

    public void stopLocationListener() {
        if (clientListener != null)
            clientListener.disconnect();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (clientListener != null && userLocation == null) {
            startLocationListener();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (clientListener != null) {
            clientListener.stopLocationUpdates();
        }
    }

    protected void loadDiputados() {
        GetDiputadosPublicationsAsyncTask task = new GetDiputadosPublicationsAsyncTask();
        task.execute();
    }

    protected void initUI() {
        flingContainer = (SwipeFlingAdapterView) findViewById(R.id.main_swipe_tinder);

        /*diputados = new ArrayList<>();
        diputados.add(new Diputado("php"));
        diputados.add(new Diputado("c"));
        diputados.add(new Diputado("python"));
        diputados.add(new Diputado("java"));
        diputados.add(new Diputado("html"));
        diputados.add(new Diputado("c++"));
        diputados.add(new Diputado("css"));
        diputados.add(new Diputado("javascript"));*/

        // arrayAdapter = new ArrayAdapter<>(this, R.layout.item, R.id.helloText, diputados);
        arrayAdapter = new MyArrayAdapter(this, auxDiputados);

        flingContainer.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();

        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                auxDiputados.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                Dialogues.Toast(getBaseContext(), "Left!", Toast.LENGTH_SHORT);
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Dialogues.Toast(getBaseContext(), "Right!", Toast.LENGTH_SHORT);
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here
                //diputados.add(new Diputado("XML ".concat(String.valueOf(i))));
                //arrayAdapter.notifyDataSetChanged();
                //Log.d("LIST", "notified");
                //i++;
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                View view = flingContainer.getSelectedView();
                view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
            }
        });

        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Dialogues.Toast(getBaseContext(), "Clicked!", Toast.LENGTH_SHORT);
            }
        });

        findViewById(R.id.main_btn_swipe_left).setOnClickListener(this);
        findViewById(R.id.main_btn_swipe_right).setOnClickListener(this);
    }

    protected void setSupportActionBar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.actionbar);
        mToolbar.setTitle("");
        mToolbar.getBackground().setAlpha(255);
        TextView actionbarTitle = (TextView) mToolbar.findViewById(R.id.actionbar_title);
        actionbarTitle.setText(getResources().getString(R.string.app_name));
        actionbarTitle.setTextColor(getResources().getColor(R.color.colorWhite));

        setSupportActionBar(mToolbar);
        getSupportActionBar().setElevation(5);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_btn_swipe_left:
                swipeLeft();
                break;
            case R.id.main_btn_swipe_right:
                swipeRight();
                break;
        }
    }

    private void swipeLeft() {
        flingContainer.getTopCardListener().selectLeft();
    }

    private void swipeRight() {
        /**
         * Trigger the right event manually.
         */
        flingContainer.getTopCardListener().selectRight();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class GetDiputadosPublicationsAsyncTask extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;

        public GetDiputadosPublicationsAsyncTask() {}

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Obteniendo json de diputados");
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            return HttpConnection.GET(HttpConnection.URL + HttpConnection.DIPUTADOS);
        }

        @Override
        protected void onPostExecute(String result) {
            Dialogues.Log(TAG_CLASS, "Result: " + result, Log.INFO);
            // Dialogues.Toast(getBaseContext(), "Result: " + result, Toast.LENGTH_LONG);

            if (result != null) {
                try {
                    diputados = GsonParser.getListDiputadosFromJSON(result);

                    if (diputados != null && diputados.size() > 0) {
                        //auxDiputados = diputados.subList(1, 30);
                        auxDiputados = getListDiputadosFromState(diputados, address.getState());

                        if (auxDiputados != null && auxDiputados.size() > 0) {
                            Dialogues.Log(TAG_CLASS, "/**************Entré INITUI: " + auxDiputados.size(), Log.ERROR);

                            initUI();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }

            if (!this.isCancelled())
                this.cancel(true);
        }
    }

    protected List<Diputado> getListDiputadosFromState(List<Diputado> listDiputados, String state) {
        List<Diputado> auxListDiputados = new ArrayList<>();

        for (Diputado diputado : listDiputados) {
            if (diputado.entidadFederativa.equals(state))
                auxListDiputados.add(diputado);
        }

        return auxListDiputados;
    }
}
