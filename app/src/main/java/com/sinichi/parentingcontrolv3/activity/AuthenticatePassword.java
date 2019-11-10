package com.sinichi.parentingcontrolv3.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinichi.parentingcontrolv3.R;
import com.sinichi.parentingcontrolv3.model.UserModel;
import com.sinichi.parentingcontrolv3.util.Constant;

import java.util.ArrayList;
import java.util.List;

public class AuthenticatePassword extends AppCompatActivity {
    private List<UserModel> userModels;
    private UserModel model;
    private Context context;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticate_password);
        context = AuthenticatePassword.this;

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Sedang menyiapkan");
        progressDialog.setCancelable(false);
        progressDialog.show();
        isAuthenticated();
        hasChildName();
        listenPasswordData();
    }

    private void isAuthenticated() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constant.SHARED_PREFS, MODE_PRIVATE);
        boolean isAuthenticated = sharedPreferences.getBoolean(Constant.IS_AUTHENTICATED, false);
        if (isAuthenticated) {
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        }
    }

    private void hasChildName() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference userRef = databaseReference.child(firebaseUser.getUid());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(Constant.PASSWORD_CHILD)) {
                    userRef.child(Constant.PASSWORD_CHILD).push().setValue(new UserModel("Dummy", "dummy"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void listenPasswordData() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference passwordRef = databaseReference.child(firebaseUser.getUid()).child(Constant.PASSWORD_CHILD);
        passwordRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userModels = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    model = snapshot.getValue(UserModel.class);
                    if (model != null) {
                        progressDialog.dismiss();
                    }
                    userModels.add(model);
                }
                SharedPreferences sharedPreferences = getSharedPreferences(Constant.SHARED_PREFS, MODE_PRIVATE);
                String currentUsername = sharedPreferences.getString(Constant.USERNAME, null);
                for (UserModel userModel : userModels) {
                    boolean isDataTersediaDiCloud = currentUsername.equals(userModel.getUsername());
                    if (isDataTersediaDiCloud) {
                        Toast.makeText(context, "Data sudah tersedia, silakan masukkan password",
                                Toast.LENGTH_SHORT).show();
                        launchPasswordDialog(userModel);
                    } else {
                        Toast.makeText(context, "Buat password untuk akun Anda",
                                Toast.LENGTH_SHORT).show();
                        launchRegisterDialog();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void launchRegisterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = getLayoutInflater().inflate(R.layout.layout_buat_password, null);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        final EditText edtNewPassword = view.findViewById(R.id.edt_new_password);
        final EditText edtKonfirmasiPassword = view.findViewById(R.id.edt_konfirmasi_password);
        Button btnRegister = view.findViewById(R.id.btn_register);
        final ImageView imgPasswordVisibilityNew = view.findViewById(R.id.img_password_visibility_new_password);
        final ImageView imgPasswordVisiblityKonfirmasi = view.findViewById(R.id.img_password_visibility_konfirmasi);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences(Constant.SHARED_PREFS, MODE_PRIVATE);
                String username = sharedPreferences.getString(Constant.USERNAME, null);
                String password = edtNewPassword.getText().toString();
                String konfirmasiPassword = edtKonfirmasiPassword.getText().toString();
                boolean passwordSudahSama = password.equals(konfirmasiPassword);
                boolean passwordTidakKosong = !password.equals("") && !konfirmasiPassword.equals("");
                if (passwordSudahSama && passwordTidakKosong) {
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference passwordRef = databaseReference.child(firebaseUser.getUid()).child(Constant.PASSWORD_CHILD);
                    UserModel userModel = new UserModel(username, password);
                    passwordRef.push().setValue(userModel);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(Constant.IS_AUTHENTICATED, true);
                    editor.apply();
                    alertDialog.dismiss();
                    Toast.makeText(context, "Membuat user baru berhasil, selamat datang di Parenting Control",
                            Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                } else if (!passwordSudahSama) {
                    edtKonfirmasiPassword.setText("");
                    edtNewPassword.setText("");
                    Toast.makeText(context, "Password tidak cocok, buat password baru dan tulis dua kali pada form yang disediakan",
                            Toast.LENGTH_LONG).show();
                } else if (!passwordTidakKosong) {
                    Toast.makeText(context, "Mohon isi form yang kosong",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        imgPasswordVisibilityNew.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    edtNewPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    imgPasswordVisibilityNew.setImageResource(R.drawable.ic_visibility_off_black_24dp);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    edtNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    imgPasswordVisibilityNew.setImageResource(R.drawable.ic_visibility_black_24dp);
                }
                return true;
            }
        });

        imgPasswordVisiblityKonfirmasi.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    edtKonfirmasiPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    imgPasswordVisiblityKonfirmasi.setImageResource(R.drawable.ic_visibility_off_black_24dp);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    edtKonfirmasiPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    imgPasswordVisiblityKonfirmasi.setImageResource(R.drawable.ic_visibility_black_24dp);
                }
                return true;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void launchPasswordDialog(final UserModel userModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = getLayoutInflater().inflate(R.layout.layout_masukkan_password, null);
        builder.setView(view);
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        TextView tvUserSaatIni = view.findViewById(R.id.tv_user_saat_ini);
        SharedPreferences sharedPreferences = getSharedPreferences(Constant.SHARED_PREFS, MODE_PRIVATE);
        String userSaatIni = sharedPreferences.getString(Constant.USERNAME, null);
        tvUserSaatIni.setText(userSaatIni);

        final EditText edtPassword = view.findViewById(R.id.edt_password);
        Button btnLogin = view.findViewById(R.id.btn_login);
        final ImageView imgPasswordVisibility = view.findViewById(R.id.img_password_visibility);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = edtPassword.getText().toString();
                if (userModel.getPassword().equals(password)) {
                    // TODO: Masuk main activity
                    Toast.makeText(context, "Login berhasil, selamat datang di Parenting Control",
                            Toast.LENGTH_LONG).show();
                    alertDialog.dismiss();
                    SharedPreferences sharedPreferences = getSharedPreferences(Constant.SHARED_PREFS, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(Constant.IS_AUTHENTICATED, true);
                    editor.apply();
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                } else {
                    edtPassword.setText("");
                    Toast.makeText(context, "Password salah, silakan coba lagi",
                            Toast.LENGTH_SHORT).show();

                }
            }
        });

        imgPasswordVisibility.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    edtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    imgPasswordVisibility.setImageResource(R.drawable.ic_visibility_off_black_24dp);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    imgPasswordVisibility.setImageResource(R.drawable.ic_visibility_black_24dp);
                }
                return true;
            }
        });
    }
}
