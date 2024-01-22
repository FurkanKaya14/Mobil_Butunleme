package com.example.mobil5.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobil5.databinding.FragmentGalleryBinding;
import com.example.mobil5.model.Photo;
import com.example.mobil5.model.Label;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private GalleryViewModel galleryViewModel;
    private RecyclerView recyclerViewPhotos;
    private Spinner spinnerFilterLabels;
    private PhotoAdapter photoAdapter;
    private List<Photo> photoList;

    private ArrayAdapter<String> labelSpinnerAdapter;
    private List<Label> labelList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel = new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerViewPhotos = binding.recyclerViewPhotos;
        spinnerFilterLabels = binding.spinnerFilterLabels;

        setupSpinner();
        setupRecyclerView();


        loadPhotosAndLabelsFromFirebase();

        return root;
    }

    private void setupSpinner() {
        labelList = new ArrayList<>();
        labelSpinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, getLabelNames(labelList));
        labelSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilterLabels.setAdapter(labelSpinnerAdapter);
    }

    private void setupRecyclerView() {
        photoList = new ArrayList<>();
        photoAdapter = new PhotoAdapter(photoList);
        recyclerViewPhotos.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewPhotos.setAdapter(photoAdapter);
    }

    private List<String> getLabelNames(List<Label> labels) {
        List<String> labelNames = new ArrayList<>();
        for (Label label : labelList) {
            labelNames.add(label.getName());
        }
        return labelNames;
    }

    private void loadPhotosAndLabelsFromFirebase() {
        loadLabelsFromFirebase();
        loadPhotosFromFirebase();
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
                labelSpinnerAdapter.addAll(String.valueOf(labels));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadPhotosFromFirebase() {
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
                labelSpinnerAdapter.addAll(getLabelNames(labels));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
