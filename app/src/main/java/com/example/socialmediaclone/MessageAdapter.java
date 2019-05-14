package com.example.socialmediaclone;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> userMessageList;
    private FirebaseAuth mAuth;
    private DatabaseReference userMessageRefs;

    public MessageAdapter (List<Message> userMessageList)
    {
        this.userMessageList = userMessageList;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView SenderMessageText,ReceiverMessageText;
        public CircleImageView receiverProfileImage;
        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            SenderMessageText = (TextView) itemView.findViewById(R.id.sender_chat);
            ReceiverMessageText = (TextView) itemView.findViewById(R.id.receiver_chat);
            receiverProfileImage = (CircleImageView) itemView.findViewById(R.id.chat_profile_image);

        }

    }
    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View V = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.chat_layout,viewGroup,false);

        mAuth = FirebaseAuth.getInstance();

        return new MessageViewHolder(V);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder messageViewHolder, int i) {

        String messageSenderID = mAuth.getCurrentUser().getUid();
        Message message = userMessageList.get(i);

        String fromUserID = message.getFrom();
        String fromMessageType = message.getType();

        userMessageRefs = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(fromUserID);

        userMessageRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    String image = dataSnapshot.child("profileimage").getValue().toString();

                    Picasso.with(messageViewHolder.receiverProfileImage.getContext()).load(image)
                            .placeholder(R.drawable.profile).into(messageViewHolder.receiverProfileImage);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(fromMessageType.equals("text"))
        {
            messageViewHolder.ReceiverMessageText.setVisibility(View.INVISIBLE);
            messageViewHolder.SenderMessageText.setVisibility(View.INVISIBLE);

            if(fromUserID.equals(messageSenderID))
            {
                messageViewHolder.SenderMessageText.setBackgroundResource(R.drawable.sender_message_bg);
                messageViewHolder.SenderMessageText.setTextColor(Color.WHITE);
                messageViewHolder.SenderMessageText.setGravity(Gravity.LEFT);
                messageViewHolder.SenderMessageText.setText(message.getMessage());
            }
            else
            {
                messageViewHolder.SenderMessageText.setVisibility(View.INVISIBLE);
                messageViewHolder.ReceiverMessageText.setVisibility(View.VISIBLE);
                messageViewHolder.receiverProfileImage.setVisibility(View.VISIBLE);

                messageViewHolder.ReceiverMessageText.setBackgroundResource(R.drawable.sender_message_bg);
                messageViewHolder.ReceiverMessageText.setTextColor(Color.WHITE);
                messageViewHolder.ReceiverMessageText.setGravity(Gravity.LEFT);
                messageViewHolder.ReceiverMessageText.setText(message.getMessage());
            }
        }
    }

    @Override
    public int getItemCount() {
        return userMessageList.size();
    }

}
