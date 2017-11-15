package uk.org.lsucs.lanvantracker.manager;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import uk.org.lsucs.lanvantracker.retrofit.RetrofitInstance;
import uk.org.lsucs.lanvantracker.retrofit.models.CurrentDropUp;
import uk.org.lsucs.lanvantracker.retrofit.models.DropUp;
import uk.org.lsucs.lanvantracker.retrofit.models.Person;
import uk.org.lsucs.lanvantracker.retrofit.models.VanStatus;
import uk.org.lsucs.lanvantracker.utils.MinimalDisposableObserver;

/**
 * Created by Zack on 11/9/2017.
 */

public class DataManager {

    private static final String TAG = "DataManager";

    private final Context context;

    private LinkedList<DropUp> dropUps;
    private Integer currentDropUpId;
    private boolean tracking;

    private Runnable currentDropUpUpdater;
    private Runnable dropUpsUpdater;
    private Runnable trackingStatusUpdater;

    private Handler mHandler;

    private BehaviorSubject<LinkedList<DropUp>> newDropUpsSubject;
    private BehaviorSubject<Integer> newCurrentDropUpSubject;
    private PublishSubject<Throwable> newConnectionError;

    private boolean lastResponseError;

    public DataManager(final Context context) {

        System.out.println("Data Manager Created");

        this.context = context;

        mHandler = new Handler();

        newDropUpsSubject = BehaviorSubject.create();
        newCurrentDropUpSubject = BehaviorSubject.create();
        newConnectionError = PublishSubject.create();

        initialiseRunnables(context);
    }

    public DisposableObserver<LinkedList<DropUp>> subscribeToNewDropUps(DisposableObserver<LinkedList<DropUp>> observer) {
        return newDropUpsSubject.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(observer);
    }

    public DisposableObserver<Integer> subscribeToNewCurrentDropUp(DisposableObserver<Integer> observer) {
        return newCurrentDropUpSubject.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(observer);
    }

    public DisposableObserver<Throwable> subscribeToNewConnectionError(DisposableObserver<Throwable> observer) {
        return newConnectionError.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(observer);
    }

    public DropUp getCurrentDropUp() {
        if(dropUps != null && currentDropUpId != null && currentDropUpId < dropUps.size()) {
            return dropUps.get(currentDropUpId);
        }
        return null;
    }

    public LinkedList<DropUp> getDropUps() {
        return dropUps;
    }

    public List<Person> getAllPeople() {
        List<Person> people = new ArrayList<>();

        if(dropUps != null) {
            for (DropUp dropUp : dropUps) {
                people.addAll(dropUp.getPeople());
            }
        }

        return people;
    }

    public Integer getCurrentDropUpId() {
        return currentDropUpId;
    }


    public boolean isTracking() {
        return tracking;
    }

    public void setCurrentDropUpId(int currentDropUpId) {

        if(currentDropUpId < dropUps.size() && currentDropUpId >=0) {

            this.currentDropUpId = currentDropUpId;
            newCurrentDropUpSubject.onNext(currentDropUpId);

            CurrentDropUp currentDropUp = new CurrentDropUp();
            currentDropUp.setCurrentDropUpId(currentDropUpId);

            new RetrofitInstance(context).dropUpAPI().setCurrentDropUp(currentDropUp)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new MinimalDisposableObserver<CurrentDropUp>() {
                        @Override
                        public void onNext(CurrentDropUp currentDropUp) {
                            super.onNext(currentDropUp);
                        }

                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                        }
                    });
        }
    }

    public void startSchedulers() {
        dropUpsUpdater.run();
        currentDropUpUpdater.run();
        trackingStatusUpdater.run();
    }

    public void restartSchedulers() {
        stopSchedulers();
        startSchedulers();
    }

    public void stopSchedulers() {
        mHandler.removeCallbacksAndMessages(null);
    }

    private void initialiseRunnables(final Context context) {

        dropUpsUpdater = new Runnable() {

            @Override
            public void run() {
                try {
                    new RetrofitInstance(context).dropUpAPI().getDropUps()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new MinimalDisposableObserver<LinkedList<DropUp>>() {
                                @Override
                                public void onNext(LinkedList<DropUp> returnedDropUps) {
                                    lastResponseError = false;
                                    if(returnedDropUps != null) {
                                        dropUps = returnedDropUps;
                                        newDropUpsSubject.onNext(returnedDropUps);
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.e(TAG, e.getMessage());
                                    if(!lastResponseError) {
                                        newConnectionError.onNext(e);
                                        lastResponseError = true;
                                    }
                                }
                            });
                } finally {
                    mHandler.postDelayed(dropUpsUpdater, 120 * 1000);
                }
            }
        };

        currentDropUpUpdater = new Runnable() {
            @Override
            public void run() {
                try {
                    new RetrofitInstance(context).dropUpAPI().getCurrentDropUp()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new MinimalDisposableObserver<CurrentDropUp>() {
                                @Override
                                public void onNext(CurrentDropUp currentDropUp) {
                                    lastResponseError = false;
                                    if(currentDropUp != null) {
                                        if(currentDropUp.getCurrentDropUpId() != null) {
                                            if (!currentDropUp.getCurrentDropUpId().equals(currentDropUpId)) {
                                                currentDropUpId = currentDropUp.getCurrentDropUpId();
                                                newCurrentDropUpSubject.onNext(currentDropUp.getCurrentDropUpId());
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.e(TAG, e.getMessage());
                                    if(!lastResponseError) {
                                        newConnectionError.onNext(e);
                                        lastResponseError = true;
                                    }
                                }
                            });
                } finally {
                    mHandler.postDelayed(currentDropUpUpdater, 1000);
                }
            }
        };

        trackingStatusUpdater = new Runnable() {
            @Override
            public void run() {
                try {
                    new RetrofitInstance(context).vanAPI().getVanStatus()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new MinimalDisposableObserver<VanStatus>() {
                                @Override
                                public void onNext(VanStatus vanStatus) {
                                    lastResponseError = false;
                                    if(vanStatus != null) {
                                        tracking = vanStatus.getStatus().equals("tracking");
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.e(TAG, e.getMessage());
                                    if(!lastResponseError) {
                                        newConnectionError.onNext(e);
                                        lastResponseError = true;
                                    }
                                }
                            });
                } finally {
                    mHandler.postDelayed(trackingStatusUpdater, 1000);
                }
            }
        };
    }
}
