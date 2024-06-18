package com.example.app_sd.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.app_sd.MainActivity;
import com.example.app_sd.R;
import com.example.app_sd.databinding.FragmentActivitiesBinding;
import com.example.app_sd.databinding.FragmentHomeBinding;
import com.example.app_sd.service.ApiService;
import com.example.app_sd.ui.weight.WeightFragment;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private NavController navController;

    private int id;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
         id = sharedPreferences.getInt("USER_ID", -1);
        Log.i("EXTRA_ID PERFIL",""+id);

        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        View view = inflater.inflate(R.layout.fragment_activities, container, false);

        // Encontrar o botão e definir o OnClickListener
        MaterialButton insertButton = root.findViewById(R.id.updateWeight);
        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(requireView());
                navController.navigate(R.id.nav_weight);
            }
        });


        ExecutorService executorService = Executors.newSingleThreadExecutor();


        executorService.execute(() -> {
            ApiService api = new ApiService();
            try {
                Log.i("ID-", "" + id);

                // Chamando os dois endpoints
                String response = api.request("GET", "/weight/user/" + id, null);
                String response2 = api.request("GET", "/users/" + id, null);
                String response3 = api.request("GET", "/meals/user/" + id, null);
                String response4 = api.request("GET", "/activity/user/" + id, null);

                // Executando o código de UI na thread principal
                getActivity().runOnUiThread(() -> {
                    String value = null;
                    String height = null;

                    try {
                        // Processando a resposta do primeiro endpoint
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray jsonArray2 = jsonResponse.getJSONArray("data");
                        JSONObject jsonObject = jsonArray2.getJSONObject(0);
                        value = jsonObject.getString("value");

                        JSONObject jsonResponse2 = new JSONObject(response2);
                        JSONObject userData = jsonResponse2.getJSONObject("data"); // Corrigido para acessar o objeto 'data'
                        height = userData.getString("height");

                        JSONObject jsonResponse3 = new JSONObject(response3);
                        JSONArray mealsData = jsonResponse3.getJSONArray("data");

                        float breakFast = 0;
                        float lunch = 0;
                        float dinner = 0;
                        float snack = 0;
                        float values = 0;
                        String type = null;

                        for (int i = 0; i < mealsData.length(); i++) {
                            JSONObject jsonObject4 = mealsData.getJSONObject(i);
                            type = jsonObject4.getString("type");
                            values = Float.parseFloat(jsonObject4.getString("calories"));

                            switch (type) {
                                case "Janta":
                                    dinner += values;
                                    break;
                                case "Lanche Livre":
                                    snack += values;
                                    break;
                                case "Almoço":
                                    lunch += values;
                                    break;
                                case "Café da Manhã":
                                    breakFast += values;
                                    break;
                            }
                        }


                            JSONObject jsonResponse4 = new JSONObject(response4);
                            JSONArray minutesData = jsonResponse4.getJSONArray("data");

                            float totalDuration = 0;

                            // Iteração sobre o JSONArray e cálculo da duração total
                            for (int i = 0; i < minutesData.length(); i++) {
                                JSONObject jsonObject5 = minutesData.getJSONObject(i);
                                float durationMinutes = Float.parseFloat(jsonObject5.getString("durationMinutes"));
                                totalDuration += durationMinutes;
                            }

                            // Definindo o valor da duração total no TextView usando binding
                            binding.minutesValue.setText(String.valueOf((int) totalDuration));



                        binding.cofeBreak.setText(String.valueOf((int) breakFast));
                        binding.lunch.setText(String.valueOf((int) lunch));
                        binding.dinner.setText(String.valueOf((int) dinner));
                        binding.snack.setText(String.valueOf((int) snack));

                        binding.heightValue.setText(height);
                        binding.weightValue.setText(value);



                        // Calculando o IMC e definindo o valor
                        float weight = Float.parseFloat(value);
                        float heightInMeters = Float.parseFloat(height);
                        float imc = weight / (heightInMeters * heightInMeters);
                        binding.imcValue.setText(String.format("%.2f", imc));



                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.i("/user/", "Erro ao processar as respostas JSON");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("/user/", "Erro ao chamar os endpoints");
            }
        });









        //final TextView textView = binding.textHome;
        //homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}