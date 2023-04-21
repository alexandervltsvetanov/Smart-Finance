package com.vladimircvetanov.smartfinance;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationRequest;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.vladimircvetanov.smartfinance.accounts.AccountsFragment;
import com.vladimircvetanov.smartfinance.date.DatePickerFragment;
import com.vladimircvetanov.smartfinance.favourites.FavouritesFragment;
import com.vladimircvetanov.smartfinance.favourites.IUpdateData;
import com.vladimircvetanov.smartfinance.model.Category;
import com.vladimircvetanov.smartfinance.model.Manager;
import com.vladimircvetanov.smartfinance.model.RowDisplayable;
import com.vladimircvetanov.smartfinance.transactionRelated.ItemType;
import com.vladimircvetanov.smartfinance.transactionRelated.NoteInputFragment;
import com.vladimircvetanov.smartfinance.transactionRelated.TransactionFragment;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, DatePickerDialog.OnDateSetListener, NoteInputFragment.NoteCommunicator, IUpdateData,
        TransactionFragment.LocationCommunicator {

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;

    private TextView userProfile;
    private View headerView;
    private View toolbarTitle;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private String currentLocation;

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        requestLocation();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getFragments() == null || fragmentManager.getFragments().isEmpty()) {
            fragmentManager.beginTransaction()
                    .add(R.id.main_fragment_frame, new DiagramFragment(), getString(R.string.diagram_fragment_tag))
                    .commit();
        }

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        headerView = navigationView.getHeaderView(0);
        userProfile = (TextView) headerView.findViewById(R.id.user_profile_link);
        userProfile.setText("Hi, " + Manager.getLoggedUser().getEmail());

        LinearLayout header = (LinearLayout) headerView.findViewById(R.id.header);
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        toolbarTitle = findViewById(R.id.diagram_fragment_link);
        toolbarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction()
                        .replace(R.id.main_fragment_frame, new DiagramFragment(), getString(R.string.diagram_fragment_tag))
                        .commit();
            }
        });
    }

    private void requestLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            CancellationTokenSource token = new CancellationTokenSource();
            fusedLocationProviderClient.getCurrentLocation(LocationRequest.QUALITY_HIGH_ACCURACY, token.getToken()).addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location != null) {
                        try {
                            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            currentLocation = addresses.get(0).getLocality();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        currentLocation = "No location";
                    }
                }
            });
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            currentLocation = "No location";
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_logout:
                LogoutDialogFragment dialog = new LogoutDialogFragment();
                dialog.show(getSupportFragmentManager(), getString(R.string.logout_button));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else super.onBackPressed();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_accounts:
                if (fragmentManager.getFragments() != null || !fragmentManager.getFragments().isEmpty()) {
                    fragmentManager.beginTransaction()
                            .replace(R.id.main_fragment_frame, new AccountsFragment(), getString(R.string.accounts_fragment_tag))
                            .addToBackStack(getString(R.string.accounts_fragment_tag))
                            .commit();
                }
                drawer.closeDrawer(GravityCompat.START);
                return true;

            case R.id.nav_favourites:
                if (fragmentManager.getFragments() != null || !fragmentManager.getFragments().isEmpty()) {
                    fragmentManager.beginTransaction()
                            .replace(R.id.main_fragment_frame, new FavouritesFragment(), getString(R.string.favourites_fragment_tag))
                            .addToBackStack(getString(R.string.transaction_fragment_tag))
                            .commit();
                }
                drawer.closeDrawer(GravityCompat.START);
                return false;

            case R.id.nav_income:
                if (fragmentManager.getFragments() != null || !fragmentManager.getFragments().isEmpty()) {
                    TransactionFragment fragment = TransactionFragment.getNewInstance(Category.Type.INCOME);
                    fragmentManager.beginTransaction()
                            .replace(R.id.main_fragment_frame, fragment, getString(R.string.transaction_fragment_tag))
                            .addToBackStack(getString(R.string.transaction_fragment_tag))
                            .commit();
                }
                drawer.closeDrawer(GravityCompat.START);
                return true;

            case R.id.nav_expense:
                if (fragmentManager.getFragments() != null || !fragmentManager.getFragments().isEmpty()) {
                    TransactionFragment fragment = TransactionFragment.getNewInstance(Category.Type.EXPENSE);
                    fragmentManager.beginTransaction()
                            .replace(R.id.main_fragment_frame, fragment, getString(R.string.transaction_fragment_tag))
                            .addToBackStack(getString(R.string.transaction_fragment_tag))
                            .commit();
                }
                drawer.closeDrawer(GravityCompat.START);
                return false;

            case R.id.nav_calendar:
                DialogFragment datePicker = DatePickerFragment.newInstance(this, DateTime.now());
                datePicker.show(getSupportFragmentManager(), getString(R.string.calendar_fragment_tag));
                drawer.closeDrawer(GravityCompat.START);
                return false;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {}

    @Override
    public void setNote(String note) {
        TransactionFragment t = (TransactionFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.transaction_fragment_tag));
        t.setNote(note);
    }

    @Override
    public void sendData(RowDisplayable item, ItemType type, Boolean delete) {
        IFragment frag;
        switch (type) {
            case ACCOUNT:
                frag = (AccountsFragment) fragmentManager.findFragmentByTag(getString(R.string.accounts_fragment_tag));
                break;
            case FAVOURITECATEGORY:
                frag = (FavouritesFragment) fragmentManager.findFragmentByTag(getString(R.string.favourites_fragment_tag));
                break;
            default:
                frag = (FavouritesFragment) fragmentManager.findFragmentByTag(getString(R.string.favourites_fragment_tag));
                break;
        }

        frag.updateData(item, type, delete);
    }

    @Override
    public String getLocation() {
        return currentLocation;
    }
}