package com.example.socialmediaclone;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendActivity extends AppCompatActivity {

    RecyclerView friendsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
    }

    @Override
    protected void onStart() {
        super.onStart();


    }
    public static class FriendViewHolder extends RecyclerView.ViewHolder
    {

        View mView;
        public FriendViewHolder(View itemView)
        {
            super(itemView);
            mView = itemView;
        }
        public void setFullname(String name){
            TextView fullname = (TextView) mView.findViewById(R.id.friends_fullnane);
            fullname.setText(name);
        }
        public void setDate(String date){
            TextView Date = (TextView) mView.findViewById(R.id.friends_date);
            Date.setText(date);
        }
        public void setProfileImage(Context ctx, String profileImage){
            CircleImageView image = (CircleImageView) mView.findViewById(R.id.friends_image);
            Picasso.with(ctx).load(profileImage).into(image);
        }

    }
}
