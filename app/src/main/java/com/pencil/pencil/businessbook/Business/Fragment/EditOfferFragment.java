package com.pencil.pencil.businessbook.Business.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.pencil.pencil.businessbook.Api.APIClient;
import com.pencil.pencil.businessbook.Api.APIInterface;
import com.pencil.pencil.businessbook.Business.Adapter.EditOfferAdapter;
import com.pencil.pencil.businessbook.Business.Interface.EditProfileInterface;
import com.pencil.pencil.businessbook.Business.Pojo.BusinessProfileResponse;
import com.pencil.pencil.businessbook.Business.Pojo.GeneralResponseBody;
import com.pencil.pencil.businessbook.Business.Pojo.Offer;
import com.pencil.pencil.businessbook.Business.Pojo.RelatedFile;
import com.pencil.pencil.businessbook.R;
import com.pencil.pencil.businessbook.Util.FileUtils;
import com.pencil.pencil.businessbook.Util.PrefManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.pencil.pencil.businessbook.Util.FileUtils.ProgressDialog;


public class EditOfferFragment extends Fragment implements EditProfileInterface {
    //UI
    private RecyclerView mOfferRecycleView;
    private EditOfferAdapter mEditOfferAdapter;
    //variable
    private static int REQUEST_GALARY;
    private Context mContext;
    //LOADING_DIALOG
    private Dialog dialog;
    private PrefManager mPrefManager;

    public EditOfferFragment() {
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
        View view = inflater.inflate(R.layout.fragment_edit_offer, container, false);
        bindVariable(view);
        mContext = getActivity();
        dialog = ProgressDialog(mContext);
        mPrefManager = new PrefManager(mContext);
        initialize();
        getProfileData();
        return view;
    }

    private void bindVariable(View view) {
        mOfferRecycleView = view.findViewById(R.id.offerRecycleView);
    }

    private void initialize() {

        mOfferRecycleView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mEditOfferAdapter = new EditOfferAdapter(mContext, new ArrayList<Offer>(), EditOfferFragment.this);

        mOfferRecycleView.setAdapter(mEditOfferAdapter);
    }

    @Override
    public void replaceImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent, REQUEST_GALARY);
    }

    @Override

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_GALARY) { //chage image or relatedfile
                Uri selectedImage = data.getData();

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), selectedImage);
                    mEditOfferAdapter.replace(bitmap, FileUtils.getImageUri(mContext, bitmap));

                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    private void getProfileData() {
        dialog.show();

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);


        Call<GeneralResponseBody<BusinessProfileResponse>> call = apiInterface.getProfileData(mPrefManager.getDataInt(PrefManager.BUSINESS_ID));

        call.enqueue(new Callback<GeneralResponseBody<BusinessProfileResponse>>() {
            @Override
            public void onResponse(@NonNull Call<GeneralResponseBody<BusinessProfileResponse>> call, @NonNull Response<GeneralResponseBody<BusinessProfileResponse>> response) {

                if (dialog != null)
                    dialog.dismiss();
                if (response.isSuccessful()) {


                    GeneralResponseBody<BusinessProfileResponse> body = response.body();

                        BusinessProfileResponse data = response.body().data;
                        mEditOfferAdapter.add(data.getOffers());


                } else {
                    Toast.makeText(mContext, "Connection error", Toast.LENGTH_LONG).show();


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

}
