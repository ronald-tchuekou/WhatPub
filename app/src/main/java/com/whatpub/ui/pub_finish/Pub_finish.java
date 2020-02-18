/*
 * Copyright © 2019.  WhatPub by Ronald Tchuekou.
 */

package com.whatpub.ui.pub_finish;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.whatpub.Adapter.Pub_adapter;
import com.whatpub.DataBase.DatabaseManager;
import com.whatpub.Models.Annonce;
import com.whatpub.R;
import com.whatpub.activities.MainActivity;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class Pub_finish extends Fragment {

    public static final int ID_FRAG = R.id.nav_pub_finish;
    private static Pub_adapter adapter;
    private static RecyclerView recyclerView;
    private View view;
    private List<Annonce> annonces;
    private static DatabaseManager databaseManager;
    private static TextView text_empty_view;
    private static Context context;
    private SwipeRefreshLayout refreshLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_all_pub, container, false);
        initDatabase();
        initViews();
        initAdapter();
        context = getContext();
        MainActivity.startSearch(adapter);
        showEmptyView();
        MainActivity.showEmptyDataView(annonces);
        refreshListener();

        return view;
    }

    /**
     * Fonction qui permet de faire la mis à jour des annonces dans l'application.
     */
    private void refreshListener () {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                notifyAdapter(context, databaseManager.getAllAnnonceFinish());
                refreshLayout.setRefreshing(false);
            }
        });
    }

    /**
     * Fonction qui permet de masquer on non le message de prevention de liste vide.
     */
    public static void showEmptyView() {
        if(adapter.getItemCount()== 0){
            text_empty_view.setText(context.getResources().getString(R.string.emptyData));
        }else{
            text_empty_view.setText("");
        }
    }

    /**
     * Fonction qui permet de notifier l'adaptateur de la classe.
     * @param context Context de l'application en cours.
     * @param annonces Liste des annonces pour une recherche.
     */
    public static void notifyAdapter(Context context, List<Annonce> annonces) {
        adapter = new Pub_adapter(context, annonces);
        recyclerView.setAdapter(adapter);
        showEmptyView();
    }

    /**
     * Fonction qui permet de reinitialiser l'adapteur.
     */
    public static void reinitAdapter () {
        adapter = new Pub_adapter(context, databaseManager.getAllAnnonceFinish());
        recyclerView.setAdapter(adapter);
        showEmptyView();
    }


    /**
     *Fonction qu initialise l'adapteur.
     */
    private void initAdapter() {
        adapter = new Pub_adapter(getContext(), databaseManager.getAllAnnonceFinish());
        recyclerView.setAdapter(adapter);
    }

    /**
     *Fonction qui permet d'initialiser la base de données.
     */
    private void initDatabase() {
        databaseManager = new DatabaseManager(getContext());
        annonces = databaseManager.getAllAnnonceFinish();
    }

    /**
     * Fonction qui permet d'initialiser les vues.
     */
    private void initViews() {
        recyclerView = view.findViewById(R.id.recycler_publications);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        text_empty_view = view.findViewById(R.id.text_empty_view);
        refreshLayout = view.findViewById(R.id.refreshLayout);
    }

}