package mx.com.factico.diputinder.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import mx.com.factico.diputinder.CandidateActivity;
import mx.com.factico.diputinder.R;
import mx.com.factico.diputinder.adapters.MyArrayAdapter;
import mx.com.factico.diputinder.beans.Candidate;
import mx.com.factico.diputinder.beans.CandidateInfo;
import mx.com.factico.diputinder.beans.Candidates;
import mx.com.factico.diputinder.beans.GeocoderResult;
import mx.com.factico.diputinder.beans.Indicator;
import mx.com.factico.diputinder.beans.Messages;
import mx.com.factico.diputinder.beans.Territory;
import mx.com.factico.diputinder.dialogues.Dialogues;
import mx.com.factico.diputinder.httpconnection.HttpConnection;
import mx.com.factico.diputinder.httpconnection.NetworkUtils;
import mx.com.factico.diputinder.location.LocationClientListener;
import mx.com.factico.diputinder.location.LocationUtils;
import mx.com.factico.diputinder.parser.GsonParser;
import mx.com.factico.diputinder.preferences.PreferencesManager;
import mx.com.factico.diputinder.utils.CacheUtils;
import mx.com.factico.diputinder.utils.Constants;
import mx.com.factico.diputinder.utils.DateUtils;
import mx.com.factico.diputinder.utils.ImageUtils;
import mx.com.factico.diputinder.utils.LinkUtils;
import mx.com.factico.diputinder.views.CustomTextView;

/**
 * Created by zace3d on 26/05/15.
 */

public class MainFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = MainFragment.class.getName();

    private List<CandidateInfo> auxCandidates = new ArrayList<>();
    private MyArrayAdapter mAdapter;

    private SwipeFlingAdapterView mSwipeFlingView;
    private View mSwipeLeftButton;
    private View mSwipeRightButton;

    private LocationClientListener clientListener;
    private LatLng userLocation;
    private DisplayImageOptions options;

    private View rootView;

    private boolean isRefreshing = false;

    private ReverseGeocoderTask reverseGeocoderTask = null;
    private GetCandidatesTask candidatesTask = null;
    private GetMessagesFromCountry messagesTask = null;

    private Messages messages = null;

    public static Fragment newInstance() {
        Bundle args = new Bundle();
        Fragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        options = ImageUtils.buildDisplayImageOptions();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        CacheUtils.unbindDrawables(rootView);
        rootView = null;

        if (mAdapter != null) mAdapter.clearAnimateFirstDisplayListener();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSwipeFlingView = (SwipeFlingAdapterView) view.findViewById(R.id.main_swipe_tinder);
        mSwipeLeftButton = view.findViewById(R.id.main_btn_swipe_left);
        mSwipeRightButton = view.findViewById(R.id.main_btn_swipe_right);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //retrieveMessagesCache();
        //retrieveCandidatesCache();
        checkLocationServices();
    }

    private void retrieveMessagesCache() {
        Activity activity = getActivity();
        if (activity == null)
            return;

        String messagesJson = PreferencesManager.getStringPreference(activity.getApplication(), PreferencesManager.MESSAGES);
        try {
            String oldDate = PreferencesManager.getStringPreference(activity.getApplication(), PreferencesManager.DATE_MESSAGES);
            String currentDate = DateUtils.getCurrentDateTime();
            long difference = DateUtils.getDifferencesInHoursBetweenDates(oldDate, currentDate);
            if (difference < 3) { // 3 horas
                messages = GsonParser.getMessagesFromJSON(messagesJson);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void retrieveCandidatesCache() {
        Activity activity = getActivity();
        if (activity == null)
            return;

        String candidatesJson = PreferencesManager.getStringPreference(activity.getApplication(), PreferencesManager.CANDIDATES);
        try {
            String oldDate = PreferencesManager.getStringPreference(activity.getApplication(), PreferencesManager.DATE_CANDIDATES);
            String currentDate = DateUtils.getCurrentDateTime();
            long difference = DateUtils.getDifferencesInHoursBetweenDates(oldDate, currentDate);
            if (difference < 3) { // 3 horas
                auxCandidates = GsonParser.getListCandidatesInfoFromJSON(candidatesJson);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkLocationServices() {
        if (auxCandidates != null && auxCandidates.size() > 0 && !isRefreshing) {
            initViews();
        } else {
            initLocationClientListener();

            if (NetworkUtils.isNetworkConnectionAvailable(getActivity())) {
                initViews();
            } else {
                setTextMessageError(getResources().getString(R.string.no_internet_connection));
            }
        }
    }

    protected void initLocationClientListener() {
        if (LocationUtils.isGpsOrNetworkProviderEnabled(getActivity())) {
            isRefreshing = true;
            showDialog(getResources().getString(R.string.getting_location));

            clientListener = new LocationClientListener(getActivity());
            clientListener.setOnLocationClientListener(new LocationClientListener.OnLocationClientListener() {
                @Override
                public void onLocationChanged(Location location) {
                    updateUserLocation(location);
                }
            });
        }
    }

    private void updateUserLocation(Location location) {
        userLocation = LocationUtils.getLatLngFromLocation(location);
        dismissDialog();

        showDialog(getResources().getString(R.string.getting_info));

        clientListener.stopLocationUpdates();

        reverseGeocoderFromLatLng(userLocation);
    }

    public void startLocationListener() {
        if (clientListener != null)
            clientListener.connect();
    }

    public void stopLocationListener() {
        if (clientListener != null)
            clientListener.disconnect();
    }

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
            }
        });
        view.setVisibility(View.VISIBLE);
        ((CustomTextView) rootView.findViewById(R.id.main_tv_error_message)).setText(messageError);
    }

    protected void showSwipeContainer() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mSwipeFlingView.getVisibility() != View.VISIBLE)
                    mSwipeFlingView.setVisibility(View.VISIBLE);

                if (mSwipeFlingView.getVisibility() != View.INVISIBLE)
                    mSwipeFlingView.setVisibility(View.INVISIBLE);

                if (mSwipeFlingView.getVisibility() != View.GONE)
                    mSwipeFlingView.setVisibility(View.GONE);
            }
        });
    }

    private void clearAdapter() {
        if (mAdapter != null) {
            mAdapter.clear();
            mAdapter.notifyDataSetChanged();
        }

        if (mSwipeFlingView != null) {
            mSwipeFlingView.requestLayout();
            mAdapter = null;
        }
    }

    private void initViews() {
        clearAdapter();

        mSwipeLeftButton.setOnClickListener(this);
        mSwipeRightButton.setOnClickListener(this);

        if (auxCandidates != null && auxCandidates.size() > 0) {
            mAdapter = new MyArrayAdapter(getActivity(), auxCandidates);

            mSwipeFlingView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();

            mSwipeFlingView.setFlingListener(new MyOnFlingListener());

            mSwipeFlingView.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
                @Override
                public void onItemClicked(int itemPosition, Object dataObject) {
                    CandidateActivity.startActivity(getActivity(), (CandidateInfo) dataObject);
                }
            });
        }

        isRefreshing = false;
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

    private class MyOnFlingListener implements SwipeFlingAdapterView.onFlingListener {
        @Override
        public void removeFirstObjectInAdapter() {
            // this is the simplest way to delete an object from the Adapter (/AdapterView)
            //Log.d("LIST", "removed object!");
            auxCandidates.remove(0);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onLeftCardExit(Object dataObject) {
        }

        @Override
        public void onRightCardExit(Object dataObject) {
            CandidateInfo candidateInfo = (CandidateInfo) dataObject;
            if (candidateInfo != null)
                showDialogSwipe(candidateInfo);
        }

        @Override
        public void onAdapterAboutToEmpty(int itemsInAdapter) {
            // Ask for more data here
            if (itemsInAdapter == 0 && !isRefreshing) {
                CustomTextView noMoreItems = (CustomTextView) rootView.findViewById(R.id.main_no_items);
                noMoreItems.setText(messages != null && messages.getNoCandidates() != null && !messages.getNoCandidates().equals("") ?
                        messages.getNoCandidates() :
                        getString(R.string.no_more_candidates));
                LinkUtils.fixTextView(noMoreItems);

                if (rootView.findViewById(R.id.main_no_items).getVisibility() != View.VISIBLE)
                    rootView.findViewById(R.id.main_no_items).setVisibility(View.VISIBLE);

                if (mSwipeFlingView.getVisibility() != View.GONE)
                    mSwipeFlingView.setVisibility(View.GONE);
            }
        }

        @Override
        public void onScroll(float scrollProgressPercent) {
            View view = mSwipeFlingView.getSelectedView();
            if (view != null) {
                view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
            }
        }
    }

    private void swipeLeft() {
        /**
         * Trigger the left event manually.
         */
        if (mSwipeFlingView != null && mSwipeFlingView.getChildCount() > 0)
            mSwipeFlingView.getTopCardListener().selectLeft();
    }

    private void swipeRight() {
        /**
         * Trigger the right event manually.
         */
        if (mSwipeFlingView != null && mSwipeFlingView.getChildCount() > 0)
            mSwipeFlingView.getTopCardListener().selectRight();
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
                    ? candidate.getTwitter().startsWith("@") ? candidate.getTwitter() : "@" + candidate.getTwitter()
                    : "#" + candidate.getNombres().replaceAll("\\s+", "")
                    + candidate.getApellidoPaterno().replaceAll("\\s+", "")
                    + candidate.getApellidoMaterno().replaceAll("\\s+", "");

            View btnTweet = view.findViewById(R.id.dialog_tweet_btn_tweet);
            btnTweet.setOnClickListener(TweetOnClickListener);

            String nombres = candidate.getNombres() != null ? candidate.getNombres() : "";
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
                boolean hasAllIndicators = false;
                List<Indicator> indicators = candidateInfo.getCandidate().getIndicators();

                if (indicators != null && indicators.size() > 0) {
                    for (Indicator indicator : indicators) {
                        if (indicator.getDocument() != null && !indicator.getDocument().equals("")) {
                            hasAllIndicators = true;
                        } else {
                            hasAllIndicators = false;
                            break;
                        }
                    }
                }

                if (hasAllIndicators) {
                    String explanationChecked = (messages != null && messages.getExplanationChecked() != null) ?
                            messages.getExplanationChecked() : getString(R.string.tweet_message_good);
                    String congratulation = (messages != null && messages.getCongratulation() != null) ?
                            messages.getCongratulation() : getString(R.string.tweet_submessage_good);
                    String tweetChecked = (messages != null && messages.getTweetChecked() != null) ?
                            ".%s " + messages.getTweetChecked() : getString(R.string.tweet_first_message_good);

                    tvMessage.setText(explanationChecked);
                    tvSubMessage.setText(congratulation);
                    btnTweet.setTag(String.format(Locale.getDefault(), tweetChecked, userName));
                } else {
                    String explanationMissing = (messages != null && messages.getExplanationMissing() != null) ?
                            messages.getExplanationMissing() : getString(R.string.tweet_message_bad);
                    String demand = (messages != null && messages.getDemand() != null) ?
                            messages.getDemand() : getString(R.string.tweet_submessage_bad);
                    String tweetMissing = (messages != null && messages.getTweetMissing() != null) ?
                            ".%s " + messages.getTweetMissing() : getString(R.string.tweet_first_message_bad);

                    tvMessage.setText(explanationMissing);
                    tvSubMessage.setText(demand);
                    btnTweet.setTag(String.format(Locale.getDefault(), tweetMissing, userName));
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
                isRefreshing = false;

                if (reverseGeocoderTask != null) reverseGeocoderTask.cancel(true);
                if (candidatesTask != null) candidatesTask.cancel(true);
                if (messagesTask != null) messagesTask.cancel(true);
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
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            if (!isRefreshing) {
                showSwipeContainer();
                initLocationClientListener();
                startLocationListener();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void reverseGeocoderFromLatLng(LatLng location) {
        if (location != null) {
            if (NetworkUtils.isNetworkConnectionAvailable(getActivity())) {
                reverseGeocoderTask = new ReverseGeocoderTask(location.latitude, location.longitude);
                reverseGeocoderTask.execute((String) null);
            } else {
                setTextMessageError(getResources().getString(R.string.no_internet_connection));
            }
        }
    }

    private class ReverseGeocoderTask extends AsyncTask<String, String, String> {
        private double latitude;
        private double longitude;

        ReverseGeocoderTask(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {
            return HttpConnection.GET(String.format(Locale.getDefault(), HttpConnection.URL_HOST + HttpConnection.GEOCODER, latitude, longitude));
        }

        @Override
        protected void onPostExecute(String result) {
            reverseGeocoderTask = null;
            //showProgress(false);
            dismissDialog();

            // Dialogues.Log(TAG_CLASS, "Result: " + result, Log.ERROR);
            // Dialogues.Toast(getActivity(), "Result: " + result, Toast.LENGTH_LONG);

            boolean hasError = false;
            if (result != null) {
                try {
                    String urlCandidates = HttpConnection.URL_HOST;
                    String urlMessages = HttpConnection.URL_HOST;

                    GeocoderResult geocoderResult = GsonParser.getGeocoderResultFromJSON(result);

                    if (geocoderResult != null) {
                        if (geocoderResult.getCountry() != null) {
                            urlCandidates += HttpConnection.COUNTRIES + File.separator + geocoderResult.getCountry().getId();
                            urlMessages += HttpConnection.COUNTRIES + File.separator + geocoderResult.getCountry().getId() + HttpConnection.MESSAGES;

                            if (geocoderResult.getState() != null) {
                                urlCandidates += HttpConnection.STATES + File.separator + geocoderResult.getState().getId();

                                if (geocoderResult.getCity() != null) {
                                    urlCandidates += HttpConnection.CITIES + File.separator + geocoderResult.getCity().getId();
                                }
                            }
                        }

                        urlCandidates += ".json";
                        urlMessages += ".json";

                        getCandidatesFromAddress(urlCandidates);

                        if (messages == null)
                            getMessagesFromCountry(urlMessages);
                    }
                } catch (Exception e) {
                    hasError = true;
                }
            } else {
                hasError = true;
            }

            if (hasError)
                setTextMessageError(getResources().getString(R.string.error_message_default));
        }

        @Override
        protected void onCancelled() {
            reverseGeocoderTask = null;
            //showProgress(false);
            dismissDialog();
        }
    }

    protected void getCandidatesFromAddress(String url) {
        candidatesTask = new GetCandidatesTask(url);
        candidatesTask.execute((String) null);
    }

    private class GetCandidatesTask extends AsyncTask<String, String, String> {
        private String url;

        GetCandidatesTask(String url) {
            this.url = url;
        }

        @Override
        protected String doInBackground(String... params) {
            return HttpConnection.GET(url);
        }

        @Override
        protected void onPostExecute(String result) {
            candidatesTask = null;
            dismissDialog();

            handleCandidatesResult(result);
        }

        @Override
        protected void onCancelled() {
            candidatesTask = null;
            //showProgress(false);
            dismissDialog();
        }
    }

    private void handleCandidatesResult(String result) {
        boolean hasNoCandidates = false;

        Dialogues.Log(TAG, "CANDIDATE RESULT: " + result, Log.ERROR);

        if (result != null) {
            try {
                Territory territory = GsonParser.getTerritoryJSON(result);

                if (territory == null) {
                    return;
                }

                Dialogues.Log(TAG, "Territory: " + territory.getName(), Log.ERROR);

                List<CandidateInfo> candidateInfoList = new ArrayList<>();

                if (territory.getPositions() != null && territory.getPositions().size() > 0) {
                    for (Territory.Positions positions : territory.getPositions()) {

                        for (Candidates candidates : positions.getCandidates()) {
                            CandidateInfo candidateInfo = new CandidateInfo();
                            candidateInfo.setTerritoryName(positions.getTerritory());
                            candidateInfo.setPosition(positions.getTitle());
                            candidateInfo.setCandidate(candidates);

                            candidateInfoList.add(candidateInfo);

                            Dialogues.Log(TAG, "Candidate Name: " + candidates.getCandidate().getNombres(), Log.ERROR);
                        }
                    }
                }

                Dialogues.Log(TAG, "Candidates count: " + candidateInfoList.size(), Log.ERROR);

                if (candidateInfoList.size() > 0) {
                    auxCandidates = candidateInfoList;

                    String jsonCandidates = GsonParser.createJsonFromObject(auxCandidates);
                    PreferencesManager.putStringPreference(getActivity().getApplication(), PreferencesManager.CANDIDATES, jsonCandidates);

                    String currentDate = DateUtils.getCurrentDateTime();
                    PreferencesManager.putStringPreference(getActivity().getApplication(), PreferencesManager.DATE_CANDIDATES, currentDate);

                    Dialogues.Log(TAG, "Before initViews", Log.ERROR);

                    initViews();

                    Dialogues.Log(TAG, "After initViews", Log.ERROR);
                } else {
                    hasNoCandidates = true;
                }
            } catch (Exception e) {
                hasNoCandidates = true;

                e.printStackTrace();
            }
        } else {
            hasNoCandidates = true;
        }

        if (hasNoCandidates) {
            Dialogues.Toast(getActivity(), "No hay candidatos en tu ubicación", Toast.LENGTH_SHORT);
        }
    }

    // Get messages to show depending on Location
    protected void getMessagesFromCountry(String url) {
        messagesTask = new GetMessagesFromCountry(url);
        messagesTask.execute((String) null);
    }

    private class GetMessagesFromCountry extends AsyncTask<String, String, String> {
        private String url;

        GetMessagesFromCountry(String url) {
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {
            return HttpConnection.GET(url);
        }

        @Override
        protected void onPostExecute(String result) {
            messagesTask = null;
            //showProgress(false);
            dismissDialog();

            handleMessagesResult(result);
        }

        @Override
        protected void onCancelled() {
            messagesTask = null;
            //showProgress(false);
            dismissDialog();
        }
    }

    private void handleMessagesResult(String result) {
        if (!TextUtils.isEmpty(result))
            return;

        try {
            List<Messages> list = GsonParser.getListMessagesFromJSON(result);

            if (list == null || list.size() == 0) {
                return;
            }

            messages = list.get(0);
            String messageJson = GsonParser.createJsonFromObject(messages);
            PreferencesManager.putStringPreference(getActivity().getApplication(), PreferencesManager.MESSAGES, messageJson);

            String currentDate = DateUtils.getCurrentDateTime();
            PreferencesManager.putStringPreference(getActivity().getApplication(), PreferencesManager.DATE_MESSAGES, currentDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void startLocationService() {
        if (!hasPermissionsGranted(Constants.LOCATION_PERMISSIONS)) {
            return;
        }

        if (!isRefreshing) {
            showSwipeContainer();
            initLocationClientListener();
            startLocationListener();
        }
        //Intent locationService = new Intent(this, LocationService.class);
        //startService(locationService);
    }

    private boolean hasPermissionsGranted(String[] permissions) {
        Activity activity = getActivity();
        if (activity == null)
            return false;

        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(activity, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
