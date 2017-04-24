package mx.com.factico.diputinder.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;
import java.util.Locale;

import mx.com.factico.diputinder.CandidateActivity;
import mx.com.factico.diputinder.R;
import mx.com.factico.diputinder.beans.Candidate;
import mx.com.factico.diputinder.beans.CandidateInfo;
import mx.com.factico.diputinder.beans.Party;
import mx.com.factico.diputinder.httpconnection.HttpConnection;
import mx.com.factico.diputinder.utils.ImageUtils;
import mx.com.factico.diputinder.utils.ScreenUtils;

/**
 * Created by zace3d on 4/27/15.
 */
public class MyArrayAdapter extends ArrayAdapter<CandidateInfo> {
    private final Activity activity;
    private final List<CandidateInfo> items;

    private final DisplayImageOptions options;

    public MyArrayAdapter(Activity activity, List<CandidateInfo> items) {
        super(activity, R.layout.item_candidate, items);
        this.activity = activity;
        this.items = items;

        options = ImageUtils.buildDisplayImageOptions();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        final ViewHolder holder;

        // reuse views
        if (convertView == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            view = inflater.inflate(R.layout.item_candidate, parent, false);
            holder = new ViewHolder();

            // configure view holder
            holder.name = (TextView) view.findViewById(R.id.item_candidate_tv_name);
            holder.imageProfile = (ImageView) view.findViewById(R.id.item_candidate_iv_profile);
            holder.imagePartido = (ImageView) view.findViewById(R.id.item_candidate_iv_partido);
            holder.imageInfo = (ImageView) view.findViewById(R.id.item_candidate_iv_profile_info);

            Point point = ScreenUtils.getScreenSize(getContext());
            int sizeIcon = point.x / 5;
            int margin = (int) ScreenUtils.convertDpToPixel(10, getContext());

            RelativeLayout.LayoutParams paramsIcon = new RelativeLayout.LayoutParams(sizeIcon, sizeIcon);
            paramsIcon.setMargins(0, 0, margin, margin);
            paramsIcon.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            paramsIcon.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            holder.imagePartido.setLayoutParams(paramsIcon);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        fillCandidate(holder, position);

        return view;
    }

    private void fillCandidate(ViewHolder holder, int position) {
        CandidateInfo candidateInfo = getItem(position);

        Candidate candidate = candidateInfo != null ? candidateInfo.getCandidate().getCandidate() : null;
        if (candidate != null) {
            holder.imageInfo.setTag(candidateInfo);
            holder.imageInfo.setOnClickListener(InfoOnClickListener);

            String nombres = candidate.getNombres() != null ? candidate.getNombres() : "";
            String apellidoPaterno = candidate.getApellidoPaterno() != null ? candidate.getApellidoPaterno() : "";
            String apellidoMaterno = candidate.getApellidoMaterno() != null ? candidate.getApellidoMaterno() : "";

            holder.name.setText(String.format(Locale.getDefault(), "%s %s %s", nombres, apellidoPaterno, apellidoMaterno));

            if (candidate.getTwitter() != null && !candidate.getTwitter().equals("") && !candidate.getTwitter().equals("no se identificó")) {
                String twitter = candidate.getTwitter().replaceAll("\\s+", "");
                ImageLoader.getInstance().displayImage(String.format(Locale.getDefault(), HttpConnection.TWITTER_IMAGE_URL, twitter), holder.imageProfile, options);
            } else {
                holder.imageProfile.setImageResource(R.drawable.drawable_bgr_gray);
            }
        }

        if (candidateInfo != null && candidateInfo.getCandidate() != null) {
            List<Party> parties = candidateInfo.getCandidate().getParty();
            if (parties != null && parties.size() > 0) {
                ImageLoader.getInstance().displayImage(parties.get(0).getImage(), holder.imagePartido, options);
            }
        }
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public CandidateInfo getItem(int position) {
        return items.get(position);
    }

    private View.OnClickListener InfoOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CandidateInfo candidate = v.getTag() != null ? (CandidateInfo) v.getTag() : null;

            if (candidate != null)
                startIntentCandidate(candidate);
        }
    };

    private void startIntentCandidate(CandidateInfo candidate) {
        Intent intent = new Intent(getContext(), CandidateActivity.class);
        intent.putExtra(CandidateActivity.TAG_CANDIDATE, candidate);
        getContext().startActivity(intent);
    }

    private static class ViewHolder {
        TextView name;
        ImageView imageProfile;
        ImageView imagePartido;
        ImageView imageInfo;
    }
}
