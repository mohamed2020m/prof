package me.ensa.professeur.ui.professeur;

import androidx.lifecycle.ViewModelProvider;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.ensa.professeur.R;
import me.ensa.professeur.databinding.FragmentProfesseurBinding;
import me.ensa.professeur.adapters.ProfesseurAdapter;
import me.ensa.professeur.classes.Professeur;
import me.ensa.professeur.classes.Specialite;
import me.ensa.professeur.utlis.SwipeToDeleteCallback;

public class ProfesseurFragment extends Fragment {
    private FragmentProfesseurBinding binding;
    private RecyclerView recyclerView;
    private ProfesseurAdapter adapter;
    private List<Professeur> list_professeurs = new ArrayList<>();
    private List<Specialite> list_specialite = new ArrayList<>();

    private final String URL = "http://192.168.0.131:8080/api/v1";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ProfesseurViewModel professeurViewModel =
                new ViewModelProvider(this).get(ProfesseurViewModel.class);

        binding = FragmentProfesseurBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = binding.recycleViewProfesseur;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true);
        adapter = new ProfesseurAdapter(requireContext());
        recyclerView.setAdapter(adapter);

        professeurViewModel = new ViewModelProvider(this).get(ProfesseurViewModel.class);

        // fetch data
        professeurViewModel.fetchData(requireContext());

        // Observe data from the ViewModel and update the adapter
        professeurViewModel.getProfesseurList().observe(getViewLifecycleOwner(), professeurs -> {
            Log.d("professeurs", professeurs.toString());
            adapter.setProfesseurs(professeurs);
        });

        // update Professeur
        ProfesseurViewModel finalProfesseurViewModel = professeurViewModel;
        recyclerView.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                View childView = rv.findChildViewUnder(e.getX(), e.getY());
                if (childView != null && e.getAction() == MotionEvent.ACTION_UP) {
                    int position = rv.getChildAdapterPosition(childView);
                    if (position != RecyclerView.NO_POSITION) {
                        Professeur Professeur = finalProfesseurViewModel.getProfesseurList().getValue().get(position);
                        showUpdateDialog(Professeur);
                    }
                }
                return false;
            }
        });

        // enable swipe to delete
        enableSwipeToDeleteAndUndo();

        return root;
    }

    public void showAddProfesseurDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_professeur, null);
        dialogBuilder.setView(dialogView);

        TextInputEditText add_lastName = dialogView.findViewById(R.id.add_lastName);
        TextInputEditText add_firstName = dialogView.findViewById(R.id.add_firstName);
        TextInputEditText add_email = dialogView.findViewById(R.id.add_email);
        TextInputEditText add_phone = dialogView.findViewById(R.id.add_phone);
        TextInputEditText add_dateEmbauche = dialogView.findViewById(R.id.dateEmbauche);

        add_dateEmbauche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(add_dateEmbauche, null);
            }
        });

        AutoCompleteTextView add_specialite = dialogView.findViewById(R.id.add_specialite);

        // fetch sepcialite form db
        String url = URL + "/spcialites";
        List<Specialite> specialites = new ArrayList<>();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest( Request.Method.GET, url, null,
            response -> {
                try {
                    Log.d("get_specialities", response.toString());
                    for (int i = 0; i < response.length(); i++) {
                        Specialite specialite = new Specialite();
                        JSONObject specialiteJson = response.getJSONObject(i);
                        specialite.setId(specialiteJson.optInt("id"));
                        specialite.setCode(specialiteJson.optString("code"));
                        specialite.setLibelle(specialiteJson.optString("libelle"));
                        specialites.add(specialite);
                    }

                    List<String> codesSpecialites = new ArrayList<>();
                    specialites.stream().forEach(f -> {
                        codesSpecialites.add(f.getCode());
                    });

                    Log.d("code :", codesSpecialites.toString());

                    ArrayAdapter<String> specialiteAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, codesSpecialites);
                    add_specialite.setAdapter(specialiteAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            },
            error -> {
                Log.e("Fetch Error", error.toString());
            }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(jsonArrayRequest);

        // show the dialog
        dialogBuilder.setTitle("Add Professeur");
        dialogBuilder.setPositiveButton("Add", (dialog, which) -> {
            String lastName = add_lastName.getText().toString().trim();
            String firstName = add_firstName.getText().toString().trim();
            String email = add_email.getText().toString().trim();
            String phone = add_phone.getText().toString().trim();
            String dateEmbauche = add_dateEmbauche.getText().toString().trim();
            String specialite = add_specialite.getText().toString().trim();

            Specialite chosenSpecialite = specialites.stream().filter( f -> f.getCode().equals(specialite)).findFirst().get();
            Log.d("chosenSpe", chosenSpecialite.getCode());

            DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            try {
                date = dateFormat1.parse(dateEmbauche);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            Professeur newProfesseur = new Professeur(lastName, firstName, phone, email, date);
            newProfesseur.setSpecialite(chosenSpecialite);
            saveProfesseurToDB(newProfesseur);

            //Notify the adapter about changes in the data
            adapter.notifyDataSetChanged();
        });

        dialogBuilder.setNegativeButton("Cancel", null);

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private void showDatePicker(TextInputEditText add_dateEmbauche, Date initDate) {
        Calendar calendar = Calendar.getInstance();
        int year, month, day;

        if(initDate == null){
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
        }else{
            year = initDate.getYear();
            month = initDate.getMonth();
            day = initDate.getDay();
            Log.d("month", String.valueOf(month));
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
            (view, selectedYear, selectedMonth, selectedDay) -> {
                // Update the calendar to the selected date
                calendar.set(selectedYear, selectedMonth, selectedDay);
                // Set the selected date to the EditText
                String selectedDate = selectedYear  + "-" + (selectedMonth + 1) + "-" + selectedDay;
                add_dateEmbauche.setText(selectedDate);
            }, year, month, day);

        datePickerDialog.show();
    }

    private void saveProfesseurToDB(Professeur professeur) {
        String url = URL + "/professeurs";
        JSONObject  professeurJson = createProfesseurJson(professeur);

        JsonObjectRequest  jsonArrayRequest = new JsonObjectRequest (Request.Method.POST, url, professeurJson,
                response -> {
                    Log.d("prof_res", response.toString());

                    int professeurId = 0;
                    try {
                        professeurId = response.getInt("id");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    Log.d("prof_res", response.toString());
                    professeur.setId(professeurId);
                    adapter.addProfesseur(professeur);

                    Log.d("res", response.toString());
                    Toast.makeText(requireContext(), "Created!", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    Log.e("Error", error.toString());
                }
        );

        // Instantiate Volley RequestQueue and add the request
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(jsonArrayRequest);
    }

    private JSONObject createProfesseurJson(Professeur professeur) {
        JSONObject professeurJson = new JSONObject();

        try {
            professeurJson.put("id", professeur.getId());
            professeurJson.put("lastName", professeur.getLastName());
            professeurJson.put("firstName", professeur.getFirstName());
            professeurJson.put("tel", professeur.getTel());
            professeurJson.put("email", professeur.getEmail());

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String strDate = dateFormat.format(professeur.getDateEmbauche());

            professeurJson.put("dateEmbauche", strDate);

            // Adding Specialite details if Professeur class includes a Specialite object
            Specialite specialite = professeur.getSpecialite();
            if (specialite != null) {
                JSONObject specialiteJson = new JSONObject();
                specialiteJson.put("id", specialite.getId());
                professeurJson.put("specialite", specialiteJson);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return professeurJson;
    }


    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(requireContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();
                final Professeur item = adapter.getData().get(position);
                adapter.removeItem(position);

                Snackbar snackbar = Snackbar
                        .make(recyclerView, "Professeur was removed.", Snackbar.LENGTH_LONG);

                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        adapter.restoreItem(item, position);
                        recyclerView.scrollToPosition(position);
                    }
                });

                snackbar.addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);
                        if (event == DISMISS_EVENT_TIMEOUT || event == DISMISS_EVENT_SWIPE) {
                            deleteProfesseur(item.getId(), position, item);
                        }
                    }
                });

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);
    }

    // delete a Professeur
    private void deleteProfesseur(int id, int position, Professeur item) {
        String deleteUrl = URL + "/professeurs/" + id;
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

        // set loader visible
//        loader.setVisibility(View.VISIBLE);
//        recyclerView.setVisibility(View.GONE);

        StringRequest request = new StringRequest(Request.Method.DELETE, deleteUrl,
                response -> {
//                    loader.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    Toast.makeText(requireContext(), response.toString(), Toast.LENGTH_SHORT).show();
                }, error -> {
            Toast.makeText(requireContext(), error.toString(), Toast.LENGTH_SHORT).show();
            Log.e("error", error.toString());
            adapter.restoreItem(item, position);
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("id", String.valueOf(id));
                return params;
            }
        };
        requestQueue.add(request);
    }

    private void showUpdateDialog(Professeur professeur) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.edit_professeur, null);
        dialogBuilder.setView(dialogView);

        // Initialize UI elements in the dialog layout
        TextInputEditText edit_lastName = dialogView.findViewById(R.id.edit_lastName);
        TextInputEditText edit_firstName = dialogView.findViewById(R.id.edit_firstName);
        TextInputEditText edit_email = dialogView.findViewById(R.id.edit_email);
        TextInputEditText edit_phone = dialogView.findViewById(R.id.edit_phone);
        TextInputEditText edit_dateEmbauche = dialogView.findViewById(R.id.edit_dateEmbauche);

        AutoCompleteTextView edit_spcialite= dialogView.findViewById(R.id.edit_specialite);

        // Populate the dialog with the Professeur's current information
        edit_lastName.setText(professeur.getLastName());
        edit_firstName.setText(professeur.getFirstName());
        edit_phone.setText(professeur.getTel());
        edit_email.setText(professeur.getEmail());

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = dateFormat.format(professeur.getDateEmbauche());
        edit_dateEmbauche.setText(strDate);

        edit_dateEmbauche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(edit_dateEmbauche, null);
            }
        });

        // fetch Specialite and roles
        fetchSpecialiteOptionsFromDatabase(dialogView, professeur.getSpecialite().getCode());

        dialogBuilder.setTitle("Update Professeur");
        dialogBuilder.setPositiveButton("Update", (dialog, which) -> {
            // Retrieve the updated information from the dialog
            String lastName = edit_lastName.getText().toString().trim();
            String firstName = edit_firstName.getText().toString().trim();
            String email = edit_email.getText().toString().trim();
            String phone = edit_phone.getText().toString().trim();
            String dateEmbauch = edit_dateEmbauche.getText().toString().trim();
            String spcialite = edit_spcialite.getText().toString().trim();

            Specialite chosenSpecialite = list_specialite.stream().filter( f -> f.getCode().equals(spcialite)).findFirst().get();

            // Update the Professeur's information in the dataset
            professeur.setLastName(lastName);
            professeur.setFirstName(firstName);
            professeur.setEmail(email);
            professeur.setTel(phone);

            Date date = null;
            try {
                DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
                date = dateFormat1.parse(dateEmbauch);
                professeur.setDateEmbauche(date);

            } catch (ParseException e) {
                Log.e("error_date", dateEmbauch);
               e.printStackTrace();
            }

            professeur.setSpecialite(chosenSpecialite);


            updateProfesseurToDB(professeur);

            // Notify the adapter of the data change
            adapter.notifyDataSetChanged();
        });

        dialogBuilder.setNegativeButton("Cancel", null);

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private void fetchSpecialiteOptionsFromDatabase(View dialogView, String currentSpecialiteCode) {
        String url = URL + "/spcialites";
        List<Specialite> specialites = new ArrayList<>();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest( Request.Method.GET, url, null,
            response -> {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        Specialite specialite = new Specialite();
                        JSONObject SpecialiteJson = response.getJSONObject(i);
                        specialite.setId(SpecialiteJson.optInt("id"));
                        specialite.setCode(SpecialiteJson.optString("code"));
                        specialite.setLibelle(SpecialiteJson.optString("libelle"));
                        specialites.add(specialite);
                    }
                    list_specialite.addAll(specialites);
                    Specialite Specialite = specialites.stream().filter(f -> f.getCode().equals(currentSpecialiteCode)).findFirst().get();
                    populateSpecialiteDropdown(specialites, dialogView, Specialite);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            },
            error -> {
                Log.e("SpecialiteFetchError", error.toString());
            }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(jsonArrayRequest);
    }

    private void populateSpecialiteDropdown(List<Specialite> specialiteList, View dialogView, Specialite currentSpecialite) {
        AutoCompleteTextView editSpecialite = dialogView.findViewById(R.id.edit_specialite);
        List<String> specialiteNames = new ArrayList<>();

        for (Specialite Specialite : specialiteList) {
            specialiteNames.add(Specialite.getCode());
        }

        ArrayAdapter<String> specialiteAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, specialiteNames);
        editSpecialite.setAdapter(specialiteAdapter);

        if (currentSpecialite != null) {
            int position = specialiteNames.indexOf(currentSpecialite.getCode());
            if (position != -1) {
                editSpecialite.setText(specialiteAdapter.getItem(position), false);
            }
        }
    }
    private void updateProfesseurToDB(Professeur professeur) {
        String url = URL + "/professeurs/" + professeur.getId();
        Log.d("url", url);
        JSONObject  professeurJson = createProfesseurJson(professeur);
        Log.d("professeur", professeur.toString());
        recyclerView.setVisibility(View.GONE);

        JsonObjectRequest  jsonArrayRequest = new JsonObjectRequest(Request.Method.PUT, url, professeurJson,
            response -> {
                try {
                    recyclerView.setVisibility(View.VISIBLE);
                    String message = response.getString("message");
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("res", response.toString());
            },
            error -> {
                Log.e("Error", error.toString());
            }
        );

        // Instantiate Volley RequestQueue and add the request
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(jsonArrayRequest);
    }
}
