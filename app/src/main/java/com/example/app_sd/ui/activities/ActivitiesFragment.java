package com.example.app_sd.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.app_sd.R;
import com.example.app_sd.databinding.FragmentActivitiesBinding;
import com.example.app_sd.databinding.FragmentPerfilBinding;

public class ActivitiesFragment extends Fragment {

    private FragmentActivitiesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentActivitiesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ActivitiesViewModel homeViewModel = new ViewModelProvider(this).get(ActivitiesViewModel.class);


        // Encontrar o botão pelo binding
        Button buttonNavigate = binding.insert;
        buttonNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Usar NavController para navegação
                Navigation.findNavController(v).navigate(R.id.nav_insert_activities);
            }
        });


        return root;
    }

    public void testeClick(){

        // Usar getActivity() ou requireActivity() para obter o contexto
        Intent intent = new Intent(requireActivity(), ActivitiesInsertFragment.class);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}