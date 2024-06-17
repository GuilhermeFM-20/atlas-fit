package com.example.app_sd.ui.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.app_sd.MainActivity;
import com.example.app_sd.R;
import com.example.app_sd.databinding.FragmentLoginBinding;
import com.example.app_sd.service.ApiService;
import com.example.app_sd.ui.home.HomeViewModel;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);



        binding = FragmentLoginBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        View view = inflater.inflate(R.layout.fragment_activities, container, false);

        // Encontrar o botão e definir o OnClickListener
        MaterialButton insertButton = root.findViewById(R.id.register);
        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(requireView());
                navController.navigate(R.id.nav_register);
            }
        });

        // Encontrar o botão pelo binding
        Button buttonNavigate = binding.button;
        buttonNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText email = (EditText) getActivity().findViewById(R.id.inputEmail);
                EditText password = (EditText) getActivity().findViewById(R.id.inputPassword);



                String jsonInputString = "{\"email\": \""+email.getText()+"\", \"password\": \""+password.getText()+"\"}";

                ExecutorService executorService = Executors.newSingleThreadExecutor();

                executorService.execute(() -> {
                    ApiService api = new ApiService();
                    try {
                        Log.i("json",jsonInputString);
                        String response = api.request("POST","/users/login", jsonInputString);
                        getActivity().runOnUiThread(() -> {

                            String status = null;
                            String data = null;
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                status = jsonObject.getString("status");
                                data = jsonObject.getString("data");
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }


                            if (status == "true") {
                                // Usar NavController para navegação

                                String id = null;
                                try {
                                    JSONObject jsonObject = new JSONObject(data);
                                    id = jsonObject.getString("id");
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                                Intent intent = new Intent(requireActivity(), MainActivity.class);
                                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putInt("USER_ID", Integer.parseInt(id));
                                editor.apply();
                                startActivity(intent);
                            } else {
                                getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Usuário ou senha inválido.", Toast.LENGTH_SHORT).show());
                            }


                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

            }
        });

        //final TextView textView = binding.textHome;
        //homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    public void testeClick(View view){
        NavController navController = NavHostFragment.findNavController(this);

        navController.navigate(R.id.nav_register);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}