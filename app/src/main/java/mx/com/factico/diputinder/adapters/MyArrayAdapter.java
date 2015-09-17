package mx.com.factico.diputinder.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.List;
import java.util.Locale;

import mx.com.factico.diputinder.CandidateActivity;
import mx.com.factico.diputinder.R;
import mx.com.factico.diputinder.beans.Candidate;
import mx.com.factico.diputinder.beans.CandidateInfo;
import mx.com.factico.diputinder.beans.Party;
import mx.com.factico.diputinder.httpconnection.HttpConnection;
import mx.com.factico.diputinder.utils.ScreenUtils;

/**
 * Created by zace3d on 4/27/15.
 */
public class MyArrayAdapter extends ArrayAdapter<CandidateInfo> {
    private final Activity activity;
    private final List<CandidateInfo> values;

    private final DisplayImageOptions options;

    public MyArrayAdapter(Activity activity, List<CandidateInfo> values) {
        super(activity, R.layout.item_diputado, values);
        this.activity = activity;
        this.values = values;

        options = new DisplayImageOptions.Builder()
                //.showImageOnLoading(R.drawable.drawable_bgr_gray)
                .showImageForEmptyUri(R.drawable.drawable_bgr_gray)
                .showImageOnFail(R.drawable.drawable_bgr_gray)
                .resetViewBeforeLoading(true)
                //.cacheInMemory(false)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                //.displayer(new FadeInBitmapDisplayer(100))
                .build();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewHolder holder;

        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.item_diputado, parent, false);
            holder = new ViewHolder();

            // configure view holder
            holder.name = (TextView) rowView.findViewById(R.id.item_diputado_tv_name);
            holder.imageProfile = (ImageView) rowView.findViewById(R.id.item_diputado_iv_profile);
            holder.imagePartido = (ImageView) rowView.findViewById(R.id.item_diputado_iv_partido);
            holder.imageInfo = (ImageView) rowView.findViewById(R.id.item_diputado_iv_profile_info);

            Point point = ScreenUtils.getScreenSize(getContext());
            int sizeIcon = point.x / 5;
            int margin = (int) ScreenUtils.convertDpToPixel(10, getContext());

            RelativeLayout.LayoutParams paramsIcon = new RelativeLayout.LayoutParams(sizeIcon, sizeIcon);
            paramsIcon.setMargins(0, 0, margin, 0);
            paramsIcon.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            paramsIcon.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            holder.imagePartido.setLayoutParams(paramsIcon);

            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }

        holder.imageInfo.setTag(getItem(position));
        holder.imageInfo.setOnClickListener(InfoOnClickListener);

        CandidateInfo candidateInfo = getItem(position);

        Candidate candidate = candidateInfo.getCandidate().getCandidate();
        if (candidate != null) {
            String nombres =  candidate.getNombres() != null ? candidate.getNombres() : "";
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

        if (candidateInfo.getCandidate() != null) {
            List<Party> parties = candidateInfo.getCandidate().getParty();
            if (parties != null && parties.size() > 0) {
                ImageLoader.getInstance().displayImage(parties.get(0).getImage(), holder.imagePartido, options);
            }
        }

        return rowView;
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public CandidateInfo getItem(int position) {
        return values.get(position);
    }

    View.OnClickListener InfoOnClickListener = new View.OnClickListener() {
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

    static class ViewHolder {
        public TextView name;
        public ImageView imageProfile;
        public ImageView imagePartido;
        public ImageView imageInfo;
    }
}
