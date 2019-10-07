package com.sinichi.parentingcontrolv3.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sinichi.parentingcontrolv3.R;
import com.sinichi.parentingcontrolv3.model.ChatModel;
import com.sinichi.parentingcontrolv3.util.Constant;

/*
    If there are any missing data retrieve from the cloud, check in
    mDatabaseReference.child.
    ----------------------------------------------------------------
    Take a look on mDatabaseReference.child(mFirebaseUser.getUid()).
    child(MESSAGES_REF)
    ----------------------------------------------------------------
    Don't forget to get username from sharedpreference
*/


public class ChatActivity extends AppCompatActivity {

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        ImageView messageImageView;
        TextView messengerTextView;
        CircleImageView messengerImageView;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.tv_message_chat);
            messageImageView = itemView.findViewById(R.id.img_message_chat);
            messengerTextView = itemView.findViewById(R.id.tv_username_chat);
            messengerImageView = itemView.findViewById(R.id.imgUserPhoto);
        }
    }

    private static final String TAG = "Results";
    private static final int REQUEST_IMAGE = 2;
    public static final String ANONYMOUS = "anonymous";
    private String mUsername;
    private String mPhotoUrl;
    private SharedPreferences mSharedPreference;
    private GoogleApiClient mGoogleApiClient;
    private Button mSendButton;
    private RecyclerView mMessageRecyclerView;
    private EditText mMessageEditText;
    private LinearLayoutManager mLinearLayoutManager;
    private ImageView mAddMessageImageView;
    private static final String LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif";

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<ChatModel, MessageViewHolder> mFirebaseAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // INITIALIZE FIREBASE
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
        } else { // TODO: Get username from SharedPreference
            mUsername = mFirebaseUser.getDisplayName();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }

        // Building GoogleApiClient
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        // Set LayoutManager
        mMessageRecyclerView = findViewById(R.id.recyclerViewChat);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);

        // Parse data from cloud to the ChatModel.class
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
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

        // Add messages Database Child over the User Uid
        DatabaseReference messageRef = mFirebaseDatabaseReference
                .child(mFirebaseUser.getUid())
                .child(Constant.MESSAGES_CHILD);

        // Building communication between local data and cloud data
        FirebaseRecyclerOptions<ChatModel> options =
                new FirebaseRecyclerOptions.Builder<ChatModel>()
                        .setQuery(messageRef, parser).build();


        mFirebaseAdapter = new FirebaseRecyclerAdapter<ChatModel, MessageViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MessageViewHolder messageViewHolder, int i, @NonNull ChatModel chatModel) {

            }

            @NonNull
            @Override
            public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                return new MessageViewHolder(inflater.inflate(R.layout.item_messages, parent, false));
            }
        };
    }
}
