package com.finalproject.it.travelfriend.Utility;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.finalproject.it.travelfriend.Model.GuideData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Firebase_guide_method {
    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    DatabaseReference mReference;
    Context mContext;
    String guideID;

    public Firebase_guide_method(Context context) {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();
        mContext = context;
    }

    public void register_new_email(String strEmail, String strPassword) {
        mAuth.createUserWithEmailAndPassword(strEmail, strPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(mContext, "Registration Failed...", Toast.LENGTH_SHORT).show();
                        } else {
                            guideID = mAuth.getCurrentUser().getUid();
                        }
                    }
                });
    }

    public void send_new_guide_data(String email, String name, String surname, String phone, String password, String gender, String profile_image, String certificate_image, String license_image, String citizen_image, String province, String district) {

        GuideData guideData = new GuideData(email, name, surname, phone, password, gender, profile_image, certificate_image, license_image, citizen_image, province, district);
        mReference.child("Guides").child(guideID).setValue(guideData);
    }
}