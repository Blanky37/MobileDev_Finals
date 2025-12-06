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

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // NavController to handle navigation between fragments
        final NavController navController = Navigation.findNavController(view);

        binding.cardEmergencyLoan.setOnClickListener(v -> {
            // Navigate to the Emergency Loan fragment
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

        binding.cardLoanHistory.setOnClickListener(v -> {
            // Navigate to the User History fragment using its ID from the navigation graph (menu)
            navController.navigate(R.id.nav_history);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
