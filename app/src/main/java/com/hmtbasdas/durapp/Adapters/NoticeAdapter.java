package com.hmtbasdas.durapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hmtbasdas.durapp.Models.Notice;
import com.hmtbasdas.durapp.Models.NoticeListener;
import com.hmtbasdas.durapp.databinding.NoticeItemBinding;

import java.util.ArrayList;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.MyViewHolder> {

    ArrayList<Notice> notices;
    Context context;
    NoticeListener noticeListener;

    public NoticeAdapter(Context context, ArrayList<Notice> notices, NoticeListener noticeListener){
        this.notices = notices;
        this.context = context;
        this.noticeListener = noticeListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        NoticeItemBinding noticeItemBinding = NoticeItemBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new MyViewHolder(noticeItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeAdapter.MyViewHolder holder, int position) {
        holder.setNoticeData(notices.get(position));
    }

    @Override
    public int getItemCount() {
        return notices.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        NoticeItemBinding binding;

        public MyViewHolder(NoticeItemBinding noticeItemBinding){
            super(noticeItemBinding.getRoot());

            binding = noticeItemBinding;
        }

        void setNoticeData(Notice notice){
            binding.dateText.setText(notice.getDate());
            binding.noticeContent.setText(notice.getContent());
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    noticeListener.onNoticeClicked(notice);
                }
            });
        }
    }
}
