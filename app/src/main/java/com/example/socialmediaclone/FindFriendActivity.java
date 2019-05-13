package com.example.socialmediaclone;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendActivity extends AppCompatActivity {

    EditText cari;
    ImageButton btnSearch;
    private FirebaseRecyclerAdapter<FindFriend, FindFriendViewHolder> firebaseRecyclerAdapter;
    Toolbar toolbar;
    DatabaseReference findRef;
    FirebaseAuth mAuth;

    RecyclerView recyclerView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);
        initilize();

    }

    private void initilize() {
        toolbar = (Toolbar) findViewById(R.id.find_friend_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Find Friend");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        findRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();

        recyclerView = (RecyclerView) findViewById(R.id.allDisplaySearchUser);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        cari = (EditText) findViewById(R.id.etFindsFriend);
        btnSearch = (ImageButton) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String people = cari.getText().toString();
                findfriend(people);
            }
        });
    }

    private void findfriend(String people) {

        Query query = findRef.orderByChild("fullname").startAt(people).endAt(people + "\uf8ff");

        FirebaseRecyclerOptions<FindFriend> options=
                new FirebaseRecyclerOptions.Builder<FindFriend>()
                        .setQuery(query,FindFriend.class)
                        .setLifecycleOwner(this)
                        .build();

        firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<FindFriend,FindFriendViewHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, int position, @NonNull FindFriend model) {

                        //final String PostKey = getRef(position).getKey();

                        holder.setFullName(model.getFullname());
                        holder.setStatus(model.getStatus());
                        holder.setProfileImage(getApplicationContext(),model.getImage());


                    }

                    @NonNull
                    @Override
                    public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                        return new FindFriendViewHolder(LayoutInflater.from(viewGroup.getContext())
                                .inflate(R.layout.all_user_display_layout, viewGroup, false));
                    }
                };

        Toast.makeText(this, "Search...", Toast.LENGTH_SHORT).show();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class FindFriendViewHolder extends RecyclerView.ViewHolder
    {

        View mView;
        public FindFriendViewHolder(View itemView)
        {
            super(itemView);
            mView = itemView;
        }
        public void setFullName(String fullName){
            TextView username = (TextView) mView.findViewById(R.id.display_profile_fullnane);
            username.setText(fullName);
        }
        public void setProfileImage(Context ctx, String profileImage){
            CircleImageView image = (CircleImageView) mView.findViewById(R.id.display_profile_image);
            Picasso.with(ctx).load(profileImage).into(image);
        }

        public void setStatus(String des){
            TextView status = (TextView) mView.findViewById(R.id.display_profile_status);
            status.setText(des);
        }




    }
}
