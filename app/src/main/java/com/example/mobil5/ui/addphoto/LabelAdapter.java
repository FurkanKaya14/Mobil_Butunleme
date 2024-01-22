package com.example.mobil5.ui.addphoto;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobil5.R;
import com.example.mobil5.model.Label;

import java.util.List;

public class LabelAdapter extends RecyclerView.Adapter<LabelAdapter.LabelViewHolder> {

    private List<Label> labelList;
    private OnLabelClickListener onLabelClickListener;

    public interface OnLabelClickListener {
        void onLabelClick(Label label);
    }

    public LabelAdapter(List<Label> labelList, OnLabelClickListener onLabelClickListener) {
        this.labelList = labelList;
        this.onLabelClickListener = onLabelClickListener;
    }

    @NonNull
    @Override
    public LabelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_label, parent, false);
        return new LabelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LabelViewHolder holder, int position) {
        Label label = labelList.get(position);
        holder.bind(label);
    }

    public void setLabels(List<Label> labels) {
        labelList = labels;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return labelList == null ? 0 : labelList.size();
    }

    public class LabelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView nameLabel;
        private View containerView;

        public LabelViewHolder(@NonNull View itemView) {
            super(itemView);
            nameLabel = itemView.findViewById(R.id.labelNameTextView);
            containerView = itemView.findViewById(R.id.labelContainerView);
            itemView.setOnClickListener(this);
        }

        public void bind(Label label) {
            nameLabel.setText(label.getName());


            containerView.setBackgroundColor(label.isSelected() ?
                    ContextCompat.getColor(itemView.getContext(), R.color.selectedLabelColor) :
                    Color.TRANSPARENT);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && labelList != null && position < labelList.size()) {
                Label label = labelList.get(position);
                onLabelClickListener.onLabelClick(label);
            }
        }
    }
}
