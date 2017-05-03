package mx.com.factico.diputinder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Locale;

import mx.com.factico.diputinder.fragments.CandidateFragment;
import mx.com.factico.diputinder.httpconnection.HttpConnection;
import mx.com.factico.diputinder.models.Candidate;
import mx.com.factico.diputinder.models.CandidateInfo;
import mx.com.factico.diputinder.utils.CacheUtils;
import mx.com.factico.diputinder.utils.ImageUtils;
import mx.com.factico.diputinder.views.CustomTextView;

/**
 * Created by zace3d on 4/30/15.
 */
public class CandidateActivity extends AppCompatActivity {
    public static final String TAG = CandidateActivity.class.getName();

    public static final String TAG_CANDIDATE = "candidate";
    private CandidateInfo candidateInfo;

    private DisplayImageOptions options;

    public static void startActivity(Activity activity, CandidateInfo candidate) {
        Intent intent = new Intent(activity, CandidateActivity.class);
        intent.putExtra(CandidateActivity.TAG_CANDIDATE, candidate);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidate);

        setSupportActionBar();

        options = ImageUtils.buildDisplayImageOptions();

        candidateInfo = (CandidateInfo) getIntent().getSerializableExtra(TAG_CANDIDATE);
        if (candidateInfo != null) {
            fillCandidateInfo();

            if (savedInstanceState == null)
                updateFragment();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        CacheUtils.unbindDrawables(findViewById(R.id.container));

        CacheUtils.clearMemoryCache();
    }

    protected void setSupportActionBar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.actionbar);
        mToolbar.setTitle("");
        TextView actionbarTitle = (TextView) mToolbar.findViewById(R.id.actionbar_title);
        actionbarTitle.setText(getResources().getString(R.string.app_name));

        setSupportActionBar(mToolbar);

        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void updateFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = CandidateFragment.newInstance(candidateInfo);
        transaction.replace(R.id.candidate_container, fragment);
        transaction.commit();
    }

    private void fillCandidateInfo() {
        if (candidateInfo == null)
            return;

        Candidate candidate = candidateInfo.getCandidate();
        if (candidate != null) {
            String nombres = candidate.getNombres() != null ? candidate.getNombres() : "";
            String apellidoPaterno = candidate.getApellidoPaterno() != null ? candidate.getApellidoPaterno() : "";
            String apellidoMaterno = candidate.getApellidoMaterno() != null ? candidate.getApellidoMaterno() : "";

            CustomTextView tvName = (CustomTextView) findViewById(R.id.candidate_name_label);
            tvName.setText(String.format(Locale.getDefault(), "%s %s %s", nombres, apellidoPaterno, apellidoMaterno));

            ImageView ivProfile = (ImageView) findViewById(R.id.candidate_image);
            if (candidate.getTwitter() != null && !candidate.getTwitter().equals("") && !candidate.getTwitter().equals("no se identificó")) {
                String twitter = candidate.getTwitter().replaceAll("\\s+", "");
                ImageLoader.getInstance().displayImage(String.format(Locale.getDefault(), HttpConnection.TWITTER_IMAGE_URL, twitter), ivProfile, options);
            } else {
                ivProfile.setImageResource(R.drawable.drawable_bgr_gray);
            }

            String partyImage = candidate.getParty() != null && candidate.getParty().getImage() != null &&
                    candidate.getParty().getImage().getThumb() != null &&
                    !TextUtils.isEmpty(candidate.getParty().getImage().getThumb().getUrl()) ?
                    candidate.getParty().getImage().getThumb().getUrl() : null;

            // Party Image
            ImageView mPartyImage = (ImageView) findViewById(R.id.candidate_party_image);
            if (!TextUtils.isEmpty(partyImage))
                ImageLoader.getInstance().displayImage(partyImage, mPartyImage, options);

            String partyName = candidate.getParty() != null && candidate.getParty().getName() != null &&
                    !TextUtils.isEmpty(candidate.getParty().getName()) ?
                    candidate.getParty().getName() : "";

            // Party Name
            TextView mPartyLabel = (TextView) findViewById(R.id.candidate_party_label);
            mPartyLabel.setText(partyName);

            String positionName = !TextUtils.isEmpty(candidateInfo.getPosition()) ?
                    candidateInfo.getPosition() : "";

            // Position Name
            TextView mPositionLabel = (TextView) findViewById(R.id.candidate_position_label);
            mPositionLabel.setText(positionName);

            String territoryName = !TextUtils.isEmpty(candidateInfo.getTerritoryName()) ?
                    candidateInfo.getTerritoryName() : "";

            // Territory Name
            TextView mTerritoryLabel = (TextView) findViewById(R.id.candidate_territory_label);
            mTerritoryLabel.setText(territoryName);
        }
    }
}
