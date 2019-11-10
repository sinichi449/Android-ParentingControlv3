package com.sinichi.parentingcontrolv3.activity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
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
import com.pubnub.api.PubNub;
import com.sinichi.parentingcontrolv3.R;
import com.sinichi.parentingcontrolv3.common.ChatAlt;
import com.sinichi.parentingcontrolv3.common.ChatViewHolder;
import com.sinichi.parentingcontrolv3.interfaces.z;
import com.sinichi.parentingcontrolv3.model.ChatModel;
import com.sinichi.parentingcontrolv3.util.Constant;
import com.sinichi.parentingcontrolv3.util.SetAppearance;

import java.util.Calendar;


public class ChatActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, z {
    private ChatAlt chatAlt;
    private static final String TAG = "Results";
    private static final int REQUEST_IMAGE = 2;
    private String mUsername;
    private String mPhotoUrl, time;
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
    FirebaseRecyclerAdapter<ChatModel, ChatViewHolder.MessageViewHolder> mFirebaseAdapter;
    private ProgressBar progressBar;
    public static PubNub pubnub;

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
        mSendButton = findViewById(R.id.btn_send);
        progressBar = findViewById(R.id.progress_circular);
    }

    @Override
    public void setBottomNavigationAction(Context context, BottomNavigationView mBottomNav) {
        SetAppearance.onBottomNavigationClick(this, this, mBottomNavigation, R.id.menu_chat);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setActionBarColor();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            SetAppearance.setStatusBarColor(this, R.color.colorBlack);
        }
        SetAppearance.hideNavigationBar(this);
        initComponents();
        setBottomNavigationAction(this, mBottomNavigation);

        // INITIALIZE FIREBASE
        // MUST BE DECLARED
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

        // Building GoogleApiClient
        chatAlt.buildGoogleApiClient(this);

        if (chatAlt.parseDataFromCloud() != null) {
            progressBar.setVisibility(View.GONE);
            Log.e(Constant.TAG, "DataSiswandi found in database");
        } else {
            Log.e(Constant.TAG, "DataSiswandi not found in database");
        }

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        messageRef = mFirebaseDatabaseReference
                .child(mFirebaseUser.getUid())
                .child(Constant.MESSAGES_CHILD);

        chatAlt.buildFirebaseRecyclerOption(messageRef);

        mFirebaseAdapter = chatAlt.makeFirebaseRecyclerViewAdapter(messageRef, this);
        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        mMessageRecyclerView.setAdapter(mFirebaseAdapter);

        chatAlt.enableSendButtonOnUserTyping(mMessageEditText, mSendButton);

        chatAlt.sendMessages(this, mSendButton, mMessageEditText,
                mPhotoUrl, mFirebaseDatabaseReference);

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

        createChannel();
//        Intent intent = new Intent(this, FirebaseMessagingService.class);
//        startService(intent);

    }


    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel chan1 = new NotificationChannel(
                    "default",
                    "default",
                    NotificationManager.IMPORTANCE_NONE);
            chan1.setLightColor(Color.TRANSPARENT);
            chan1.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
            notificationManager.createNotificationChannel(chan1);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    final Uri uri = data.getData();
                    Log.d(TAG, "Uri: " + uri.toString());
                    Calendar calendar = Calendar.getInstance();
                    int minute = calendar.get(Calendar.MINUTE);
                    String minuteStr;
                    if (minute < 10) {
                        minuteStr = "0" + minute;
                    } else {
                        minuteStr = String.valueOf(minute);
                    }
                    time = calendar.get(Calendar.HOUR_OF_DAY) + ":" + minuteStr;
                    ChatModel tempMessage = new ChatModel(null, mUsername, mPhotoUrl,
                            LOADING_IMAGE_URL, time);
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
                                                                        task.getResult().toString(), time);
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
        SetAppearance.hideNavigationBar(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(ChatActivity.this, "Google Play Service Error!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        SetAppearance.hideNavigationBar(this);
    }

    private void setActionBarColor() {
        getSupportActionBar().setTitle("Chat");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorSkyBlue)));
    }

}
