package com.example.aadhaartextextractor;

import static com.example.aadhaartextextractor.Utils.CameraUtils.getBitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aadhaartextextractor.PostProcessing.AadhaarProcessing;

import com.example.aadhaartextextractor.Utils.CameraUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class aadhaarActivity extends AppCompatActivity {

    private ImageView frontImageView,backImageView;
    String mCurrentPhotoPath,mCurrentPhotoPath2;
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_TAKE_PHOTO_2 = 2;
    private Bitmap mImageBitmap,mImageBitmap2;

    private EditText name,dob,gender,pin,addressLine,addressLine2,fatherName,aadhaar;
    private Button reset;


    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_aadhaar);

        frontImageView = findViewById(R.id.frontImageView);
        backImageView = findViewById(R.id.backImageView);
        name = findViewById(R.id.name);
        dob = findViewById(R.id.dob);
        gender = findViewById(R.id.gender);
        pin = findViewById(R.id.pin);
        addressLine = findViewById(R.id.addressLine);
        fatherName = findViewById(R.id.fatherName);
        addressLine2=findViewById(R.id.addressLine2);
        aadhaar=findViewById(R.id.aadhaar);
        reset = findViewById(R.id.reset);
            Button ocr = findViewById(R.id.ocr);

        ocr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extractInfo();
            }
        });


    }






    public void takePicture(View view) {

        Intent takePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePic.resolveActivity(getPackageManager())!=null){
            File photoFile = null;
            try{
                photoFile = CameraUtils.createImagefile(this);
                mCurrentPhotoPath = photoFile.getAbsolutePath();
            }catch (IOException ex){
                Toast.makeText(this,"Error Creating File",Toast.LENGTH_SHORT).show();
            }
            if(photoFile != null){
                Uri photoURL = FileProvider.getUriForFile(this,
                        "com.example.aadhaartextextractor.fileprovider",
                        photoFile);

                takePic.putExtra(MediaStore.EXTRA_OUTPUT,photoURL);
                startActivityForResult(takePic,REQUEST_TAKE_PHOTO);
            }
        }

    }

    public void takeBackPicture(View view) {
        Intent takePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePic.resolveActivity(getPackageManager())!=null){
            File photoFile = null;
            try{
                photoFile = CameraUtils.createImagefile(this);
                mCurrentPhotoPath2 = photoFile.getAbsolutePath();
            }catch (IOException ex){
                Toast.makeText(this,"Error Creating File",Toast.LENGTH_SHORT).show();
            }
            if(photoFile != null){
                Uri photoURL = FileProvider.getUriForFile(this,
                        "com.example.aadhaartextextractor.fileprovider",
                        photoFile);
                takePic.putExtra(MediaStore.EXTRA_OUTPUT,photoURL);
                startActivityForResult(takePic,REQUEST_TAKE_PHOTO_2);
            }
        }
    }


    public void extractInfo() {

        if(mImageBitmap != null && mImageBitmap2 != null){
            FirebaseVisionTextRecognizer recognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
            FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(mImageBitmap);
            recognizer.processImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                @Override
                public void onSuccess(FirebaseVisionText firebaseVisionText) {
                    HashMap<String,String> dataMap = new AadhaarProcessing()
                            .processExtractTextForFrontPic(firebaseVisionText, getApplicationContext());
                    presentFrontOutput(dataMap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show();
                }
            });



            FirebaseVisionImage image2 = FirebaseVisionImage.fromBitmap(mImageBitmap2);
            recognizer.processImage(image2).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                @Override
                public void onSuccess(FirebaseVisionText firebaseVisionText) {
                    HashMap<String,String> dataMap = new AadhaarProcessing()
                            .processExtractedTextForBackPic(firebaseVisionText, getApplicationContext());
                    presentBackOutput(dataMap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }else{
            Toast.makeText(this,"Please Take Both Front and Back Image First",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK){
            mImageBitmap = CameraUtils.getBitmap(mCurrentPhotoPath);
            frontImageView.setImageBitmap(mImageBitmap);
            reset.setVisibility(View.VISIBLE);

        } else if (requestCode == REQUEST_TAKE_PHOTO_2 && resultCode == RESULT_OK) {
            mImageBitmap2 = CameraUtils.getBitmap(mCurrentPhotoPath2);
            backImageView.setImageBitmap(mImageBitmap2);
            reset.setVisibility(View.VISIBLE);

        }
    }

    public void reset(View view) {
        mImageBitmap = null;
        mImageBitmap2 = null;
        frontImageView.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ap));
        backImageView.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ap));
        name.getText().clear();
        dob.getText().clear();
        gender.getText().clear();
        pin.getText().clear();
        addressLine.getText().clear();
        fatherName.getText().clear();
        addressLine2.getText().clear();
        aadhaar.getText().clear();

    }

    private void presentFrontOutput(HashMap<String,String> datamap){
        if(datamap != null){
            aadhaar.setText(datamap.get("aadhaar"), TextView.BufferType.EDITABLE);
            gender.setText(datamap.get("gender"), TextView.BufferType.EDITABLE);
            fatherName.setText(datamap.get("fatherName"), TextView.BufferType.EDITABLE);
            dob.setText(datamap.get("yob"), TextView.BufferType.EDITABLE);
            name.setText(datamap.get("name"), TextView.BufferType.EDITABLE);
        }
    }

    private void presentBackOutput(HashMap<String,String> datamap){
        if(datamap != null){
            addressLine.setText(datamap.get("addressLine"), TextView.BufferType.EDITABLE);
            addressLine2.setText(datamap.get("addressLine2"), TextView.BufferType.EDITABLE);
            pin.setText(datamap.get("pin"), TextView.BufferType.EDITABLE);

        }
    }
}