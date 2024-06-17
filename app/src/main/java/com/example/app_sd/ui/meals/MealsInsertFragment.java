package com.example.app_sd.ui.meals;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.app_sd.databinding.FragmentMealsInsertBinding;
import com.example.app_sd.databinding.FragmentMealsBinding;
import com.example.app_sd.databinding.FragmentMealsInsertBinding;

public class MealsInsertFragment extends Fragment {

    private FragmentMealsInsertBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMealsInsertBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        MealsViewModel homeViewModel = new ViewModelProvider(this).get(MealsViewModel.class);


        Spinner spinner = binding.tipo;

        String[] items = {"Selecione uma opção...","Café da Manhã", "Almoço", "Janta", "Lanche"};


        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, items);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Pegando o item selecionado
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