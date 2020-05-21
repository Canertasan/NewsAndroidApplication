package com.example.canertasanhomework3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder>{
    List<CommentItem> comItems;
    Context context;

    public CommentsAdapter(List<CommentItem> comItems, Context context) {
        this.comItems = comItems;
        this.context = context;
    }

    @NonNull
    @Override
    public CommentsAdapter.CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.comments_row_layout,parent,false);
        return new CommentsAdapter.CommentsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsViewHolder holder, int position) {
        holder.txtTitle.setText(comItems.get(position).getName());
        holder.txtComment.setText(comItems.get(position).getMessage());
    }

    @Override
    public int getItemCount() {
        return comItems.size();
    }

    class CommentsViewHolder extends RecyclerView.ViewHolder {

        TextView txtTitle;
        TextView txtComment;
        ConstraintLayout root;

        public CommentsViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtComment = itemView.findViewById(R.id.txtComment);
            root = itemView.findViewById(R.id.container_comment);
        }
    }


}
