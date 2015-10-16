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
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.text.util.Linkify;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import mx.com.factico.diputinder.CandidateActivity;
import mx.com.factico.diputinder.R;
import mx.com.factico.diputinder.WebViewActivity;
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
import mx.com.factico.diputinder.utils.DateUtils;
import mx.com.factico.diputinder.utils.LinkUtils;
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

    private boolean isRefreshing = false;

    private ReverseGeocoderTask reverseGeocoderTask = null;
    private GetCandidatesTask candidatesTask = null;
    private GetMessagesFromCountry messagesTask = null;

    private Messages messages = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        CacheUtils.unbindDrawables(rootView);
        rootView = null;
        Runtime.getRuntime().gc();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // you are visible to user now - so set whatever you need
            // initResources();
        }
        else {
            // you are no longer visible to the user so cleanup whatever you need
            // cleanupResources();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
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


            String messagesJson = PreferencesManager.getStringPreference(getActivity().getApplication(), PreferencesManager.MESSAGES);
            try {
                String oldDate = PreferencesManager.getStringPreference(getActivity().getApplication(), PreferencesManager.DATE_MESSAGES);
                String currentDate = DateUtils.getCurrentDateTime();
                long difference = DateUtils.getDifferencesInHoursBetweenDates(oldDate, currentDate);
                if (difference < 3) { // 3 horas
                    messages = GsonParser.getMessagesFromJSON(messagesJson);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            String candidatesJson = PreferencesManager.getStringPreference(getActivity().getApplication(), PreferencesManager.CANDIDATES);
            try {
                String oldDate = PreferencesManager.getStringPreference(getActivity().getApplication(), PreferencesManager.DATE_CANDIDATES);
                String currentDate = DateUtils.getCurrentDateTime();
                long difference = DateUtils.getDifferencesInHoursBetweenDates(oldDate, currentDate);
                if (difference < 3) { // 3 horas
                    auxCandidates = GsonParser.getListCandidatesInfoFromJSON(candidatesJson);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            createView();
        }

        return rootView;
    }

    private void createView() {
        if (auxCandidates != null && auxCandidates.size() > 0 && !isRefreshing) {
            initUI();
        } else {
            initLocationClientListener();

            if (NetworkUtils.isNetworkConnectionAvailable(getActivity())) {
                initUI();
            } else {
                setTextMessageError(getResources().getString(R.string.no_internet_connection));
            }
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
                    dismissDialog();

                    //Dialogues.Toast(getActivity(), "Latitude: " + userLocation.latitude + ", Longitude: " + userLocation.longitude, Toast.LENGTH_SHORT);

                    showDialog(getResources().getString(R.string.getting_info));

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

    private void setTextMessageError(String messageError) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

        View view = rootView.findViewById(R.id.main_btn_refresh);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSwipeContainer();
                isRefreshing = true;
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
        if (arrayAdapter != null) {
            arrayAdapter.clear();
            arrayAdapter.notifyDataSetChanged();
        }

        if (flingContainer != null) {
            flingContainer.requestLayout();
            flingContainer = null;
            arrayAdapter = null;
        }

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
                    if (itemsInAdapter == 0 && !isRefreshing) {
                        CustomTextView noMoreItems = (CustomTextView) rootView.findViewById(R.id.main_no_items);
                        noMoreItems.setText(messages != null && messages.getNoCandidates() != null && !messages.getNoCandidates().equals("") ?
                                messages.getNoCandidates() :
                                getString(R.string.no_more_candidates));
                        LinkUtils.fixTextView(noMoreItems);
                        // noMoreItems.setAutoLinkMask(Linkify.ALL);
                        /*LinkUtils.autoLink(noMoreItems, new LinkUtils.OnClickListener() {
                            @Override
                            public void onLinkClicked(final String link) {
                                // Log.i("SensibleUrlSpan", "リンククリック:" + link);
                                // Dialogues.Toast(getActivity().getBaseContext(), "link", Toast.LENGTH_LONG);
                            }

                            @Override
                            public void onClicked() {
                                // Log.i("SensibleUrlSpan", "ビュークリック");
                                // Dialogues.Toast(getActivity().getBaseContext(), "ビュークリック", Toast.LENGTH_LONG);
                            }
                        });*/

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

    private void startIntentCandidate(CandidateInfo candidate) {
        /*Intent intent = new Intent(getActivity(), CandidateActivity.class);
        intent.putExtra(CandidateActivity.TAG_CANDIDATE, candidate);
        startActivity(intent);*/

        // Construct an Intent as normal
        Intent intent = new Intent(getActivity(), CandidateActivity.class);
        intent.putExtra(CandidateActivity.TAG_CANDIDATE, candidate);

        // BEGIN_INCLUDE(start_activity)
        /**
         * Now create an {@link android.app.ActivityOptions} instance using the
         * {@link ActivityOptionsCompat#makeSceneTransitionAnimation(Activity, Pair[])} factory
         * method.
         */
        ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                getActivity(),
                // Now we provide a list of Pair items which contain the view we can transitioning
                // from, and the name of the view it is transitioning to, in the launched activity
                new Pair<View, String>(flingContainer.getSelectedView().findViewById(R.id.item_candidate_iv_profile),
                        CandidateActivity.VIEW_NAME_HEADER_IMAGE),
                new Pair<View, String>(flingContainer.getSelectedView().findViewById(R.id.item_candidate_tv_name),
                        CandidateActivity.VIEW_NAME_HEADER_TITLE));

        // Now we can start the Activity, providing the activity options as a bundle
        ActivityCompat.startActivity(getActivity(), intent, activityOptions.toBundle());
        // END_INCLUDE(start_activity)
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
                    ? candidate.getTwitter().startsWith("@") ? candidate.getTwitter() : "@" + candidate.getTwitter()
                    : "#" + candidate.getNombres().replaceAll("\\s+", "")
                    + candidate.getApellidoPaterno().replaceAll("\\s+", "")
                    + candidate.getApellidoMaterno().replaceAll("\\s+", "");

            View btnTweet = view.findViewById(R.id.dialog_tweet_btn_tweet);
            btnTweet.setOnClickListener(TweetOnClickListener);

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

    /**
     * Shows the progress UI and hides the login form.
     */
    /*@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }*/

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
            if (!isRefreshing) {
                showSwipeContainer();

                isRefreshing = true;
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
        protected void onPreExecute() {}

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
        protected void onPreExecute() {}

        @Override
        protected String doInBackground(String... params) {
            return HttpConnection.GET(url);
        }

        @Override
        protected void onPostExecute(String result) {
            candidatesTask = null;
            //showProgress(false);
            dismissDialog();

            // Dialogues.Log(TAG_CLASS, "Result: " + result, Log.ERROR);
            // Dialogues.Toast(getActivity(), "Result: " + result, Toast.LENGTH_LONG);

            boolean hasNoCandidates = false;

            if (result != null) {
                //Dialogues.Toast(getActivity(), "Result: " + result, Toast.LENGTH_LONG);

                try {
                    Territory territory = GsonParser.getTerritoryJSON(result);

                    if (territory == null) {
                        return;
                    }

                    //PreferencesManager.putStringPreference(getActivity().getApplication(), PreferencesManager.JSON_PDF, json_PDF);

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

                        String jsonCandidates = GsonParser.createJsonFromObject(auxCandidates);
                        PreferencesManager.putStringPreference(getActivity().getApplication(), PreferencesManager.CANDIDATES, jsonCandidates);

                        String currentDate = DateUtils.getCurrentDateTime();
                        PreferencesManager.putStringPreference(getActivity().getApplication(), PreferencesManager.DATE_CANDIDATES, currentDate);

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

            if (hasNoCandidates) {
                Dialogues.Toast(getActivity(), "No hay candidatos en tu ubicación", Toast.LENGTH_SHORT);
            }
        }

        @Override
        protected void onCancelled() {
            candidatesTask = null;
            //showProgress(false);
            dismissDialog();
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
        protected void onPreExecute() {}

        @Override
        protected String doInBackground(String... params) {
            return HttpConnection.GET(url);
        }

        @Override
        protected void onPostExecute(String result) {
            messagesTask = null;
            //showProgress(false);
            dismissDialog();

            // Dialogues.Log(TAG_CLASS, "Result: " + result, Log.ERROR);
            // Dialogues.Toast(getActivity(), "Result: " + result, Toast.LENGTH_LONG);

            if (result != null) {
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
            } else {

            }
        }

        @Override
        protected void onCancelled() {
            messagesTask = null;
            //showProgress(false);
            dismissDialog();
        }
    }
}
