package mx.com.factico.diputinder.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import mx.com.factico.diputinder.CandidateActivity;
import mx.com.factico.diputinder.R;
import mx.com.factico.diputinder.adapters.MyArrayAdapter;
import mx.com.factico.diputinder.dialogues.Dialogues;
import mx.com.factico.diputinder.httpconnection.HttpConnection;
import mx.com.factico.diputinder.httpconnection.NetworkUtils;
import mx.com.factico.diputinder.location.LocationClientListener;
import mx.com.factico.diputinder.location.LocationUtils;
import mx.com.factico.diputinder.models.Candidate;
import mx.com.factico.diputinder.models.CandidateInfo;
import mx.com.factico.diputinder.models.GeocoderResult;
import mx.com.factico.diputinder.models.HasTerritory;
import mx.com.factico.diputinder.models.Messages;
import mx.com.factico.diputinder.parser.GsonParser;
import mx.com.factico.diputinder.preferences.PreferencesManager;
import mx.com.factico.diputinder.utils.CacheUtils;
import mx.com.factico.diputinder.utils.DateUtils;
import mx.com.factico.diputinder.utils.LinkUtils;
import mx.com.factico.diputinder.views.CustomTextView;

/**
 * Created by zace3d on 26/05/15.
 */

public class MainFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = MainFragment.class.getName();

    private static final String TWITTER_DIALOG_TAG = "twitter_dialog_tag";

    private List<CandidateInfo> auxCandidates = new ArrayList<>();
    private MyArrayAdapter mAdapter;

    private SwipeFlingAdapterView mSwipeFlingView;
    private View mSwipeLeftButton;
    private View mSwipeRightButton;
    private TextView mNoItems;
    private View mSwipeContainer;
    private View mErrorMessage;

    private LocationClientListener clientListener;
    private LatLng userLocation;

    private View rootView;

    private boolean isRefreshing = false;

    private ReverseGeocoderTask reverseGeocoderTask = null;
    private GetCandidatesTask candidatesTask = null;
    private GetMessagesFromCountry messagesTask = null;

    private Messages messages = null;
    private boolean isFirstTime = true;

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

        cancelTasks();
    }

    private void cancelTasks() {
        if (reverseGeocoderTask != null) {
            reverseGeocoderTask.cancel(true);
            reverseGeocoderTask = null;
        }

        if (candidatesTask != null) {
            candidatesTask.cancel(true);
            candidatesTask = null;
        }

        if (messagesTask != null) {
            messagesTask.cancel(true);
            messagesTask = null;
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSwipeFlingView = (SwipeFlingAdapterView) view.findViewById(R.id.main_swipe_tinder);
        mSwipeLeftButton = view.findViewById(R.id.main_btn_swipe_left);
        mSwipeRightButton = view.findViewById(R.id.main_btn_swipe_right);
        mNoItems = (TextView) view.findViewById(R.id.main_no_items);
        mSwipeContainer = view.findViewById(R.id.main_swipe_container);
        mErrorMessage = view.findViewById(R.id.main_btn_refresh);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        retrieveMessagesCache();
        retrieveCandidatesCache();
        initViews();

        if (auxCandidates != null && auxCandidates.size() > 0 && !isRefreshing) {
            mAdapter.notifyDataSetChanged();
        } else {
            initLocationClientListener();
        }
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
                Messages messages = GsonParser.getMessagesFromJSON(messagesJson);
                if (messages != null && !TextUtils.isEmpty(messages.getCongratulation()))
                    this.messages = messages;
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
                List<CandidateInfo> candidates = GsonParser.getListCandidatesInfoFromJSON(candidatesJson);
                if (candidates != null && candidates.size() > 0) {
                    this.auxCandidates = candidates;

                    isFirstTime = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initLocationClientListener() {
        if (LocationUtils.isGpsOrNetworkProviderEnabled(getActivity())) {
            cancelTasks();

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
        mErrorMessage.setVisibility(View.VISIBLE);
        mSwipeContainer.setVisibility(View.GONE);
        mNoItems.setVisibility(View.GONE);
        ((CustomTextView) rootView.findViewById(R.id.main_tv_error_message)).setText(messageError);
    }

    private void clearAdapter() {
        if (mAdapter != null) {
            mAdapter.clear();
            mAdapter.notifyDataSetChanged();
        }
    }

    private void initViews() {
        mSwipeLeftButton.setOnClickListener(this);
        mSwipeRightButton.setOnClickListener(this);

        mAdapter = new MyArrayAdapter(getActivity(), auxCandidates);

        mSwipeFlingView.setAdapter(mAdapter);
        mSwipeFlingView.setFlingListener(new MyOnFlingListener());
        mSwipeFlingView.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                CandidateActivity.startActivity(getActivity(), (CandidateInfo) dataObject);
            }
        });
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
            if (auxCandidates == null || auxCandidates.size() == 0)
                return;

            auxCandidates.remove(0);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onLeftCardExit(Object dataObject) {
        }

        @Override
        public void onRightCardExit(Object dataObject) {
            CandidateInfo candidateInfo = (CandidateInfo) dataObject;
            showTwitterDialog(candidateInfo, messages);
        }

        @Override
        public void onAdapterAboutToEmpty(int itemsInAdapter) {
            if (isFirstTime)
                return;

            // Ask for more data here
            if (itemsInAdapter == 0 && !isRefreshing) {
                mNoItems.setText(messages != null && messages.getNoCandidates() != null && !messages.getNoCandidates().equals("") ?
                        messages.getNoCandidates() :
                        getString(R.string.no_more_candidates));
                LinkUtils.fixTextView(mNoItems);

                mNoItems.setVisibility(View.VISIBLE);
                mSwipeContainer.setVisibility(View.GONE);
                mErrorMessage.setVisibility(View.GONE);
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

    private void showTwitterDialog(CandidateInfo candidateInfo, Messages messages) {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        Fragment prev = getChildFragmentManager().findFragmentByTag(TWITTER_DIALOG_TAG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment dialogFragment = TwitterFragmentDialog.newInstance(candidateInfo, messages);
        dialogFragment.show(ft, TWITTER_DIALOG_TAG);
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
                cancelTasks();

                isRefreshing = false;
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
                mSwipeContainer.setVisibility(View.VISIBLE);
                mErrorMessage.setVisibility(View.GONE);
                mNoItems.setVisibility(View.GONE);
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
                isRefreshing = false;

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
            String url = String.format(Locale.getDefault(), HttpConnection.API_GEOCODER, latitude, longitude);
            return HttpConnection.GET(url);
        }

        @Override
        protected void onPostExecute(String result) {
            dismissDialog();

            handleReverseGeocoderResult(result);
        }
    }

    private void handleReverseGeocoderResult(String result) {
        boolean hasError = false;

        //Dialogues.Log(TAG, "REVERSE API_GEOCODER RESULT: " + result, Log.ERROR);

        if (result != null) {
            try {
                JSONObject root = new JSONObject(result);
                if (root.has("data")) {
                    JSONObject obj = root.getJSONObject("data");
                    String geocoder = obj.optJSONObject("geocoder").toString();

                    String urlCandidates = "";
                    String urlMessages = "";

                    GeocoderResult geocoderResult = GsonParser.getGeocoderResultFromJSON(geocoder);

                    if (geocoderResult != null) {
                        if (geocoderResult.getCountry() != null) {
                            urlCandidates += HttpConnection.API_COUNTRIES + File.separator +
                                    geocoderResult.getCountry().getId();

                            urlMessages += HttpConnection.API_COUNTRIES + File.separator +
                                    geocoderResult.getCountry().getId() + File.separator + HttpConnection.MESSAGES;

                            if (geocoderResult.getState() != null) {
                                urlCandidates += HttpConnection.STATES + File.separator +
                                        geocoderResult.getState().getId();

                                if (geocoderResult.getCity() != null) {
                                    urlCandidates += HttpConnection.CITIES + File.separator +
                                            geocoderResult.getCity().getId();
                                }
                            }

                            urlCandidates += File.separator + HttpConnection.CANDIDATES;
                        }

                        getCandidatesFromAddress(urlCandidates);

                        if (messages == null)
                            getMessagesFromCountry(urlMessages);
                    } else {
                        hasError = true;
                    }
                } else {
                    hasError = true;
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

    private void getCandidatesFromAddress(String url) {
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
            dismissDialog();

            handleCandidatesResult(result);
        }
    }

    private void handleCandidatesResult(String result) {
        boolean hasNoCandidates = false;

        //Dialogues.Log(TAG, "CANDIDATES RESULT: " + result, Log.ERROR);

        if (result != null) {
            try {
                JSONObject root = new JSONObject(result);
                if (root.has("data")) {
                    JSONObject obj = root.getJSONObject("data");

                    JSONObject contentObj = obj.optJSONObject("content");

                    String has_territories = contentObj.optJSONArray("has_territories").toString();

                    List<HasTerritory> hasTerritories = GsonParser.getListHasTerritoriesFromJSON(has_territories);

                    List<CandidateInfo> candidateInfoList = new ArrayList<>();

                    for (HasTerritory hasTerritory : hasTerritories) {

                        if (hasTerritory.getCandidates() != null) {
                            for (Candidate candidate : hasTerritory.getCandidates()) {
                                CandidateInfo candidateInfo = new CandidateInfo();
                                if (hasTerritory.getTerritory() != null)
                                    candidateInfo.setTerritoryName(hasTerritory.getTerritory().getName());
                                if (hasTerritory.getPosition() != null)
                                    candidateInfo.setPosition(hasTerritory.getPosition().getTitle());
                                candidateInfo.setCandidate(candidate);

                                candidateInfoList.add(candidateInfo);
                            }
                        }
                    }

                    if (candidateInfoList.size() > 0) {
                        if (auxCandidates != null) {
                            auxCandidates.clear();
                            mAdapter.clear();
                            mAdapter.notifyDataSetChanged();

                            auxCandidates.addAll(candidateInfoList);
                            mAdapter.notifyDataSetChanged();

                            isRefreshing = false;

                            String jsonCandidates = GsonParser.createJsonFromObject(auxCandidates);
                            PreferencesManager.putStringPreference(getActivity().getApplication(), PreferencesManager.CANDIDATES, jsonCandidates);

                            String currentDate = DateUtils.getCurrentDateTime();
                            PreferencesManager.putStringPreference(getActivity().getApplication(), PreferencesManager.DATE_CANDIDATES, currentDate);

                            mSwipeContainer.setVisibility(View.VISIBLE);
                            mErrorMessage.setVisibility(View.GONE);
                            mNoItems.setVisibility(View.GONE);
                        } else {
                            hasNoCandidates = true;
                        }
                    } else {
                        hasNoCandidates = true;
                    }
                }
            } catch (Exception e) {
                hasNoCandidates = true;

                e.printStackTrace();
            }
        } else {
            hasNoCandidates = true;
        }

        isFirstTime = false;

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
            handleMessagesResult(result);
        }
    }

    private void handleMessagesResult(String result) {
        if (TextUtils.isEmpty(result))
            return;

        try {
            messages = GsonParser.getMessagesFromJSON(result);

            String messageJson = GsonParser.createJsonFromObject(messages);
            PreferencesManager.putStringPreference(getActivity().getApplication(), PreferencesManager.MESSAGES, messageJson);

            String currentDate = DateUtils.getCurrentDateTime();
            PreferencesManager.putStringPreference(getActivity().getApplication(), PreferencesManager.DATE_MESSAGES, currentDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startLocationService() {
        if (!isRefreshing) {
            mSwipeFlingView.setVisibility(View.VISIBLE);
            mNoItems.setVisibility(View.GONE);
            initLocationClientListener();
            startLocationListener();
        }
    }
}
