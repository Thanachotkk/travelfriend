package com.finalproject.it.travelfriend.User.RegisterPackage;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.finalproject.it.travelfriend.MainUserActivity;
import com.finalproject.it.travelfriend.Model.BookingData;
import com.finalproject.it.travelfriend.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jaeger.library.StatusBarUtil;
import com.squareup.picasso.Picasso;

public class RequestPackageStepTwo extends AppCompatActivity {
    Toolbar toolbar;
    TextView txtBank,txtNumberBank,txtTotalPrice;
    String strBank,strBankNumber,strTotalMoney,strBookingId,strPackageID;
    ImageView imgMoneyTransferSlip;
    private static final int request_Code_IMG_Money_Transfer_Slip = 11;
    FirebaseDatabase mDatabase;
    DatabaseReference mReferenceBooking,mReferencePackage;
    StorageReference mStorage;
    Uri IMG_Money_Transfer_Slip_Uri;
    Button btnRequestPackage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_package_step_two);
        toolbar = findViewById(R.id.app_bar);
        toolbar.setTitleTextAppearance(this, R.style.FontForActionBar);
        txtBank = findViewById(R.id.txt_bank);
        txtNumberBank = findViewById(R.id.txt_number_bank);
        txtTotalPrice = findViewById(R.id.txt_total_price);
        imgMoneyTransferSlip = findViewById(R.id.img_money_tranfer_slip);
        btnRequestPackage = findViewById(R.id.btn_request_package);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back_app_bar));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        StatusBarUtil.setColor(this,getResources().getColor(R.color.yellow));

        mDatabase = FirebaseDatabase.getInstance();
        mReferenceBooking = mDatabase.getReference().child("Booking");
        mReferencePackage = mDatabase.getReference().child("Packages");
        mStorage = FirebaseStorage.getInstance().getReference();
        strBookingId = getIntent().getExtras().getString("BookingId");
        bindData();

        imgMoneyTransferSlip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("image/*");
                startActivityForResult(intent,request_Code_IMG_Money_Transfer_Slip);
            }
        });

        btnRequestPackage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentRequestPackageStepTwo = new Intent(RequestPackageStepTwo.this,MainUserActivity.class);
                select_image();
                startActivity(intentRequestPackageStepTwo);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == request_Code_IMG_Money_Transfer_Slip && resultCode == RESULT_OK
                && data != null &&data.getData() != null){
            IMG_Money_Transfer_Slip_Uri = data.getData();
            Picasso.with(this).load(IMG_Money_Transfer_Slip_Uri).into(imgMoneyTransferSlip);
        }
    }

    private void bindData() {
        mReferenceBooking.child(strBookingId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                strPackageID = dataSnapshot.child("packageId").getValue(String.class);
                strTotalMoney = dataSnapshot.child("booking_total_price").getValue(String.class);

                mReferencePackage.child(strPackageID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        strBank = dataSnapshot.child("bank").getValue(String.class);
                        strBankNumber = dataSnapshot.child("bank_number").getValue(String.class);

                        txtBank.setText(strBank);
                        txtNumberBank.setText(strBankNumber);
                        txtTotalPrice.setText(strTotalMoney+ " THB");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void select_image(){
        if (IMG_Money_Transfer_Slip_Uri != null){
            StorageReference imageMTSPath = mStorage.child("Booking").child(strBookingId).child("Money_Transfer_Slip.jpg");
            imageMTSPath.putFile(IMG_Money_Transfer_Slip_Uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful());
                    Uri downloadUrlMTS = urlTask.getResult();
                    final String strMTSimage = String.valueOf(downloadUrlMTS);
                    mReferenceBooking.child(strBookingId).child("booking_money_transfer_slip").setValue(strMTSimage);
                    mReferenceBooking.child(strBookingId).child("request_status").setValue("wait_check_payment");
                }
            });
        }
    }

}