package com.example.aquino_bembo_finals.ui.slideshow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
// The TextView is no longer needed, so we can remove this import.
// import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
// The default ViewModel is not used, so this can be removed as well.
// import androidx.lifecycle.ViewModelProvider;

import com.example.aquino_bembo_finals.databinding.FragmentSlideshowBinding;

public class SlideshowFragment extends Fragment {

    private FragmentSlideshowBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // The default ViewModel logic is removed as it's not needed.
        // SlideshowViewModel slideshowViewModel =
        //         new ViewModelProvider(this).get(SlideshowViewModel.class);

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // The line causing the error is removed.
        // It was looking for a TextView that does not exist in your actual layout.
        // final TextView textView = binding.textSlideshow;
        // slideshowViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        // This fragment is now a blank canvas. You can start adding the logic
        // for your "Special Loan" or "Regular Loan" screen here.

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Important for preventing memory leaks
    }
}
