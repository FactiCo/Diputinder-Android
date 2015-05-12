package mx.com.factico.diputinder.adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.List;
import java.util.Locale;

import mx.com.factico.diputinder.R;
import mx.com.factico.diputinder.beans.Diputado;
import mx.com.factico.diputinder.httpconnection.HttpConnection;

/**
 * Created by zace3d on 4/27/15.
 */
public class MyArrayAdapter extends ArrayAdapter<Diputado> {
    private final Activity activity;
    private final List<Diputado> values;

    private final DisplayImageOptions options;

    public MyArrayAdapter(Activity activity, List<Diputado> values) {
        super(activity, R.layout.item_diputado, values);
        this.activity = activity;
        this.values = values;

        options = new DisplayImageOptions.Builder()
                //.showImageOnLoading(R.drawable.ic_profile_orange)
                .showImageOnLoading(null)
                .showImageForEmptyUri(R.drawable.ic_launcher)
                .showImageOnFail(R.drawable.ic_launcher)
                .resetViewBeforeLoading(true)
                //.cacheInMemory(false)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300))
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
            holder.image = (ImageView) rowView.findViewById(R.id.item_diputado_iv_profile);

            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }

        Diputado bean = getItem(position);
        holder.name.setText(String.format(Locale.getDefault(), "%s %s %s", bean.getNombres(), bean.getApellidoPaterno(), bean.getApellidoMaterno()));
        if (bean.getTwitter() != null && !bean.getTwitter().equals("")) {
            String twitter = bean.getTwitter().replaceAll("\\s+", "");
            ImageLoader.getInstance().displayImage(String.format(Locale.getDefault(), HttpConnection.TWITTER_IMAGE_URL, twitter), holder.image, options);
        } else {
            if (bean.getGnero() != null) {
                if (bean.getGnero().equals("F"))
                    holder.image.setImageResource(R.drawable.ic_profile_women);
                else if (bean.getGnero().equals("M"))
                    holder.image.setImageResource(R.drawable.ic_profile_men);
            }
        }

        return rowView;
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public Diputado getItem(int position) {
        return values.get(position);
    }

    static class ViewHolder {
        public TextView name;
        public ImageView image;
    }
}
