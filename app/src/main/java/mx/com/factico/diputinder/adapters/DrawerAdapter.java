package mx.com.factico.diputinder.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import mx.com.factico.diputinder.PdfViewerActivity;
import mx.com.factico.diputinder.R;
import mx.com.factico.diputinder.WebViewActivity;
import mx.com.factico.diputinder.beans.DrawerOption;
import mx.com.factico.diputinder.dialogues.Dialogues;
import mx.com.factico.diputinder.views.CustomTextView;

/**
 * Created by zace3d on 26/05/15.
 */
public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.DrawerViewHolder> {
    private List<DrawerOption> items;
    private OnItemClickListener onItemClickListener;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    protected class DrawerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public int holderId;

        public CustomTextView title;

        public CustomTextView websiteLiguePolitico;
        public CustomTextView websiteFactico;

        public DrawerViewHolder(View view, int viewType) {
            super(view);

            if (viewType == TYPE_ITEM) {
                title = (CustomTextView) view.findViewById(R.id.drawer_item_option);
                view.setOnClickListener(this);
                holderId = 1;
            } else {
                websiteLiguePolitico = (CustomTextView) view.findViewById(R.id.drawer_header_description_liguepolitico);
                websiteFactico = (CustomTextView) view.findViewById(R.id.drawer_header_description_factico);

                websiteLiguePolitico.setOnClickListener(this);
                websiteFactico.setOnClickListener(this);

                holderId = 0;
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.drawer_header_description_liguepolitico:
                    startWebViewIntent(v.getContext(), v.getContext().getResources().getString(R.string.drawer_header_liguepolitico_website));
                    break;
                case R.id.drawer_header_description_factico:
                    startWebViewIntent(v.getContext(), v.getContext().getResources().getString(R.string.drawer_header_factico_website));
                    break;
                default:
                    if (onItemClickListener != null)
                        onItemClickListener.onItemClick(v, getAdapterPosition());
                    break;
            }
        }
    }

    private void startWebViewIntent(Context context, String url) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    public DrawerAdapter(List<DrawerOption> items) {
        this.items = items;
    }

    @Override
    public DrawerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_item_option, parent, false);
            return new DrawerViewHolder(v, viewType);

        } else if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_header, parent, false);
            return new DrawerViewHolder(v, viewType);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(DrawerViewHolder holder, int position) {
        if (holder.holderId == 1) {
            holder.title.setText(items.get(position - 1).getTitle());

        } else if (holder.holderId == TYPE_HEADER) {
            //viewHolder.description.setText(name);
            //viewHolder.website.setText(email);
        }
    }

    @Override
    public int getItemCount() {
        return items.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        if (onItemClickListener != null)
            this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
