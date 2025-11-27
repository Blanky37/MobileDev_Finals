package com.example.aquino_bembo_finals.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.aquino_bembo_finals.R;
import com.example.aquino_bembo_finals.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment using View Binding
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // The old code that caused the error is now removed.
        // It was trying to find a TextView called "textHome" which no longer exists.
        // final TextView textView = binding.textHome;

        // You can now access your actual views. For example, to set the employee name:
        // binding.tvEmployeeName.setText("Bembo Aquino");

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get the NavController to handle navigation between fragments
        final NavController navController = Navigation.findNavController(view);

        // Set up click listeners for the loan cards to navigate to other screens
        binding.cardEmergencyLoan.setOnClickListener(v -> {
            // Navigate to the Emergency Loan fragment using its ID from the navigation graph
            navController.navigate(R.id.nav_emergencyloan);
        });

        binding.cardSpecialLoan.setOnClickListener(v -> {
            // Navigate to the Special Loan fragment
            navController.navigate(R.id.nav_specialloan);
        });

        binding.cardRegularLoan.setOnClickListener(v -> {
            // Navigate to the Regular Loan fragment
            navController.navigate(R.id.nav_regularloan);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // This is important to prevent memory leaks by cleaning up the binding reference
        binding = null;
    }
}
