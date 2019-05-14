package com.example.socialmediaclone;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendActivity extends AppCompatActivity {

    RecyclerView friendsList;
    DatabaseReference usersRef,friendRefs;
    FirebaseAuth mAuth;
    Toolbar toolbar;
    String current_user_id;
    private FirebaseRecyclerAdapter<Friend, FriendViewHolder> firebaseRecyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        initialize();

    }



    public void initialize() {
        setToolbar();
        friendsList = (RecyclerView) findViewById(R.id.recycleFriends);
        friendsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        friendsList.setLayoutManager(linearLayoutManager);
        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        friendRefs = FirebaseDatabase.getInstance().getReference().child("Friend").child(current_user_id);
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.friend_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Friend");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Friend> options=
                new FirebaseRecyclerOptions.Builder<Friend>()
                        .setQuery(friendRefs,Friend.class)
                        .setLifecycleOwner(this)
                        .build();

        firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Friend, FriendViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(final FriendViewHolder holder, int position, @NonNull final Friend model) {

                        final String  userID = getRef(position).getKey();
                        holder.setDate(model.getDate());
                        usersRef.child(userID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists())
                                {
                                    final String usernName = dataSnapshot.child("fullname").getValue().toString();
                                    final String profileImage = dataSnapshot.child("profileimage").getValue().toString();

                                    holder.setFullname(usernName);
                                    holder.setProfileImage(getApplicationContext(),profileImage);

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                    @NonNull
                    @Override
                    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                        return new FriendViewHolder(LayoutInflater.from(viewGroup.getContext())
                                .inflate(R.layout.all_friend_layout, viewGroup, false));
                    }
                };
        friendsList.setAdapter(firebaseRecyclerAdapter);

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
