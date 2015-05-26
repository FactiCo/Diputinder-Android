package mx.com.factico.diputinder.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import mx.com.factico.diputinder.R;
import mx.com.factico.diputinder.beans.DrawerOption;
import mx.com.factico.diputinder.dialogues.Dialogues;

/**
 * Created by zace3d on 26/05/15.
 */
public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.DrawerViewHolder> {
    private List<DrawerOption> items;
    private OnItemClickListener onItemClickListener;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    /*
Ver diputados
Ver gobernadores
¿Qué es Por el México que merecemos? http://www.mexicoquemerecemos.com/agenda.php
¿Qué es Candidato Transparente? https://candidatotransparente.mx/#/acerca_de
¿Qué es Fáctico? http://www.factico.com.mx/
     */
    public class DrawerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public int holderId;

        public TextView title;

        public ImageView profileImage;
        public TextView description;
        public TextView website;

        public DrawerViewHolder(View view, int viewType) {
            super(view);

            if (viewType == TYPE_ITEM) {
                title = (TextView) view.findViewById(R.id.drawer_item_option);
                view.setOnClickListener(this);
                holderId = 1;
            } else {
                description = (TextView) view.findViewById(R.id.drawer_header_description_factico);
                website = (TextView) view.findViewById(R.id.drawer_header_website_factico);
                profileImage = (ImageView) view.findViewById(R.id.drawer_header_icon);
                holderId = 0;
            }
        }

        @Override
        public void onClick(View v) {
            Dialogues.Toast(v.getContext(), "Click in position: " + getPosition(), Toast.LENGTH_LONG);

            if (onItemClickListener != null)
                onItemClickListener.onItemClick(v, getPosition());
        }
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
            holder.profileImage.setImageResource(R.drawable.ic_factico);
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
        public void onItemClick(View view, int position);
    }
}
