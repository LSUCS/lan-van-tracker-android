package uk.org.lsucs.lanvantracker.fragments.people;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import uk.org.lsucs.lanvantracker.R;
import uk.org.lsucs.lanvantracker.retrofit.models.Person;

/**
 * Created by Zack on 11/11/2017.
 */

public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.PersonViewHolder> {

    private List<Person> people;
    private Context context;

    PeopleAdapter(List<Person> people) {
        this.people = people;
    }

    @Override
    public int getItemCount() {
        return people.size();
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_person, parent, false);
        PersonViewHolder pvh = new PersonViewHolder(v);
        context = parent.getContext();
        return pvh;
    }

    @Override
    public void onBindViewHolder(final PersonViewHolder holder, final int position) {
        holder.personDetails.setText(new StringBuilder(people.get(position).getName()).append(" - ").append(people.get(position).getPhonenumber()));
        holder.personPhoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + people.get(holder.getAdapterPosition()).getPhonenumber()));
                context.startActivity(intent);
            }
        });
    }

    public static class PersonViewHolder extends RecyclerView.ViewHolder {
        TextView personDetails;
        ImageView personPhoneButton;

        PersonViewHolder(View itemView) {
            super(itemView);
            personDetails = itemView.findViewById(R.id.person_details);
            personPhoneButton = itemView.findViewById(R.id.person_phone_button);
        }
    }

    public void setPeople(List<Person> people) {
        this.people = people;
    }
}
