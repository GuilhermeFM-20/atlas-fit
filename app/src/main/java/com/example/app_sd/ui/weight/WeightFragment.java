package com.example.app_sd.ui.weight;

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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {



        FragmentWeightBinding binding = FragmentWeightBinding.inflate(inflater, container, false);
        View root = binding.getRoot();





        WeightViewModel homeViewModel = new ViewModelProvider(this).get(WeightViewModel.class);

        //Spinner spinner = binding.combo;


        // Obtendo a Intent da Activity associada ao Fragment
        // Em qualquer Activity ou Fragment, por exemplo, no PerfilFragment
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        int id = sharedPreferences.getInt("USER_ID", -1);
        Log.i("EXTRA_ID PERFIL",""+id);


        MaterialButton insertButton = root.findViewById(R.id.button);
        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                atualizarButton(v, (long) id);
            }
        });



        return root;
    }

    public void atualizarButton(View view, Long id){

        EditText value = (EditText) view.findViewById(R.id.inputWeight1);

        // Obter a data atual
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        // Formatar a data no padrão desejado (yyyy-MM-dd)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = sdf.format(currentDate);

        String jsonInputString = "{\"idUser\": \""+id+"\", \"value\": \""+value.getText()+"\", \"date\": \""+formattedDate+"\"}";

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(() -> {
            ApiService api = new ApiService();
            try {
                Log.i("json",jsonInputString);
                String response = api.request("POST","/weight/", jsonInputString);
                getActivity().runOnUiThread(() -> {

                    NavController navController = Navigation.findNavController(requireView());
                    navController.navigate(R.id.nav_home);

                });
            } catch (Exception e) {
                e.printStackTrace();

            }
        });

;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}