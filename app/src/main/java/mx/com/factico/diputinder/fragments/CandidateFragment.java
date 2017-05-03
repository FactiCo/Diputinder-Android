package mx.com.factico.diputinder.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import mx.com.factico.diputinder.R;
import mx.com.factico.diputinder.WebViewActivity;
import mx.com.factico.diputinder.models.Answer;
import mx.com.factico.diputinder.models.CandidateInfo;
import mx.com.factico.diputinder.models.Indicator;
import mx.com.factico.diputinder.models.Question;
import mx.com.factico.diputinder.models.Section;

/**
 * Created by Edgar Z. on 5/3/17.
 */

public class CandidateFragment extends Fragment {

    public static final String CANDIDATE_INFO = "candidate_info";
    private CandidateInfo candidateInfo;

    private LinearLayout mIndicatorsContainer;

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

        mIndicatorsContainer = (LinearLayout) view.findViewById(R.id.candidate_indicators_container);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        fillIndicators();
    }

    private void fillIndicators() {
        List<Indicator> indicators = candidateInfo.getCandidate().getIndicators();
        if (indicators != null && indicators.size() > 0) {

            for (Indicator indicator : indicators) {
                for (Section section : indicator.getSections()) {
                    View view = createSection(mIndicatorsContainer, section);

                    if (view != null)
                        mIndicatorsContainer.addView(view);
                }
            }
        }
    }

    private View createSection(ViewGroup root, Section section) {
        Activity activity = getActivity();
        if (activity == null)
            return null;

        View view = activity.getLayoutInflater().inflate(R.layout.item_section, root, false);

        TextView mSectionNameLabel = (TextView) view.findViewById(R.id.item_section_name_label);
        mSectionNameLabel.setText(section.getName());

        LinearLayout mAnswersLayout = (LinearLayout) view.findViewById(R.id.item_section_answers);

        View mDocumentView = view.findViewById(R.id.item_section_document);

        for (Question question : section.getQuestions()) {
            View sectionView = null;
            switch (question.getElementType()) {
                case "MultipleOption":
                    sectionView = createMultipleOptionView(mAnswersLayout, question);
                    break;
                case "OpenAnswer":
                    sectionView = createOpenAnswerView(mAnswersLayout, question);
                    break;
                case "Document":
                    createDocumentView(mDocumentView, question);
                    break;
            }

            if (sectionView != null)
                mAnswersLayout.addView(sectionView);
        }

        return view;
    }

    private View createMultipleOptionView(ViewGroup root, Question question) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.item_question_multiple_option, root, false);

        String answer = question.getAnswer() != null && !TextUtils.isEmpty(question.getAnswer().getOption()) ?
                question.getAnswer().getOption() : "";

        TextView answerLabel = (TextView) view.findViewById(R.id.item_question_option_label);
        answerLabel.setText(answer);

        return view;
    }

    private View createOpenAnswerView(ViewGroup root, Question question) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.item_question_open_answer, root, false);

        Answer answer = question.getAnswer();

        String ans = answer != null && answer.getInputs() != null &&
                answer.getInputs().size() > 0 && !TextUtils.isEmpty(answer.getInputs().get(0).getContent()) ?
                answer.getInputs().get(0).getContent() : "";

        TextView answerLabel = (TextView) view.findViewById(R.id.item_question_input_label);
        answerLabel.setText(ans);

        return view;
    }

    private void createDocumentView(View root, Question question) {
        //View view = getActivity().getLayoutInflater().inflate(R.layout.item_question_document, root, false);
        //return view;

        String answer = question.getAnswer() != null && !TextUtils.isEmpty(question.getAnswer().getDocument()) ?
                question.getAnswer().getDocument() : "";

        if (!TextUtils.isEmpty(answer)) {
            root.setVisibility(View.VISIBLE);
            root.setTag(answer);
            root.setOnClickListener(DocumentPdfOnClickListener);
        }
    }

    private View.OnClickListener DocumentPdfOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getTag() == null)
                return;

            String url = v.getTag().toString();

            if (url != null && !url.equals(""))
                startWebViewIntent(url);
        }
    };

    private void startWebViewIntent(String url) {
        Activity activity = getActivity();
        if (activity == null)
            return;

        Intent intent = new Intent(activity, WebViewActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("actionbarTitle", getString(R.string.lbl_document));
        startActivity(intent);
    }
}
