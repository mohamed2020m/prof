package me.ensa.professeur.ui.professeur;

import androidx.lifecycle.ViewModel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.ensa.professeur.classes.Professeur;
import me.ensa.professeur.classes.Specialite;

public class ProfesseurViewModel extends ViewModel {
    private MutableLiveData<List<Professeur>> professeursList = new MutableLiveData<>();

    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void fetchData(Context context) {
        String url = "http://192.168.0.131:8080/api/v1/professeurs";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    // Process the JSON response from the server
                    List<Professeur> professeurs = parseJsonResponse(response);
                    Log.d("res", response.toString());
                    professeursList.setValue(professeurs);
                    for(Professeur p : professeurs){
                        Log.d("professseur lastName: ", p.getSpecialite().getCode());
                    }
                },
                error -> {
                    errorLiveData.setValue(error.toString());
                    Log.e("Error", error.toString());
                }
        );

        // Instantiate Volley RequestQueue and add the request
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonArrayRequest);
    }

    // Method to get LiveData of student list
    public LiveData<List<Professeur>> getProfesseurList() {
        return professeursList;
    }

    private List<Professeur> parseJsonResponse(JSONArray response) {
        List<Professeur> professeurs;
        try {
            Type listType = new TypeToken<List<Professeur>>() {}.getType();
            Gson gson = new Gson();
            professeurs = gson.fromJson(response.toString(), listType);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            professeurs = new ArrayList<>();
        }

        return professeurs;
    }

//    private List<Professeur> parseJsonResponse(JSONArray response) {
//        List<Professeur> professeurs = new ArrayList<>();
//
//        try {
//            for (int i = 0; i < response.length(); i++) {
//                JSONObject professeursObj = response.getJSONObject(i);
//                Professeur professeur = new Professeur();
//
//                professeur.setId(professeursObj.optInt("id"));
//                professeur.setFirstName(professeursObj.optString("firstName"));
//                professeur.setLastName(professeursObj.optString("lastName"));
//                professeur.setEmail(professeursObj.optString("email"));
//                professeur.setTel(professeursObj.optString("tel"));
//                String dateString = professeursObj.optString("dateEmbauche");
//
//                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//
//                try {
//                    Date date = dateFormat.parse(dateString);
//                    professeur.setDateEmbauche(date);
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//
//
//                JSONObject specialiteObj = professeursObj.optJSONObject("specialite");
//                if (specialiteObj != null) {
//                    Specialite filiere = new Specialite();
//                    filiere.setId(specialiteObj.optInt("id"));
//                    filiere.setCode(specialiteObj.optString("code"));
//                    filiere.setLibelle(specialiteObj.optString("libelle"));
//                    professeur.setSpecialite(filiere);
//                }
//
//                professeurs.add(professeur);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return professeurs;
//    }
}