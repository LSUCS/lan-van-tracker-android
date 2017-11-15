package uk.org.lsucs.lanvantracker.fragments.dashboard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import io.reactivex.disposables.CompositeDisposable;
import uk.org.lsucs.lanvantracker.MainActivity;
import uk.org.lsucs.lanvantracker.R;
import uk.org.lsucs.lanvantracker.manager.DataManager;
import uk.org.lsucs.lanvantracker.retrofit.models.DropUp;
import uk.org.lsucs.lanvantracker.utils.MinimalDisposableObserver;

/**
 * Created by Zack on 11/9/2017.
 */

public class DashboardFragment extends Fragment implements View.OnClickListener {

    private CompositeDisposable subscriptions = new CompositeDisposable();
    private DataManager dataManager;

    private TextView addressText;
    private Button addressButton;
    private Button nextDropUpButton;
    private Button previousDropUpButton;

    public static DashboardFragment newInstance() {
        DashboardFragment fragment = new DashboardFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        dataManager = ((MainActivity) getActivity()).getDataManager();
        initViews(view);
        registerListeners();
        return view;
    }

    public void initViews(View view) {
        addressText = view.findViewById(R.id.address_text);
        addressButton = view.findViewById(R.id.address_button);
        nextDropUpButton = view.findViewById(R.id.next_dropup_button);
        previousDropUpButton = view.findViewById(R.id.previous_dropup_button);
    }

    public void registerListeners() {
        addressButton.setOnClickListener(this);
        nextDropUpButton.setOnClickListener(this);
        previousDropUpButton.setOnClickListener(this);
    }

    public void startSubscriptions() {
        subscriptions.add(dataManager.subscribeToNewCurrentDropUp(new MinimalDisposableObserver<Integer>() {
            @Override
            public void onNext(Integer integer) {
                super.onNext(integer);
                DropUp currentDropUp = dataManager.getCurrentDropUp();
                if(currentDropUp != null) {
                    addressText.setText(currentDropUp.getAddress());
                }
            }
        }));
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

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.address_button: {
                if(dataManager.getCurrentDropUpId() != null) {
                    Uri gmmIntentUri = Uri.parse("google.navigation:mode=d&q=" + dataManager.getCurrentDropUp().getAddress().replace(' ', '+'));
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                    break;
                } else {
                    Snackbar.make(getView(), "Current DropUp ID is not set, is there something wrong with the server or your internet connection?", Snackbar.LENGTH_LONG).show();
                }
            }
            case R.id.next_dropup_button: {
                if(dataManager.getCurrentDropUpId() != null) {
                    Integer currentId = dataManager.getCurrentDropUpId();
                    dataManager.setCurrentDropUpId(currentId != null ? currentId + 1 : 0);
                    break;
                } else {
                    Snackbar.make(getView(), "Current DropUp ID is not set, is there something wrong with the server or your internet connection?", Snackbar.LENGTH_LONG).show();
                }
            }
            case R.id.previous_dropup_button: {
                if(dataManager.getCurrentDropUpId() != null) {
                    Integer currentId = dataManager.getCurrentDropUpId();
                    dataManager.setCurrentDropUpId(currentId != null ? currentId - 1 : 0);
                    break;
                } else {
                    Snackbar.make(getView(), "Current DropUp ID is not set, is there something wrong with the server or your internet connection?", Snackbar.LENGTH_LONG).show();
                }
            }
        }
    }
}
