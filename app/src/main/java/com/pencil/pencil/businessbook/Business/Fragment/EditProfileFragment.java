package com.pencil.pencil.businessbook.Business.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.ViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.pencil.pencil.businessbook.Adapter.CategoryAdapter;
import com.pencil.pencil.businessbook.Api.APIClient;
import com.pencil.pencil.businessbook.Api.APIInterface;
import com.pencil.pencil.businessbook.Business.Activity.BusinessProfileActivity;
import com.pencil.pencil.businessbook.Business.Activity.CreateBusinessActivity;
import com.pencil.pencil.businessbook.Business.Activity.MapSearchActivity;
import com.pencil.pencil.businessbook.Business.Adapter.EditOfferAdapter;
import com.pencil.pencil.businessbook.Business.Adapter.RelatedEditProFileAdapter;
import com.pencil.pencil.businessbook.Business.Interface.EditProfileInterface;
import com.pencil.pencil.businessbook.Business.Pojo.BusinessProfileResponse;
import com.pencil.pencil.businessbook.Business.Pojo.Category;
import com.pencil.pencil.businessbook.Business.Pojo.GeneralResponseBody;
import com.pencil.pencil.businessbook.Business.Pojo.Offer;
import com.pencil.pencil.businessbook.Business.Pojo.RelatedFile;
import com.pencil.pencil.businessbook.R;
import com.pencil.pencil.businessbook.Util.FileUtils;
import com.pencil.pencil.businessbook.Util.PrefManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.pencil.pencil.businessbook.Util.FileUtils.ProgressDialog;

public class EditProfileFragment extends Fragment implements EditProfileInterface, OnMapReadyCallback {

    //UI Variable
    private LinearLayout mBusinesImageLinear;
    private ImageView mBusinesIv;
    private LinearLayout mLogoLinear;
    private ImageView mLogoIv;
    private EditText mUserNameET;

    private EditText mEmailET;
    private EditText mAddressET;
    private EditText mContactNoET;
    private EditText mBusinessNameET;
    private Spinner mCategorySpinner;
    private LinearLayout mCategoryLinear;
    private EditText mBusinessDescriptionET;
    private Button mSetLocationBtn;
    private Button mConfirmBtn;
    private RecyclerView mRelatedFileRecycleView;
    private RelatedEditProFileAdapter mRelatedFileAdapter;
    private View mView;
    private LinearLayout mMapLinear;

    //variable
    private static int REQUEST_GALARY = 1;
    private static int REQUEST_CAMERA = 2;
    private static int REQUEST_BUSiNESS = 3;
    private static int REQUEST_PERMISSION = 4;
    private static int SEARCH_LOCATION = 5;
    private static int REQUEST_PERMISSION_LOCATION = 6;
    private static int REPLACE_RELATED_FILE = 7;
    //variable
    private Context mContext;
    private String imagePath;
    private int screen_width;
    private int mSelectedCategoryId=-1;
    //map
    private Double mSelectedLat = null;
    private Double mSelectedLong = null;
    private String mSelectedCity = "";
    private String mSelectedRegion = "";
    private Marker mMarker;
    //location
    // Google Map variables
    private GoogleMap mMainMap;
    //LOADING_DIALOG
    private Dialog dialog;
    private PrefManager mPrefManager;
    private File mLogoFile;
    private File mBusinessFile;
    private CategoryAdapter mCategoryAdapter;
    ArrayList<Category> mCategories;


    public EditProfileFragment() {
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
        View mView = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        mContext = getActivity();
        Display display = ((Activity) mContext).getWindowManager().getDefaultDisplay();

        screen_width = display.getWidth();
        dialog = ProgressDialog(mContext);
        mPrefManager = new PrefManager(mContext);

        bindVariable(mView);
        listener();
        initialize();
        initializeMap();
        initializeDropDown();
        return mView;
    }

    private void bindVariable(View v) {
        mBusinesImageLinear = v.findViewById(R.id.businessImageLinear);
        mBusinesIv = v.findViewById(R.id.businessIv);
        mLogoLinear = v.findViewById(R.id.businessLogoLinear);
        mLogoIv = v.findViewById(R.id.logoIv);
        mUserNameET = v.findViewById(R.id.userNameET);

        mEmailET = v.findViewById(R.id.emailET);
        mAddressET = v.findViewById(R.id.addressET);
        mContactNoET = v.findViewById(R.id.contactNoET);
        mBusinessNameET = v.findViewById(R.id.businessNameET);
        mCategorySpinner = v.findViewById(R.id.categoryDropDown);
        mCategoryLinear = v.findViewById(R.id.categoryLinear);
        mBusinessDescriptionET = v.findViewById(R.id.businessDescriptionET);
        mSetLocationBtn = v.findViewById(R.id.setLocationBtn);
        mConfirmBtn = v.findViewById(R.id.confirmBtn);
        mRelatedFileRecycleView = v.findViewById(R.id.relatedFileRecycleView);
        mMapLinear = v.findViewById(R.id.mapLinear);
    }

    private void initialize() {
        ((Activity) mContext).setTitle(getResources().getText(R.string.edit_title));

        mRelatedFileRecycleView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));


        mRelatedFileAdapter = new RelatedEditProFileAdapter(mContext, new ArrayList<RelatedFile>(), EditProfileFragment.this);

        mRelatedFileRecycleView.setAdapter(mRelatedFileAdapter);
        Display display = ((Activity) mContext).getWindowManager().getDefaultDisplay();

        int width = display.getWidth();
        mLogoIv.getLayoutParams().width=width/3;
        mLogoIv.getLayoutParams().height=120;
        mBusinesIv.getLayoutParams().width=width/3;
        mBusinesIv.getLayoutParams().height=120;
        getProfileData();

    }

    private void initializeDropDown() {
        mSelectedCategoryId =-1;
      //  final String[] code = new String[]{"category", "pharmacy", "pharmacy", "pharmacy", "pharmacy", "pharmacy"};

        mCategoryAdapter = new CategoryAdapter(mContext, new ArrayList<Category>());
        mCategorySpinner.setAdapter(mCategoryAdapter);
        getCategory();
        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(mCategories!=null)
                mSelectedCategoryId = mCategories.get(i).getId();


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void listener() {
        mLogoLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(intent, REQUEST_GALARY);
            }
        });
        mBusinesImageLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        mSetLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // if(isValid()){
                Intent profileIntent = new Intent(mContext, MapSearchActivity.class);
                profileIntent.putExtra("currentLocation", false);
                startActivityForResult(profileIntent, SEARCH_LOCATION);

                // }
            }
        });
        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()) {
                    updateAccount();
                }
            }
        });
    }

    @Override

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_GALARY) {


                Uri selectedImage = data.getData();

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), selectedImage);
                    mLogoFile = new File(FileUtils.getRealPathFromURI(mContext, FileUtils.getImageUri(mContext, bitmap)));
                    mLogoIv.setImageBitmap(Bitmap.createScaledBitmap(bitmap, screen_width / 3, 120, false));
                    // mLogoIv.setImageBitmap(bitmap);


                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_CAMERA) {
                mBusinesIv.getLayoutParams().width = screen_width / 3;
                mBusinesIv.getLayoutParams().height = 120;
                Uri uri;

                mBusinessFile = new File(imagePath);
                if (mBusinessFile.exists()) {

                    uri = Uri.fromFile(mBusinessFile);
                    Glide.with(mContext).load(uri.toString())
                            .into(mBusinesIv);
                }


            } else if (requestCode == REQUEST_BUSiNESS) { //change

                Uri selectedImage = data.getData();

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), selectedImage);
                    mBusinessFile = new File(FileUtils.getRealPathFromURI(mContext, FileUtils.getImageUri(mContext, bitmap)));

                    mBusinesIv.setImageBitmap(Bitmap.createScaledBitmap(bitmap, screen_width / 3, 120, false));
                    // mLogoIv.setImageBitmap(bitmap);


                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else if (requestCode == SEARCH_LOCATION) {
                mSelectedLat = data.getDoubleExtra("lat", 0d);
                mSelectedLong = data.getDoubleExtra("long", 0d);
                mSelectedRegion = data.getStringExtra("region");
                mSelectedCity = data.getStringExtra("city");
                if (mMainMap == null) {
                    initializeMap();
                } else {
                    LatLng latLng = new LatLng(mSelectedLat, mSelectedLong);
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(latLng).zoom(15).build();
                    if (mMarker != null)
                        mMarker.remove();
                    mMainMap.addMarker(new MarkerOptions().position(latLng)
                            .title("selected Location"));
                    mMainMap.animateCamera(CameraUpdateFactory
                            .newCameraPosition(cameraPosition));
                }
            } else { //chage image or relatedfile
                Uri selectedImage = data.getData();

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), selectedImage);
                    // mLogoIv.setImageBitmap(bitmap);
                    RelatedFile relatedFile = new RelatedFile("");
                    relatedFile.setmRelatedFileBitmap(bitmap);
                    relatedFile.setmRelatedFileUri(FileUtils.getImageUri(mContext, bitmap));
                    mRelatedFileAdapter.replace(relatedFile);

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

    private void selectImage() {


        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};


        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setTitle("Add Photo!");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo"))

                {
                    if (Build.VERSION.SDK_INT > 15) {
                        final String[] permissions = {
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA};

                        final List<String> permissionsToRequest = new ArrayList<>();
                        for (String permission : permissions) {
                            if (ActivityCompat.checkSelfPermission(mContext, permission) != PackageManager.PERMISSION_GRANTED) {
                                permissionsToRequest.add(permission);
                            }
                        }
                        if (!permissionsToRequest.isEmpty()) {
                            ActivityCompat.requestPermissions((AppCompatActivity) mContext, permissionsToRequest.toArray(new String[permissionsToRequest.size()]), REQUEST_PERMISSION);
                        } else {
                            dispatchTakePictureIntent();
                        }
                    }
                } else if (options[item].equals("Choose from Gallery"))

                {

                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    startActivityForResult(intent, REQUEST_BUSiNESS);


                } else if (options[item].equals("Cancel")) {

                    dialog.dismiss();

                }

            }

        });

        builder.show();

    }

    //PICK_IMAGE
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(mContext.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                imagePath = photoFile.getAbsolutePath();
                System.out.println("path=" + photoFile.getAbsolutePath());
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(mContext,
                        "com.pencil.pencil.businessbook.provider",
                        photoFile);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                } else {
                    List<ResolveInfo> resInfoList = mContext.getPackageManager().queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        mContext.grantUriPermission(packageName, photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                }

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(takePictureIntent, REQUEST_CAMERA);
            }
        }
    }

    //CREATE_FILE
    private File createImageFile() throws IOException {
        // Create an image file name
        Locale locale = new Locale("en");
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", locale).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );


        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {

            dispatchTakePictureIntent();

        } else if (requestCode == REQUEST_PERMISSION_LOCATION) {
            onMapReady(mMainMap);
        }
    }

    @Override
    public void replaceImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent, REPLACE_RELATED_FILE);
    }

    private Boolean isValid(
    ) {
        String emailStr = mEmailET.getText().toString().trim();

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        Boolean valid = true;
        if (mUserNameET.getText().toString().trim().isEmpty()) {
            valid = false;
            mUserNameET.setError(getResources().getString(R.string.username_required));
            mUserNameET.requestFocus();
        } else if (emailStr.isEmpty()) {
            valid = false;
            mEmailET.setError(getResources().getString(R.string.email_required));
            mEmailET.requestFocus();
        } else if (!emailStr.matches(emailPattern)) {
            valid = false;
            mEmailET.setError(getResources().getString(R.string.email_validation));
            mEmailET.requestFocus();
        } else if (mAddressET.getText().toString().trim().isEmpty()) {
            valid = false;
            mAddressET.setError(getResources().getString(R.string.address_required));
            mAddressET.requestFocus();
        } else if (mContactNoET.getText().toString().trim().isEmpty()) {
            valid = false;
            mContactNoET.setError(getResources().getString(R.string.contact_required));
            mContactNoET.requestFocus();
        } else if (mBusinessNameET.getText().toString().trim().isEmpty()) {
            valid = false;
            mBusinessNameET.setError(getResources().getString(R.string.business_name_required));
            mBusinessNameET.requestFocus();
        } else if (mSelectedCategoryId==-1) {
            valid = false;
            Toast.makeText(mContext, getResources().getString(R.string.category_required), Toast.LENGTH_LONG).show();

            mCategoryLinear.requestFocus();
        } else if (mBusinessDescriptionET.getText().toString().trim().isEmpty()) {
            valid = false;
            mBusinessDescriptionET.setError(getResources().getString(R.string.business_description_required));
            mBusinessDescriptionET.requestFocus();
        }
        return valid;
    }

    //initialize map
    private void initializeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) (getChildFragmentManager()
                .findFragmentById(R.id.map));
        mMapLinear.setVisibility(View.VISIBLE);
        mapFragment.getMapAsync(EditProfileFragment.this);

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


    }

    private void getProfileData() {
        dialog.show();

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);

        // Call<GeneralResponseBody<BusinessProfileResponse>> call = apiInterface.getProfileData(7);

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

    private void setProfileData(final BusinessProfileResponse data) {
        mUserNameET.setText(mPrefManager.getUsername());
        mAddressET.setText(data.getAddress());
        mBusinessNameET.setText(data.getName());
        mBusinessDescriptionET.setText(data.getDescription());
        mContactNoET.setText(data.getContact_number());
        mSelectedCity = data.getCity();
        mSelectedRegion = data.getRegoin();

        mEmailET.setText(data.getOwner().getEmail());
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
        Glide.with(mContext).load(data.getImage()).into(mBusinesIv);
//        Glide.with(mContext)
//                .asBitmap()
//                .load(data.getImage())
//                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
//                .into(new SimpleTarget<Bitmap>(50, 50) {
//                    @Override
//                    public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
//                        mBusinessFile= new File(FileUtils.getRealPathFromURI(mContext, FileUtils.getImageUri(mContext, bitmap)));
//                    }
//                });


        if (data.getLogo() != null) {
            Glide.with(mContext).load(data.getLogo()).into(mLogoIv);
//            Glide.with(mContext)
//                    .asBitmap()
//                    .load(data.getLogo())
//                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
//                    .into(new SimpleTarget<Bitmap>(50, 50) {
//                        @Override
//                        public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
//                            mLogoFile= new File(FileUtils.getRealPathFromURI(mContext, FileUtils.getImageUri(mContext, bitmap)));
//                        }
//                    });

        }
//            }});
        ArrayList<Offer> offers = data.getOffers();

        ArrayList<RelatedFile> relatedFiles = data.getFiles();
        mSelectedCategoryId=data.getCategory().getId();

        mRelatedFileAdapter.add(relatedFiles);
    }

    private void updateAccount() {


        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        MultipartBody.Part filePart = null;
        if (mBusinessFile != null)
            filePart = MultipartBody.Part.createFormData("image", mBusinessFile.getName(), RequestBody.create(MediaType.parse("image/*"), mBusinessFile));
        MultipartBody.Part logoPart = null;
        if (mLogoFile != null)
            logoPart = MultipartBody.Part.createFormData("logo", mLogoFile.getName(), RequestBody.create(MediaType.parse("image/*"), mLogoFile));
        // CreateBusinessInput createBusinessInput=new CreateBusinessInput(mPrefManager.getDataString(PrefManager.USER_NAME),mBusinessDescriptionET.getText().toString().trim(),mContactNoET.getText().toString().trim(),mSelectedCity,mSelectedRegion,mAddressET.getText().toString().trim(),mSelectedLong+"",mSelectedLat+"","1",""+mPrefManager.getDataInt(PrefManager.OWNER_ID));
        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), mBusinessNameET.getText().toString().trim());
        String desc = mBusinessDescriptionET.getText().toString().trim();
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), mBusinessDescriptionET.getText().toString().trim());
        RequestBody contact_number = RequestBody.create(MediaType.parse("text/plain"), mContactNoET.getText().toString().trim());
        RequestBody city = RequestBody.create(MediaType.parse("text/plain"), mSelectedCity);
        final RequestBody regoin = RequestBody.create(MediaType.parse("text/plain"), mSelectedRegion);
        RequestBody address = RequestBody.create(MediaType.parse("text/plain"), mAddressET.getText().toString().trim());
        RequestBody langitude = RequestBody.create(MediaType.parse("text/plain"), mSelectedLong + "");
        RequestBody lattitude = RequestBody.create(MediaType.parse("text/plain"), mSelectedLat + "");
        RequestBody category_id = RequestBody.create(MediaType.parse("text/plain"),  mSelectedCategoryId+"");
        RequestBody owner_id = RequestBody.create(MediaType.parse("text/plain"), mPrefManager.getDataString(PrefManager.OWNER_ID));

        int bus = mPrefManager.getDataInt(PrefManager.BUSINESS_ID);
        String owner = mPrefManager.getDataString(PrefManager.OWNER_ID);

        dialog.show();
        Call<GeneralResponseBody<Object>> call = apiInterface.editProfile(mPrefManager.getDataInt(PrefManager.BUSINESS_ID), name, description, contact_number, city, regoin, address, langitude, lattitude, category_id, owner_id, filePart, logoPart);

        call.enqueue(new Callback<GeneralResponseBody<Object>>() {
            @Override
            public void onResponse(@NonNull Call<GeneralResponseBody<Object>> call, @NonNull Response<GeneralResponseBody<Object>> response) {
                Log.v("XXXX", call.request().body().toString());
                if (dialog != null)
                    dialog.dismiss();
                if (response.isSuccessful()) {

                    GeneralResponseBody<Object> body = response.body();

                    String flag = body.flag;
                    if (flag.equals("1")) {
                        Toast.makeText(mContext, getResources().getText(R.string.sucess_update), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mContext, getResources().getText(R.string.failed_update), Toast.LENGTH_LONG).show();
                    }

                } else {
                    Gson gson = new Gson();


                }

            }

            @Override
            public void onFailure(@NonNull Call<GeneralResponseBody<Object>> call, @NonNull Throwable t) {

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
    private void getCategory() {


        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);


        final Dialog dialog = FileUtils.ProgressDialog(mContext);
      //  dialog.show();

        Call<GeneralResponseBody<ArrayList<Category>>> call = apiInterface.getCategory();

        call.enqueue(new Callback<GeneralResponseBody<ArrayList<Category>>>() {
            @Override
            public void onResponse(@NonNull Call<GeneralResponseBody<ArrayList<Category>>> call, @NonNull Response<GeneralResponseBody<ArrayList<Category>>> response) {

                if (dialog != null)
                    dialog.dismiss();
                if (response.isSuccessful()) {

                    GeneralResponseBody<ArrayList<Category>> result = response.body();
                    mCategories = result.data;
                   mCategoryAdapter.add( mCategories);
                   if(mSelectedCategoryId!=-1)
                   for (int i=0;i<mCategories.size();i++){
                       if(mSelectedCategoryId==mCategories.get(i).getId()){
                           mCategorySpinner.setSelection(i);
                           break;
                       }
                   }


                } else {
                    Gson gson = new Gson();


                }

            }

            @Override
            public void onFailure(@NonNull Call<GeneralResponseBody<ArrayList<Category>>> call, @NonNull Throwable t) {

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
