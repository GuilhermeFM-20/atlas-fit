package com.example.app_sd.ui.meals;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.app_sd.R;
import com.example.app_sd.databinding.FragmentMealsInsertBinding;
import com.example.app_sd.service.ApiService;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MealsInsertFragment extends Fragment {

    private FragmentMealsInsertBinding binding;
    private String selectedItem;
    private LinearLayout tableLayout;
    private ExecutorService executorService;
    private int id;

    @SuppressLint("WrongViewCast")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        id = sharedPreferences.getInt("USER_ID", -1);
        Log.i("EXTRA_ID PERFIL", "" + id);

        binding = FragmentMealsInsertBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        MealsViewModel homeViewModel = new ViewModelProvider(this).get(MealsViewModel.class);

        // Inicialize o TableLayout corretamente
        tableLayout = root.findViewById(R.id.table2);
        executorService = Executors.newSingleThreadExecutor();

        Spinner spinner2 = binding.inputType;
        String[] items2 = {"Selecione uma opção...", "Café da Manhã", "Almoço", "Janta", "Lanche Livre"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, items2);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItem = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedItem = null;
            }
        });

        MaterialButton searchButton = root.findViewById(R.id.search);
        searchButton.setOnClickListener(v -> fetchAndDisplayData(binding.inputSearch.getText().toString()));

        MaterialButton insertButton = root.findViewById(R.id.insert);
        insertButton.setOnClickListener(this::insertFunc);

        return root;
    }

    public void fetchAndDisplayData(String descricao) {

        // Limpar todas as linhas da tabela (deixar apenas o cabeçalho)
        int childCount = tableLayout.getChildCount();
        if (childCount > 1) { // Verifica se há mais de uma linha (além do cabeçalho)
            tableLayout.removeViews(1, childCount - 1); // Remove todas as linhas após o cabeçalho
        }
        executorService.execute(() -> {
            String response = getCaloriasPorDescricao(descricao);
            if (response != null) {
                getActivity().runOnUiThread(() -> populateTableWithApiData(response));
            } else {
                getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Erro ao chamar a API.", Toast.LENGTH_SHORT).show());
            }
        });
    }

    public String getCaloriasPorDescricao(String meals) {
        String urlString = "https://caloriasporalimentoapi.herokuapp.com/api/calorias/?descricao=" + meals;
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            return response.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;

        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public void populateTableWithApiData(String jsonResponse) {
        try {
            // Parse the JSON response
            JSONArray jsonArray = new JSONArray(jsonResponse);

            // Loop through the array and create rows for each item
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                // Extract values from JSON object
                String descricao = jsonObject.getString("descricao");
                String quantidade = jsonObject.getString("quantidade");
                String calorias = jsonObject.getString("calorias");

                // Create a new row layout
                LinearLayout rowLayout = new LinearLayout(getActivity());
                LinearLayout.LayoutParams rowLayoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                rowLayout.setBackgroundResource(R.drawable.border);
                rowLayout.setLayoutParams(rowLayoutParams);

                // Create a TextView for calorias
                TextView caloriasTextView = new TextView(getActivity());
                LinearLayout.LayoutParams caloriasTextViewParams = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                );
                caloriasTextView.setLayoutParams(caloriasTextViewParams);
                caloriasTextView.setGravity(Gravity.CENTER);
                caloriasTextView.setTextColor(Color.BLACK);
                caloriasTextView.setBackgroundResource(R.drawable.back_white_and_border);
                caloriasTextView.setPadding(8, 8, 8, 8);
                caloriasTextView.setText(calorias);

                caloriasTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Obter o texto atual do inputValue e converter para float
                        String inputValueStr = binding.inputValue.getText().toString().trim(); // Obtém o texto do EditText e remove espaços em branco
                        float valueNow = 0.0f; // Valor padrão se estiver vazio
                        if (!inputValueStr.isEmpty()) {
                            try {
                                valueNow = Float.parseFloat(inputValueStr);
                            } catch (NumberFormatException e) {
                                e.printStackTrace(); // Tratar erro de conversão, se necessário
                            }
                        }

                        // Obter as calorias da TextView
                        String calorias = caloriasTextView.getText().toString();
                        String numerosApenas = calorias.replaceAll("[^0-9.]", ""); // Remover todos os caracteres exceto dígitos e ponto (caso haja ponto decimal)
                        float caloriasFloat = 0.0f; // Valor padrão
                        if (!numerosApenas.isEmpty()) {
                            try {
                                caloriasFloat = Float.parseFloat(numerosApenas);
                            } catch (NumberFormatException e) {
                                e.printStackTrace(); // Tratar erro de conversão, se necessário
                            }
                        }

                        // Calcular o novo valor
                        float newValue = valueNow + caloriasFloat;

                        // Atualizar o texto do inputValue
                        binding.inputValue.setText(String.valueOf(newValue));

                        // Exibir toast com informações
                        Toast.makeText(getActivity(), "Calorias de " + descricao + ": " + calorias, Toast.LENGTH_SHORT).show();
                    }
                });


                // Create a TextView for descricao (Refeição)
                TextView descricaoTextView = new TextView(getActivity());
                LinearLayout.LayoutParams descricaoTextViewParams = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        2
                );
                descricaoTextView.setLayoutParams(descricaoTextViewParams);
                descricaoTextView.setGravity(Gravity.CENTER);
                descricaoTextView.setPadding(8, 8, 8, 8);
                descricaoTextView.setTextColor(Color.BLACK);
                descricaoTextView.setBackgroundResource(R.drawable.back_white_and_border);
                descricaoTextView.setText(descricao);


                // Create a TextView for quantidade (Qtde)
                TextView quantidadeTextView = new TextView(getActivity());
                LinearLayout.LayoutParams quantidadeTextViewParams = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        2
                );
                quantidadeTextView.setLayoutParams(quantidadeTextViewParams);
                quantidadeTextView.setText(quantidade);
                quantidadeTextView.setPadding(8, 8, 8, 8);
                quantidadeTextView.setGravity(Gravity.CENTER);
                quantidadeTextView.setTextColor(Color.BLACK);
                quantidadeTextView.setBackgroundResource(R.drawable.back_white_and_border);



                // Add TextViews to the row layout
                rowLayout.addView(caloriasTextView);
                rowLayout.addView(descricaoTextView);
                rowLayout.addView(quantidadeTextView);

                // Add row layout to the table layout
                tableLayout.addView(rowLayout);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void insertFunc(View v) {
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = sdf.format(currentDate);

        String jsonInputString = "{\"name\": \"" + binding.inputName.getText() + "\", \"idUser\":\"" + id + "\", \"calories\": \"" + binding.inputValue.getText() + "\", \"type\": \"" + selectedItem + "\", \"formattedDate\": \"" + formattedDate + "\"}";
        Log.i("json", jsonInputString);
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(() -> {
            ApiService api = new ApiService();
            try {
                Log.i("json", jsonInputString);
                String response = api.request("POST", "/meals/", jsonInputString);
                getActivity().runOnUiThread(() -> {
                    NavController navController = Navigation.findNavController(requireView());
                    navController.navigate(R.id.nav_meals);
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
