package com.example.app_sd.ui.activities;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.example.app_sd.databinding.FragmentActivitiesInsertBinding;

public class ActivitiesInsertFragment extends Fragment {

    private FragmentActivitiesInsertBinding binding;
    private String itemSelecionadoType;
    private String itemSelecionadoIntensity;
    private  int id;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        id = sharedPreferences.getInt("USER_ID", -1);
        Log.i("EXTRA_ID PERFIL",""+id);
        binding = FragmentActivitiesInsertBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ActivitiesViewModel homeViewModel = new ViewModelProvider(this).get(ActivitiesViewModel.class);


        Spinner spinner = binding.tipo;

        String[] items = {"Selecione uma opção...","Aeróbica", "Musculação", "Flexibilidade", "Equilíbrio"};


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

        Spinner spinner2 = binding.intensidade;

        String[] items2 = {"Selecione uma opção...","Baixa", "Moderada", "Alta"};


        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, items2);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner2.setAdapter(adapter2);

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

    public void insertFunc(View v){

        EditText name = (EditText) v.findViewById(R.id.inputName);
        EditText minutes = (EditText) v.findViewById(R.id.inputMinutes);


        String jsonInputString = "{\"name\": \""+name.getText()+"\", \"idUser\":\""+id+"\", \"durationMinutes\": \""+minutes.getText()+"\", \"activityType\": \""+ itemSelecionadoType +"\", \"intensityLevel\": \""+ itemSelecionadoIntensity +"\"}";

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(() -> {
            ApiService api = new ApiService();
            try {
                Log.i("json",jsonInputString);
                String response = api.request("POST","/activity/", jsonInputString);
                getActivity().runOnUiThread(() -> {

                    NavController navController = Navigation.findNavController(requireView());
                    navController.navigate(R.id.nav_activities);

                });
            } catch (Exception e) {
                e.printStackTrace();

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}