package com.example.mobil5.ui.addphoto;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest.*;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.example.mobil5.R;
import com.example.mobil5.databinding.FragmentAddphotoBinding;
import com.example.mobil5.model.Label;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddPhotoFragment extends Fragment {

    private FragmentAddphotoBinding binding;
    private AddPhotoViewModel addPhotoViewModel;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView photoImageView;
    private RecyclerView labelRecyclerView;
    private LabelAdapter labelAdapter;
    private Label selectedLabel;
    private static final int REQUEST_CAMERA_PERMISSION = 2;
    private TextView selectedLabelTextView;


    private Button takePhotoButton;
    private Button addPhotoButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        addPhotoViewModel =
                new ViewModelProvider(this).get(AddPhotoViewModel.class);

        binding = FragmentAddphotoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        photoImageView = binding.photoImageView;
        takePhotoButton = binding.buttonCapturePhoto;
        addPhotoButton = binding.buttonAddPhoto;


        labelRecyclerView = root.findViewById(R.id.labelRecyclerView);
        labelAdapter = new LabelAdapter(new ArrayList<>(), new LabelAdapter.OnLabelClickListener() {
            @Override
            public void onLabelClick(Label label) {
                selectedLabel = label;
                updateLabelInfoUI(label);
                Toast.makeText(requireContext(), "Selected Label: " + label.getName(), Toast.LENGTH_SHORT).show();
                loadLabelsFromFirebase();
            }
        });
        labelRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        labelRecyclerView.setAdapter(labelAdapter);

        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImageToFirebase(selectedLabel);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void dispatchTakePictureIntent() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // İzin yok, kullanıcıdan izin iste
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{android.Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        } else {
            // Kamera izni var, fotoğraf çekme işlemini başlat
            startCameraIntent();
        }
    }

    private void startCameraIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

// ...

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Kullanıcı izni verdi, fotoğraf çekme işlemini başlat
                startCameraIntent();
            } else {
                // Kullanıcı izni vermedi, bir mesaj gösterilebilir
                Toast.makeText(requireContext(), "Kamera izni reddedildi.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");


            photoImageView.setImageBitmap(imageBitmap);

            loadLabelsFromFirebase();
        }
    }
    private void uploadImageToFirebase(Label selectedLabel) {
        if (selectedLabel != null) {
            System.out.println("seçilen etiket: "+selectedLabel);
            if (photoImageView.getDrawable() != null) {
                System.out.println("seçilen etiket: ife girdi ");
                Bitmap bitmap = getBitmapFromDrawable(photoImageView.getDrawable());

                uploadImageToFirebase(bitmap, selectedLabel);
                Toast.makeText(requireContext(), "Fotoğraf Eklendi.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Fotoğraf alınamadı.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(requireContext(), "Etiket seçilmedi.", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        Bitmap bitmap = null;
        if (drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawable || drawable instanceof VectorDrawableCompat) {
            bitmap = getBitmapFromVectorDrawable(drawable);
        }
        return bitmap;
    }

    private Bitmap getBitmapFromVectorDrawable(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private void uploadImageToFirebase(Bitmap bitmap, Label selectedLabel) {
        if (bitmap != null && selectedLabel != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            FirebaseStorage storage = FirebaseStorage.getInstance("gs://mobil5.appspot.com");
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            StorageReference userImagesRef = storage.getReference().child("images/" + userId);

            String photoId = UUID.randomUUID().toString();
            StorageReference imageRef = userImagesRef.child(photoId + ".jpg");

            imageRef.putBytes(data)
                    .addOnSuccessListener(taskSnapshot -> {
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            List<String> labelIds = new ArrayList<>();
                            List<String> labelNames = new ArrayList<>();
                            labelIds.add(selectedLabel.getId());
                            labelNames.add(selectedLabel.getName());

                            savePhotoInfoToDatabase(uri.toString(), labelIds.toString(), labelNames.toString());
                            Toast.makeText(requireContext(), "Fotoğraf Storage yüklendi", Toast.LENGTH_SHORT).show();
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), "Fotoğraf yüklenirken bir hata oluştu.", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(requireContext(), "Etiket seçilmedi veya fotoğraf alınamadı.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateLabelInfoUI(Label selectedLabel) {
        if (selectedLabel != null && binding != null) {
            binding.selectedLabelTextView.setText(selectedLabel.getName());
        }
    }

    private void savePhotoInfoToDatabase(String imageUrl, String labelId, String labelName) {

    }

    private void loadLabelsFromFirebase() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference labelsRef = FirebaseDatabase.getInstance().getReference("labels").child(userId);

        labelsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Label> labels = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Label label = snapshot.getValue(Label.class);
                    labels.add(label);
                }
                labelAdapter.setLabels(labels);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
