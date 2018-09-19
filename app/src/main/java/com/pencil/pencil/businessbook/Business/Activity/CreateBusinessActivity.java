package com.pencil.pencil.businessbook.Business.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.pencil.pencil.businessbook.Business.Pojo.BusinessProfileResponse;
import com.pencil.pencil.businessbook.Business.Pojo.Category;
import com.pencil.pencil.businessbook.Business.Pojo.CreateBusinessInput;
import com.pencil.pencil.businessbook.Business.Pojo.GeneralResponseBody;
import com.pencil.pencil.businessbook.R;
import com.pencil.pencil.businessbook.Util.FileUtils;
import com.pencil.pencil.businessbook.Util.PrefManager;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.pencil.pencil.businessbook.Util.FileUtils.ProgressDialog;

public class CreateBusinessActivity extends AppCompatActivity implements OnMapReadyCallback {
    //UI Variable
    private LinearLayout mBusinesImageLinear;
    private ImageView mBusinesIv;
    private LinearLayout mLogoLinear;
    private ImageView mLogoIv;

    private EditText mAddressET;
    private EditText mContactNoET;
    private EditText mBusinessNameET;
    private Spinner mCategorySpinner;
    private LinearLayout mCategoryLinear;
    private EditText mBusinessDescriptionET;
    private Button mSetLocationBtn;
    private Button mConfirmBtn;
    private LinearLayout mMapLinear;
    private Marker mMarker;

    //location
    // Google Map variables
    private GoogleMap mMainMap;
    private View mMapView;
    //variable
    private Context mContext;
    private int mSelectedCategoryId=-1;
    private int screen_width;
    private Double mSelectedLat = null;
    private Double mSelectedLong = null;
    private String mSelectedCity="";
    private String mSelectedAdress="";
    private String mSelectedRegion="";
    private Boolean mSelectImg = false;
   private File mLogoFile;
    private File mBusinessFile;
    private    Dialog dialog;

    private String imagePath;
    private PrefManager  mPrefManager;
    private CategoryAdapter mCategoryAdapter;
    ArrayList<Category> mCategories;
    //static //variable
    private static int REQUEST_GALARY = 1;
    private static int REQUEST_CAMERA = 2;
    private static int REQUEST_BUSiNESS = 3;
    private static int REQUEST_PERMISSION = 4;
    private static int SEARCH_LOCATION = 5;
    private static int REQUEST_PERMISSION_LOCATION = 6;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_business);
        mContext = CreateBusinessActivity.this;
        Display display = ((Activity) mContext).getWindowManager().getDefaultDisplay();
        screen_width = display.getWidth();
        mPrefManager = new PrefManager(this);
        dialog= ProgressDialog(  mContext);
        bindVariable();
        initializeDropDown();
        //initializeMap();
        listener();

    }

    private void bindVariable() {
        mBusinesImageLinear = findViewById(R.id.businessImageLinear);
        mBusinesIv = findViewById(R.id.businessIv);
        mLogoLinear = findViewById(R.id.businessLogoLinear);
        mLogoIv = findViewById(R.id.logoIv);

        mAddressET = findViewById(R.id.addressET);
        mContactNoET = findViewById(R.id.contactNoET);
        mBusinessNameET = findViewById(R.id.businessNameET);
        mCategorySpinner = findViewById(R.id.categoryDropDown);
        mCategoryLinear = findViewById(R.id.categoryLinear);
        mBusinessDescriptionET = findViewById(R.id.businessDescriptionET);
        mSetLocationBtn = findViewById(R.id.setLocationBtn);
        mConfirmBtn = findViewById(R.id.confirmBtn);
        mMapLinear = findViewById(R.id.mapLinear);
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
        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isValid()) {
                    dialog.show();
                    CreateAccount();
//                    Intent profileIntent = new Intent(CreateBusinessActivity.this, BusinessProfileActivity.class);
//                    startActivity(profileIntent);
                }
            }
        });
        mSetLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if(isValid()){
                Intent profileIntent = new Intent(CreateBusinessActivity.this, MapSearchActivity.class);
                profileIntent.putExtra("currentLocation", true);
                startActivityForResult(profileIntent, SEARCH_LOCATION);
                //  finish();
//               }
            }
        });
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_GALARY) {


                Uri selectedImage = data.getData();
                mLogoFile= new File(getRealPathFromURI(selectedImage));
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), selectedImage);
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
                mSelectImg = true;
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
                mSelectImg = true;
                Uri selectedImage = data.getData();
// CALL THIS METHOD TO GET THE ACTUAL PATH
                mBusinessFile = new File(getRealPathFromURI(selectedImage));
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), selectedImage);
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
                mSelectedRegion= data.getStringExtra("region");
                mSelectedCity= data.getStringExtra("city");
                if (mMainMap == null) {
                    initializeMap();
                } else {
                    LatLng latLng = new LatLng(mSelectedLat, mSelectedLong);
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(latLng).zoom(15).build();
                    if (mMarker != null) {
                        mMarker.remove();
                    }
                    mMarker = mMainMap.addMarker(new MarkerOptions().position(latLng)
                            .title("selected Location"));
                    mMainMap.animateCamera(CameraUpdateFactory
                            .newCameraPosition(cameraPosition));
                }

            }
        }
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

    private void initializeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mMapLinear.setVisibility(View.VISIBLE);
        mapFragment.getMapAsync(CreateBusinessActivity.this);

    }


    private Boolean isValid(
    ) {

        Boolean valid = true;
        if (mAddressET.getText().toString().trim().isEmpty()) {
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
        } else if (mSelectedLat == null) {
            valid = false;
            Toast.makeText(mContext, getResources().getString(R.string.choose_location), Toast.LENGTH_LONG).show();
        } else if (mSelectImg == false) {
            valid = false;
            Toast.makeText(mContext, getResources().getString(R.string.upload_business), Toast.LENGTH_LONG).show();
        }
        return valid;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMainMap = googleMap;
        //set current location


        // Enable MyLocation Button in the Map
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_LOCATION);

        } else {


            // Get the button view
            mMainMap.setMyLocationEnabled(true);
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
    }

    private void CreateAccount() {


        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        MultipartBody.Part filePart=null;
        if(mLogoFile!=null)
       filePart = MultipartBody.Part.createFormData("image", mLogoFile.getName(), RequestBody.create(MediaType.parse("image/*"),mLogoFile ));
        MultipartBody.Part logoPart=null;
        if(mBusinessFile!=null)
      logoPart = MultipartBody.Part.createFormData("logo", mBusinessFile.getName(), RequestBody.create(MediaType.parse("image/*"), mBusinessFile));
       // CreateBusinessInput createBusinessInput=new CreateBusinessInput(mPrefManager.getDataString(PrefManager.USER_NAME),mBusinessDescriptionET.getText().toString().trim(),mContactNoET.getText().toString().trim(),mSelectedCity,mSelectedRegion,mAddressET.getText().toString().trim(),mSelectedLong+"",mSelectedLat+"","1",""+mPrefManager.getDataInt(PrefManager.OWNER_ID));
        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), mBusinessNameET.getText().toString());
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), mBusinessDescriptionET.getText().toString().trim());
        RequestBody contact_number = RequestBody.create(MediaType.parse("text/plain"), mContactNoET.getText().toString().trim());
        RequestBody city = RequestBody.create(MediaType.parse("text/plain"),mSelectedCity);
        final RequestBody regoin = RequestBody.create(MediaType.parse("text/plain"), mSelectedRegion);
        RequestBody address = RequestBody.create(MediaType.parse("text/plain"), mAddressET.getText().toString().trim());
        RequestBody langitude = RequestBody.create(MediaType.parse("text/plain"), mSelectedLong+"");
        RequestBody lattitude = RequestBody.create(MediaType.parse("text/plain"), mSelectedLat+"");
        RequestBody category_id= RequestBody.create(MediaType.parse("text/plain"), mSelectedCategoryId+"");
        RequestBody owner_id = RequestBody.create(MediaType.parse("text/plain"),mPrefManager.getDataInt(PrefManager.OWNER_ID)+"");




        Call<GeneralResponseBody<BusinessProfileResponse >> call = apiInterface.createBusiness(name, description,contact_number,city,regoin,address,langitude,lattitude,category_id,owner_id,filePart,logoPart);

        call.enqueue(new Callback<GeneralResponseBody<BusinessProfileResponse>>() {
            @Override
            public void onResponse(@NonNull Call<GeneralResponseBody<BusinessProfileResponse>> call, @NonNull Response<GeneralResponseBody<BusinessProfileResponse>> response) {
                Log.v("XXXX",call.request().body().toString());
                if (dialog != null)
                    dialog.dismiss();
                if (response.isSuccessful()) {

//                    dialog.dismiss();
                    GeneralResponseBody<BusinessProfileResponse> body=response.body();
                    BusinessProfileResponse businessProfileResponse=body.data;
                    mPrefManager.setDataInt(PrefManager.BUSINESS_ID, businessProfileResponse.getId());

                    mPrefManager.commit();

                    Intent profileIntent = new Intent(CreateBusinessActivity.this, BusinessProfileActivity.class);
                    startActivity(profileIntent);


                }else {
                    Gson gson = new Gson();



                }

            }

            @Override
            public void onFailure(@NonNull Call<GeneralResponseBody<BusinessProfileResponse>> call, @NonNull Throwable t) {

                try {
                    if (dialog != null)
                        dialog.dismiss();
                    Log.e("failed", t.getMessage()+call.request().url());
                    Toast.makeText(mContext, "Connection error", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
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
                    mCategorySpinner.setSelection(0);




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