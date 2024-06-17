package com.example.app_sd.ui.meals;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.app_sd.R;
import com.example.app_sd.databinding.FragmentMealsBinding;
import com.example.app_sd.databinding.FragmentMealsBinding;
import com.example.app_sd.service.ApiService;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MealsFragment extends Fragment {

    private FragmentMealsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        int id = sharedPreferences.getInt("USER_ID", -1);
        Log.i("EXTRA_ID PERFIL",""+id);

        binding = FragmentMealsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        MealsViewModel homeViewModel = new ViewModelProvider(this).get(MealsViewModel.class);




        View view = inflater.inflate(R.layout.fragment_meals, container, false);

        // Encontrar o botão e definir o OnClickListener
        MaterialButton insertButton = view.findViewById(R.id.insert);
        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testeClick(v);
            }
        });


        ExecutorService executorService = Executors.newSingleThreadExecutor();


        executorService.execute(() -> {
            ApiService api = new ApiService();
            try {
                SharedPreferences sharedPreferences2 = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                int idUser = sharedPreferences2.getInt("USER_ID", -1);
                String response = api.request("GET","/users/", null);
                getActivity().runOnUiThread(() -> {
                    
                    // Obtém a referência do layout pai onde a LinearLayout será adicionada
                    LinearLayout tableLayout = view.findViewById(R.id.table2);

                    String id2 = null;
                    String nome = null;

                    try{
                        if (tableLayout != null) {

                            JSONArray jsonArray = new JSONArray(response);
                            // Adiciona linhas dinamicamente
                            for (int i = 0; i <= jsonArray.length(); i++) { // Exemplo: adicionar 10 linhas

                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                 id2 = jsonObject.getString("id");
                                 nome = jsonObject.getString("name");

                                LinearLayout rowLayout = new LinearLayout(getActivity());
                                LinearLayout.LayoutParams rowLayoutParams = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                );
                                rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                                rowLayout.setBackgroundResource(R.drawable.border);
                                rowLayout.setLayoutParams(rowLayoutParams);

                                TextView codeTextView = new TextView(getActivity());
                                LinearLayout.LayoutParams codeTextViewParams = new LinearLayout.LayoutParams(
                                        0,
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        1
                                );
                                codeTextView.setLayoutParams(codeTextViewParams);
                                codeTextView.setText(id2);
                                codeTextView.setPadding(8, 8, 8, 8);
                                codeTextView.setTextColor(Color.BLACK);
                                codeTextView.setGravity(Gravity.CENTER);
                                codeTextView.setBackgroundResource(R.drawable.back_white_and_border);

                                TextView nameTextView = new TextView(getActivity());
                                LinearLayout.LayoutParams nameTextViewParams = new LinearLayout.LayoutParams(
                                        0,
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        2
                                );
                                nameTextView.setLayoutParams(nameTextViewParams);
                                nameTextView.setText(nome);
                                nameTextView.setPadding(8, 8, 8, 8);
                                nameTextView.setTextColor(Color.BLACK);
                                nameTextView.setGravity(Gravity.CENTER);
                                nameTextView.setBackgroundResource(R.drawable.back_white_and_border);

                                rowLayout.addView(codeTextView);
                                rowLayout.addView(nameTextView);

                                // Adicione um evento de clique ao valor de "id" na tabela
                                codeTextView.setOnClickListener(v -> {
                                    // Obtenha o id clicado
                                    String idClicado = ((TextView) v).getText().toString();

                                    // Inicie uma nova Activity para exibir os detalhes com base no id clicado
//                                    Intent intent = new Intent(MainActivity.this, UpdateActivity.class);
//                                    intent.putExtra("id", idClicado);
//                                    startActivity(intent);
                                });

                                Log.e("MealsFragment", "CHEGOU AQUI:"+rowLayout);

                                tableLayout.addView(rowLayout);
                            }
                        } else {
                            Log.e("MealsFragment", "tableLayout is null");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                });
            } catch (Exception e) {
                e.printStackTrace();

            }
        });








        return view;
    }

    public void testeClick(View view){
        Navigation.findNavController(view).navigate(R.id.nav_insert_meals);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}