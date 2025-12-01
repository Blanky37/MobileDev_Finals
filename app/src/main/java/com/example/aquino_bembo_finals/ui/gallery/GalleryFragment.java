package com.example.aquino_bembo_finals.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
// We are removing the TextView, so this import is no longer needed.
// import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
// The default template includes a ViewModel, but it's not needed for this layout yet.
// import androidx.lifecycle.ViewModelProvider;

import com.example.aquino_bembo_finals.databinding.FragmentGalleryBinding;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // The original code that caused the error is removed.
        // final TextView textView = binding.textGallery;
        // galleryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        // --- Your new logic will go here ---
        // For example, you will set up your RecyclerView here later on.
        // binding.rvLoanApplications.setLayoutManager(new LinearLayoutManager(getContext()));
        // You can also control the visibility of the empty state.
        // binding.layoutEmptyState.setVisibility(View.VISIBLE);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
