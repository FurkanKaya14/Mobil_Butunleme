package com.example.mobil5.ui.gallery;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.mobil5.R;
import com.example.mobil5.databinding.FragmentGalleryBinding;
import com.example.mobil5.model.Photo;
import com.example.mobil5.model.Label;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private GalleryViewModel galleryViewModel;
    private RecyclerView recyclerViewPhotos;
    private Spinner spinnerFilterLabels;
    private PhotoAdapter photoAdapter;
    private List<Photo> photoList;
    private ImageView imageViewPhoto;
    private ArrayAdapter<String> labelSpinnerAdapter;
    private List<Label> labelList;
    private String selectedLabel;
    private String eskiLabel="";
    private Boolean kontrol=false;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        galleryViewModel = new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        imageViewPhoto = root.findViewById(R.id.imageViewPhoto);
        recyclerViewPhotos = binding.recyclerViewPhotos;
        spinnerFilterLabels= binding.spinnerFilterLabels;



        setupSpinner();
        spinnerFilterLabels.post(new Runnable() {
            @Override
            public void run() {
                spinnerFilterLabels.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                        // Seçilen etiketin ID'sini alıyoruz
                        if (labelList.size() > 0) {
                            String selectedLabelId = labelList.get(position).getId();
                            //Log.d("LOAD_PHOTOS", "Selected Label ID11: " + selectedLabelId);
                            selectedLabel = labelList.get(position).getName();
                            System.out.println("seçilen labeleÇ: "+selectedLabel);

                            // Seçilen etikete göre fotoğrafları yüklüyoruz
                            loadPhotosFromFirebase(selectedLabel);


                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                        // Hiçbir şey seçilmediğinde burası çalışır, şu an için bir şey yapmaya gerek yok
                    }
                });
            }
        });
        setupRecyclerView();
        loadLabelsAndPhotosFromFirebase();


        return root;
    }

    private void loadLabelsAndPhotosFromFirebase() {
        loadLabelsFromFirebase();
    }
    private void SpinnerGuncelle(Spinner spinner) {
        List<String> labelNames = getLabelNames(labelList);

        // ArrayAdapter ile Spinner'ı güncelleme
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, labelNames);
        spinner.setAdapter(adapter);
    }


    private void setupRecyclerView() {

        recyclerViewPhotos.setLayoutManager(new LinearLayoutManager(requireContext()));
        photoAdapter = new PhotoAdapter(photoList);
        recyclerViewPhotos.setAdapter(photoAdapter);
    }
    private void showPhotos(List<Photo> photos) {
        recyclerViewPhotos.clearOnScrollListeners();
        // Her seferinde yeni bir PhotoAdapter oluşturma
        photoAdapter = new PhotoAdapter(photos);
        recyclerViewPhotos.setAdapter(photoAdapter);
    }
    private List<String> getLabelNames(List<Label> labels) {
        List<String> labelNames = new ArrayList<>();
        for (Label label : labelList) {
            labelNames.add(label.getName());
        }
        return labelNames;
    }


    private void loadLabelsFromFirebase() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference labelsRef = FirebaseDatabase.getInstance().getReference("labels").child(userId);
        System.out.println("labelreferans:"+labelsRef);

        labelsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Label> labels = new ArrayList<>();
                List<String> labelNames = new ArrayList<>();
                //System.out.println("label ref childeren: "+dataSnapshot.getChildrenCount());
                //System.out.println("label ref childeren name: "+dataSnapshot.getChildren());
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Label label = snapshot.getValue(Label.class);
                    if (label != null) {
                        labels.add(label);
                        labelNames.add(label.getName());
                        //System.out.println("label name: "+label.getName());
                        //Log.d("LOAD_LABELS", "Number of labels: " + labels.size());
                    }
                }

                 // labelList'i güncelleme
                labelList = labels;
                labelSpinnerAdapter.clear();
                labelSpinnerAdapter.addAll(labelNames);
                SpinnerGuncelle(spinnerFilterLabels);
                    // Load photos after labels are loaded
                loadPhotosFromFirebase(null);

                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });


    }


    private void loadPhotosFromFirebase(String selectedLabelName) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference imagesRef = FirebaseStorage.getInstance().getReference("images/").child(userId);

        imagesRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                List<Photo> photos = new ArrayList<>();
                for (StorageReference item : listResult.getItems()) {
                    String photoId = item.getName();
                    Photo photo = new Photo();
                    photo.setId(photoId);
                    getImageUrlFromStorage(userId, photoId, new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String imageUrl) {
                            photo.setImageUrl(imageUrl);

                            // Eğer etiket seçili değilse veya fotoğrafın etiketleri içinde seçilen etiket varsa ekleme
                            if (selectedLabelName == null || labelNamesContainLabel(selectedLabelName, photo.getLabelNames())) {
                                photos.add(photo);
                                showPhotos(photos);
                                loadLabelsForPhoto(photo, new OnSuccessListener<List<Label>>() {
                                    @Override
                                    public void onSuccess(List<Label> labels) {
                                        photo.setLabels(labels);
                                        showPhotos(photos);
                                    }
                                });
                            }
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("LOAD_PHOTOS", "Error listing photos: " + e.getMessage());
            }
        });
    }

    private boolean labelNamesContainLabel(String selectedLabelName, List<String> labelIds) {
        if (labelIds != null) {
            for (Label label : labelList) {
                if (label.getName().equals(selectedLabelName) && labelIds.contains(label.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void loadLabelsForPhoto(Photo photo, OnSuccessListener<List<Label>> onSuccessListener) {
        List<String> labelIds = photo.getLabelIds();

        if (labelIds == null || labelIds.isEmpty()) {
            // LabelIds boş veya null ise, onSuccessListener'ı hemen çağırma
            onSuccessListener.onSuccess(new ArrayList<>());
            return;
        }

        List<Label> labels = new ArrayList<>();
        System.out.println("labelıds: " + labelIds);

        for (String labelId : labelIds) {
            DatabaseReference labelRef = FirebaseDatabase.getInstance().getReference("labels").child(labelId);
            System.out.println("labelrefler:" + labelRef);

            labelRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Label label = dataSnapshot.getValue(Label.class);
                    System.out.println("son labelicerik:" + label);

                    if (label != null) {
                        System.out.println("son label:" + label);
                        labels.add(label);

                        // Eğer labels listesi, labelIds listesinin uzunluğuna eşit veya büyükse onSuccessListener'ı çağırın
                        if (labels.size() >= labelIds.size()) {
                            onSuccessListener.onSuccess(labels);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error
                    Log.e("LOAD_LABELS", "Error loading labels: " + error.getMessage());
                }
            });
        }
    }

    private void getImageUrlFromStorage(String userId, String photoId, OnSuccessListener<String> onSuccessListener) {
        // Firebase Storage referansı oluşturma
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://mobil5.appspot.com");
        System.out.println("foto id: "+photoId);

        // Fotoğrafın bulunduğu yol
        String photoPath = "images/" + userId + "/" + photoId;

        // Fotoğrafın bulunduğu Storage referansını alma
        StorageReference photoRef = storageRef.child(photoPath);
        System.out.println("songotoreff:"+photoRef);
        // Fotoğrafın URL'sini al ve onSuccessListener ile geri döndür

        photoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Fotoğrafın URL'si başarıyla alındı
                String imageUrl = uri.toString();
                //System.out.println("imageurl ne. "+imageUrl);
                onSuccessListener.onSuccess(imageUrl);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("GET_IMAGE_URL", "Error getting image URL: " + exception.getMessage());
            }
        });
    }

    private void loadPhotosAndLabelsFromFirebase() {
        loadLabelsFromFirebase();
        // loadPhotosFromFirebase metodunu çağırırken şu an seçili etiket yok, bu nedenle null verdim
        loadPhotosFromFirebase(null);
    }

    private void setupSpinner() {
        labelList = new ArrayList<>();
        labelSpinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, getLabelNames(labelList));
        labelSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilterLabels.setAdapter(labelSpinnerAdapter);

        // Etiketlerin yüklenmesi tamamlandığında çağrılacak olan listener'ı burada tanımlama
        spinnerFilterLabels.post(new Runnable() {
            @Override
            public void run() {
                spinnerFilterLabels.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                        // Seçilen etiketin ID'sini alma
                        if (labelList.size() > 0) {
                            String selectedLabelId = labelList.get(position).getId();
                            Log.d("LOAD_PHOTOS", "Selected Label ID: " + selectedLabelId);


                            // Seçilen etikete göre fotoğrafları yükleme
                            loadPhotosFromFirebase(selectedLabelId);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
