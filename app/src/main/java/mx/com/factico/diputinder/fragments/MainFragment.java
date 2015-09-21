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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import mx.com.factico.diputinder.CandidateActivity;
import mx.com.factico.diputinder.R;
import mx.com.factico.diputinder.adapters.MyArrayAdapter;
import mx.com.factico.diputinder.beans.Candidate;
import mx.com.factico.diputinder.beans.CandidateInfo;
import mx.com.factico.diputinder.beans.CandidateType;
import mx.com.factico.diputinder.beans.Candidates;
import mx.com.factico.diputinder.beans.GeocoderResult;
import mx.com.factico.diputinder.beans.Indicator;
import mx.com.factico.diputinder.beans.Territory;
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
    public static final String CANDIDATE_TYPE = "candidate_type";

    public static final String TAG_CLASS = MainFragment.class.getSimpleName();

    private List<CandidateInfo> auxCandidates = new ArrayList<>();
    private MyArrayAdapter arrayAdapter;
    private int i;

    private SwipeFlingAdapterView flingContainer;

    private LocationClientListener clientListener;
    private LatLng userLocation;
    private DisplayImageOptions options;

    private View rootView;

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

        options = new DisplayImageOptions.Builder()
                //.showImageOnLoading(null)
                .showImageForEmptyUri(R.drawable.drawable_bgr_gray)
                .showImageOnFail(R.drawable.drawable_bgr_gray)
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                        //.displayer(new FadeInBitmapDisplayer(300))
                .build();

        createView();

        return rootView;
    }

    private void createView() {
        /*state = PreferencesUtils.getStringPreference(getActivity().getApplication(), PreferencesUtils.STATE);
        if ((state != null && !state.equals(""))) {
            //Dialogues.Toast(getActivity(), "State: " + state + ", CandidateType: " + candidateType, Toast.LENGTH_SHORT);
            loadCandidatos(candidateType);
        } else */

        if (LocationUtils.isGpsOrNetworkProviderEnabled(getActivity())) {
            initLocationClientListener();

            if (NetworkUtils.isNetworkConnectionAvailable(getActivity())) {
                initUI();
            } else {
                setTextMessageError(getResources().getString(R.string.no_internet_connection));
            }
        } else {
            setTextMessageError(getResources().getString(R.string.no_gps_enabled));
        }
    }

    protected void initLocationClientListener() {
        if (LocationUtils.isGpsOrNetworkProviderEnabled(getActivity())) {
            showDialog(getResources().getString(R.string.getting_location));

            clientListener = new LocationClientListener(getActivity());
            clientListener.setOnLocationClientListener(new LocationClientListener.OnLocationClientListener() {
                @Override
                public void onLocationChanged(Location location) {
                    userLocation = LocationUtils.getLatLngFromLocation(location);

                    //Dialogues.Toast(getActivity(), "Latitude: " + userLocation.latitude + ", Longitude: " + userLocation.longitude, Toast.LENGTH_SHORT);

                    showDialog(getResources().getString(R.string.getting_location));

                    clientListener.stopLocationUpdates();

                    reverseGeocoderFromLatLng(userLocation);
                }
            });
        } else {
            setTextMessageError(getResources().getString(R.string.no_gps_enabled));
        }
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

    /*protected void loadCandidatos() {
        showDialog(getResources().getString(R.string.getting_info));

        if (rootView.findViewById(R.id.main_btn_refresh).getVisibility() != View.GONE)
            rootView.findViewById(R.id.main_btn_refresh).setVisibility(View.GONE);

        String url = null;

        if (url != null) {
            if (NetworkUtils.isNetworkConnectionAvailable(getActivity())) {
                GetCandidatesAsyncTask task = new GetCandidatesAsyncTask(url);
                task.execute();

                if (arrayAdapter != null) {
                    arrayAdapter.clear();
                    arrayAdapter.notifyDataSetChanged();
                }
            } else {
                setTextMessageError(getResources().getString(R.string.no_internet_connection));
            }
        } else {
            dismissDialog();
        }
    }*/

    private void setTextMessageError(String messageError) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

        View view = rootView.findViewById(R.id.main_btn_refresh);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSwipeContainer();

                initLocationClientListener();
                startLocationListener();

                /*if (state != null) {
                    loadCandidatos(candidateType);
                } else {
                    initLocationClientListener();
                }*/
            }
        });
        view.setVisibility(View.VISIBLE);
        ((CustomTextView) rootView.findViewById(R.id.main_tv_error_message)).setText(messageError);
    }

    protected void showSwipeContainer() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (rootView.findViewById(R.id.main_swipe_tinder).getVisibility() != View.VISIBLE)
                    rootView.findViewById(R.id.main_swipe_tinder).setVisibility(View.VISIBLE);

                if (rootView.findViewById(R.id.main_no_items).getVisibility() != View.INVISIBLE)
                    rootView.findViewById(R.id.main_no_items).setVisibility(View.INVISIBLE);

                if (rootView.findViewById(R.id.main_btn_refresh).getVisibility() != View.GONE)
                    rootView.findViewById(R.id.main_btn_refresh).setVisibility(View.GONE);
            }
        });
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

        if (auxCandidates != null && auxCandidates.size() > 0) {
            arrayAdapter = new MyArrayAdapter(getActivity(), auxCandidates);

            flingContainer.setAdapter(arrayAdapter);
            arrayAdapter.notifyDataSetChanged();

            flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
                @Override
                public void removeFirstObjectInAdapter() {
                    // this is the simplest way to delete an object from the Adapter (/AdapterView)
                    //Log.d("LIST", "removed object!");
                    auxCandidates.remove(0);
                    arrayAdapter.notifyDataSetChanged();
                }

                @Override
                public void onLeftCardExit(Object dataObject) {}

                @Override
                public void onRightCardExit(Object dataObject) {
                    CandidateInfo candidateInfo = (CandidateInfo) dataObject;
                    if (candidateInfo != null)
                        showDialogSwipe(candidateInfo);
                }

                @Override
                public void onAdapterAboutToEmpty(int itemsInAdapter) {
                    // Ask for more data here

                    if (itemsInAdapter == 0) {
                        if (rootView.findViewById(R.id.main_no_items).getVisibility() != View.VISIBLE)
                            rootView.findViewById(R.id.main_no_items).setVisibility(View.VISIBLE);

                        if (rootView.findViewById(R.id.main_swipe_tinder).getVisibility() != View.GONE)
                            rootView.findViewById(R.id.main_swipe_tinder).setVisibility(View.GONE);
                    }
                }

                @Override
                public void onScroll(float scrollProgressPercent) {
                    View view = flingContainer.getSelectedView();
                    if (view != null) {
                        view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                        view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
                    }
                }
            });

            flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
                @Override
                public void onItemClicked(int itemPosition, Object dataObject) {
                    startIntentCandidate((CandidateInfo) dataObject);
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

    private void startIntentCandidate(CandidateInfo candidate) {
        Intent intent = new Intent(getActivity(), CandidateActivity.class);
        intent.putExtra(CandidateActivity.TAG_CANDIDATE, candidate);
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
    private void showDialogSwipe(CandidateInfo candidateInfo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_tweet, null, false);

        Candidate candidate = candidateInfo.getCandidate().getCandidate();

        if (candidate != null) {
            CustomTextView tvMessage = (CustomTextView) view.findViewById(R.id.dialog_tweet_tv_message);
            CustomTextView tvSubMessage = (CustomTextView) view.findViewById(R.id.dialog_tweet_tv_submessage);

            String userName = (candidate.getTwitter() != null && !candidate.getTwitter().equals(""))
                    ? "@" + candidate.getTwitter()
                    : "#" + candidate.getNombres().replaceAll("\\s+", "")
                    + candidate.getApellidoPaterno().replaceAll("\\s+", "")
                    + candidate.getApellidoMaterno().replaceAll("\\s+", "");

            View btnTweet = view.findViewById(R.id.dialog_tweet_btn_tweet);
            btnTweet.setOnClickListener(TweetOnClickListener);

            View btnTweetInvite = view.findViewById(R.id.dialog_tweet_btn_tweet_invite);
            btnTweetInvite.setTag(String.format(Locale.getDefault(), getResources().getString(R.string.tweet_second_message_invite), userName));
            btnTweetInvite.setOnClickListener(TweetOnClickListener);

            String nombres =  candidate.getNombres() != null ? candidate.getNombres() : "";
            String apellidoPaterno = candidate.getApellidoPaterno() != null ? candidate.getApellidoPaterno() : "";
            String apellidoMaterno = candidate.getApellidoMaterno() != null ? candidate.getApellidoMaterno() : "";

            CustomTextView tvName = (CustomTextView) view.findViewById(R.id.dialog_tweet_tv_name);
            tvName.setText(String.format(Locale.getDefault(), "%s %s %s", nombres, apellidoPaterno, apellidoMaterno));

            ImageView ivProfile = (ImageView) view.findViewById(R.id.dialog_tweet_iv_profile);
            if (candidate.getTwitter() != null && !candidate.getTwitter().equals("")) {
                String twitter = candidate.getTwitter().replaceAll("\\s+", "");
                ImageLoader.getInstance().displayImage(String.format(Locale.getDefault(), HttpConnection.TWITTER_IMAGE_URL, twitter), ivProfile, options);
            } else {
                ivProfile.setImageResource(R.drawable.drawable_bgr_gray);
            }

            if (candidateInfo.getCandidate() != null) {
                boolean hasAllIndicators = true;
                List<Indicator> indicators = candidateInfo.getCandidate().getIndicators();

                if (indicators != null && indicators.size() > 0) {
                    for (Indicator indicator : indicators) {
                        if (indicator.getDocument() != null && !indicator.getDocument().equals("")) {
                            //hasAllIndicators = true;
                        } else {
                            hasAllIndicators = false;
                            break;
                        }
                    }
                }

                if (hasAllIndicators) {
                    tvMessage.setText(getResources().getString(R.string.tweet_message_good));
                    tvSubMessage.setText(getResources().getString(R.string.tweet_submessage_good));
                    btnTweet.setTag(String.format(Locale.getDefault(), getResources().getString(R.string.tweet_first_message_good), userName));
                } else {
                    tvMessage.setText(getResources().getString(R.string.tweet_message_bad));
                    tvSubMessage.setText(getResources().getString(R.string.tweet_submessage_bad));
                    btnTweet.setTag(String.format(Locale.getDefault(), getResources().getString(R.string.tweet_first_message_bad), userName));
                }
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
            showSwipeContainer();

            initLocationClientListener();
            startLocationListener();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void reverseGeocoderFromLatLng(LatLng location) {
        if (location != null) {
            if (NetworkUtils.isNetworkConnectionAvailable(getActivity())) {
                ReverseGeocoderAsyncTask task = new ReverseGeocoderAsyncTask(location.latitude, location.longitude);
                task.execute();
            } else {
                setTextMessageError(getResources().getString(R.string.no_internet_connection));
            }
        }
    }

    private class ReverseGeocoderAsyncTask extends AsyncTask<String, String, String> {
        private double latitude;
        private double longitude;

        public ReverseGeocoderAsyncTask(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected String doInBackground(String... params) {
            return HttpConnection.GET(String.format(Locale.getDefault(), HttpConnection.URL_HOST + HttpConnection.GEOCODER, latitude, longitude));
        }

        @Override
        protected void onPostExecute(String result) {
            // Dialogues.Log(TAG_CLASS, "Result: " + result, Log.ERROR);
            // Dialogues.Log(TAG_CLASS, "json_PDF: " + json_PDF, Log.ERROR);
            // Dialogues.Toast(getActivity(), "Result: " + result, Toast.LENGTH_LONG);

            if (result != null) {
                String urlCandidates = HttpConnection.URL_HOST;

                GeocoderResult geocoderResult = GsonParser.getGeocoderResultFromJSON(result);

                if (geocoderResult != null) {
                    if (geocoderResult.getCountry() != null) {
                        urlCandidates += HttpConnection.COUNTRIES + File.separator + geocoderResult.getCountry().getId();

                        if (geocoderResult.getState() != null) {
                            urlCandidates += HttpConnection.STATES + File.separator + geocoderResult.getState().getId();

                            if (geocoderResult.getCity() != null) {
                                urlCandidates += HttpConnection.CITIES + File.separator + geocoderResult.getCity().getId();
                            }
                        }
                    }

                    urlCandidates += ".json";

                    //Dialogues.Toast(getActivity(), "urlCandidates: " + urlCandidates, Toast.LENGTH_SHORT);
                    getCandidatesFromAddress(urlCandidates);
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

    protected void getCandidatesFromAddress(String url) {
        GetCandidatesAsyncTask task = new GetCandidatesAsyncTask(url);
        task.execute();
    }

    private class GetCandidatesAsyncTask extends AsyncTask<String, String, String> {
        private String url;

        public GetCandidatesAsyncTask(String url) {
            this.url = url;
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected String doInBackground(String... params) {
            return HttpConnection.GET(url);
        }

        @Override
        protected void onPostExecute(String result) {
            // Dialogues.Log(TAG_CLASS, "Result: " + result, Log.ERROR);
            // Dialogues.Log(TAG_CLASS, "json_PDF: " + json_PDF, Log.ERROR);
            // Dialogues.Toast(getActivity(), "Result: " + result, Toast.LENGTH_LONG);

            boolean hasNoCandidates = false;

            if (result != null) {
                //Dialogues.Toast(getActivity(), "Result: " + result, Toast.LENGTH_LONG);

                try {
                    Territory territory = GsonParser.getTerritoryJSON(result);

                    if (territory == null) {
                        return;
                    }

                    isFirstTime = false;
                    //PreferencesUtils.putStringPreference(getActivity().getApplication(), PreferencesUtils.JSON_PDF, json_PDF);

                    List<CandidateInfo> candidateInfoList = new ArrayList<>();

                    if (territory.getPositions() != null && territory.getPositions().size() > 0) {
                        for (Territory.Positions positions : territory.getPositions()) {

                            for (Candidates candidates : positions.getCandidates()) {
                                CandidateInfo candidateInfo = new CandidateInfo();
                                candidateInfo.setTerritoryName(positions.getTerritory());
                                candidateInfo.setPosition(positions.getTitle());
                                candidateInfo.setCandidate(candidates);

                                candidateInfoList.add(candidateInfo);
                            }
                        }
                    }

                    if (candidateInfoList.size() > 0) {
                        auxCandidates = candidateInfoList;

                        //Dialogues.Toast(getActivity(), "Size auxCandidates: " + auxCandidates.size(), Toast.LENGTH_SHORT);

                        initUI();
                    } else {
                        hasNoCandidates = true;
                    }
                } catch (Exception e) {
                    hasNoCandidates = true;

                    e.printStackTrace();
                }
            } else {
                hasNoCandidates = true;
                //setTextMessageError(getResources().getString(R.string.error_message_default));
            }

            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }

            if (hasNoCandidates) {
                Dialogues.Toast(getActivity(), "No hay candidatos en tu ubicación", Toast.LENGTH_SHORT);
            }

            if (!this.isCancelled())
                this.cancel(true);
        }
    }
}
