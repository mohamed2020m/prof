package me.ensa.professeur.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import me.ensa.professeur.R;
import me.ensa.professeur.classes.Specialite;

public class SpecialiteAdapter extends RecyclerView.Adapter<SpecialiteAdapter.SpecialiteViewHolder>{
    private List<Specialite> specialites;
    private Context context;

    public SpecialiteAdapter(Context context) {
        this.context = context;
    }

    public List<Specialite> getspecialites() {
        return specialites;
    }

    public void addSpecialite(Specialite student) {
        // Check if the dataset exists
        if (specialites == null) {
            specialites = new ArrayList<>();
        }

        // Add the new student to the dataset
        specialites.add(student);

        // Notify the adapter that the dataset has changed
        notifyDataSetChanged();
    }

    public void setspecialites(List<Specialite> specialites) {
        this.specialites = specialites;
        notifyDataSetChanged();
    }

    public List<Specialite> getData(){
        return specialites;
    }

    public void removeItem(int position) {
        specialites.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Specialite item, int position) {
        specialites.add(position, item);
        notifyItemInserted(position);
    }

    @NonNull
    @Override
    public SpecialiteViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(this.context).inflate(R.layout.specialites_list, viewGroup, false);
        final SpecialiteViewHolder holder = new SpecialiteViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SpecialiteViewHolder SpecialiteViewHolder, int i) {
        SpecialiteViewHolder.code.setText(specialites.get(i).getCode());
        SpecialiteViewHolder.libelle.setText(specialites.get(i).getLibelle());

    }

    @Override
    public int getItemCount() {
        if(specialites == null){
            return 0;
        }
        return specialites.size();
    }

    public class SpecialiteViewHolder extends RecyclerView.ViewHolder {
        TextView code, libelle;
        CardView parent;
        public SpecialiteViewHolder(@NonNull View itemView) {
            super(itemView);
            code = itemView.findViewById(R.id.code);
            libelle = itemView.findViewById(R.id.libelle);

            parent = itemView.findViewById(R.id.parent);
        }
    }

}