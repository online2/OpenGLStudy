package com.linfc.opengl.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.linfc.opengl.R;
import com.linfc.opengl.ac.ShapeActivity;

import java.util.List;

/**
 * created by Linfc on 2019/5/10
 */
public class ShapeSelectAdapter extends RecyclerView.Adapter<ShapeSelectAdapter.ShapeSelevtViewHolder> {

    private OnItemClickListener mItemClickListener;
    private List<ShapeActivity.ShapeData> mData;


    public ShapeSelectAdapter(OnItemClickListener itemClickListener, List<ShapeActivity.ShapeData> data) {
        mItemClickListener = itemClickListener;
        mData = data;
    }

    @NonNull
    @Override
    public ShapeSelevtViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ShapeSelevtViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shape_select_item, viewGroup, false), mItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ShapeSelevtViewHolder viewHolder, int i) {

        viewHolder.setData(i,mData.get(i));

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ShapeSelevtViewHolder extends RecyclerView.ViewHolder {
        private OnItemClickListener mItemClickListener;
        private final TextView mTvText;

        public ShapeSelevtViewHolder(@NonNull View itemView, OnItemClickListener itemClickListener) {
            super(itemView);
            mItemClickListener = itemClickListener;
            mTvText = itemView.findViewById(R.id.tvText);

        }

        public void setData(final int i, ShapeActivity.ShapeData shapeData) {
            mTvText.setText(shapeData.getTitle());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener!=null) {
                        mItemClickListener.onItemClickListener(i);
                    }
                }
            });

        }
    }

    public interface OnItemClickListener {
        void onItemClickListener(int position);
    }
}
