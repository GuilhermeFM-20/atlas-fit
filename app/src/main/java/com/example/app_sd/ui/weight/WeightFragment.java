package com.example.app_sd.ui.weight;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.app_sd.R;
import com.example.app_sd.databinding.FragmentWeightBinding;
import com.example.app_sd.service.ApiService;
import com.example.app_sd.ui.weight.WeightViewModel;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WeightFragment extends Fragment {

    private FragmentWeightBinding binding;
    protected String prefix = "/users/";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {



        FragmentWeightBinding binding = FragmentWeightBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        MaterialButton insertButton = root.findViewById(R.id.button2);
        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                atualizarButton(v);
            }
        });



        WeightViewModel homeViewModel = new ViewModelProvider(this).get(WeightViewModel.class);

        //Spinner spinner = binding.combo;


        // Obtendo a Intent da Activity associada ao Fragment
        // Em qualquer Activity ou Fragment, por exemplo, no PerfilFragment
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        int id = sharedPreferences.getInt("USER_ID", -1);
        Log.i("EXTRA_ID PERFIL",""+id);

        String[] items = {"Selecione uma opção...","Sedentário", "Levemente Ativo", "Moderadamente Ativo", "Muito Ativo", "Extremamente Ativo"};


        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, items);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //spinner.setAdapter(adapter);


        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(() -> {
            ApiService api = new ApiService();
            try {
                SharedPreferences sharedPreferences2 = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                int idUser = sharedPreferences2.getInt("USER_ID", -1);
                String response = api.request("GET",this.prefix+idUser, null);
                getActivity().runOnUiThread(() -> {
                    JSONObject jsonObject = null;
                    String name = null;
                    String email = null;

                    try {
                        jsonObject = new JSONObject(response);
                        name = jsonObject.getString("name");
                        email = jsonObject.getString("email");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    EditText inputName = (EditText) getActivity().findViewById(R.id.inputWeight);
                    EditText inputEmail = (EditText) getActivity().findViewById(R.id.inputEmail);

                    inputName.setText(name);
                    inputEmail.setText(email);


                });
            } catch (Exception e) {
                e.printStackTrace();

            }
        });

        return root;
    }

    public void atualizarButton(View view){
        Navigation.findNavController(view).navigate(R.id.nav_insert_activities);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}