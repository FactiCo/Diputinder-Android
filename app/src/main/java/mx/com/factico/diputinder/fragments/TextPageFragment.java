package mx.com.factico.diputinder.fragments;

import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

import mx.com.factico.diputinder.R;
import mx.com.factico.diputinder.models.Text;
import mx.com.factico.diputinder.utils.CacheUtils;
import mx.com.factico.diputinder.utils.ScreenUtils;

/**
 * Created by zace3d on 18/05/15.
 */
public class TextPageFragment extends Fragment {
    public static final String TAG = TextPageFragment.class.getName();

    private static final String TEXT = "text";
    private static final String INDEX = "index";

    private Text text;
    private int index;

    private ViewGroup mRoot;
    private ImageView mLogoImage;
    private TextView mTitleLabel;
    private TextView mContentLabel;

    /**
     * Instances a new fragment with a background color and an index page.
     *
     * @param text  text
     * @param index index page
     * @return a new page
     */
    public static Fragment newInstance(int index, Text text) {
        Fragment fragment = new TextPageFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(INDEX, index);
        bundle.putSerializable(TEXT, text);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load parameters when the initial creation of the fragment is done
        this.text = (getArguments() != null) ? (Text) getArguments().getSerializable(TEXT) : null;
        this.index = (getArguments() != null) ? getArguments().getInt(INDEX) : -1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = (ViewGroup) inflater.inflate(R.layout.fragment_text, container, false);
        return mRoot;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        CacheUtils.unbindDrawables(mRoot);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mLogoImage = (ImageView) view.findViewById(R.id.text_image_logo);
        mTitleLabel = (TextView) view.findViewById(R.id.text_tv_title);
        mContentLabel = (TextView) view.findViewById(R.id.text_tv_content);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        createView();
    }

    private void createView() {
        mLogoImage.setImageResource(text.getResImage());

        if (!TextUtils.isEmpty(text.getTitle()))
            mTitleLabel.setText(text.getTitle());

        if (!TextUtils.isEmpty(text.getContent()))
            mContentLabel.setText(text.getContent());
    }
}
