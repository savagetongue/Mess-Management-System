package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Raise_complaint extends AppCompatActivity {

    private static final int REQUEST_CODE_IMAGE = 101;
    private EditText editTextComplaint;
    private Button btnAttachImage, btnSubmitComplaint;
    private ImageView imageViewAttached;
    private Bitmap selectedImageBitmap;

    private FirebaseFirestore db;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.raise_complaint);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        editTextComplaint = findViewById(R.id.editTextComplaint);
        btnAttachImage = findViewById(R.id.btnAttachImage);
        btnSubmitComplaint = findViewById(R.id.btnSubmitComplaint);
        imageViewAttached = findViewById(R.id.imageViewAttached);

        btnAttachImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the gallery to select an image
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, REQUEST_CODE_IMAGE);
            }
        });

        btnSubmitComplaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String complaintText = editTextComplaint.getText().toString();

                // Check if selectedImageBitmap is not null
                if (selectedImageBitmap != null) {
                    // Upload the image to Firestore Storage and get the download URL
                    uploadImageToFirestoreStorage(complaintText);
                } else {
                    // Save the complaint details to Firestore without an image
                    saveComplaintToFirestore(complaintText, null);
                }

                // Display a toast message (optional)
                Toast.makeText(Raise_complaint.this, "Complaint submitted!", Toast.LENGTH_SHORT).show();

                // Optionally, you can clear the editText and imageViewAttached for a new complaint
                editTextComplaint.getText().clear();
                imageViewAttached.setVisibility(View.GONE);
            }
        });
    }

    private void uploadImageToFirestoreStorage(final String complaintText) {
        // Convert the image to bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos); // Adjust compression quality here
        byte[] imageData = baos.toByteArray();

        // Create a unique filename for the image
        String filename = "complaint_" + System.currentTimeMillis() + ".jpg";

        // Reference to the Firebase Storage
        StorageReference storageRef = storage.getReference().child("complaint_images/" + filename);

        // Upload the image to Firebase Storage
        storageRef.putBytes(imageData)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Image uploaded successfully, get the download URL
                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUrl = uri.toString();
                                // Save the complaint details to Firestore with the image URL
                                saveComplaintToFirestore(complaintText, imageUrl);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(Exception e) {
                                // Handle failure to get download URL
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        // Handle failure to upload image
                    }
                });
    }

    private void saveComplaintToFirestore(String complaintText, String imageUrl) {
        // Get current timestamp as Firestore Timestamp
        Timestamp currentDate = Timestamp.now();

        Map<String, Object> complaintData = new HashMap<>();
        complaintData.put("complaintText", complaintText);
        complaintData.put("date", currentDate);

        if (imageUrl != null) {
            complaintData.put("imageUrl", imageUrl);
        }

        // Add the complaint data to the "complaints" collection in Firestore
        db.collection("complaints")
                .add(complaintData)  // Use add() to generate a unique document ID
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Complaint added successfully
                        Log.d("Firestore", "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e("Firestore", "Error adding document", e);
                        // Handle failure to add complaint
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK && data != null) {
            try {
                // Get the selected image URI
                Uri selectedImageUri = data.getData();

                // Load the selected image into the ImageView
                imageViewAttached.setImageURI(selectedImageUri);
                imageViewAttached.setVisibility(View.VISIBLE);

                // Convert the selected image URI to Bitmap
                selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
            } catch (IOException e) {
                e.printStackTrace(); // gives logcat output (erros)
            }
        }
    }
}
