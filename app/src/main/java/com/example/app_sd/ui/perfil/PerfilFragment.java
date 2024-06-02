package com.example.app_sd.ui.perfil;

import static android.content.Intent.getIntent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.app_sd.MyApp;
import com.example.app_sd.R;
import com.example.app_sd.databinding.FragmentPerfilBinding;

public class PerfilFragment extends Fragment {

    private FragmentPerfilBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {



        FragmentPerfilBinding binding = FragmentPerfilBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        PerfilViewModel homeViewModel = new ViewModelProvider(this).get(PerfilViewModel.class);

        Spinner spinner = binding.combo;


        // Obtendo a Intent da Activity associada ao Fragment
        // Em qualquer Activity ou Fragment, por exemplo, no PerfilFragment
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        int id = sharedPreferences.getInt("USER_ID", -1);
        Log.i("EXTRA_ID PERFIL",""+id);

        String[] items = {"Selecione uma opção...","Sedentário", "Levemente Ativo", "Moderadamente Ativo", "Muito Ativo", "Extremamente Ativo"};


        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, items);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Nada a fazer
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}