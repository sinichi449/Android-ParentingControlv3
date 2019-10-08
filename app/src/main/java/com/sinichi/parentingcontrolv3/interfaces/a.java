package com.sinichi.parentingcontrolv3.interfaces;

import com.google.firebase.auth.FirebaseUser;

public interface a {

    FirebaseUser getUser();
    void initilizeFirebaseAuth();
    void firebaseAuthWithGoogle();

}
