package com.example.socialmediaclone;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity {

    RecyclerView recyclerComment;ImageButton post;Toolbar toolbar;EditText inputComment;
    DatabaseReference postRefs;DatabaseReference usersRefs;
    FirebaseAuth mAuth;String Post_Key,current_user_id;
    private FirebaseRecyclerAdapter<Comment, CommentsViewHolder> firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        initialize();
        Post_Key = getIntent().getExtras().get("Postkey").toString();

        postRefs = FirebaseDatabase.getInstance().getReference().child("Post").child(Post_Key).child("Comments");

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usersRefs.child(current_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            String UsernName = dataSnapshot.child("username").getValue().toString();

                            ValidateComment(UsernName);

                            inputComment.setText("");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    private void ValidateComment(String usernName) {
        String commentText = inputComment.getText().toString();

        if(TextUtils.isEmpty(commentText)){
            Toast.makeText(this, "please Write some text ... ", Toast.LENGTH_SHORT).show();
        }else{
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
            final String saveCurrentDate = currentDate.format(calendar.getTime());

            Calendar calendar1 = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
            final String saveCurrentTime = currentTime.format(calendar1.getTime());

            final String randomKey = current_user_id + saveCurrentDate + saveCurrentTime;

            HashMap commentsMap = new HashMap();
            commentsMap.put("uid",current_user_id);
            commentsMap.put("comment",commentText);
            commentsMap.put("date",saveCurrentDate);
            commentsMap.put("time",saveCurrentTime);
            commentsMap.put("username",usernName);

            postRefs.child(randomKey).updateChildren(commentsMap)
                    .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if(task.isSuccessful()){

                                Toast.makeText(CommentActivity.this, "Comment success", Toast.LENGTH_SHORT).show();

                            }else{
                                Toast.makeText(CommentActivity.this, "Error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        }
    }

    public void initialize() {
        setToolbar();
        recyclerComment = (RecyclerView) findViewById(R.id.recycleComment);
        post = (ImageButton) findViewById(R.id.btnPostComment);
        inputComment = (EditText) findViewById(R.id.inputComment);
        recyclerComment.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerComment.setLayoutManager(linearLayoutManager);
        usersRefs = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();



    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.comment_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comment");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public static class CommentsViewHolder extends RecyclerView.ViewHolder
    {

        View mView;
        public CommentsViewHolder(View itemView)
        {
            super(itemView);
            mView = itemView;
        }
        public void setUsername1(String comment_username){
            TextView username = (TextView) mView.findViewById(R.id.comment_username);
            username.setText(comment_username);
        }
        public void setDate(String date){
            TextView Date = (TextView) mView.findViewById(R.id.comment_date);
            Date.setText(date);
        }
        public void setTime(String time){
            TextView Time = (TextView) mView.findViewById(R.id.comment_time);
            Time.setText(time);
        }
        public void setComment(String time){
            TextView Comments = (TextView) mView.findViewById(R.id.comment_text);
            Comments.setText(time);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Comment> options=
                new FirebaseRecyclerOptions.Builder<Comment>()
                        .setQuery(postRefs,Comment.class)
                        .setLifecycleOwner(this)
                        .build();

        firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Comment, CommentsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(CommentsViewHolder holder, int position, @NonNull Comment model) {

                        holder.setUsername1(model.getUsername());
                        holder.setTime(model.getTime());
                        holder.setComment(model.getComment());
                        holder.setDate(model.getDate());
                    }
                    @NonNull
                    @Override
                    public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                        return new CommentsViewHolder(LayoutInflater.from(viewGroup.getContext())
                                .inflate(R.layout.all_comment_layout, viewGroup, false));
                    }
                };
        recyclerComment.setAdapter(firebaseRecyclerAdapter);
    }
}
