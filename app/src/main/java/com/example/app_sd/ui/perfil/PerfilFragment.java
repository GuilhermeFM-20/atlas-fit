package com.example.app_sd.ui.perfil;

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

import com.example.app_sd.R;
import com.example.app_sd.databinding.FragmentPerfilBinding;
import com.example.app_sd.service.ApiService;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PerfilFragment extends Fragment {

    private FragmentPerfilBinding binding;
    private String prefix = "/users/";
    private String selectedItem;
    private int id;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        PerfilViewModel homeViewModel = new ViewModelProvider(this).get(PerfilViewModel.class);

        Spinner spinner = binding.combo;

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        id = sharedPreferences.getInt("USER_ID", -1);
        Log.i("EXTRA_ID PERFIL", "" + id);

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

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            ApiService api = new ApiService();
            try {
                String response = api.request("GET", prefix + id, null);
                getActivity().runOnUiThread(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        binding.inputName.setText(jsonObject.getString("name"));
                        binding.inputEmail.setText(jsonObject.getString("email"));
                        binding.inputHeight.setText(jsonObject.getString("height"));
                        binding.inputBirth.setText(jsonObject.getString("birthDate"));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        MaterialButton insertButton = root.findViewById(R.id.button);
        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertFunc();
            }
        });

        return root;
    }

    public void insertFunc() {
        String jsonInputString = "{\"name\": \"" + binding.inputName.getText() + "\", \"email\":\"" + binding.inputEmail.getText() + "\", \"password\": \"\", \"height\": \"" + binding.inputHeight.getText() + "\", \"birthDate\": \"" + binding.inputBirth.getText() + "\", \"gender\":\"" + selectedItem + "\"}";

        Log.i("json", jsonInputString);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            ApiService api = new ApiService();
            try {
                String response = api.request("PUT", "/users/" + id, jsonInputString);
                getActivity().runOnUiThread(() -> {
                    // Update UI if necessary
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
