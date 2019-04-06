package com.example.hospitalaplications.text_detection;

import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.text.FirebaseVisionText;

public class TextRecognition {

    private FirebaseVisionImage image;
    private FirebaseVisionText text;

    public TextRecognition(byte[] data, int rotation) {
        FirebaseVisionImageMetadata.Builder builder = new FirebaseVisionImageMetadata.Builder();
        builder.setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21);

        builder.setRotation(rotation);
        builder.setWidth(480);
        builder.setHeight(360);

        image = FirebaseVisionImage.fromByteArray(data, builder.build());
    }

    public FirebaseVisionImage getImage() {
        return image;
    }
}
