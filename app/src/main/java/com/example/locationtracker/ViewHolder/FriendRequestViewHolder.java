package com.example.locationtracker.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.locationtracker.R;

public class FriendRequestViewHolder extends RecyclerView.ViewHolder {

    public TextView txt_user_email;
    public ImageView btn_accept, btn_decline;

    public FriendRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_user_email = (TextView)itemView.findViewById(R.id.txt_user_email);
            btn_accept = (ImageView)itemView.findViewById(R.id.btn_accept);
            btn_decline= (ImageView)itemView.findViewById(R.id.btn_decline);
    }
}