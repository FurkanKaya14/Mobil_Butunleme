package com.example.mobil5.ui.addlabel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobil5.R;
import com.example.mobil5.databinding.FragmentAddlabelBinding;
import com.example.mobil5.model.Label;
import com.example.mobil5.ui.addphoto.LabelAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddLabelFragment extends Fragment implements LabelAdapter.OnLabelClickListener {

    private FragmentAddlabelBinding binding;
    private AddLabelViewModel addLabelViewModel;
    private Button addButton;
    private EditText labelNameEditText;

    private RecyclerView labelRecyclerView;
    private LabelAdapter labelAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        addLabelViewModel =
                new ViewModelProvider(this).get(AddLabelViewModel.class);

        binding = FragmentAddlabelBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        EditText labelNameEditText = binding.editTextLabelName;
        Button addButton = binding.buttonAddLabel;
        labelRecyclerView = root.findViewById(R.id.labelRecyclerView);
        labelAdapter = new LabelAdapter(new ArrayList<>(), this); // OnLabelClickListener eklenerek adapter oluşturuldu
        labelRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        labelRecyclerView.setAdapter(labelAdapter);
        loadLabelsFromFirebase();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addLabelToFirebase (labelNameEditText.getText().toString());
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void addLabelToFirebase(String labelName) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference labelsRef = FirebaseDatabase.getInstance().getReference("labels").child(userId);

        String labelId = labelsRef.push().getKey();
        Label label = new Label(labelId, labelName);

        labelsRef.child(labelId).setValue(label)
                .addOnSuccessListener(aVoid -> {

                    Toast.makeText(requireContext(), "Etiket başarıyla eklendi.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(requireContext(), "Etiket eklenirken bir hata oluştu.", Toast.LENGTH_SHORT).show();
                });

        navigateToAddPhotoFragment();
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

    private void navigateToAddPhotoFragment() {

        Navigation.findNavController(requireView()).navigate(R.id.action_nav_addlabel_to_nav_addphoto);
    }


    @Override
    public void onLabelClick(Label label) {

    }
}
