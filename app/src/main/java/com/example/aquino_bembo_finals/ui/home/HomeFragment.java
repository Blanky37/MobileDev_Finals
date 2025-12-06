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
import com.example.aquino_bembo_finals.DatabaseHelper;
import com.example.aquino_bembo_finals.MainActivity;
import com.example.aquino_bembo_finals.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private DatabaseHelper databaseHelper;
    private String currentEmployeeId;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        databaseHelper = new DatabaseHelper(getContext());

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() != null) {
            MainActivity mainActivity = (MainActivity) getActivity();
            currentEmployeeId = mainActivity.getCurrentEmployeeId();

            loadUserInfo();
        }

        // NavController to handle navigation between fragments
        final NavController navController = Navigation.findNavController(view);

        binding.cardEmergencyLoan.setOnClickListener(v -> {
            navController.navigate(R.id.nav_emergencyloan);
        });

        binding.cardSpecialLoan.setOnClickListener(v -> {
            navController.navigate(R.id.nav_specialloan);
        });

        binding.cardRegularLoan.setOnClickListener(v -> {
            navController.navigate(R.id.nav_regularloan);
        });

        binding.cardLoanHistory.setOnClickListener(v -> {
            navController.navigate(R.id.nav_history);
        });
    }

    private void loadUserInfo() {
        if (currentEmployeeId != null && !currentEmployeeId.isEmpty()) {
            boolean found = databaseHelper.GetUserDetails(currentEmployeeId);
            if (found) {
                String fullName = databaseHelper.getFullName();
                String employeeId = databaseHelper.getEmployeeID();

                binding.tvWelcome.setText("Welcome Back!");
                binding.tvEmployeeName.setText(fullName);
                binding.tvEmployeeId.setText("ID: " + employeeId);
            } else {
                // If user not found
                binding.tvWelcome.setText("Welcome!");
                binding.tvEmployeeName.setText("User Not Found");
                binding.tvEmployeeId.setText("ID: Unknown");
            }
        } else {
            // If no employee ID is set
            if (getActivity() != null) {
                MainActivity mainActivity = (MainActivity) getActivity();
                currentEmployeeId = mainActivity.getCurrentEmployeeId();

                if (currentEmployeeId != null && !currentEmployeeId.isEmpty()) {
                    loadUserInfo();
                } else {
                    binding.tvWelcome.setText("Welcome!");
                    binding.tvEmployeeName.setText("Guest User");
                    binding.tvEmployeeId.setText("Please log in");
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserInfo();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}