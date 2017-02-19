package com.nextdots.marvelcomics.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.internal.bind.util.ISO8601Utils;
import com.nextdots.marvelcomics.R;
import com.nextdots.marvelcomics.di.component.AppComponent;
import com.nextdots.marvelcomics.messages.ActualizarFavoritoMessage;
import com.nextdots.marvelcomics.messages.ObtenerFavoritoMessage;
import com.nextdots.marvelcomics.messages.ToolbarMessage;
import com.nextdots.marvelcomics.presenters.DetalleComicPresenter;
import com.nextdots.marvelcomics.rest.comic.Date;
import com.nextdots.marvelcomics.rest.comic.Item;
import com.nextdots.marvelcomics.rest.comic.Item_;
import com.nextdots.marvelcomics.rest.comic.Result;
import com.nextdots.marvelcomics.utils.Constants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import mehdi.sakout.dynamicbox.DynamicBox;

public class DetalleComicFragment extends BaseFragment {

    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.app_bar)
    AppBarLayout appBar;
    private Integer idComic;
    private String comicsListResultItem;

    public static final String TAG = DetalleComicFragment.class.getSimpleName();
    @Inject
    DetalleComicPresenter detalleComicPresenter;
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

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

    private Result result;
    private DynamicBox dynamicBox;
    private Toolbar toolbar;
    private View mView;

    @Override
    protected void setupComponent(AppComponent appComponent) {
        appComponent.inject(this);
    }

    private Unbinder bind;

    public DetalleComicFragment() {
        // Required empty public constructor
    }

    public static DetalleComicFragment newInstance(Integer idComic, String comicsListResultItem) {
        DetalleComicFragment fragment = new DetalleComicFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.ID_COMIC, idComic);
        args.putString(Constants.COMICS_LIST_RESULT_ITEM, comicsListResultItem);
        fragment.setArguments(args);
        return fragment;
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
        if (getArguments() != null) {
            idComic = getArguments().getInt(Constants.ID_COMIC);
            comicsListResultItem = getArguments().getString(Constants.COMICS_LIST_RESULT_ITEM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detalle_comic, container, false);
        bind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        EventBus.getDefault().post(new ToolbarMessage(toolbar));
        toolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mView = view;
                actualizarFavorito();
            }
        });
        dynamicBox = new DynamicBox(getActivity(), contentDetalleComic);
        detalleComicPresenter.comic(idComic, Constants.TS, Constants.APIKEY, Constants.HASH);
        dynamicBox.showLoadingLayout();
        fab.setVisibility(View.GONE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(Result result) {
        this.result = result;
        dynamicBox.hideAll();
        loadResult();
        fab.setVisibility(View.VISIBLE);
        obtenerFavorito();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onError(Throwable t){
        getActivity().onBackPressed();
    }

    private void loadResult() {
        toolbarLayout.setTitle(result.getTitle());
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
        detalleComicPresenter.obtenerFavorito();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void isFavorito(ObtenerFavoritoMessage favoritoMessage){
        if(favoritoMessage.isFavorito()){
            fab.setImageResource(R.drawable.ic_star_black_24dp);
        } else {
            fab.setImageResource(R.drawable.ic_star_border_black_24dp);
        }
    }

    private void actualizarFavorito(){
        detalleComicPresenter.actualizarFavorito(comicsListResultItem, result);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFavoritoActualizado(ActualizarFavoritoMessage message) {
        if(message.isFavorito()){
            fab.setImageResource(R.drawable.ic_star_black_24dp);
            Snackbar.make(mView, result.getTitle() + " agregado como favorito.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else{
            fab.setImageResource(R.drawable.ic_star_border_black_24dp);
            Snackbar.make(mView, result.getTitle() + " quitado como favorito.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    @Override
    public void onDestroy() {
        bind.unbind();
        super.onDestroy();
    }

}
