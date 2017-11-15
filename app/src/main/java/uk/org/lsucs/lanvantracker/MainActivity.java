package uk.org.lsucs.lanvantracker;

import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Random;

import io.reactivex.disposables.CompositeDisposable;
import uk.org.lsucs.lanvantracker.fragments.dashboard.DashboardFragment;
import uk.org.lsucs.lanvantracker.fragments.dropups.DropUpFragment;
import uk.org.lsucs.lanvantracker.fragments.home.HomeFragment;
import uk.org.lsucs.lanvantracker.fragments.people.PeopleFragment;
import uk.org.lsucs.lanvantracker.manager.DataManager;
import uk.org.lsucs.lanvantracker.retrofit.models.DropUp;
import uk.org.lsucs.lanvantracker.utils.MinimalDisposableObserver;

public class MainActivity extends AppCompatActivity {

    private CompositeDisposable subscriptions = new CompositeDisposable();
    private DataManager dataManager;

    private BottomNavigationView navigationView;
    private ProgressBar spinner;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("CURRENT_NAV", navigationView.getSelectedItemId());
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            return navigateToFragment(item.getItemId());
        }
    };

    private boolean navigateToFragment(int navigationId) {
        Fragment selectedFragment = null;
        switch (navigationId) {
            case R.id.navigation_home:
                selectedFragment = HomeFragment.newInstance();
                break;
            case R.id.navigation_dashboard:
                selectedFragment = DashboardFragment.newInstance();
                break;
            case R.id.navigation_people:
                selectedFragment = PeopleFragment.newInstance();
                break;
            case R.id.navigation_dropups:
                selectedFragment = DropUpFragment.newInstance();
                break;
        }

        if(selectedFragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_frame, selectedFragment);
            transaction.commit();
            return true;
        }

        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataManager = new DataManager(this);

        int navId = R.id.navigation_home;

        if(savedInstanceState != null) {
            navId = savedInstanceState.getInt("CURRENT_NAV", R.id.navigation_home);
        }

        navigateToFragment(navId);

        spinner = findViewById(R.id.spinner);

        navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        dataManager.startSchedulers();
        startSubscriptions();
    }

    @Override
    protected void onPause() {
        super.onPause();
        dataManager.stopSchedulers();
        subscriptions.clear();
    }

    public void showLoadingBar() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        spinner.setVisibility(View.VISIBLE);
    }

    public void hideLoadingBar() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        spinner.setVisibility(View.GONE);
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public void startSubscriptions() {
        subscriptions.add(dataManager.subscribeToNewConnectionError(new MinimalDisposableObserver<Throwable>() {
            @Override
            public void onNext(Throwable error) {
                System.out.println("Thrown!");
                final Snackbar snackBar = Snackbar.make(navigationView, "Something went wrong when trying to update the data from the server, is there an issue with your connection/the server?", Snackbar.LENGTH_INDEFINITE);
                snackBar.setAction("Dismiss", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snackBar.dismiss();
                    }
                }).show();
            }
        }));
    }
}