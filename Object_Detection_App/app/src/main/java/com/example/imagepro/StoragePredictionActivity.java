package com.example.imagepro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.IOException;

public class StoragePredictionActivity extends AppCompatActivity {
    private Button select_image;
    private ImageView image_v;
    private objectDetectorClass objectDetectorClass;
    int SELECT_PICTURE=200;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_prediction);
        select_image=findViewById(R.id.select_image);
        image_v=findViewById(R.id.image_v);

        try{
            objectDetectorClass=new objectDetectorClass(getAssets(),"custom_model.tflite","custom_label.txt",320);
            Log.d("MainActivity","Model is successfully loaded");
        }
        catch (IOException e){
            Log.d("MainActivity","Getting some error");
            e.printStackTrace();
        }
        select_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image_chooser();
            }
        });
    }

    private void image_chooser() {
        Intent i=new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i,"Select Picture"),SELECT_PICTURE);
    }
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (resultCode==RESULT_OK){
            if (requestCode==SELECT_PICTURE){
                Uri selectedImageUri=data.getData();
                if (selectedImageUri!=null){
                    Log.d("StoragePrediction","Outpu_uri: "+selectedImageUri);
                    Bitmap bitmap=null;
                    try{
                        bitmap= MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImageUri);
                    }
                    catch (IOException e){
                        e.printStackTrace();
                    }
                    Mat selected_image=new Mat(bitmap.getHeight(),bitmap.getWidth(), CvType.CV_8UC4);
                    Utils.bitmapToMat(bitmap,selected_image);
                    selected_image=objectDetectorClass.recognizePhoto(selected_image);
                    Bitmap bitmap1=null;
                    bitmap1=Bitmap.createBitmap(selected_image.cols(),selected_image.rows(),Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(selected_image,bitmap);
                    image_v.setImageBitmap(bitmap1);
                }
            }
        }
    }
}