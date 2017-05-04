package mx.com.factico.diputinder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
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
public class CandidateActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = CandidateActivity.class.getName();

    public static final String TAG_CANDIDATE = "candidate";
    private CandidateInfo candidateInfo;

    private DisplayImageOptions options;

    private TextView mNameLabel;
    private ImageView mImageProfile;
    private ImageView mPartyImage;
    private TextView mPartyLabel;
    private TextView mPositionLabel;
    private TextView mTerritoryLabel;
    private ImageView mEmailImage;
    private ImageView mFacebookImage;
    private ImageView mTwitterImage;

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
        initViews();

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

    private void initViews() {
        mNameLabel = (TextView) findViewById(R.id.candidate_name_label);
        mImageProfile = (ImageView) findViewById(R.id.candidate_image);
        mPartyImage = (ImageView) findViewById(R.id.candidate_party_image);
        mPartyLabel = (TextView) findViewById(R.id.candidate_party_label);
        mPositionLabel = (TextView) findViewById(R.id.candidate_position_label);
        mTerritoryLabel = (TextView) findViewById(R.id.candidate_territory_label);
        mEmailImage = (ImageView) findViewById(R.id.candidate_email_image);
        mFacebookImage = (ImageView) findViewById(R.id.candidate_facebook_image);
        mTwitterImage = (ImageView) findViewById(R.id.candidate_twitter_image);
    }

    private void fillCandidateInfo() {
        if (candidateInfo == null)
            return;

        Candidate candidate = candidateInfo.getCandidate();
        if (candidate != null) {

            // Candidate Name
            String nombres = candidate.getNombres() != null ? candidate.getNombres() : "";
            String apellidoPaterno = candidate.getApellidoPaterno() != null ? candidate.getApellidoPaterno() : "";
            String apellidoMaterno = candidate.getApellidoMaterno() != null ? candidate.getApellidoMaterno() : "";
            mNameLabel.setText(String.format(Locale.getDefault(), "%s %s %s", nombres, apellidoPaterno, apellidoMaterno));

            // Candidate Image
            if (candidate.getTwitter() != null && !candidate.getTwitter().equals("") && !candidate.getTwitter().equals("no se identificó")) {
                String twitter = candidate.getTwitter().replaceAll("\\s+", "");
                ImageLoader.getInstance().displayImage(String.format(Locale.getDefault(), HttpConnection.TWITTER_IMAGE_URL, twitter), mImageProfile, options);
            } else {
                mImageProfile.setImageResource(R.drawable.drawable_bgr_gray);
            }

            // Party Image
            String partyImage = candidate.getParty() != null && candidate.getParty().getImage() != null &&
                    candidate.getParty().getImage().getThumb() != null &&
                    !TextUtils.isEmpty(candidate.getParty().getImage().getThumb().getUrl()) ?
                    candidate.getParty().getImage().getThumb().getUrl() : null;
            if (!TextUtils.isEmpty(partyImage))
                ImageLoader.getInstance().displayImage(partyImage, mPartyImage, options);

            // Party Name
            String partyName = candidate.getParty() != null && candidate.getParty().getName() != null &&
                    !TextUtils.isEmpty(candidate.getParty().getName()) ?
                    candidate.getParty().getName() : "";
            mPartyLabel.setText(partyName);

            // Position Name
            String positionName = !TextUtils.isEmpty(candidateInfo.getPosition()) ?
                    candidateInfo.getPosition() : "";
            mPositionLabel.setText(positionName);

            // Territory Name
            String territoryName = !TextUtils.isEmpty(candidateInfo.getTerritoryName()) ?
                    candidateInfo.getTerritoryName() : "";
            mTerritoryLabel.setText(territoryName);

            String email = !TextUtils.isEmpty(candidate.getEmail()) ? candidate.getEmail() : null;
            String facebook = !TextUtils.isEmpty(candidate.getFacebook()) ? candidate.getFacebook() : null;
            String twitter = !TextUtils.isEmpty(candidate.getTwitter()) ? candidate.getTwitter() : null;

            if (!TextUtils.isEmpty(email)) {
                mEmailImage.setTag(email);
                mEmailImage.setOnClickListener(this);
            }
            if (!TextUtils.isEmpty(facebook)) {
                mFacebookImage.setTag(facebook);
                mFacebookImage.setOnClickListener(this);
            }
            if (!TextUtils.isEmpty(twitter)) {
                mTwitterImage.setTag(twitter);
                mTwitterImage.setOnClickListener(this);
            }

        }
    }

    @Override
    public void onClick(View view) {
        String tag = view.getTag().toString();
        switch (view.getId()) {
            case R.id.candidate_email_image:
                if (!TextUtils.isEmpty(tag)) openSendEmail(tag);
                break;
            case R.id.candidate_facebook_image:
                if (!TextUtils.isEmpty(tag)) openChromeCustomTab(tag);
                break;
            case R.id.candidate_twitter_image:
                if (!TextUtils.isEmpty(tag)) {
                    String url = "https://twitter.com/" + tag;
                    openChromeCustomTab(url);
                }
                break;
        }
    }

    private void openSendEmail(String email) {
        Intent mailer = new Intent(Intent.ACTION_SEND);
        mailer.setType("message/rfc822");
        mailer.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        mailer.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
        mailer.putExtra(android.content.Intent.EXTRA_TEXT, "");
        startActivity(Intent.createChooser(mailer, "Enviar correo electrónico"));
    }


    private void openChromeCustomTab(String url) {
        Context context = getApplicationContext();
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(ContextCompat.getColor(context, R.color.colorWhite));
        builder.setStartAnimations(context, R.anim.slide_up, R.anim.no_anim);
        builder.setExitAnimations(context, R.anim.no_anim, R.anim.slide_bottom);
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        customTabsIntent.launchUrl(context, Uri.parse(url));
    }
}
