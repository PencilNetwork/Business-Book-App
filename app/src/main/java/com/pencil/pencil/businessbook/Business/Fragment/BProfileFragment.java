package com.pencil.pencil.businessbook.Business.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.pencil.pencil.businessbook.Api.APIClient;
import com.pencil.pencil.businessbook.Api.APIInterface;
import com.pencil.pencil.businessbook.Business.Activity.CreateBusinessActivity;
import com.pencil.pencil.businessbook.Business.Adapter.OfferAdapter;
import com.pencil.pencil.businessbook.Business.Adapter.RelatedFileAdapter;
import com.pencil.pencil.businessbook.Business.Interface.EditProfileInterface;
import com.pencil.pencil.businessbook.Business.Pojo.BusinessProfileResponse;
import com.pencil.pencil.businessbook.Business.Pojo.GeneralResponseBody;
import com.pencil.pencil.businessbook.Business.Pojo.Offer;
import com.pencil.pencil.businessbook.Business.Pojo.RelatedFile;
import com.pencil.pencil.businessbook.R;
import com.pencil.pencil.businessbook.Util.PrefManager;
//import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.pencil.pencil.businessbook.Util.FileUtils.ProgressDialog;


public class BProfileFragment extends Fragment implements OnMapReadyCallback {
    //UI Variable
    @BindView(R.id.offerRecycleView)
    public RecyclerView mOfferRecyclerView;
    @BindView(R.id.relatedFileRecycleView)
    public RecyclerView mRelatedFileRecyclerView;

    OfferAdapter mOfferAdapter;
    RelatedFileAdapter mRelatedFileAdapter;
    @BindView(R.id.usernameTv)
    public TextView mUserNameTv;
    @BindView(R.id.addressTv)
    public TextView mAddressTv;
    @BindView(R.id.businessNameTv)
    public TextView mBusinessNameTv;
    @BindView(R.id.businessDescriptionTv)
    public TextView mBusinessDescriptionTv;
    @BindView(R.id.contactNumberTv)
    public TextView mContactNoTv;
    @BindView(R.id.categoryTv)
    public TextView mCategoryTv;
    @BindView(R.id.emailTv)
    public TextView mEmailTv;
    @BindView(R.id.logoIv)
    public ImageView mLogoIv;
    @BindView(R.id.businessIv)
    public ImageView mBusinessIv;
    //variable
    private Context mContext;
    //LOADING_DIALOG
    private Dialog dialog;
    PrefManager mPrefManager;
    //map
    private Double mSelectedLat = null;
    private Double mSelectedLong = null;

    private Marker mMarker;
    //location
    // Google Map variables
    private GoogleMap mMainMap;
    //static
    private static int REQUEST_PERMISSION_LOCATION = 4;

    public BProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_bprofile, container, false);
        mContext = getActivity();
        ButterKnife.bind(this, v);
        bindVariable(v);
        dialog = ProgressDialog(mContext); initialize();
        getProfileData();


        initializeMap();


        return v;
    }

    private void initializeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) (getChildFragmentManager()
                .findFragmentById(R.id.map));

        mapFragment.getMapAsync(BProfileFragment.this);

    }

    private void bindVariable(View view) {

    }

    private void initialize() {
        mOfferRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));

        mOfferAdapter = new OfferAdapter(mContext, new ArrayList<Offer>());

        mOfferRecyclerView.setAdapter(mOfferAdapter);

        mRelatedFileRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));

        mRelatedFileAdapter = new RelatedFileAdapter(mContext, new ArrayList<RelatedFile>(), false);

        mRelatedFileRecyclerView.setAdapter(mRelatedFileAdapter);
        mPrefManager = new PrefManager(mContext);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    private void getProfileData() {
        dialog.show();

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);

        //   Call<GeneralResponseBody<BusinessProfileResponse>> call = apiInterface.getProfileData(7);

        Call<GeneralResponseBody<BusinessProfileResponse>> call = apiInterface.getProfileData(mPrefManager.getDataInt(PrefManager.BUSINESS_ID));

        call.enqueue(new Callback<GeneralResponseBody<BusinessProfileResponse>>() {
            @Override
            public void onResponse(@NonNull Call<GeneralResponseBody<BusinessProfileResponse>> call, @NonNull Response<GeneralResponseBody<BusinessProfileResponse>> response) {

                if (dialog != null)
                    dialog.dismiss();
                if (response.isSuccessful()) {


                    GeneralResponseBody<BusinessProfileResponse> body = response.body();
                    BusinessProfileResponse data = response.body().data;

                    setProfileData(data);
                    Log.v("XXXX", body.toString());

                } else {
                    Gson gson = new Gson();


                }

            }

            @Override
            public void onFailure(@NonNull Call<GeneralResponseBody<BusinessProfileResponse>> call, @NonNull Throwable t) {

                try {
                    if (dialog != null)
                        dialog.dismiss();
                    Log.e("failed", t.getMessage() + call.request().url());
                    Toast.makeText(mContext, "Connection error", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void setProfileData(BusinessProfileResponse data) {
        mUserNameTv.setText(data.getName());
        mAddressTv.setText(data.getAddress());
        mBusinessNameTv.setText(data.getName());
        mBusinessDescriptionTv.setText(data.getDescription());
        mContactNoTv.setText(data.getContact_number());


        mEmailTv.setText(data.getOwner().getEmail());
        mSelectedLat = Double.parseDouble(data.getLattitude());
        mSelectedLong = Double.parseDouble(data.getLangitude());
        if (mMainMap != null) {
            LatLng latLng = new LatLng(mSelectedLat, mSelectedLong);
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng).zoom(15).build();
            mMainMap.addMarker(new MarkerOptions().position(latLng)
                    .title("selected Location"));
            mMainMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
        }

        Glide.with(mContext).load(data.getImage()).into(mBusinessIv);
        if (data.getLogo() != null) {
            Glide.with(mContext).load(data.getLogo()).into(mLogoIv);
        }
        ArrayList<Offer> offers = data.getOffers();
        mOfferAdapter.clearAll();
        if(offers!=null)
      mOfferAdapter.add(offers);
        mRelatedFileAdapter.clearAll();
        ArrayList<RelatedFile> relatedFiles = data.getFiles();
        mRelatedFileAdapter.addAll(relatedFiles);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMainMap = googleMap;


        // Get the button view

        if (mSelectedLat != null) {
            LatLng latLng = new LatLng(mSelectedLat, mSelectedLong);
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng).zoom(15).build();
            mMainMap.addMarker(new MarkerOptions().position(latLng)
                    .title("selected Location"));
            mMainMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
        }


        // }
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Refresh your fragment here
            if(mContext!=null ){
                dialog = ProgressDialog(mContext);
                mPrefManager = new PrefManager(mContext);
                getProfileData();
            }
        }}
}
