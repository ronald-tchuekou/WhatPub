/*
 * Copyright © 2019.  WhatPub by Ronald Tchuekou.
 * Les modifications appliquées ici sont les suivantes.
 * 1 --> Au chois de l'option à propos de l'application, on redirige vers une nouvelle activité.
 * qui ne contient rien que les informations concernant l'application et son anteur.
 */

package com.whatpub.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.whatpub.Adapter.Pub_adapter;
import com.whatpub.DataBase.DatabaseManager;
import com.whatpub.Services.WhatPubService;
import com.whatpub.Models.Annonce;
import com.whatpub.ui.all_pub.All_pub;
import com.whatpub.ui.pub_course.Pub_course;
import com.whatpub.ui.pub_finish.Pub_finish;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.whatpub.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import static com.whatpub.R.menu.main;

/**
 * Classe contenant les fonctions principale de l'application.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private AppBarConfiguration mAppBarConfiguration;
    private Activity activity;
    private DrawerLayout drawer;
    private Menu menu;
    private FloatingActionButton fab;
    private static int elementOfList;

    private static final int DELETEACTIVITY_CALL_ID = 1211;
    private static final int DISPLAYACTIVITY_CALL_ID = 1213;
    private static final int ADDACTIVITY_CALL_ID = 1212;

    public static NavDestination currentFragment;
    private static MaterialSearchView searchView;
    public static Activity mainActivity;
    public static int id_current_fragment;
    public static TextView emptyData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "Est Crée");

        this.activity = this;
        mainActivity = this;

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, AddPublicationActivity.class);
                startActivityForResult(intent, ADDACTIVITY_CALL_ID);
            }
        });

        drawer = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_all_pub, R.id.nav_about, R.id.nav_pub_finish, R.id.nav_pub_in_course)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Fonction qui permet de faire des choses aux cliques sur les items de la drawer navigation.
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener(){
            @Override
            public void onDestinationChanged(@NonNull NavController navcontroller, @NonNull
                    NavDestination destination, @Nullable Bundle arguments) {
                currentFragment = destination;
                int id = destination.getId();
                if (id == R.id.nav_about) {
                    hideMenu();
                    if(fab.getVisibility() == FloatingActionButton.VISIBLE)
                        fab.hide();
                }else{
                    showMenu();
                    if (fab.getVisibility() != FloatingActionButton.VISIBLE)
                        fab.show();
                }
                id_current_fragment = id;
            }

            private void hideMenu() {
                if (menu != null && menu.hasVisibleItems() )
                    menu.setGroupVisible(R.id.group_option_main, false);
            }

            private void showMenu () {
                if ( menu != null )
                    menu.setGroupVisible(R.id.group_option_main, true);
            }
        });

        searchView = findViewById(R.id.search_view);
        emptyData = findViewById(R.id.emptyData);
        elementOfList = 2; // Date de création.
        startService();

    }

    /**
     * Fonction qui permet de lancer le service.
     */
    public void startService() {
        Intent serviceIntent = new Intent(this, WhatPubService.class);
        startService(serviceIntent);
        Log.d(TAG, "startService : le travail commence !");
    }

    /**
     * Fonction qui permet de ce deplacer vers l'activité de détailles d'une publication.
     * @param annonces Liste des annonces.
     * @param position Position de l'annonce dans la liste des annonces.
     */
    public static void goToDisplay(List annonces, int position) {
        Annonce annonce = (Annonce) annonces.get(position);
        Intent intent = new Intent(mainActivity, DisplayPublicationActivity.class);
        intent.putExtra("id_annonce", annonce.getId_annonce());
        mainActivity.startActivityForResult(intent, DISPLAYACTIVITY_CALL_ID);
        Log.d(TAG, "Display the publication");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(searchView.isSearchOpen()) // Si on fais une recherche.
            notifyAdapters(activity);
        else // Si on ne fais pas de recherche.
            refreachAdapter();
        Log.d(TAG, "Le retour d'une autre activité");
    }

    /**
     * Fonction permet de notifier l'adaptater correspondant.
     * @param activity Context courant de l'application.
     */
    public static void notifyAdapters (Context activity) {
        List<Annonce> annonceList = Pub_adapter.annonces;
        if (currentFragment != null) {
            if (currentFragment.getId() == R.id.nav_all_pub) {
                All_pub.notifyAdapter(activity, annonceList);
            } else if (currentFragment.getId() == R.id.nav_pub_finish) {
                Pub_finish.notifyAdapter(activity, annonceList);
            } else if (currentFragment.getId() == R.id.nav_pub_in_course) {
                Pub_course.notifyAdapter(activity, annonceList);
            }
        }
        Log.d(TAG, "Notification de l'adapteur");
    }

    /**
     * Fonction qui permet de faire une nouvelle recharge de l'adaptateur.
     */
    public static void refreachAdapter () {
        if (currentFragment.getId() == R.id.nav_all_pub){
            All_pub.reinitAdapter();
        }else if (currentFragment.getId() == R.id.nav_pub_finish){
            Pub_finish.reinitAdapter();
        }else if (currentFragment.getId() == R.id.nav_pub_in_course) {
            Pub_course.reinitAdapter();
        }
        Log.d(TAG, "Rafraichissement de l'adaptateur.");
    }

    @Override
    public void onBackPressed() {
        //  Handle back click to close menu
        if (this.drawer.isDrawerOpen(GravityCompat.START)) {
            this.drawer.closeDrawer(GravityCompat.START);
        } else {
            if (currentFragment.getId() == R.id.nav_about) {
                AlertDialog.Builder useNavBar = new AlertDialog.Builder(MainActivity.this);
                useNavBar.setIcon(R.mipmap.ic_launcher);
                useNavBar.setTitle(R.string.titleInfo);
                useNavBar.setMessage(R.string.msgInfo);
                useNavBar.setPositiveButton("OK", null);
                useNavBar.show();
            }else
                System.exit(RESULT_OK);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(main, menu);
        this.menu = menu;
        // ici on lis le bouton de recherche à la barre de recherche.
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        searchView.setHint(getResources().getString(R.string.search_hint));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_deleted) { // Ici on gère la suppréssion des elements.
            Intent intent = new Intent(activity, DeletePublicationsActivity.class);
            intent.putExtra("id_currentFragment", id_current_fragment);
            startActivityForResult(intent, DELETEACTIVITY_CALL_ID);
        }
        else if (id == R.id.action_sort) { // Ici on gère le tris des elements.
            AlertDialog.Builder pupop = new AlertDialog.Builder(this);
            pupop.setTitle(getResources().getString(R.string.titlePupop));
            pupop.setSingleChoiceItems(new String[]{"Par nom", "Par delais", "Par date de creation"}, elementOfList, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    elementOfList = which;
                    switch (which){
                        case 0:
                            DatabaseManager.changeOrder("nom");
                            break;
                        case 1:
                            DatabaseManager.changeOrder("delais");
                            break;
                        case 2:
                            DatabaseManager.changeOrder("date");
                            break;
                    }
                    dialog.dismiss();
                    refreachAdapter(); // ici, on recharge l'adaptateur.
                }
            });
            pupop.setPositiveButton(getResources().getString(R.string.cancle), null);
            pupop.show();
        }
        else { // Cette partie concerne le menu drawer.
            drawer.openDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /**
     * Fonction qui permet de faire la recherche d'un element.
     */
    public static void startSearch(final Pub_adapter adapter){
        Log.d(TAG, "La recherche des elements est lancée.");
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                showEmptyDataView(adapter.getAnnonces());
                return false;
            }
        });
    }

    /**
     * Fonction qui peremt d'afficher ou non le message de pas de resultat.
     * @param annonces List des annonces.
     */
    public static void showEmptyDataView (List annonces) {
        if (emptyData != null)
            if(annonces.size() == 0)
                emptyData.setText(R.string.emptyData);
            else
                emptyData.setText("");
    }

}
