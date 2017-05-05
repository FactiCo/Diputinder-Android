package mx.com.factico.diputinder.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;
import java.util.Locale;

import mx.com.factico.diputinder.R;
import mx.com.factico.diputinder.models.Candidate;
import mx.com.factico.diputinder.models.CandidateInfo;
import mx.com.factico.diputinder.models.Messages;
import mx.com.factico.diputinder.dialogues.Dialogues;
import mx.com.factico.diputinder.httpconnection.HttpConnection;
import mx.com.factico.diputinder.utils.CacheUtils;
import mx.com.factico.diputinder.utils.ImageUtils;

/**
 * Created by Edgar Z. on 24/04/17.
 */

public class TwitterFragmentDialog extends DialogFragment {

    private static final String CANDIDATE_INFO = "candidate_info";
    private CandidateInfo candidateInfo;

    private static final String MESSAGES = "messages";
    private Messages messages;

    private DisplayImageOptions options;

    private View mRoot;
    private TextView mMessageLabel;
    private TextView mSubMessageLabel;
    private View mTweetButton;
    private TextView mNameLabel;
    private ImageView mCandidateImage;

    public static DialogFragment newInstance(CandidateInfo candidateInfo, Messages messages) {
        Bundle args = new Bundle();
        args.putSerializable(CANDIDATE_INFO, candidateInfo);
        args.putSerializable(MESSAGES, messages);
        DialogFragment fragment = new TwitterFragmentDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        candidateInfo = getArguments() != null ? (CandidateInfo) getArguments().getSerializable(CANDIDATE_INFO) : null;
        messages = getArguments() != null ? (Messages) getArguments().getSerializable(MESSAGES) : null;

        options = ImageUtils.buildDisplayImageOptions();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.dialog_tweet, container, false);

        mMessageLabel = (TextView) mRoot.findViewById(R.id.dialog_tweet_tv_message);
        mSubMessageLabel = (TextView) mRoot.findViewById(R.id.dialog_tweet_tv_submessage);
        mTweetButton = mRoot.findViewById(R.id.dialog_tweet_btn_tweet);
        mNameLabel = (TextView) mRoot.findViewById(R.id.dialog_tweet_tv_name);
        mCandidateImage = (ImageView) mRoot.findViewById(R.id.dialog_tweet_iv_profile);

        return mRoot;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        if (dialog.getWindow() != null) {
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        }
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        fillCandidateInfo();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        CacheUtils.unbindDrawables(mRoot);
        ImageLoader.getInstance().stop();
    }

    private void fillCandidateInfo() {
        if (candidateInfo == null || candidateInfo.getCandidate() == null)
            return;

        Candidate candidate = candidateInfo.getCandidate();

        if (candidate != null) {
            String userName = (candidate.getTwitter() != null && !candidate.getTwitter().equals(""))
                    ? candidate.getTwitter().startsWith("@") ? candidate.getTwitter() : "@" + candidate.getTwitter()
                    : "#" + candidate.getNombres().replaceAll("\\s+", "")
                    + candidate.getApellidoPaterno().replaceAll("\\s+", "")
                    + candidate.getApellidoMaterno().replaceAll("\\s+", "");

            mTweetButton.setOnClickListener(TweetOnClickListener);

            String nombres = candidate.getNombres() != null ? candidate.getNombres() : "";
            String apellidoPaterno = candidate.getApellidoPaterno() != null ? candidate.getApellidoPaterno() : "";
            String apellidoMaterno = candidate.getApellidoMaterno() != null ? candidate.getApellidoMaterno() : "";

            mNameLabel.setText(String.format(Locale.getDefault(), "%s %s %s", nombres, apellidoPaterno, apellidoMaterno));

            if (candidate.getTwitter() != null && !candidate.getTwitter().equals("")) {
                String twitter = candidate.getTwitter().replaceAll("\\s+", "");
                ImageLoader.getInstance().displayImage(String.format(Locale.getDefault(), HttpConnection.TWITTER_IMAGE_URL, twitter), mCandidateImage, options);
            } else {
                mCandidateImage.setImageResource(R.drawable.drawable_bgr_gray);
            }

            if (candidateInfo.getCandidate() != null) {
                String explanationChecked = (messages != null && messages.getExplanationChecked() != null) ?
                        messages.getExplanationChecked() : getString(R.string.tweet_message_good);
                String congratulation = (messages != null && messages.getCongratulation() != null) ?
                        messages.getCongratulation() : getString(R.string.tweet_submessage_good);
                String tweetChecked = (messages != null && messages.getTweetChecked() != null) ?
                        ".%s " + messages.getTweetChecked() : getString(R.string.tweet_first_message_good);

                mMessageLabel.setText(explanationChecked);
                mSubMessageLabel.setText(congratulation);
                mTweetButton.setTag(String.format(Locale.getDefault(), tweetChecked, userName));
            }
        }
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
            Intent shareIntent = findTwitterClient();
            shareIntent.putExtra(Intent.EXTRA_TEXT, messageTweet);
            startActivity(Intent.createChooser(shareIntent, "Compartir"));
        } else {
            String twitter = candidateInfo.getCandidate() != null ? candidateInfo.getCandidate().getTwitter() : "";
            String url = !TextUtils.isEmpty(twitter) ? "https://twitter.com/" + twitter : "";
            openChromeCustomTab(url);
            //Dialogues.Toast(getActivity(), "Necesitas tener instalada la app de Twitter para poder compartir", Toast.LENGTH_LONG);
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

        for (String twitterApp : twitterApps) {
            for (ResolveInfo resolveInfo : list) {
                String p = resolveInfo.activityInfo.packageName;
                if (p != null && p.startsWith(twitterApp)) {
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

    private void openChromeCustomTab(String url) {
        Activity activity = getActivity();
        if (!URLUtil.isValidUrl(url) || activity == null)
            return;

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(ContextCompat.getColor(activity, R.color.colorWhite));
        //builder.setStartAnimations(activity, R.anim.slide_up, R.anim.no_anim);
        //builder.setExitAnimations(activity, R.anim.no_anim, R.anim.slide_bottom);
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        customTabsIntent.launchUrl(activity, Uri.parse(url));
    }
}
