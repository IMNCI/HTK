package org.ministryofhealth.healthtalkkit.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.ministryofhealth.healthtalkkit.R;
import org.ministryofhealth.healthtalkkit.model.HealthTalkKit;
import org.ministryofhealth.healthtalkkit.utils.ItemAnimation;

import java.util.List;

public class KitAdapter extends RecyclerView.Adapter<KitAdapter.ViewHolder> {
    private List<HealthTalkKit> kits;
    private final ItemClick mClickListener;
    private int animation_type = ItemAnimation.FADE_IN;

    public KitAdapter(ItemClick itemClick){
        mClickListener = itemClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layout = R.layout.title_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layout, parent, shouldAttachToParentImmediately);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HealthTalkKit kit = kits.get(position);
        holder.txtTitle.setText(kit.getTitle());
        holder.titleLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        setAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        if (null == kits) return 0;
        return kits.size();
    }

    public void setKits(List<HealthTalkKit> kits){
        this.kits = kits;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView txtTitle;
        LinearLayout titleLayout;
        public ViewHolder(View itemView) {
            super(itemView);

            txtTitle = itemView.findViewById(R.id.title);
            titleLayout = itemView.findViewById(R.id.titleLayout);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            HealthTalkKit kit = kits.get(position);
            mClickListener.onClick(kit);
        }
    }

    public interface ItemClick{
        void onClick(HealthTalkKit kit);
    }

    private int lastPosition = -1;
    private boolean on_attach = true;

    private void setAnimation(View view, int position) {
        if (position > lastPosition) {
            ItemAnimation.animate(view, on_attach ? position : -1, animation_type);
            lastPosition = position;
        }
    }
}
