package me.ensa.professeur.ui.specialite;

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
import java.util.ArrayList;
import java.util.List;

import me.ensa.professeur.classes.Specialite;

public class SpecialiteViewModel extends ViewModel {
    private MutableLiveData<List<Specialite>> specialiteList = new MutableLiveData<>();

    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void fetchData(Context context) {
        String url = "http://192.168.0.131:8080/api/v1/spcialites";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    // Process the JSON response from the server
                    List<Specialite> specialites = parseJsonResponse(response);
                    specialiteList.setValue(specialites);
                    Log.d("f", response.toString());
                    for(Specialite s : specialites){
                        Log.d("SpecialiteCode: ", s.getCode());
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
    public LiveData<List<Specialite>> getSpecialiteList() {
        return specialiteList;
    }

    private List<Specialite> parseJsonResponse(JSONArray response) {
        List<Specialite> specialites;
        try {
            Type listType = new TypeToken<List<Specialite>>() {}.getType();
            Gson gson = new Gson();
            specialites = gson.fromJson(response.toString(), listType);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            specialites = new ArrayList<>();
        }

        return specialites;
    }

//    private List<Specialite> parseJsonResponse(JSONArray response) {
//        List<Specialite> specialites = new ArrayList<>();
//
//        try {
//            for (int i = 0; i < response.length(); i++) {
//                JSONObject specialiteObj = response.getJSONObject(i);
//                Specialite specialite = new Specialite();
//
//                specialite.setId(specialiteObj.optInt("id"));
//                specialite.setCode(specialiteObj.optString("code"));
//                specialite.setLibelle(specialiteObj.optString("libelle"));
//
//                specialites.add(specialite);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return specialites;
//    }
}