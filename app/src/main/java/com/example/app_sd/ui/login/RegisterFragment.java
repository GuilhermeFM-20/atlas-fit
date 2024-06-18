package com.example.app_sd.ui.login;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.app_sd.LoginActivity;
import com.example.app_sd.MainActivity;
import com.example.app_sd.R;
import com.example.app_sd.RegisterActivity;
import com.example.app_sd.databinding.FragmentLoginBinding;
import com.example.app_sd.databinding.FragmentRegisterBinding;
import com.example.app_sd.service.ApiService;
import com.example.app_sd.ui.home.HomeViewModel;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;

    private String selectedItem;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Spinner spinner = binding.combo;

        String[] items = {"Selecione uma opção...", "Masculino", "Feminino"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItem = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedItem = null;
            }
        });



        //View view = inflater.inflate(R.layout.fragment_activities, container, false);

        // Encontrar o botão e definir o OnClickListener
        MaterialButton insertButton = root.findViewById(R.id.button);
        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testeClick(v);
            }
        });

        MaterialButton returnBtn = root.findViewById(R.id.returnBtn);
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });





        //final TextView textView = binding.textHome;
        //homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    public void testeClick(View view){
        String jsonInputString = "{\"name\": \"" + binding.inputName.getText() + "\", \"email\":\"" + binding.inputEmail.getText() + "\", \"password\": \"\", \"height\": \"" + binding.inputHeight.getText() + "\", \"birthDate\": \"" + binding.inputBirth.getText() + "\", \"gender\":\"" + selectedItem + "\"}";

        Log.i("json", jsonInputString);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            ApiService api = new ApiService();
            try {
                String response = api.request("POST", "/users/", jsonInputString);

                // Processar a resposta na thread principal
                getActivity().runOnUiThread(() -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String message = jsonResponse.getString("msg");

                        // Verificar se a atualização foi bem-sucedida ou não
                        if ("Houve algum erro na atualização.".equals(message)) {
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Erro ao processar resposta.", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                // Tratamento de erro ao fazer a requisição
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getActivity(), "Erro ao atualizar registro.", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}