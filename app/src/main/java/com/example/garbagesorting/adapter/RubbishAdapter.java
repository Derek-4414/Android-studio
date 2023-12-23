package com.example.garbagesorting.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.garbagesorting.R;
import com.example.garbagesorting.bean.Rubbish;

import java.util.ArrayList;
import java.util.List;

public class RubbishAdapter extends RecyclerView.Adapter<RubbishAdapter.ViewHolder> {
    private Activity mActivity;
    private List<Rubbish> list;
    private RequestOptions headerRO = new RequestOptions().circleCrop();//圆角变换
    private ItemListener mItemListener;
    public void setItemListener(ItemListener itemListener){
        this.mItemListener = itemListener;
    }

    public RubbishAdapter(){
        list = new ArrayList<>();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mActivity = (Activity) parent.getContext();
        View view= LayoutInflater.from(mActivity).inflate(R.layout.rv_item_rubbish,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Rubbish rubbish = list.get(position);
        if (rubbish != null) {
            holder.tvName.setText(rubbish.getName());
            Glide.with(mActivity)
                    .load(rubbish.getImg())
                    .apply(headerRO)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(holder.ivImg);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemListener!=null){
                        mItemListener.ItemClick(rubbish);
                    }
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mItemListener!=null){
                        mItemListener.ItemLongClick(rubbish);
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    /**
     * 添加数据
     * @param listAdd
     */
    public void addItem(List<Rubbish> listAdd) {
        //如果是加载第一页，需要先清空数据列表
        this.list.clear();
        if (listAdd!=null){
            //添加数据
            this.list.addAll(listAdd);
        }
        //通知RecyclerView进行改变--整体
        notifyDataSetChanged();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private ImageView ivImg;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            ivImg = itemView.findViewById(R.id.iv_img);
        }
    }
    public interface ItemListener{
        void ItemClick(Rubbish rubbish);
        void ItemLongClick(Rubbish rubbish);
    }
}
