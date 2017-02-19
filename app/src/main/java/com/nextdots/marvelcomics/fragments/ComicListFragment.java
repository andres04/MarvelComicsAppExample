package com.nextdots.marvelcomics.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.nextdots.marvelcomics.R;
import com.nextdots.marvelcomics.adapters.ComicListAdapter;
import com.nextdots.marvelcomics.di.component.AppComponent;
import com.nextdots.marvelcomics.messages.FragmentMessage;
import com.nextdots.marvelcomics.presenters.ComicListPresenter;
import com.nextdots.marvelcomics.rest.comics.ComicsResponse;
import com.nextdots.marvelcomics.rest.comics.Result;
import com.nextdots.marvelcomics.utils.Constants;
import com.nextdots.marvelcomics.utils.PFRecyclerViewAdapter;
import com.nextdots.marvelcomics.utils.Util;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import mehdi.sakout.dynamicbox.DynamicBox;

public class ComicListFragment extends BaseFragment implements PFRecyclerViewAdapter.OnViewHolderClick, SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {

    private boolean offline;

    public static final String TAG = ComicListFragment.class.getSimpleName();
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @Inject
    ComicListPresenter comicListPresenter;

    @BindView(R.id.rvComics)
    RecyclerView rvComics;
    private ComicListAdapter comicListAdapter;

    private Unbinder bind;
    private List<Result> results = new ArrayList<>();
    private Menu menu;

    private SearchView searchView;
    private DynamicBox dynamicBox;
    private boolean loading = false;


    public ComicListFragment() {
        // Required empty public constructor
    }

    public static ComicListFragment newInstance(boolean offline) {
        ComicListFragment fragment = new ComicListFragment();
        Bundle args = new Bundle();
        args.putBoolean(Constants.OFFLINE, offline);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void setupComponent(AppComponent appComponent) {
        appComponent.inject(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            offline = getArguments().getBoolean(Constants.OFFLINE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comic_list, container, false);
        bind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeRefreshLayout.setOnRefreshListener(this);
        dynamicBox = new DynamicBox(getActivity(), view);
        comicListAdapter = new ComicListAdapter(getContext(), this);
        comicListAdapter.setList(results);
        rvComics.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvComics.setAdapter(comicListAdapter);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                onRefresh();
            }
        });
    }

    @Override
    public void onRefresh() {
        results.clear();
        dynamicBox.showLoadingLayout();
        comicListAdapter.notifyDataSetChanged();
        loading = true;
        comicListPresenter.getComics(Constants.TS, Constants.APIKEY, Constants.HASH, Constants.LIMIT, offline);
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_comic_list, menu);
        this.menu = menu;
        SearchManager searchManager = (SearchManager) getContext().getSystemService(Context.SEARCH_SERVICE);
        final MenuItem searchItem = this.menu.findItem(R.id.app_bar_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_refresh) {
            Collections.shuffle(results);
            comicListAdapter.notifyDataSetChanged();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        comicListAdapter.filter(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        comicListAdapter.filter(newText);
        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(ComicsResponse comicsResponse) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
            loading = false;
            dynamicBox.hideAll();
            if (comicsResponse.getData().getResults() != null) {
                results.addAll(comicsResponse.getData().getResults());
                loadComics();
            }
        }
    }

    private void loadComics() {
        Collections.shuffle(results);
        comicListAdapter.updateAuxList();
        comicListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view, int position) {
        if(!loading) {
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.ID_COMIC, comicListAdapter.getItem(position).getId());
            bundle.putString(Constants.COMICS_LIST_RESULT_ITEM, Util.getGson().toJson(comicListAdapter.getItem(position)));
            EventBus.getDefault().post(new FragmentMessage(FragmentMessage.FROM_COMIC_LIST_TO_DETALLE_COMIC, bundle));
        }
    }

    @Override
    public void onDestroy() {
        bind.unbind();
        super.onDestroy();
    }

    public SearchView getSearchView() {
        return searchView;
    }

}
