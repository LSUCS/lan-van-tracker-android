package uk.org.lsucs.lanvantracker.fragments.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.File;
import java.net.URI;
import java.util.LinkedList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import uk.org.lsucs.lanvantracker.MainActivity;
import uk.org.lsucs.lanvantracker.R;
import uk.org.lsucs.lanvantracker.manager.DataManager;
import uk.org.lsucs.lanvantracker.retrofit.RetrofitInstance;
import uk.org.lsucs.lanvantracker.retrofit.models.DropUp;
import uk.org.lsucs.lanvantracker.service.LocationService;
import uk.org.lsucs.lanvantracker.utils.MinimalDisposableObserver;
import uk.org.lsucs.lanvantracker.utils.SharedPreferencesUtil;

/**
 * Created by Zack on 11/9/2017.
 */

public class HomeFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private MainActivity mainActivity;
    private DataManager dataManager;

    private EditText authKeyText;
    private Button authKeyButton;
    private Switch trackingSwitch;
    private Button uploadButton;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void initViews(View view) {
        authKeyText = view.findViewById(R.id.auth_key_text);
        authKeyButton = view.findViewById(R.id.auth_key_button);

        trackingSwitch = view.findViewById(R.id.tracking_switch);
        trackingSwitch.setChecked(LocationService.isRunning());

        uploadButton = view.findViewById(R.id.upload_csv_button);
    }

    public void registerListeners() {
        authKeyButton.setOnClickListener(this);
        uploadButton.setOnClickListener(this);

        trackingSwitch.setOnCheckedChangeListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        mainActivity = (MainActivity) getActivity();
        dataManager = mainActivity.getDataManager();
        initViews(view);
        registerListeners();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        authKeyText.setText(SharedPreferencesUtil.getStringValue(getContext(), SharedPreferencesUtil.AUTH_KEY));
        trackingSwitch.setOnCheckedChangeListener(null);
        trackingSwitch.setOnCheckedChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.auth_key_button: {
                SharedPreferencesUtil.addKeyValue(getContext(), SharedPreferencesUtil.AUTH_KEY, String.valueOf(authKeyText.getText()));
                Snackbar.make(getView(), "Authentication key saved successfully.", Snackbar.LENGTH_LONG).show();
                dataManager.restartSchedulers();
                break;
            }
            case R.id.upload_csv_button: {
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            1);
                } else {
                    openFilePicker();
                }
                break;
            }
        }
    }

    private void openFilePicker() {

        DialogProperties properties = new DialogProperties();

        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = new File(DialogConfigs.DEFAULT_DIR);
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.offset = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = null;

        FilePickerDialog dialog = new FilePickerDialog(getActivity(), properties);
        dialog.setTitle("Select the CSV");

        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                if(files.length >= 1) {
                    File selectedFile = new File(files[0]);
                    if(selectedFile.getAbsolutePath().toLowerCase().endsWith(".csv")) {
                        //String type = getContext().getContentResolver().getType(Uri.fromFile(selectedFile));
                        RequestBody requestFile = RequestBody.create(MediaType.parse("test/csv"), selectedFile);
                        MultipartBody.Part body = MultipartBody.Part.createFormData("file", selectedFile.getName(), requestFile);
                        mainActivity.showLoadingBar();
                        mainActivity.getRetrofitInstance().dropUpAPI().setDropUps(body)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new MinimalDisposableObserver<LinkedList<DropUp>>() {
                                    @Override
                                    public void onNext(LinkedList<DropUp> dropUps) {
                                        Snackbar.make(getView(), "The new DropUps were uploaded successfully", Snackbar.LENGTH_LONG).show();
                                        mainActivity.hideLoadingBar();
                                        dataManager.restartSchedulers();
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        mainActivity.hideLoadingBar();
                                        Snackbar.make(getView(), "Something went wrong whilst trying to upload the new DropUps, please try again", Snackbar.LENGTH_LONG).show();
                                    }
                                });
                    } else {
                        Snackbar.make(getView(), "The file you select must be a CSV and end in .csv", Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        });

        dialog.show();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch(compoundButton.getId()) {
            case R.id.tracking_switch: {
                if(b) {
                    if(dataManager.isTracking()) {
                        Snackbar.make(getView(), "Tracking is already running on another device.", Snackbar.LENGTH_LONG).show();
                        compoundButton.setChecked(false);
                    } else {
                        if (ContextCompat.checkSelfPermission(getActivity(),
                                Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {

                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    0);
                            trackingSwitch.setChecked(false);
                        } else {
                            enableLocationTracking();
                        }
                    }
                } else {
                    getActivity().stopService(new Intent(getActivity(), LocationService.class));
                }
                break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    enableLocationTracking();
                    trackingSwitch.setChecked(true);
                } else {
                    Snackbar.make(getView(), "You must give the app location permissions to enable tracking.", Snackbar.LENGTH_LONG).show();
                }
                break;
            }
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    openFilePicker();

                } else {
                    Snackbar.make(getView(), "You must give the app file read permissions in order to upload the CSV plan.", Snackbar.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    public void enableLocationTracking() {

        Intent intent = new Intent(getActivity(), LocationService.class);
        ContextCompat.startForegroundService(getActivity(), intent);
    }
}
