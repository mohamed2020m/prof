package me.ensa.professeur.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.ensa.professeur.R;
import me.ensa.professeur.classes.Professeur;

public class ProfesseurAdapter extends RecyclerView.Adapter<ProfesseurAdapter.ProfesseurViewHolder>{
    private List<Professeur> professeurs;
    private Context context;

    public ProfesseurAdapter(Context context) {
        this.context = context;
    }

    public List<Professeur> getProfesseurs() {
        return professeurs;
    }

    public void addProfesseur(Professeur student) {
        // Check if the dataset exists
        if (professeurs == null) {
            professeurs = new ArrayList<>();
        }

        // Add the new student to the dataset
        professeurs.add(student);

        // Notify the adapter that the dataset has changed
        notifyDataSetChanged();
    }

    public void setProfesseurs(List<Professeur> professeurs) {
        this.professeurs = professeurs;
        notifyDataSetChanged();
    }

    public List<Professeur> getData(){
        return professeurs;
    }

    public void removeItem(int position) {
        professeurs.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Professeur item, int position) {
        professeurs.add(position, item);
        notifyItemInserted(position);
    }

    @NonNull
    @Override
    public ProfesseurAdapter.ProfesseurViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(this.context).inflate(R.layout.professeur_list, viewGroup, false);
        final ProfesseurAdapter.ProfesseurViewHolder holder = new ProfesseurAdapter.ProfesseurViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProfesseurAdapter.ProfesseurViewHolder ProfesseurViewHolder, int i) {
        ProfesseurViewHolder.name.setText(professeurs.get(i).getLastName());
        ProfesseurViewHolder.email.setText(professeurs.get(i).getEmail());
        ProfesseurViewHolder.phone.setText(professeurs.get(i).getTel());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = dateFormat.format(professeurs.get(i).getDateEmbauche());
        ProfesseurViewHolder.dateEmbauche.setText(strDate);
        ProfesseurViewHolder.specialite_code.setText(professeurs.get(i).getSpecialite().getCode());
    }

    @Override
    public int getItemCount() {
        if(professeurs == null){
            return 0;
        }
        return professeurs.size();
    }

    public class ProfesseurViewHolder extends RecyclerView.ViewHolder {
        TextView name, email, phone, specialite_code, dateEmbauche;
        CardView parent;
        public ProfesseurViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            email = itemView.findViewById(R.id.email);
            phone = itemView.findViewById(R.id.phone);
            specialite_code = itemView.findViewById(R.id.specialite_code);
            dateEmbauche = itemView.findViewById(R.id.dateEmbauche);

            parent = itemView.findViewById(R.id.parent);
        }
    }

}
