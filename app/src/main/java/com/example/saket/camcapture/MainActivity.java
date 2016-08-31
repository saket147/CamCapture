package com.example.saket.camcapture;


import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.Manifest;


public class MainActivity extends AppCompatActivity
{
    Uri uriSavedImage,mfinal;
    ContentValues values;


    Button btnCamera;
    private static final int PICK_FROM_CAMERA = 0;
    private static final int VIDEO_CAPTURE = 102;
    private int STORAGE_PERMISSION_CODE = 23;
    private int RECORD_AUDIO_CODE = 23;
    private int CAPTURE_VIDEO_OUTPUT = 23;
    private CompressImage compressImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCamera=(Button) findViewById(R.id.btncamera);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                compressImage = new CompressImage();
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i, PICK_FROM_CAMERA);

//
//                    Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    File imagesFolder = new File(Environment.getExternalStorageDirectory(), "CamCapture");
//                    imagesFolder.mkdirs(); // <----
//                    File image = new File(imagesFolder, "image_001.jpeg");
//                    uriSavedImage = Uri.fromFile(image);
//
//
//                imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, mfinal);
//                    startActivityForResult(imageIntent, PICK_FROM_CAMERA);

            }
        });



        btnCamera.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                if(isWriteStorageAllowed()||isCaptureVideoOutput()||isRecordAudioCode()) {
                    File mediaFile =
                            new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                                    + "/myvideo.mp4");

                    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

                    Uri videoUri = Uri.fromFile(mediaFile);

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
                    intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                    intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);
                    intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 10380902L);

                    startActivityForResult(intent, VIDEO_CAPTURE);
                }
               // requestStoragePermission();
                //requestCaptureVideoOutput();
                //requestRecordAudio();

                return true;
            }
        });
    }
    private boolean isWriteStorageAllowed() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        return false;
    }

    private boolean isRecordAudioCode() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO);

        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        return false;
    }
    private boolean isCaptureVideoOutput() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAPTURE_VIDEO_OUTPUT);

        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        return false;
    }

    private void requestStoragePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)){

        }
        ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                STORAGE_PERMISSION_CODE);
    }
    private void requestRecordAudio(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.RECORD_AUDIO)){

        }


        ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.RECORD_AUDIO},
                STORAGE_PERMISSION_CODE);
    }
    private void requestCaptureVideoOutput(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAPTURE_VIDEO_OUTPUT)){

        }

        ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.CAPTURE_VIDEO_OUTPUT},
                STORAGE_PERMISSION_CODE);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == STORAGE_PERMISSION_CODE){

            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                Toast.makeText(this,"Permission granted now you can read the storage",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this,"Oops you just denied STORAGE_PERMISSION_CODE the permission",Toast.LENGTH_LONG).show();
            }
        }
        if(requestCode == CAPTURE_VIDEO_OUTPUT){

            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                Toast.makeText(this,"Permission granted now you can capture video",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this,"Oops you just denied the CAPTURE_VIDEO_OUTPUT permission",Toast.LENGTH_LONG).show();
            }
        }
        if(requestCode == RECORD_AUDIO_CODE){

            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                Toast.makeText(this,"Permission granted now you can record audio",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this,"Oops you just denied the RECORD_AUDIO_CODE permission",Toast.LENGTH_LONG).show();
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VIDEO_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Video saved to:\n" +
                            data.getData(), Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Video recording cancelled.",
                            Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to record video",
                            Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == PICK_FROM_CAMERA) {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");

               Uri selectedImageUri = getImageUri(getApplicationContext(), photo);
                    Uri newuri = Uri.parse("file://"+compressImage.compressImage(selectedImageUri.toString(), this));
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), newuri);
                createDirectoryAndSaveFile(bitmap,"hellobitch");
                Toast.makeText(MainActivity.this,"Create and save dire",Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    private void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {

        File direct = new File(Environment.getExternalStorageDirectory() + "/Directory");
        if (!direct.exists()) {
            File wallpaperDirectory = new File("/sdcard/DirName/");
            wallpaperDirectory.mkdirs();
        }
        File file = new File(new File("/sdcard/DirName/"), fileName);
        if (file.exists()) {
            file.delete();
        }
        try {

            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}


