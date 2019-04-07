package com.example.hospitalaplications;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.otaliastudios.cameraview.Audio;
import com.otaliastudios.cameraview.BitmapCallback;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraOptions;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.FileCallback;
import com.otaliastudios.cameraview.Frame;
import com.otaliastudios.cameraview.FrameProcessor;
import com.otaliastudios.cameraview.Gesture;
import com.otaliastudios.cameraview.GestureAction;
import com.otaliastudios.cameraview.Mode;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.VideoResult;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private CameraView cameraView;
    private TextView textView;
    private BitmapCallback bitmapCallback;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textview);
        cameraView = findViewById(R.id.camera);
        imageView = findViewById(R.id.imageView);
        cameraView.setAudio(Audio.OFF);
        if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 321321);
        } else {
            cameraView.setMode(Mode.PICTURE);

            cameraView.setLifecycleOwner(this);
            cameraView.addCameraListener(cameraListener);
            //cameraView.addFrameProcessor(frameProcessor);
            cameraView.mapGesture(Gesture.TAP, GestureAction.CAPTURE);
        }
    }
    private byte[] data;
    private int rotation;
    private FrameProcessor frameProcessor = new FrameProcessor() {
        @Override
        public void process(@NonNull Frame frame) {
            data = frame.getData();
            rotation = frame.getRotation()/90;
        }
    };

    private CameraListener cameraListener = new CameraListener() {

        @Override
        public void onCameraOpened(@NonNull CameraOptions options) {
            super.onCameraOpened(options);
        }

        @Override
        public void onPictureTaken(@NonNull PictureResult result) {
            super.onPictureTaken(result);
            result.toBitmap(1000, 1000, new BitmapCallback() {
                @Override
                public void onBitmapReady(@Nullable Bitmap bitmap) {
                    FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
                    runTextRecognition(image);
                }
            });
        }

        @Override
        public void onVideoTaken(@NonNull VideoResult result) {
            super.onVideoTaken(result);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            super.onOrientationChanged(orientation);
        }
    };

    private void runTextRecognition(FirebaseVisionImage image) {
        FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();

        Task<FirebaseVisionText> results = textRecognizer.processImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                processTextRecognition(firebaseVisionText);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void processTextRecognition(FirebaseVisionText text) {

        List<FirebaseVisionText.TextBlock> textBlocks = text.getTextBlocks();
        if(textBlocks.size() == 0) {
            Toast.makeText(this, "No text found", Toast.LENGTH_SHORT).show();
        }
        for(FirebaseVisionText.TextBlock textBlock : textBlocks) {
            List<FirebaseVisionText.Line> lines = textBlock.getLines();
            for(FirebaseVisionText.Line line : lines) {
                List<FirebaseVisionText.Element> elements = line.getElements();
                textView.append(line.getText() + "\n");
            }
        }
    }


}
