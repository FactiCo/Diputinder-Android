package mx.com.factico.diputinder.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mx.com.factico.diputinder.MainActivity;
import mx.com.factico.diputinder.R;

/**
 * Created by Edgar Z. on 25/04/17.
 */

public class NoPermissionsFragment extends Fragment implements View.OnClickListener {

    private View mOkButton;

    public static Fragment newInstance() {
        Bundle args = new Bundle();
        Fragment fragment = new NoPermissionsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_no_permissions, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mOkButton = view.findViewById(R.id.no_permissions_ok_button);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mOkButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.no_permissions_ok_button:
                requestPermissions();
                break;
        }
    }

    private void requestPermissions() {
        MainActivity activity = (MainActivity) getActivity();
        if (activity == null)
            return;

        activity.requestLocationPermissions();
    }
}
