package com.example.socialmediaclone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.ImageButton;

public class CommentActivity extends AppCompatActivity {

    RecyclerView recyclerComment;
    ImageButton post;
    Toolbar toolbar;
    EditText inputComment;
    String Post_Key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        initialize();
        Post_Key = getIntent().getExtras().get("Postkey").toString();

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

    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.comment_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comment");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
}
