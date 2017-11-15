package uk.org.lsucs.lanvantracker.fragments.people;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import io.reactivex.disposables.CompositeDisposable;
import uk.org.lsucs.lanvantracker.MainActivity;
import uk.org.lsucs.lanvantracker.R;
import uk.org.lsucs.lanvantracker.manager.DataManager;
import uk.org.lsucs.lanvantracker.retrofit.models.DropUp;
import uk.org.lsucs.lanvantracker.retrofit.models.Person;
import uk.org.lsucs.lanvantracker.utils.MinimalDisposableObserver;

/**
 * Created by Zack on 11/9/2017.
 */

public class PeopleFragment extends Fragment {

    private DataManager dataManager;
    private PeopleAdapter peopleAdapter;
    private CompositeDisposable subscriptions = new CompositeDisposable();

    private RecyclerView personHolder;

    public static PeopleFragment newInstance() {
        PeopleFragment fragment = new PeopleFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dataManager = ((MainActivity) getActivity()).getDataManager();
        View view = inflater.inflate(R.layout.fragment_people, container, false);
        personHolder = view.findViewById(R.id.people_holder);
        personHolder.setLayoutManager(new LinearLayoutManager(getContext()));
        peopleAdapter = new PeopleAdapter(dataManager.getCurrentDropUp() != null ? dataManager.getCurrentDropUp().getPeople() : new ArrayList<Person>());
        personHolder.setAdapter(peopleAdapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        startSubscriptions();
    }

    @Override
    public void onPause() {
        super.onPause();
        subscriptions.clear();
    }

    public void startSubscriptions() {
        subscriptions.add(dataManager.subscribeToNewCurrentDropUp(new MinimalDisposableObserver<Integer>() {
            @Override
            public void onNext(Integer integer) {
                super.onNext(integer);
                DropUp currentDropUp = dataManager.getCurrentDropUp();
                if (currentDropUp != null) {
                    peopleAdapter.setPeople(currentDropUp.getPeople());
                    peopleAdapter.notifyDataSetChanged();
                }
            }
        }));
    }
}