package com.nextdots.marvelcomics.fragments;

import android.accounts.NetworkErrorException;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nextdots.marvelcomics.R;
import com.nextdots.marvelcomics.activities.DetalleComicActivity;
import com.nextdots.marvelcomics.adapters.ComicListAdapter;
import com.nextdots.marvelcomics.application.AppPreferences;
import com.nextdots.marvelcomics.di.component.AppComponent;
import com.nextdots.marvelcomics.rest.RestApi;
import com.nextdots.marvelcomics.rest.comics.ComicsResponse;
import com.nextdots.marvelcomics.rest.comics.Result;
import com.nextdots.marvelcomics.utils.Constants;
import com.nextdots.marvelcomics.utils.PFRecyclerViewAdapter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import mehdi.sakout.dynamicbox.DynamicBox;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ComicListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComicListFragment extends BaseFragment implements Callback<ComicsResponse>, PFRecyclerViewAdapter.OnViewHolderClick, SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String TAG = ComicListFragment.class.getSimpleName();
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    @Inject
    RestApi restApi;
    @Inject
    AppPreferences appPreferences;

    @BindView(R.id.rvComics)
    RecyclerView rvComics;
    private ComicListAdapter comicListAdapter;

    Gson gson = new Gson();
    private Unbinder bind;
    private List<Result> results = new ArrayList<>();
    private Menu menu;

    private SearchView searchView;
    private DynamicBox dynamicBox;
    private boolean loading = false;


    public ComicListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ComicListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ComicListFragment newInstance(String param1, String param2) {
        ComicListFragment fragment = new ComicListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void setupComponent(AppComponent appComponent) {
        appComponent.inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_comic_list, container, false);
        bind = ButterKnife.bind(this, inflate);
        return inflate;
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
        restApi.comics(Constants.TS, Constants.APIKEY, Constants.HASH, Constants.LIMIT).enqueue(ComicListFragment.this);
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

    @Override
    public void onResponse(Call<ComicsResponse> call, Response<ComicsResponse> response) {
        swipeRefreshLayout.setRefreshing(false);
        loading = false;
        dynamicBox.hideAll();
        if (response.isSuccessful()) {
            if (response.body().getData().getResults() != null) {
                results.addAll(response.body().getData().getResults());
                loadComics();
            } else {
                onFailure(call, new NetworkErrorException(Constants.NETWORK_ERROR_MESSAGE));
            }
        } else {
            onFailure(call, new NetworkErrorException(Constants.NETWORK_ERROR_MESSAGE));
        }
    }

    @Override
    public void onFailure(Call<ComicsResponse> call, Throwable t) {
        Log.e(TAG, "onFailure", t);
        swipeRefreshLayout.setRefreshing(false);
        dynamicBox.hideAll();
        loading = false;
        String sComicListData = appPreferences.getPreference(Constants.COMIC_LIST_DATA, String.class);
        Type tComicListData = new TypeToken<HashMap<Integer, Result>>() {
        }.getType();
        HashMap<Integer, Result> comicListData
                = gson.fromJson(sComicListData, tComicListData);
        if (comicListData == null) {
            comicListData = new HashMap<Integer, Result>();
        }
        results.addAll(comicListData.values());
        loadComics();
    }

    private void loadComics() {
        Collections.shuffle(results);
        comicListAdapter.updateAuxList();
        comicListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view, int position) {
        if(!loading) {
            Intent intent = new Intent(getContext(), DetalleComicActivity.class);
            intent.putExtra(Constants.ID_COMIC, comicListAdapter.getItem(position).getId());
            intent.putExtra(Constants.COMICS_LIST_RESULT_ITEM, gson.toJson(comicListAdapter.getItem(position)));
            startActivity(intent);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        bind.unbind();
        super.onDestroy();
    }

    public SearchView getSearchView() {
        return searchView;
    }

    public void setSearchView(SearchView searchView) {
        this.searchView = searchView;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
