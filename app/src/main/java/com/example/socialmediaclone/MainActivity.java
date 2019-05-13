package com.example.socialmediaclone;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private RecyclerView postList;
    DatabaseReference UsersRef;
    DatabaseReference postRef,likesRef;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar mToolbar;
    private FirebaseRecyclerAdapter<Posts,PostsViewHolder> firebaseRecyclerAdapter;
    String currentUserID;
    FirebaseAuth mAuth;
    Boolean likeCheck = false;
    CircleImageView nav_profile;
    ImageButton addNewPostButton;
    TextView nav_full;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postRef = FirebaseDatabase.getInstance().getReference().child("Post");
        likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        //untuk mengatur toolbar
        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        //agar toolbar muncul pada layout activity_main
        setSupportActionBar(mToolbar);
        currentUserID = mAuth.getCurrentUser().getUid();
        getSupportActionBar().setTitle("Home");
        //untuk mengatur button 3 baris untuk membuga navigationView
        drawerLayout = (DrawerLayout) findViewById(R.id.drawLayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this,drawerLayout,R.string.drawer_open,R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        addNewPostButton = (ImageButton) findViewById(R.id.add_new_post_button);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = (NavigationView) findViewById(R.id.navMenu);
        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);

        postList = (RecyclerView) findViewById(R.id.all_user_post_list);
        postList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        /*linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);*/
        postList.setLayoutManager(linearLayoutManager);


        nav_profile = (CircleImageView) navView.findViewById(R.id.nav_photo_profile);
        nav_full = (TextView) navView.findViewById(R.id.nav_full_name);

        UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.hasChild("fullname")){
                        String fullname = dataSnapshot.child("fullname").getValue().toString();
                        nav_full.setText(fullname);
                    }
                    if(dataSnapshot.hasChild("profileimage")){
                        String image = dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.with(MainActivity.this).load(image).placeholder(R.drawable.profile).into(nav_profile);
                    }

                }
                else {
                       Toast.makeText(MainActivity.this, "Profile name do not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //event untuk memgatur event ketika item pada navigationView ditekan
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                userMenuSelector(menuItem);
                return false;
            }
        });

        addNewPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sentToPostActivity();
            }
        });


    }

    private void sentToPostActivity(){
        Intent intent = new Intent(MainActivity.this,PostActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            sendToLoginActivity();
        }else{
            checkExistanceUser();
        }

        Query query = postRef.orderByChild("date");

        FirebaseRecyclerOptions<Posts> options=
                new FirebaseRecyclerOptions.Builder<Posts>()
                        .setQuery(query,Posts.class)
                        .setLifecycleOwner(this)
                        .build();

        firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Posts, PostsViewHolder>(options) {


                    @Override
                    protected void onBindViewHolder(@NonNull PostsViewHolder holder, int position, @NonNull Posts model) {

                        final String PostKey = getRef(position).getKey();


                        holder.setFullName(model.getFullname());
                        holder.setData(model.getDate());
                        holder.setDescription(model.getDescription());
                        holder.setPostImage(getApplicationContext(),model.getPostimage());
                        holder.setProfileImage(getApplicationContext(),model.getProfileimage());
                        holder.setTime(model.getTime());
                        holder.setLikeButtonStatus(PostKey);

                        holder.commentButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent clickPostIntent = new Intent(MainActivity.this, CommentActivity.class);
                                clickPostIntent.putExtra("Postkey", PostKey);
                                startActivity(clickPostIntent);
                            }
                        });

                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent clickPostIntent = new Intent(MainActivity.this,ClickPostActivity.class);
                                clickPostIntent.putExtra("Postkey",PostKey);
                                startActivity(clickPostIntent);
                            }
                        });

                        holder.likeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                likeCheck = true;

                                likesRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        if(likeCheck.equals(true))
                                        {
                                            if(dataSnapshot.child(PostKey).hasChild(currentUserID))
                                            {
                                                likesRef.child(PostKey).child(currentUserID).removeValue();
                                                likeCheck = false;
                                            }
                                            else
                                            {
                                                likesRef.child(PostKey).child(currentUserID).setValue(true);
                                                likeCheck = false;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                        return new PostsViewHolder(LayoutInflater.from(viewGroup.getContext())
                                .inflate(R.layout.all_post_layout, viewGroup, false));
                    }
                };
        postList.setAdapter(firebaseRecyclerAdapter);

    }

    public static class PostsViewHolder extends RecyclerView.ViewHolder
    {

        ImageButton likeButton,commentButton;
        TextView sumLike;
        int countLikes;
        String currentUserId;
        DatabaseReference LIKESREF;

        View mView;
        public PostsViewHolder(View itemView)
        {
            super(itemView);
            mView = itemView;

            likeButton = (ImageButton) mView.findViewById(R.id.btnLikeDislike);
            commentButton = (ImageButton) mView.findViewById(R.id.btnComment);
            sumLike = (TextView) mView.findViewById(R.id.jumlahLike);

            LIKESREF = FirebaseDatabase.getInstance().getReference().child("Likes");
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        }
        public void setFullName(String fullName){
            TextView username = (TextView) mView.findViewById(R.id.post_profile_name);
            username.setText(fullName);
        }
        public void setProfileImage(Context ctx, String profileImage){
            CircleImageView image = (CircleImageView) mView.findViewById(R.id.post_profile_image);
            Picasso.with(ctx).load(profileImage).into(image);
        }
        public void setTime(String time){
            TextView PostTime = (TextView) mView.findViewById(R.id.post_time);
            PostTime.setText("    " + time);
        }
        public void setData(String date){
            TextView PostDate = (TextView) mView.findViewById(R.id.post_date);
            PostDate.setText("    " +date);
        }
        public void setDescription(String des){
            TextView postdes = (TextView) mView.findViewById(R.id.post_description);
            postdes.setText(des);
        }
        public void setPostImage(Context ctx, String profileImage){
            ImageView image = (ImageView) mView.findViewById(R.id.post_image);
            Picasso.with(ctx).load(profileImage).into(image);
        }


        public void setLikeButtonStatus(final String postKey) {
            LIKESREF.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.child(postKey).hasChild(currentUserId))
                    {
                       countLikes = (int) dataSnapshot.child(postKey).getChildrenCount();
                       likeButton.setImageResource(R.drawable.like);
                       sumLike.setText(Integer.toString(countLikes) + (" Likes"));
                    }
                    else
                    {
                        countLikes = (int) dataSnapshot.child(postKey).getChildrenCount();
                        likeButton.setImageResource(R.drawable.dislike);
                        sumLike.setText(Integer.toString(countLikes) + " Likes");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void checkExistanceUser() {

        final String current_user_id = mAuth.getCurrentUser().getUid();

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(current_user_id)){
                    sendToSetupActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendToSetupActivity(){
        Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }

    private void sendToLoginActivity() {
        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    //untuk mengatur event ketika butonn 3 baris ditekan
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void userMenuSelector(MenuItem item){
        switch (item.getItemId()){
            case R.id.nav_add_post:
                sentToPostActivity();
                break;
            case R.id.nav_find_friend:
                Intent intent = new Intent(MainActivity.this,FindFriendActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_friend:

                break;
            case R.id.nav_home:
                sentToHomeActivity();
                break;
            case R.id.nav_logout:
                mAuth.signOut();
                sendToLoginActivity();
                break;
            case R.id.nav_message:

                break;
            case R.id.nav_profile:
                sentToProfileActivity();
                break;
            case R.id.nav_setting:
                sentToSettingActivity();
                break;
        }
    }

    private void sentToHomeActivity() {
        Intent homeIntent = new Intent(MainActivity.this,MainActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
        finish();
    }

    private void sentToProfileActivity() {
        Intent settingIntent = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(settingIntent);

    }

    private void sentToSettingActivity() {
        Intent settingIntent = new Intent(MainActivity.this, SettingActivity.class);
        startActivity(settingIntent);
    }

}
