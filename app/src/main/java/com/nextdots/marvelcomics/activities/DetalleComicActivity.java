package com.nextdots.marvelcomics.activities;

import android.accounts.NetworkErrorException;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.internal.bind.util.ISO8601Utils;
import com.google.gson.reflect.TypeToken;
import com.nextdots.marvelcomics.R;
import com.nextdots.marvelcomics.application.AppPreferences;
import com.nextdots.marvelcomics.di.component.AppComponent;
import com.nextdots.marvelcomics.rest.RestApi;
import com.nextdots.marvelcomics.rest.comic.ComicResponse;
import com.nextdots.marvelcomics.rest.comic.Date;
import com.nextdots.marvelcomics.rest.comic.Item;
import com.nextdots.marvelcomics.rest.comic.Item_;
import com.nextdots.marvelcomics.rest.comics.Result;
import com.nextdots.marvelcomics.utils.Constants;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import mehdi.sakout.dynamicbox.DynamicBox;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetalleComicActivity extends BaseActivity implements Callback<ComicResponse> {

    public static final String TAG = DetalleComicActivity.class.getSimpleName();
    @Inject
    RestApi restApi;
    @Inject
    AppPreferences appPreferences;
    @BindView(R.id.precio)
    TextView tvPrecio;
    @BindView(R.id.fecha)
    TextView tvFecha;
    @BindView(R.id.numPaginas)
    TextView tvNumPaginas;
    @BindView(R.id.series)
    TextView tvSeries;
    @BindView(R.id.creadores)
    TextView tvCreadores;
    @BindView(R.id.personajes)
    TextView tvPersonajes;
    @BindView(R.id.descripcion)
    TextView tvDescripcion;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.header_logo)
    ImageView ivHeaderLogo;
    @BindView(R.id.content_detalle_comic)
    LinearLayout contentDetalleComic;

    private int idComic;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

    private com.nextdots.marvelcomics.rest.comic.Result result;
    private DynamicBox dynamicBox;

    @Override
    protected void setupComponent(AppComponent appComponent) {
        appComponent.inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_comic);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actualizarFavorito(view);
            }
        });
        idComic = getIntent().getIntExtra(Constants.ID_COMIC, -1);
        dynamicBox = new DynamicBox(this, contentDetalleComic);
        restApi.comic(idComic, Constants.TS, Constants.APIKEY, Constants.HASH).enqueue(this);
        dynamicBox.showLoadingLayout();
        fab.setVisibility(View.GONE);
    }

    @Override
    public void onResponse(Call<ComicResponse> call, Response<ComicResponse> response) {
        dynamicBox.hideAll();
        if (response.isSuccessful()) {
            List<com.nextdots.marvelcomics.rest.comic.Result> results = response.body().getData().getResults();
            if (results != null && !results.isEmpty()) {
                result = results.get(0);
                if (result != null) {
                    loadResult();
                } else {
                    finish();
                }

            } else {
                onFailure(call, new NetworkErrorException(Constants.NETWORK_ERROR_MESSAGE));
            }
            fab.setVisibility(View.VISIBLE);
            obtenerFavorito();
        } else {
            onFailure(call, new NetworkErrorException(Constants.NETWORK_ERROR_MESSAGE));
        }

    }

    @Override
    public void onFailure(Call<ComicResponse> call, Throwable t) {
        dynamicBox.hideAll();
        Log.e(TAG, "onFailure", t);
        String sComicData = appPreferences.getPreference(Constants.COMIC_DATA, String.class);
        Type tComicData = new TypeToken<HashMap<Integer, com.nextdots.marvelcomics.rest.comic.Result>>() {
        }.getType();
        HashMap<Integer, com.nextdots.marvelcomics.rest.comic.Result> comicData
                = gson.fromJson(sComicData, tComicData);
        if (comicData != null) {
            result = comicData.get(idComic);
            if (result != null) {
                loadResult();
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

    private void loadResult() {
        setTitle(result.getTitle());
        if (result.getPrices() != null && !result.getPrices().isEmpty()) {
            if (!result.getPrices().get(0).getPrice().equals(0.0)) {
                tvPrecio.setText("$" + result.getPrices().get(0).getPrice().toString());
            } else {
                tvPrecio.setText(getString(R.string.agotado));
            }
        } else {
            tvPrecio.setText(getString(R.string.agotado));
        }
        if (result.getCreators().getItems() != null && !result.getCreators().getItems().isEmpty()) {
            StringBuilder sbCreators = new StringBuilder();
            for (Item item : result.getCreators().getItems()) {
                sbCreators.append(item.getName());
                sbCreators.append(", ");
            }
            sbCreators.deleteCharAt(sbCreators.length() - 1);
            sbCreators.deleteCharAt(sbCreators.length() - 1);
            sbCreators.append(".");
            tvCreadores.setText(sbCreators.toString());
        }
        if (result.getCharacters().getItems() != null && !result.getCharacters().getItems().isEmpty()) {
            StringBuilder sbCharacters = new StringBuilder();
            for (Item_ item : result.getCharacters().getItems()) {
                sbCharacters.append(item.getName());
                sbCharacters.append(", ");
            }
            sbCharacters.deleteCharAt(sbCharacters.length() - 1);
            sbCharacters.deleteCharAt(sbCharacters.length() - 1);
            sbCharacters.append(".");
            tvPersonajes.setText(sbCharacters.toString());
        }
        if (result.getSeries() != null && !result.getSeries().getName().isEmpty()) {
            StringBuilder sbSeries = new StringBuilder();
            sbSeries.append(result.getSeries().getName());
            sbSeries.append(".");
            tvSeries.setText(sbSeries.toString());
        }
        if (result.getDates() != null && !result.getDates().isEmpty()) {
            StringBuilder sbDates = new StringBuilder();
            for (Date date : result.getDates()) {
                sbDates.append(date.getType().replace("onsaleDate", "Fecha de venta").replace("focDate", "Fecha de creación"));
                sbDates.append(": ");
                try {
                    sbDates.append(simpleDateFormat.format(ISO8601Utils.parse(date.getDate(), new ParsePosition(0))));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                sbDates.append(", ");
            }
            sbDates.deleteCharAt(sbDates.length() - 1);
            sbDates.deleteCharAt(sbDates.length() - 1);
            sbDates.append(".");
            tvFecha.setText(sbDates.toString());
        }
        tvNumPaginas.setText(result.getPageCount().toString());
        tvDescripcion.setText(result.getDescription());
        String image = result.getThumbnail().getPath() + "/landscape_incredible." + result.getThumbnail().getExtension();
        Glide.with(this).load(image).asBitmap().centerCrop().into(ivHeaderLogo);
    }

    private void obtenerFavorito() {
        String sComicListData = appPreferences.getPreference(Constants.COMIC_LIST_DATA, String.class);
        Type tComicListData = new TypeToken<HashMap<Integer, Result>>() {
        }.getType();
        HashMap<Integer, Result> comicListData
                = gson.fromJson(sComicListData, tComicListData);
        if (comicListData == null) {
            comicListData = new HashMap<Integer, Result>();
        }

        Result comicsResult = comicListData.get(idComic);
        if (comicsResult != null) {
            fab.setImageResource(R.drawable.ic_star_black_24dp);
        } else {
            fab.setImageResource(R.drawable.ic_star_border_black_24dp);
        }
    }

    private void actualizarFavorito(View view) {
        String sComicListData = appPreferences.getPreference(Constants.COMIC_LIST_DATA, String.class);
        String sComicData = appPreferences.getPreference(Constants.COMIC_DATA, String.class);

        Type tComicListData = new TypeToken<HashMap<Integer, Result>>() {
        }.getType();
        Type tComicData = new TypeToken<HashMap<Integer, com.nextdots.marvelcomics.rest.comic.Result>>() {
        }.getType();

        HashMap<Integer, Result> comicListData
                = gson.fromJson(sComicListData, tComicListData);
        HashMap<Integer, com.nextdots.marvelcomics.rest.comic.Result> comicData
                = gson.fromJson(sComicData, tComicData);

        if (comicListData == null) {
            comicListData = new HashMap<Integer, Result>();
        }
        if (comicData == null) {
            comicData = new HashMap<Integer, com.nextdots.marvelcomics.rest.comic.Result>();
        }

        Result comicsResult = comicListData.get(idComic);
        if (comicsResult == null) {
            comicListData.put(idComic, gson.fromJson(getIntent().getStringExtra(Constants.COMICS_LIST_RESULT_ITEM)
                    , Result.class));
            comicData.put(idComic, result);

            appPreferences.savePreference(Constants.COMIC_LIST_DATA, gson.toJson(comicListData));
            appPreferences.savePreference(Constants.COMIC_DATA, gson.toJson(comicData));

            fab.setImageResource(R.drawable.ic_star_black_24dp);
            Snackbar.make(view, result.getTitle() + " agregado como favorito.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else {
            comicListData.remove(idComic);
            comicData.remove(idComic);

            appPreferences.savePreference(Constants.COMIC_LIST_DATA, gson.toJson(comicListData));
            appPreferences.savePreference(Constants.COMIC_DATA, gson.toJson(comicData));

            fab.setImageResource(R.drawable.ic_star_border_black_24dp);
            Snackbar.make(view, result.getTitle() + " quitado como favorito.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

}
