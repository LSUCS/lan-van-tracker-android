package uk.org.lsucs.lanvantracker.fragments.dropups;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;

import io.reactivex.disposables.CompositeDisposable;
import uk.org.lsucs.lanvantracker.MainActivity;
import uk.org.lsucs.lanvantracker.R;
import uk.org.lsucs.lanvantracker.manager.DataManager;
import uk.org.lsucs.lanvantracker.retrofit.models.DropUp;
import uk.org.lsucs.lanvantracker.utils.MinimalDisposableObserver;

/**
 * Created by Zack on 11/9/2017.
 */

public class DropUpFragment extends Fragment {

    private DataManager dataManager;
    private DropUpAdapter dropUpAdapter;
    private CompositeDisposable subscriptions = new CompositeDisposable();

    private RecyclerView dropUpHolder;

    public static DropUpFragment newInstance() {
        DropUpFragment fragment = new DropUpFragment();
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
        View view = inflater.inflate(R.layout.fragment_dropups, container, false);
        dropUpHolder = view.findViewById(R.id.dropups_holder);
        dropUpHolder.setLayoutManager(new LinearLayoutManager(getContext()));
        dropUpAdapter = new DropUpAdapter(dataManager.getDropUps() != null ? dataManager.getDropUps() : new LinkedList<DropUp>(), dataManager);
        dropUpHolder.setAdapter(dropUpAdapter);
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
        subscriptions.add(dataManager.subscribeToNewDropUps(new MinimalDisposableObserver<LinkedList<DropUp>>() {
            @Override
            public void onNext(LinkedList<DropUp> dropUps) {
                super.onNext(dropUps);
                if (dropUps != null) {
                    dropUpAdapter = new DropUpAdapter(dropUps, dataManager);
                    dropUpHolder.setAdapter(dropUpAdapter);
                }
            }
        }));

        subscriptions.add(dataManager.subscribeToNewCurrentDropUp(new MinimalDisposableObserver<Integer>() {
            @Override
            public void onNext(Integer currentDropUp) {
                super.onNext(currentDropUp);
                if (currentDropUp != null) {
                    dropUpAdapter = new DropUpAdapter(dataManager.getDropUps() != null ? dataManager.getDropUps() : new LinkedList<DropUp>(), dataManager);
                    dropUpHolder.setAdapter(dropUpAdapter);
                }
            }
        }));
    }
}