package com.sinichi.parentingcontrolv3.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sinichi.parentingcontrolv3.R;
import com.sinichi.parentingcontrolv3.common.ChatAlt;
import com.sinichi.parentingcontrolv3.interfaces.z;
import com.sinichi.parentingcontrolv3.model.ChatModel;
import com.sinichi.parentingcontrolv3.util.Constant;
import com.sinichi.parentingcontrolv3.util.SetAppearance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/*
    If there are any missing data retrieve from the cloud, check in
    mDatabaseReference.child.
    ----------------------------------------------------------------
    Take A look on mDatabaseReference.child(mFirebaseUser.getUid()).
    child(MESSAGES_REF)
    ----------------------------------------------------------------
    Don't forget to get username from SharedPreference
*/

// TODO: Check for stability

public class ChatActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, z {
    private ChatAlt chatAlt;
    private static final String TAG = "Results";
    private static final int REQUEST_IMAGE = 2;
    private String mUsername;
    private String mPhotoUrl;
    private ImageView mSendButton;
    private RecyclerView mMessageRecyclerView;
    private EditText mMessageEditText;
    private LinearLayoutManager mLinearLayoutManager;
    private ImageView mAddMessageImageView;
    private static final String LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif";
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;
    private BottomNavigationView mBottomNavigation;
    private DatabaseReference messageRef;
    FirebaseRecyclerAdapter<ChatModel, ChatAlt.MessageViewHolder> mFirebaseAdapter;

    @Override
    public void initComponents() {
        mBottomNavigation = findViewById(R.id.bottom_navigation);
        chatAlt = new ChatAlt();
        // Set LayoutManager
        mMessageRecyclerView = findViewById(R.id.recyclerViewChat);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageEditText = findViewById(R.id.messageEditText);
        mAddMessageImageView = findViewById(R.id.addMessageImageView);
        messageRef = mFirebaseDatabaseReference
                .child(chatAlt.getUserFirebaseUser().getUid())
                .child(Constant.MESSAGES_CHILD);
        mFirebaseAdapter = chatAlt.makeFirebaseRecyclerViewAdapter(messageRef, this);
        chatAlt.getUserFirebaseUser();
    }

    @Override
    public void setBottomNavigationAction(Context context, BottomNavigationView mBottomNav) {
        SetAppearance.onBottomNavigationClick(this, this, mBottomNavigation, R.id.menu_chat);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initComponents();
        setBottomNavigationAction(this, mBottomNavigation);

        // INITIALIZE FIREBASE
        // MUST BE DECLARED
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

        // Building GoogleApiClient
        chatAlt.buildGoogleApiClient(this);

        // TODO: Parse data from cloud to the ChatModel.class
        chatAlt.parseDataFromCloud();

        // TODO: Add Messages' Database child over the User Uid
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        // TODO: Building communication between local data and cloud data
        chatAlt.buildFirebaseRecyclerOption(messageRef);

        // TODO: FirebaseRecyclerView
        mMessageRecyclerView.setAdapter(chatAlt.makeFirebaseRecyclerViewAdapter(messageRef, this));

        // TODO: On activity launched, go to the last message position
        chatAlt.goToLastMessagePosition(messageRef, this, mLinearLayoutManager, mMessageRecyclerView);

        // TODO: Enable send button when user typing
        chatAlt.enableSendButtonOnUserTyping(mMessageEditText, mSendButton);

        mSendButton = findViewById(R.id.sendButton);
        // TODO: Send button behavior
        chatAlt.sendMessages(this, mSendButton, mMessageEditText, mPhotoUrl, mFirebaseDatabaseReference);

        // TODO: When user wants to send images
        mAddMessageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Select image for image message on click.
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_IMAGE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    final Uri uri = data.getData();
                    Log.d(TAG, "Uri: " + uri.toString());

                    ChatModel tempMessage = new ChatModel(null, mUsername, mPhotoUrl,
                            LOADING_IMAGE_URL);
                    mFirebaseDatabaseReference.child(mFirebaseUser.getUid()).child(Constant.MESSAGES_CHILD).push()
                            .setValue(tempMessage, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError,
                                                       DatabaseReference databaseReference) {
                                    if (databaseError == null) {
                                        String key = databaseReference.getKey();
                                        StorageReference storageReference =
                                                FirebaseStorage.getInstance()
                                                        .getReference(mFirebaseUser.getUid())
                                                        .child(key)
                                                        .child(uri.getLastPathSegment());

                                        putImageInStorage(storageReference, uri, key);
                                    } else {
                                        Log.w(TAG, "Unable to write message to database.",
                                                databaseError.toException());
                                    }
                                }
                            });
                }
            }
        }
    }

    private void putImageInStorage(StorageReference storageReference, Uri uri, final String key) {
        storageReference.putFile(uri).addOnCompleteListener(ChatActivity.this,
                new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            task.getResult().getMetadata().getReference().getDownloadUrl()
                                    .addOnCompleteListener(ChatActivity.this,
                                            new OnCompleteListener<Uri>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Uri> task) {
                                                    if (task.isSuccessful()) {
                                                        ChatModel friendlyMessage =
                                                                new ChatModel(null, mUsername, mPhotoUrl,
                                                                        task.getResult().toString());
                                                        mFirebaseDatabaseReference.child(Constant.MESSAGES_CHILD).child(key)
                                                                .setValue(friendlyMessage);
                                                    }
                                                }
                                            });
                        } else {
                            Log.w(TAG, "Image upload task was not successful.",
                                    task.getException());
                        }
                    }
                });
    }


    @Override
    public void onResume() {
        mFirebaseAdapter.startListening();
        super.onResume();
    }

    @Override
    public void onPause() {
        mFirebaseAdapter.stopListening();
        super.onPause();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(ChatActivity.this, "Google Play Service Error!", Toast.LENGTH_SHORT).show();
    }
}
