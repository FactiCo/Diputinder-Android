package mx.com.factico.diputinder.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mx.com.factico.diputinder.R;
import mx.com.factico.diputinder.models.CandidateInfo;

/**
 * Created by Edgar Z. on 5/3/17.
 */

public class CandidateFragment extends Fragment {

    public static final String CANDIDATE_INFO = "candidate_info";
    private CandidateInfo candidateInfo;

    public static Fragment newInstance(CandidateInfo candidateInfo) {
        Bundle args = new Bundle();
        args.putSerializable(CANDIDATE_INFO, candidateInfo);
        Fragment fragment = new CandidateFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        candidateInfo = getArguments() != null ? (CandidateInfo) getArguments().getSerializable(CANDIDATE_INFO) : null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_candidate, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        fillCandidate();
    }

    private void fillCandidate() {
        /*List<Indicator> indicators = candidateInfo.getCandidate().getIndicators();
        if (indicators != null && indicators.size() > 0) {
            LinearLayout vgIndicators = (LinearLayout) findViewById(R.id.candidate_vg_indicators);

            for (Indicator indicator : indicators) {
                View indicatorView = createIndicatorView(indicator, sizeIcons);

                if (indicatorView != null)
                    vgIndicators.addView(indicatorView);

            }
        }*/
    }

    /*private View createIndicatorView(Indicator indicator, int sizeIcons) {
        RelativeLayout view = (RelativeLayout) getLayoutInflater().inflate(R.layout.item_indicator, null, false);
        view.setOnClickListener(WebViewOnClickListener);

        RelativeLayout.LayoutParams paramsIconsStatus = new RelativeLayout.LayoutParams(sizeIcons / 2, sizeIcons / 2);
        paramsIconsStatus.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        paramsIconsStatus.addRule(RelativeLayout.CENTER_VERTICAL);

        RelativeLayout.LayoutParams paramsIcons = new RelativeLayout.LayoutParams((int) (sizeIcons / 1.3), (int) (sizeIcons / 1.3));
        paramsIcons.setMargins(5, 10, 5, 10);
        paramsIcons.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

        CustomTextView tvName = (CustomTextView) view.findViewById(R.id.indicator_tv_name);
        tvName.setText(indicator.getName());

        ImageView ivIndicator = (ImageView) view.findViewById(R.id.indicator_iv_icon);
        ivIndicator.setLayoutParams(paramsIcons);

        ImageView ivIndicatorStatus = (ImageView) view.findViewById(R.id.indicator_iv_status);
        ivIndicatorStatus.setLayoutParams(paramsIconsStatus);

        return view;
    }

    private View.OnClickListener WebViewOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getTag() != null) {
                String url = v.getTag().toString();

                if (url != null && !url.equals(""))
                    startWebViewIntent(url);
                else
                    Dialogues.Toast(getBaseContext(), "No ha presentado documento.", Toast.LENGTH_SHORT);
            } else {
                Dialogues.Toast(getBaseContext(), "No ha presentado documento.", Toast.LENGTH_SHORT);
            }
        }
    };

    private void startWebViewIntent(String url) {
        Intent intent = new Intent(getBaseContext(), WebViewActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("actionbarTitle", getString(R.string.lbl_document));
        startActivity(intent);
    }*/
}
