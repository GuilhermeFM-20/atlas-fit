package com.example.app_sd.ui.activities;

import android.os.Bundle;
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

import com.example.app_sd.databinding.FragmentActivitiesBinding;
import com.example.app_sd.databinding.FragmentActivitiesInsertBinding;

public class ActivitiesInsertFragment extends Fragment {

    private FragmentActivitiesInsertBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentActivitiesInsertBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ActivitiesViewModel homeViewModel = new ViewModelProvider(this).get(ActivitiesViewModel.class);

        Spinner spinner = binding.tipo;

        String[] items = {"Aeróbica", "Musculação", "Flexibilidade", "Equilíbrio"};


        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, items);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Pegando o item selecionado
                String selectedItem = parent.getItemAtPosition(position).toString();
                // Exibindo um toast com o item selecionado
                Toast.makeText(parent.getContext(), "Selecionado: " + selectedItem, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Nada a fazer
            }
        });

        Spinner spinner2 = binding.intensidade;

        String[] items2 = {"Baixa", "Moderada", "Alta"};


        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, items2);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner2.setAdapter(adapter2);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Pegando o item selecionado
                String selectedItem = parent.getItemAtPosition(position).toString();
                // Exibindo um toast com o item selecionado
                Toast.makeText(parent.getContext(), "Selecionado: " + selectedItem, Toast.LENGTH_SHORT).show();
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