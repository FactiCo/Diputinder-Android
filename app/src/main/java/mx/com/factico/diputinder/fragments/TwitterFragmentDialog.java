package mx.com.factico.diputinder.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;
import java.util.Locale;

import mx.com.factico.diputinder.R;
import mx.com.factico.diputinder.beans.Candidate;
import mx.com.factico.diputinder.beans.CandidateInfo;
import mx.com.factico.diputinder.beans.Messages;
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
        if (candidateInfo == null || candidateInfo.getCandidate() == null ||
                candidateInfo.getCandidate().getCandidate() == null)
            return;

        Candidate candidate = candidateInfo.getCandidate().getCandidate();

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
}
