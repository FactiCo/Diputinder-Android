package mx.com.factico.diputinder.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import mx.com.factico.diputinder.DiputadoActivity;
import mx.com.factico.diputinder.R;
import mx.com.factico.diputinder.adapters.MyArrayAdapter;
import mx.com.factico.diputinder.beans.Address;
import mx.com.factico.diputinder.beans.CandidatoType;
import mx.com.factico.diputinder.beans.Candidatos;
import mx.com.factico.diputinder.beans.Diputado;
import mx.com.factico.diputinder.beans.StateType;
import mx.com.factico.diputinder.dialogues.Dialogues;
import mx.com.factico.diputinder.httpconnection.HttpConnection;
import mx.com.factico.diputinder.httpconnection.NetworkUtils;
import mx.com.factico.diputinder.location.LocationClientListener;
import mx.com.factico.diputinder.location.LocationUtils;
import mx.com.factico.diputinder.parser.GsonParser;
import mx.com.factico.diputinder.utils.PreferencesUtils;
import mx.com.factico.diputinder.utils.ScreenUtils;
import mx.com.factico.diputinder.views.CustomTextView;

/**
 * Created by zace3d on 26/05/15.
 */

public class MainFragment extends Fragment implements View.OnClickListener {
    public static final String INDEX = "index";
    public static final String CANDIDATO_TYPE = "candidato_type";

    public static final String TAG_CLASS = MainFragment.class.getSimpleName();

    private List<Diputado> auxDiputados = new ArrayList<>();
    private MyArrayAdapter arrayAdapter;
    private int i;

    private SwipeFlingAdapterView flingContainer;

    private LocationClientListener clientListener;
    private LatLng userLocation;
    private String state;
    private DisplayImageOptions options;

    private CandidatoType candidatoType = CandidatoType.DIPUTADO;
    private View rootView;

    private String json_PDF;
    private boolean isFirstTime = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        int i = getArguments().getInt(INDEX);
        candidatoType = CandidatoType.getCandidatoType(getArguments().getInt(CANDIDATO_TYPE));

        createView();

        return rootView;
    }

    private void createView() {
        json_PDF = PreferencesUtils.getStringPreference(getActivity().getApplication(), PreferencesUtils.JSON_PDF);

        state = PreferencesUtils.getStringPreference(getActivity().getApplication(), PreferencesUtils.STATE);
        if (state != null && !state.equals("")) {
            //Dialogues.Toast(getActivity(), "State: " + state + ", CandidatoType: " + candidatoType, Toast.LENGTH_SHORT);
            loadCandidatos(candidatoType);
        } else if (LocationUtils.isGpsOrNetworkProviderEnabled(getActivity())) {
            initLocationClientListener();

            if (NetworkUtils.isNetworkConnectionAvailable(getActivity())) {
                showDialog(getResources().getString(R.string.getting_city));
                initUI();
            } else {
                setTextMessageError(getResources().getString(R.string.no_internet_connection));
            }
        } else {
            setTextMessageError(getResources().getString(R.string.no_gps_enabled));
        }

        options = new DisplayImageOptions.Builder()
                //.showImageOnLoading(null)
                .showImageForEmptyUri(R.drawable.ic_avatar_no)
                .showImageOnFail(R.drawable.ic_avatar_no)
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                //.displayer(new FadeInBitmapDisplayer(300))
                .build();
    }

    protected void initLocationClientListener() {
        clientListener = new LocationClientListener(getActivity());
        clientListener.setOnLocationClientListener(new LocationClientListener.OnLocationClientListener() {
            @Override
            public void onLocationChanged(Location location) {
                userLocation = LocationUtils.getLatLngFromLocation(location);
                // Dialogues.Toast(getBaseContext(), "Find location: " + location.getLatitude() + ", " + location.getLongitude(), Toast.LENGTH_LONG);

                Address address = LocationUtils.getAdressFromLatLong(getActivity(), location.getLatitude(), location.getLongitude());

                if (address != null) {
                    /*Dialogues.Log(TAG_CLASS, "Address: " + address.getAddress(), Log.ERROR);
                    Dialogues.Log(TAG_CLASS, "City: " + address.getCity(), Log.ERROR);
                    Dialogues.Log(TAG_CLASS, "State: " + address.getState(), Log.ERROR);
                    Dialogues.Log(TAG_CLASS, "Country: " + address.getCountry(), Log.ERROR);
                    Dialogues.Log(TAG_CLASS, "PostalCode: " + address.getPostalCode(), Log.ERROR);
                    Dialogues.Log(TAG_CLASS, "KnownName: " + address.getKnownName(), Log.ERROR);*/

                    if (address.getState() != null && !address.getState().equals("")) {
                        PreferencesUtils.putStringPreference(getActivity().getApplication(), PreferencesUtils.STATE, address.getState());

                        state = address.getState();
                        // state = StateType.getStateName(StateType.getStateType(state));

                        //if (state == null)
                        loadCandidatos(candidatoType);
                    }

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
    public void onPause() {
        super.onPause();

        if (clientListener != null) {
            clientListener.stopLocationUpdates();
        }
    }

    protected void loadCandidatos(CandidatoType candidatoType) {
        showDialog(getResources().getString(R.string.getting_info));

        if (rootView.findViewById(R.id.main_btn_refresh).getVisibility() != View.GONE)
            rootView.findViewById(R.id.main_btn_refresh).setVisibility(View.GONE);

        String url = null;

        if (candidatoType.equals(CandidatoType.DIPUTADO)) {
            url = HttpConnection.URL + HttpConnection.DIPUTADOS;

        } else if (candidatoType.equals(CandidatoType.GOBERNADOR)) {
            url = HttpConnection.URL + HttpConnection.GOBERNADORES;

        } else if (candidatoType.equals(CandidatoType.ALCALDIAS)) {
            url = HttpConnection.URL + HttpConnection.ALCALDIAS;
        }

        if (url != null) {
            if (NetworkUtils.isNetworkConnectionAvailable(getActivity())) {
                GetDiputadosPublicationsAsyncTask task = new GetDiputadosPublicationsAsyncTask(url);
                task.execute();

                if (arrayAdapter != null) {
                    arrayAdapter.clear();
                    arrayAdapter.notifyDataSetChanged();
                }
            } else {
                setTextMessageError(getResources().getString(R.string.no_internet_connection));
            }
        }/* else {
            dismissDialog();
        }*/
    }

    private void setTextMessageError(String messageError) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

        View view = rootView.findViewById(R.id.main_btn_refresh);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state != null) {
                    loadCandidatos(candidatoType);
                } else {
                    showDialog(getResources().getString(R.string.getting_city));
                    initLocationClientListener();
                }
            }
        });
        view.setVisibility(View.VISIBLE);
        ((CustomTextView) rootView.findViewById(R.id.main_tv_error_message)).setText(messageError);
    }

    protected void initUI() {
        flingContainer = (SwipeFlingAdapterView) rootView.findViewById(R.id.main_swipe_tinder);

        Point point = ScreenUtils.getScreenSize(getActivity());
        int width = point.x / 4;

        View btnSwipeLeft = rootView.findViewById(R.id.main_btn_swipe_left);
        LinearLayout.LayoutParams paramsLeft = new LinearLayout.LayoutParams(width, width);
        paramsLeft.setMargins(0, 0, width / 3, 0);
        btnSwipeLeft.setLayoutParams(paramsLeft);
        btnSwipeLeft.setOnClickListener(this);

        View btnSwipeRight = rootView.findViewById(R.id.main_btn_swipe_right);
        LinearLayout.LayoutParams paramsRight = new LinearLayout.LayoutParams(width, width);
        paramsRight.setMargins(width / 3, 0, 0, 0);
        btnSwipeRight.setLayoutParams(paramsRight);
        btnSwipeRight.setOnClickListener(this);

        if (auxDiputados != null && auxDiputados.size() > 0) {
            // arrayAdapter = new ArrayAdapter<>(this, R.layout.item, R.id.helloText, diputados);
            arrayAdapter = new MyArrayAdapter(getActivity(), auxDiputados);

            flingContainer.setAdapter(arrayAdapter);
            arrayAdapter.notifyDataSetChanged();

            flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
                @Override
                public void removeFirstObjectInAdapter() {
                    // this is the simplest way to delete an object from the Adapter (/AdapterView)
                    //Log.d("LIST", "removed object!");
                    auxDiputados.remove(0);
                    arrayAdapter.notifyDataSetChanged();
                }

                @Override
                public void onLeftCardExit(Object dataObject) {}

                @Override
                public void onRightCardExit(Object dataObject) {
                    Diputado diputado = (Diputado) dataObject;
                    if (diputado != null)
                        showDialogSwipe(diputado);
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

            flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
                @Override
                public void onItemClicked(int itemPosition, Object dataObject) {
                    startIntentDiputado((Diputado) dataObject);
                }
            });
        }
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

    private void startIntentDiputado(Diputado diputado) {
        Intent intent = new Intent(getActivity(), DiputadoActivity.class);
        intent.putExtra(DiputadoActivity.TAG_DIPUTADO, diputado);
        startActivity(intent);
    }

    private void swipeLeft() {
        /**
         * Trigger the left event manually.
         */
        if (flingContainer != null && flingContainer.getChildCount() > 0)
            flingContainer.getTopCardListener().selectLeft();
    }

    private void swipeRight() {
        /**
         * Trigger the right event manually.
         */
        if (flingContainer != null && flingContainer.getChildCount() > 0)
            flingContainer.getTopCardListener().selectRight();
    }

    private AlertDialog alertDialog;
    private void showDialogSwipe(Diputado diputado) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_tweet, null, false);

        CustomTextView tvMessage = (CustomTextView) view.findViewById(R.id.dialog_tweet_tv_message);
        CustomTextView tvSubMessage = (CustomTextView) view.findViewById(R.id.dialog_tweet_tv_submessage);

        String userName = (diputado.getTwitter() != null && !diputado.getTwitter().equals(""))
                ? "@" + diputado.getTwitter()
                : "#" + diputado.getNombres().replaceAll("\\s+", "")
                + diputado.getApellidoPaterno().replaceAll("\\s+", "")
                + diputado.getApellidoMaterno().replaceAll("\\s+", "");

        View btnTweet = view.findViewById(R.id.dialog_tweet_btn_tweet);
        btnTweet.setOnClickListener(TweetOnClickListener);

        View btnTweetInvite = view.findViewById(R.id.dialog_tweet_btn_tweet_invite);
        btnTweetInvite.setTag(String.format(Locale.getDefault(), getResources().getString(R.string.tweet_second_message_invite), userName));
        btnTweetInvite.setOnClickListener(TweetOnClickListener);

        if ((diputado.getPatrimonialPDF() != null && !diputado.getPatrimonialPDF().equals(""))
                && (diputado.getInteresesPDF() != null && !diputado.getInteresesPDF().equals(""))
                && (diputado.getFiscalPDF() != null && !diputado.getFiscalPDF().equals(""))) {
            tvMessage.setText(getResources().getString(R.string.tweet_message_good));
            tvSubMessage.setText(getResources().getString(R.string.tweet_submessage_good));
            btnTweet.setTag(String.format(Locale.getDefault(), getResources().getString(R.string.tweet_first_message_good), userName));

            //btnTweetInvite.setVisibility(View.GONE);
        } else {
            tvMessage.setText(getResources().getString(R.string.tweet_message_bad));
            tvSubMessage.setText(getResources().getString(R.string.tweet_submessage_bad));
            btnTweet.setTag(String.format(Locale.getDefault(), getResources().getString(R.string.tweet_first_message_bad), userName));
        }

        CustomTextView tvName = (CustomTextView) view.findViewById(R.id.dialog_tweet_tv_name);
        tvName.setText(String.format(Locale.getDefault(), "%s %s %s", diputado.getNombres(), diputado.getApellidoPaterno(), diputado.getApellidoMaterno()));

        ImageView ivProfile = (ImageView) view.findViewById(R.id.dialog_tweet_iv_profile);
        if (diputado.getTwitter() != null && !diputado.getTwitter().equals("")) {
            String twitter = diputado.getTwitter().replaceAll("\\s+", "");
            ImageLoader.getInstance().displayImage(String.format(Locale.getDefault(), HttpConnection.TWITTER_IMAGE_URL, twitter), ivProfile, options);
        } else {
            if (diputado.getGnero() != null) {
                if (diputado.getGnero().equals("F"))
                    ivProfile.setImageResource(R.drawable.ic_avatar_women);
                else if (diputado.getGnero().equals("M"))
                    ivProfile.setImageResource(R.drawable.ic_avatar_men);
            }
        }

        builder.setView(view);

        alertDialog = builder.create();
        alertDialog.setCancelable(true);
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
    }

    View.OnClickListener TweetOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String messageTweet = v.getTag() != null ? v.getTag().toString() : null;

            if (messageTweet != null && !messageTweet.equals(""))
                startShareIntent(messageTweet);
        }
    };

    private void startShareIntent(String messageTweet) {
        if (appInstalledOrNot("com.twitter.android")) {
            /*if (dialog != null && dialog.isShowing())
                dialog.dismiss();*/

            Intent shareIntent = findTwitterClient();
            shareIntent.putExtra(Intent.EXTRA_TEXT, messageTweet);
            startActivity(Intent.createChooser(shareIntent, "Compartir"));
        } else {
            Dialogues.Toast(getActivity(), "Necesitas tener instalada la app de Twitter para poder compartir", Toast.LENGTH_LONG);
        }
    }

    public Intent findTwitterClient() {
        final String[] twitterApps = {
                // package // name - nb installs (thousands)
                "com.twitter.android", // official - 10 000
                //"com.twidroid", // twidroid - 5 000
                //"com.handmark.tweetcaster", // Tweecaster - 5 000
                //"com.thedeck.android"
        }; // TweetDeck - 5 000 };
        Intent tweetIntent = new Intent();
        tweetIntent.setType("text/plain");
        final PackageManager packageManager = getActivity().getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(tweetIntent, PackageManager.MATCH_DEFAULT_ONLY);

        for (int i = 0; i < twitterApps.length; i++) {
            for (ResolveInfo resolveInfo : list) {
                String p = resolveInfo.activityInfo.packageName;
                if (p != null && p.startsWith(twitterApps[i])) {
                    tweetIntent.setPackage(p);
                    return tweetIntent;
                }
            }
        }

        return null;
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getActivity().getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    private ProgressDialog dialog;
    private void showDialog(String message) {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();

        dialog = new ProgressDialog(getActivity());
        dialog.setMessage(message);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            showDialog(getResources().getString(R.string.getting_city));
            initLocationClientListener();
            startLocationListener();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class GetDiputadosPublicationsAsyncTask extends AsyncTask<String, String, String> {
        private String url;

        public GetDiputadosPublicationsAsyncTask(String url) {
            this.url = url;
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected String doInBackground(String... params) {
            if ((json_PDF == null || json_PDF.equals("")) && isFirstTime) {
                json_PDF = HttpConnection.GET(HttpConnection.URL + HttpConnection.PDFS);
                // Dialogues.Log(TAG_CLASS, "Entré primera vez: " + json_PDF, Log.ERROR);
            } else {
                // Dialogues.Log(TAG_CLASS, "Entré segunda vez: " + json_PDF, Log.ERROR);
            }

            return HttpConnection.GET(url);
        }

        @Override
        protected void onPostExecute(String result) {
            // Dialogues.Log(TAG_CLASS, "Result: " + result, Log.ERROR);
            // Dialogues.Log(TAG_CLASS, "json_PDF: " + json_PDF, Log.ERROR);
            // Dialogues.Toast(getActivity(), "Result: " + result, Toast.LENGTH_LONG);

            if (result != null) {
                if (json_PDF == null || json_PDF.equals("")) {
                    json_PDF = PreferencesUtils.getStringPreference(getActivity().getApplication(), PreferencesUtils.JSON_PDF);
                }

                if (json_PDF != null && !json_PDF.equals("")) {
                    try {
                        List<Diputado> diputadosAux = GsonParser.getListDiputadosFromJSON(result);
                        Candidatos candidatos = GsonParser.getCandidatosFromJSON(json_PDF);

                        if (candidatos == null || diputadosAux == null) {
                            return;
                        }

                        isFirstTime = false;
                        PreferencesUtils.putStringPreference(getActivity().getApplication(), PreferencesUtils.JSON_PDF, json_PDF);

                        if (diputadosAux.size() > 0) {
                            // Dialogues.Log(TAG_CLASS, "Size Candidatos: " + diputadosAux.size(), Log.ERROR);

                            String stateApi = StateType.getStateName(StateType.getStateType(state));
                            // Dialogues.Log(TAG_CLASS, "Estado: " + stateApi, Log.ERROR);

                            List<Diputado> diputadosUnorder = getListDiputadosFromState(diputadosAux, stateApi);
                            // Dialogues.Log(TAG_CLASS, "Size Filter: " + diputadosUnorder.size(), Log.ERROR);

                            List<Diputado> auxDiputadosOrdered = getOrderedListDiputados(diputadosUnorder);
                            // Dialogues.Log(TAG_CLASS, "Size Ordered: " + auxDiputadosOrdered.size(), Log.ERROR);

                            if (auxDiputadosOrdered.size() > 0) {
                                List<Diputado> candidatosPDF = candidatos.getCandidatos();
                                if (candidatosPDF != null && candidatosPDF.size() > 0) {
                                    // Dialogues.Log(TAG_CLASS, "Size candidatosPDF: " + candidatosPDF.size(), Log.ERROR);

                                    for (Diputado diputado : candidatosPDF) {
                                        // Dialogues.Log(TAG_CLASS, "Diputado: " + diputado.getNombres(), Log.ERROR);

                                        if (auxDiputadosOrdered.contains(diputado)) {
                                            int indexOf = auxDiputadosOrdered.indexOf(diputado);
                                            if (indexOf != -1) {
                                                auxDiputadosOrdered.get(indexOf).setPatrimonialPDF(diputado.getPatrimonialPDF());
                                                auxDiputadosOrdered.get(indexOf).setFiscalPDF(diputado.getFiscalPDF());
                                                auxDiputadosOrdered.get(indexOf).setInteresesPDF(diputado.getInteresesPDF());
                                            }
                                            // Dialogues.Log(TAG_CLASS, "Lo contiene: " + diputado.getNombres() + diputado.getApellidoPaterno(), Log.ERROR);
                                        } else {
                                            // Dialogues.Log(TAG_CLASS, "NO lo contiene: " + diputado.getNombres() + diputado.getApellidoPaterno(), Log.ERROR);
                                        }
                                    }
                                } else {
                                    // Dialogues.Log(TAG_CLASS, "candidatosPDF NULL", Log.ERROR);
                                }

                                auxDiputados = auxDiputadosOrdered;

                                initUI();
                            } else {
                                Dialogues.Toast(getActivity(), "No se encontraron coincidencias en tu Entidad Federativa.", Toast.LENGTH_SHORT);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    setTextMessageError(getResources().getString(R.string.error_message_default));
                }
            } else {
                setTextMessageError(getResources().getString(R.string.error_message_default));
            }

            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }

            if (!this.isCancelled())
                this.cancel(true);
        }
    }

    protected List<Diputado> getOrderedListDiputados(List<Diputado> listDiputados) {
        List<Diputado> auxTwitter = new ArrayList<>();
        List<Diputado> auxNoTwitter = new ArrayList<>();

        for (Diputado diputado : listDiputados) {
            if (diputado.getTwitter() != null && !diputado.getTwitter().equals("") && !diputado.getTwitter().equals("no se identificó"))
                auxTwitter.add(diputado);
            else
                auxNoTwitter.add(diputado);
        }

        if (auxNoTwitter.size() > 0)
            auxTwitter.addAll(auxNoTwitter);

        return auxTwitter;
    }

    protected List<Diputado> getListDiputadosFromState(List<Diputado> listDiputados, String state) {
        List<Diputado> auxListDiputados = new ArrayList<>();

        for (Diputado diputado : listDiputados) {
            if (diputado.getEntidadFederativa().contains(state))
                auxListDiputados.add(diputado);
        }

        return auxListDiputados;
    }
}
