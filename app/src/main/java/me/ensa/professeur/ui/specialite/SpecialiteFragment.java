package me.ensa.professeur.ui.specialite;

import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import me.ensa.professeur.R;
import me.ensa.professeur.databinding.FragmentSpecialiteBinding;
import me.ensa.professeur.adapters.SpecialiteAdapter;
import me.ensa.professeur.classes.Specialite;
import me.ensa.professeur.utlis.SwipeToDeleteCallback;

public class SpecialiteFragment extends Fragment {
    private FragmentSpecialiteBinding binding;
    private RecyclerView recyclerView;
    private SpecialiteAdapter adapter;
    private final String URL = "http://192.168.0.131:8080/api/v1";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        SpecialiteViewModel specialiteViewModel = new ViewModelProvider(this).get(SpecialiteViewModel.class);

        binding = FragmentSpecialiteBinding.inflate(inflater, container, false);

        View root = binding.getRoot();
        recyclerView = binding.recycleViewSpecialite;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true);
        adapter = new SpecialiteAdapter(requireContext());
        recyclerView.setAdapter(adapter);

        specialiteViewModel = new ViewModelProvider(this).get(SpecialiteViewModel.class);

        // fetch data
        specialiteViewModel.fetchData(requireContext());

        specialiteViewModel.getSpecialiteList().observe(getViewLifecycleOwner(), students -> {
            adapter.setspecialites(students);
        });

        // update Filiere
        SpecialiteViewModel finalSpecialiteViewModel = specialiteViewModel;
        recyclerView.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                View childView = rv.findChildViewUnder(e.getX(), e.getY());
                if (childView != null && e.getAction() == MotionEvent.ACTION_UP) {
                    int position = rv.getChildAdapterPosition(childView);
                    if (position != RecyclerView.NO_POSITION) {
                        Specialite specialite = finalSpecialiteViewModel.getSpecialiteList().getValue().get(position);
                        showUpdateDialog(specialite);
                    }
                }
                return false;
            }
        });

//        // enable swipe to delete
        enableSwipeToDeleteAndUndo();

        return root;
    }

    public void showAddSpecialiteDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_specialite, null);
        dialogBuilder.setView(dialogView);

        TextInputEditText add_code = dialogView.findViewById(R.id.add_code);
        TextInputEditText add_libelle = dialogView.findViewById(R.id.add_libelle);

        // show the dialog
        dialogBuilder.setTitle("Add Specialite");
        dialogBuilder.setPositiveButton("Add", (dialog, which) -> {
            String code = add_code.getText().toString().trim();
            String libelle = add_libelle.getText().toString().trim();

            Specialite newFiliere = new Specialite(code, libelle);
            try {
                saveSpecialiteToDB(newFiliere);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            //Notify the adapter about changes in the data
            adapter.notifyDataSetChanged();
        });

        dialogBuilder.setNegativeButton("Cancel", null);

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private void saveSpecialiteToDB(Specialite specialite) throws JSONException {
        String url = URL + "/spcialites";
        JSONObject filiereJSON = new JSONObject();
        filiereJSON.put("code", specialite.getCode());
        filiereJSON.put("libelle", specialite.getLibelle());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.POST, url, filiereJSON,
                response -> {

                    int filiereId = 0;
                    try {
                        filiereId = response.getInt("id");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    specialite.setId(filiereId);
                    adapter.addSpecialite(specialite);

                    recyclerView.setVisibility(View.VISIBLE);

                    Log.d("res", response.toString());
                    Toast.makeText(requireContext(), "Specialite Created!", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    Log.e("Error", error.toString());
                }
        );

        // Instantiate Volley RequestQueue and add the request
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(jsonObjectRequest);
    }


    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(requireContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();
                final Specialite item = adapter.getData().get(position);
                Log.d("item", item.getId() + ", " + item.getCode());
                adapter.removeItem(position);

                Snackbar snackbar = Snackbar
                        .make(recyclerView, "Filiere was removed.", Snackbar.LENGTH_LONG);

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
                            deleteSpecialite(item.getId(), position, item);
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

    // delete a Filiere
    private void deleteSpecialite(int id, int position, Specialite item) {
        String deleteUrl = URL + "/spcialites/" + id;

        // set loader visible
//        loader.setVisibility(View.VISIBLE);
//        recyclerView.setVisibility(View.GONE);

        StringRequest request = new StringRequest(Request.Method.DELETE, deleteUrl,
                response -> {
//                    loader.setVisibility(View.GONE);
//                    recyclerView.setVisibility(View.VISIBLE);
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

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(request);
    }

    // update Filiere
    private void showUpdateDialog(Specialite specialite) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.edit_specialite, null);
        dialogBuilder.setView(dialogView);

        // Initialize UI elements in the dialog layout
        TextInputEditText edit_specialite_code = dialogView.findViewById(R.id.edit_specialite_code);
        TextInputEditText edit_specialite_libelle = dialogView.findViewById(R.id.edit_specialite_libelle);

        // Populate the dialog with the student's current information
        edit_specialite_code.setText(specialite.getCode());
        edit_specialite_libelle.setText(specialite.getLibelle());

        dialogBuilder.setTitle("Update Specialite");
        dialogBuilder.setPositiveButton("Update", (dialog, which) -> {
            // Retrieve the updated information from the dialog
            String code = edit_specialite_code.getText().toString().trim();
            String libelle = edit_specialite_libelle.getText().toString().trim();

            specialite.setCode(code);
            specialite.setLibelle(libelle);

            try {
                updateSpcialiteToDB(specialite);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            // Notify the adapter of the data change
            adapter.notifyDataSetChanged();
        });

        dialogBuilder.setNegativeButton("Cancel", null);

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private void updateSpcialiteToDB(Specialite specialite) throws JSONException {
        String url = URL + "/spcialites/" + specialite.getId();
        JSONObject specialiteJSON = new JSONObject();
        specialiteJSON.put("id", specialite.getId());
        specialiteJSON.put("code", specialite.getCode());
        specialiteJSON.put("libelle", specialite.getLibelle());

        recyclerView.setVisibility(View.GONE);
//        loader.setVisibility(View.VISIBLE);

        JsonObjectRequest  jsonArrayRequest = new JsonObjectRequest(Request.Method.PUT, url, specialiteJSON,
                response -> {
                    try {
//                        loader.setVisibility(View.GONE);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}