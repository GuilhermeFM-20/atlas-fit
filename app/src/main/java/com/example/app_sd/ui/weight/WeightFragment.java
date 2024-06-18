package com.example.app_sd.ui.weight;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.app_sd.MainActivity;
import com.example.app_sd.R;
import com.example.app_sd.databinding.FragmentWeightBinding;
import com.example.app_sd.service.ApiService;
import com.example.app_sd.ui.weight.WeightViewModel;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WeightFragment extends Fragment {

    private FragmentWeightBinding binding;
    protected String prefix = "/users/";

    private Long id = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {



        binding = FragmentWeightBinding.inflate(inflater, container, false);
        View root = binding.getRoot();





        WeightViewModel homeViewModel = new ViewModelProvider(this).get(WeightViewModel.class);

        //Spinner spinner = binding.combo;


        // Obtendo a Intent da Activity associada ao Fragment
        // Em qualquer Activity ou Fragment, por exemplo, no PerfilFragment
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        id = (long) sharedPreferences.getInt("USER_ID", -1);





        View view = inflater.inflate(R.layout.fragment_weight, container, false);
        ExecutorService executorService = Executors.newSingleThreadExecutor();


        executorService.execute(() -> {
            ApiService api = new ApiService();
            try {

                String response = api.request("GET","/weight/user/"+id, null);
                getActivity().runOnUiThread(() -> {

                    // Obtém a referência do layout pai onde a LinearLayout será adicionada
                    LinearLayout tableLayout = root.findViewById(R.id.table2);

                    String value = null;
                    String date = null;

                    try{
                        if (tableLayout != null) {

                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray jsonArray = jsonResponse.getJSONArray("data");

                            // Adiciona linhas dinamicamente
                            for (int i = 0; i <= jsonArray.length(); i++) { // Exemplo: adicionar 10 linhas

                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                value = jsonObject.getString("value");
                                date = jsonObject.getString("date");

                                Log.i("value",value);
                                Log.i("date",date);

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
                                codeTextView.setText(value);
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
                                nameTextView.setText(date);
                                nameTextView.setPadding(8, 8, 8, 8);
                                nameTextView.setTextColor(Color.BLACK);
                                nameTextView.setGravity(Gravity.CENTER);
                                nameTextView.setBackgroundResource(R.drawable.back_white_and_border);

                                rowLayout.addView(codeTextView);
                                rowLayout.addView(nameTextView);

                                Log.e("ActivitiesFragment", "CHEGOU AQUI:"+rowLayout);

                                tableLayout.addView(rowLayout);
                            }
                        } else {
                            Log.e("ActivitiesFragment", "tableLayout is null");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                });
            } catch (Exception e) {
                e.printStackTrace();

            }
        });

        // Encontrar o botão e definir o OnClickListener
        MaterialButton insertButton = root.findViewById(R.id.buttonSubmit);
        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                atualizarButton(root);
            }
        });


        return root;
    }

    public void atualizarButton(View view){

        Log.i("weight", String.valueOf(binding.inputWeight1.getText()));
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = sdf.format(currentDate);

        String jsonInputString = "{\"idUser\": \""+id+"\", \"value\": \""+binding.inputWeight1.getText()+"\", \"date\": \""+formattedDate+"\"}";

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(() -> {
            ApiService api = new ApiService();
            try {
                Log.i("json",jsonInputString);
                api.request("POST","/weight/", jsonInputString);
                getActivity().runOnUiThread(() -> {

                    NavController navController = Navigation.findNavController(requireView());
                    navController.navigate(R.id.nav_home);

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