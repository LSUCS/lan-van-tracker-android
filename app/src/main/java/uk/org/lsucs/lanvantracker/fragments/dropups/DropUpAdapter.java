package uk.org.lsucs.lanvantracker.fragments.dropups;

import android.content.Context;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import uk.org.lsucs.lanvantracker.R;
import uk.org.lsucs.lanvantracker.manager.DataManager;
import uk.org.lsucs.lanvantracker.retrofit.RetrofitInstance;
import uk.org.lsucs.lanvantracker.retrofit.models.CurrentDropUp;
import uk.org.lsucs.lanvantracker.retrofit.models.DropUp;
import uk.org.lsucs.lanvantracker.utils.MinimalDisposableObserver;

/**
 * Created by Zack on 11/11/2017.
 */

public class DropUpAdapter extends RecyclerView.Adapter<DropUpAdapter.DropUpViewHolder> {

    private List<DropUp> dropUps;
    private Context context;
    private DataManager dataManager;

    DropUpAdapter(List<DropUp> dropUps, DataManager dataManager) {
        this.dropUps = dropUps;
        this.dataManager = dataManager;
        setHasStableIds(true);
    }

    @Override
    public int getItemCount() {
        return dropUps.size();
    }

    @Override
    public DropUpViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dropup, parent, false);
        DropUpViewHolder dvh = new DropUpViewHolder(v);
        context = parent.getContext();
        return dvh;
    }

    @Override
    public void onBindViewHolder(final DropUpViewHolder holder, final int position) {
        holder.dropUpDetails.setText(new StringBuilder(dropUps.get(position).getTime()).append(" - ").append(dropUps.get(position).getAddress()).append(" - No. People: ").append(dropUps.get(position).getPeople().size()));

        if(dataManager.getCurrentDropUpId() == position) {
            holder.rootLayout.setBackgroundColor(Color.parseColor("#59f15d22"));
        }
    }

    public static class DropUpViewHolder extends RecyclerView.ViewHolder {
        TextView dropUpDetails;
        ConstraintLayout rootLayout;

        DropUpViewHolder(View itemView) {
            super(itemView);
            dropUpDetails = itemView.findViewById(R.id.dropup_details);
            rootLayout = itemView.findViewById(R.id.root_layout);
        }
    }

    public void setDropUps(List<DropUp> dropUps) {
        this.dropUps = dropUps;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
