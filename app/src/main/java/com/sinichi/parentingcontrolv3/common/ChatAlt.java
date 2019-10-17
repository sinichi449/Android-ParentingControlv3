package com.sinichi.parentingcontrolv3.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sinichi.parentingcontrolv3.R;
import com.sinichi.parentingcontrolv3.model.ChatModel;
import com.sinichi.parentingcontrolv3.util.Constant;

import static android.content.Context.MODE_PRIVATE;

public class ChatAlt {

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView messageTextView;
        public ImageView messageImageView;
        public TextView messengerTextView;
        public ConstraintLayout constraintLayout;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.tv_message_chat);
            messageImageView = itemView.findViewById(R.id.img_message_chat);
            constraintLayout = itemView.findViewById(R.id.constraint_item_message);
//            messengerTextView = itemView.findViewById(R.id.tv_username_chat);
        }
    }

    public void buildGoogleApiClient(Context context) {
        new GoogleApiClient.Builder(context)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
    }

    public SnapshotParser<ChatModel> parseDataFromCloud() {
        SnapshotParser<ChatModel> parser = new SnapshotParser<ChatModel>() {
            @NonNull
            @Override
            public ChatModel parseSnapshot(@NonNull DataSnapshot snapshot) {
                ChatModel friendlyMessage = snapshot.getValue(ChatModel.class);
                if (friendlyMessage != null) {
                    friendlyMessage.setId(snapshot.getKey());
                }
                return friendlyMessage;
            }
        };
        return parser;
    }

    public FirebaseRecyclerOptions<ChatModel> buildFirebaseRecyclerOption(DatabaseReference messageRef) {
        return new FirebaseRecyclerOptions.Builder<ChatModel>()
                .setQuery(messageRef, parseDataFromCloud()).build();
    }

    public FirebaseRecyclerAdapter<ChatModel, MessageViewHolder> makeFirebaseRecyclerViewAdapter(DatabaseReference messageRef, final Context context) {
         return new FirebaseRecyclerAdapter<ChatModel, MessageViewHolder>(buildFirebaseRecyclerOption(messageRef)) {
            @NonNull
            @Override
            public ChatAlt.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                return new ChatAlt.MessageViewHolder(inflater.inflate(R.layout.item_messages, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull final ChatAlt.MessageViewHolder viewHolder,
                                            int i,
                                            @NonNull ChatModel friendlyMessage) {
                // TODO: Set chat logic
                SharedPreferences sharedPreferences = context.getSharedPreferences(Constant.SHARED_PREFS, MODE_PRIVATE);
                // TODO: Inflate via view root
                ConstraintSet set = new ConstraintSet();
                ConstraintLayout layout = viewHolder.constraintLayout;
                set.clone(layout);

                if (friendlyMessage.getText() != null) {
                    viewHolder.messageTextView.setText(friendlyMessage.getText());
                    viewHolder.messageTextView.setVisibility(TextView.VISIBLE);
                    viewHolder.messageImageView.setVisibility(ImageView.GONE);

                    // Set chat bubble to be in left or to be in right
                    if (friendlyMessage.getName().equals(sharedPreferences.getString(Constant.USERNAME, "Anonymous"))) {
                        set.connect(R.id.tv_message_chat,ConstraintSet.END, viewHolder.constraintLayout.getId(),ConstraintSet.END);
                        viewHolder.messageTextView.setBackgroundResource(R.drawable.bg_outgoing_chat);
                        viewHolder.messageTextView.setTextColor(Color.parseColor("#000000"));
                        set.applyTo(layout);
                    }
                } else if (friendlyMessage.getImageUrl() != null) {
                    String imageUrl = friendlyMessage.getImageUrl();
                    if (imageUrl.startsWith("gs://")) {
                        StorageReference storageReference = FirebaseStorage.getInstance()
                                .getReferenceFromUrl(imageUrl);
                        storageReference.getDownloadUrl().addOnCompleteListener(
                                new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            String downloadUrl = task.getResult().toString();
                                            Glide.with(viewHolder.messageImageView.getContext())
                                                    .load(downloadUrl)
                                                    .into(viewHolder.messageImageView);
                                        } else {
                                            Log.w(Constant.TAG, "Getting download url was not successful.",
                                                    task.getException());
                                        }
                                    }
                                });
                    } else {
                        Glide.with(viewHolder.messageImageView.getContext())
                                .load(friendlyMessage.getImageUrl())
                                .into(viewHolder.messageImageView);
                    }
                    viewHolder.messageImageView.setVisibility(ImageView.VISIBLE);
                    viewHolder.messageTextView.setVisibility(TextView.GONE);
                }
            }
        };
    }

    public void goToLastMessagePosition(final DatabaseReference messageRef, final Context context, final LinearLayoutManager mLinearLayoutManager
    , final RecyclerView mMessageRecyclerView) {
        makeFirebaseRecyclerViewAdapter(messageRef, context).registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = makeFirebaseRecyclerViewAdapter(messageRef, context).getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        makeFirebaseRecyclerViewAdapter(messageRef, context).startListening();
    }

    public void enableSendButtonOnUserTyping(TextView mMessageEditText, final ImageView mSendButton) {
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public FirebaseUser getUserFirebaseUser() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        return auth.getCurrentUser();
    }

    public void sendMessages(final Context context, ImageView mSendButton, final TextView mMessageEditText,
                             final  String mPhotoUrl, final DatabaseReference mFirebaseDatabaseReference) {
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Send messages on click.
                SharedPreferences mSharedPrefs = context.getSharedPreferences(Constant.SHARED_PREFS, Context.MODE_PRIVATE);
                String username = mSharedPrefs.getString(Constant.USERNAME, "Anonymous");
                ChatModel friendlyMessage = new
                        ChatModel(mMessageEditText.getText().toString(),
                        username,
                        mPhotoUrl,
                        null);

                // TODO: Send to database
                mFirebaseDatabaseReference
                        .child(getUserFirebaseUser().getUid())
                        .child(Constant.MESSAGES_CHILD)
                        .push().setValue(friendlyMessage);
                mMessageEditText.setText("");
            }
        });
    }

}
