package com.hungdt.waterplan.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.hungdt.waterplan.KEY;
import com.hungdt.waterplan.R;
import com.hungdt.waterplan.database.DBHelper;
import com.hungdt.waterplan.dataset.Constant;
import com.hungdt.waterplan.model.Plant;
import com.hungdt.waterplan.model.Remind;
import com.hungdt.waterplan.view.adater.RemindAdapter;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddPlanActivity extends AppCompatActivity {

    private ImageView imgBack, imgDeletePlant, imgPlantAvatar;
    private CircleImageView imgGallery, imgCamera;
    private TextView txtSave;
    private EditText edtPlanName, edtPlanNote;
    private RecyclerView rcvListCareSchedule;
    private LinearLayout addRemind;

    private RemindAdapter remindAdapter;

    private Plant plant;
    private List<String> checkRemind = new ArrayList<>();
    private List<Remind> saveRemind = new ArrayList<>();
    private List<Remind> reminds = new ArrayList<>();

    final Calendar calendar = Calendar.getInstance();

    private static final int CAMERA_PERMISSION_CODE = 200;
    private static final int GALLERY_PERMISSION_CODE = 201;
    private static final int CAMERA_REQUEST_CODE = 202;
    private static final int GALLERY_REQUEST_CODE = 203;
    String currentPhotoPath = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plan);

        initView();

        String[] listCheck = {this.getResources().getString(R.string.water),
                this.getResources().getString(R.string.fertilize),
                this.getResources().getString(R.string.spray),
                this.getResources().getString(R.string.prune)};
        checkRemind.addAll(Arrays.asList(listCheck));

        Intent intent = getIntent();
        final String type = intent.getStringExtra(KEY.TYPE);
        assert type != null;
        if (type.equals(KEY.TYPE_CREATE)) {
            imgDeletePlant.setVisibility(View.GONE);
        } else if (type.equals(KEY.TYPE_EDIT)) {
            plant = (Plant) intent.getSerializableExtra(KEY.PLANT);
            if (plant != null) {
                saveRemind.addAll(plant.getReminds());
                reminds.addAll(plant.getReminds());
                edtPlanName.setText(plant.getPlantName());
                edtPlanNote.setText(plant.getPlantNote());
                if (!plant.getPlantImage().equals("")) {
                    currentPhotoPath = plant.getPlantImage();
                    Glide
                            .with(this)
                            .load(plant.getPlantImage())
                            .placeholder(R.drawable.tree_default)
                            .into(imgPlantAvatar);
                }
            }
        }
        if (reminds != null) {
            for (int i = 0; i < reminds.size(); i++) {
                if (reminds.get(i).getRemindType().equals(this.getResources().getString(R.string.water))) {
                    checkRemind.remove(this.getResources().getString(R.string.water));
                }
                if (reminds.get(i).getRemindType().equals(this.getResources().getString(R.string.fertilize))) {
                    checkRemind.remove(this.getResources().getString(R.string.fertilize));
                }
                if (reminds.get(i).getRemindType().equals(this.getResources().getString(R.string.spray))) {
                    checkRemind.remove(this.getResources().getString(R.string.spray));
                }
                if (reminds.get(i).getRemindType().equals(this.getResources().getString(R.string.prune))) {
                    checkRemind.remove(this.getResources().getString(R.string.prune));
                }
            }
        }
        remindAdapter = new RemindAdapter(this, reminds);
        rcvListCareSchedule.setLayoutManager(new LinearLayoutManager(this));
        rcvListCareSchedule.setAdapter(remindAdapter);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPlanActivity.super.onBackPressed();
            }
        });

        imgDeletePlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDeletePlantDialog();
            }
        });


        txtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtPlanName.getText().toString().isEmpty()) {
                    Toast.makeText(AddPlanActivity.this, "Give the plant a name!", Toast.LENGTH_SHORT).show();
                } else {
                    if (type.equals(KEY.TYPE_CREATE)) {
                        if (edtPlanNote.getText().toString().isEmpty()) {
                            DBHelper.getInstance(AddPlanActivity.this).addPlan(edtPlanName.getText().toString(), currentPhotoPath, "");
                        } else {
                            DBHelper.getInstance(AddPlanActivity.this).addPlan(edtPlanName.getText().toString(), currentPhotoPath, edtPlanNote.getText().toString());
                        }
                        if (reminds != null) {
                            String plantID = DBHelper.getInstance(AddPlanActivity.this).getLastPlanID();
                            for (int i = 0; i < reminds.size(); i++) {
                                Remind remind = reminds.get(i);
                                DBHelper.getInstance(AddPlanActivity.this).addRemind(plantID, remind.getRemindType(), remind.getRemindCreateDT(),remind.getRemindTime(), remind.getCareCycle());
                            }
                            reminds.clear();
                        }
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra(KEY.TYPE_RESULT, KEY.CREATE);
                        setResult(Activity.RESULT_OK, resultIntent);
                    }
                    if (type.equals(KEY.TYPE_EDIT)) {
                        if (edtPlanNote.getText().toString().isEmpty()) {
                            DBHelper.getInstance(AddPlanActivity.this).updatePlan(plant.getPlantID(), edtPlanName.getText().toString(), currentPhotoPath, "");
                        } else {
                            DBHelper.getInstance(AddPlanActivity.this).updatePlan(plant.getPlantID(), edtPlanName.getText().toString(), currentPhotoPath, edtPlanNote.getText().toString());
                        }
                        //todo
                        if (reminds.size() == 0) {
                            DBHelper.getInstance(AddPlanActivity.this).deletePlanRemind(plant.getPlantID());
                        }
                        //chưa clear list! trong database ok!
                        else {
                            if (saveRemind.size() == 0) {
                                for (int i = 0; i < reminds.size(); i++) {
                                    DBHelper.getInstance(AddPlanActivity.this)
                                            .addRemind(plant.getPlantID(),
                                                    reminds.get(i).getRemindType(),
                                                    reminds.get(i).getRemindCreateDT(),
                                                    reminds.get(i).getRemindTime(),
                                                    reminds.get(i).getCareCycle());
                                }
                            } else {
                                List<String> idSave = new ArrayList<>();
                                for (int i = 0; i < saveRemind.size(); i++) {
                                    idSave.add(saveRemind.get(i).getRemindID());
                                }

                                for (int i = 0; i < reminds.size(); i++) {
                                    boolean check = true;
                                    for (int j = 0; j < saveRemind.size(); j++) {
                                        if (reminds.get(i).getRemindType().equals(saveRemind.get(j).getRemindType())) {
                                            //nếu trùng thì update
                                            DBHelper.getInstance(AddPlanActivity.this)
                                                    .updateRemind(reminds.get(i).getRemindID(),
                                                            reminds.get(i).getRemindCreateDT(),
                                                            reminds.get(i).getRemindTime(),
                                                            reminds.get(i).getCareCycle());
                                            String id = saveRemind.get(j).getRemindID();
                                            idSave.remove(id);
                                            check = false;
                                        }
                                    }
                                    if (check) {
                                        // khác thì add
                                        DBHelper.getInstance(AddPlanActivity.this)
                                                .addRemind(plant.getPlantID(),
                                                        reminds.get(i).getRemindType(),
                                                        reminds.get(i).getRemindCreateDT(),
                                                        reminds.get(i).getRemindTime(),
                                                        reminds.get(i).getCareCycle());
                                    }
                                }
                                if (idSave.size() > 0) {
                                    //xóa bỏ nếu đã xóa khỏi danh sách
                                    for (int i = 0; i < idSave.size(); i++) {
                                        DBHelper.getInstance(AddPlanActivity.this).deleteOneRemind(idSave.get(i));
                                    }
                                }
                            }
                            saveRemind.clear();
                            reminds.clear();
                        }
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra(KEY.TYPE_RESULT, KEY.UPDATE);
                        setResult(Activity.RESULT_OK, resultIntent);
                    }
                    finish();
                }
            }
        });


        imgCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String typePermission = "camera";
                askPermissions(typePermission);
            }
        });

        imgGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String typePermission = "gallery";
                askPermissions(typePermission);
            }
        });

        addRemind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChoseTypeDialog();
            }
        });

        remindAdapter.setOnRemindItemClickListener(new RemindAdapter.OnRemindItemClickListener() {
            @Override
            public void OnItemDateTimeClicked(int position) {
                openEditRemindDialog(reminds.get(position), position);
            }

            @Override
            public void OnItemDeleteClicked(int position) {
                checkRemind.add(reminds.get(position).getRemindType());
                reminds.remove(position);
                remindAdapter.notifyDataSetChanged();
                addRemind.setVisibility(View.VISIBLE);
            }
        });
    }

    private void askPermissions(String typePermission) {
        if (typePermission.equals("camera")) {
            if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) ||
                    (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                    (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSION_CODE);
            } else {
                dispatchTakePictureIntent();
            }
        }
        if (typePermission.equals("gallery")) {
            if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                    (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, GALLERY_PERMISSION_CODE);
            } else {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
            }

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                File f = new File(currentPhotoPath);
                imgPlantAvatar.setImageURI(Uri.fromFile(f));
                //
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);
            }
        }

        if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                while (cursor.moveToNext()) {
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String yourRealPath = cursor.getString(columnIndex);
                    currentPhotoPath = yourRealPath;
                    Glide.with(this).load(currentPhotoPath).placeholder(R.drawable.tree_default).into(imgPlantAvatar);
                }
                cursor.close();


            }
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.hungdt.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    private void openDeletePlantDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.delete_plant_dialog);

        final Button btnYes = dialog.findViewById(R.id.btnYes);
        final Button btnNo = dialog.findViewById(R.id.btnNo);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBHelper.getInstance(AddPlanActivity.this).deletePlan(plant.getPlantID());
                if (reminds.size() > 0) {
                    DBHelper.getInstance(AddPlanActivity.this).deletePlanRemind(plant.getPlantID());
                }
                Intent resultIntent = new Intent();
                resultIntent.putExtra(KEY.TYPE_RESULT, KEY.DELETE);
                setResult(Activity.RESULT_OK, resultIntent);
                dialog.dismiss();
                finish();
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void openChoseTypeDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.choose_type_dialog);

        final LinearLayout llWater = dialog.findViewById(R.id.llWater);
        final LinearLayout llFertilizer = dialog.findViewById(R.id.llFertilizer);
        final LinearLayout llSpray = dialog.findViewById(R.id.llSpray);
        final LinearLayout llPrune = dialog.findViewById(R.id.llPrune);

        for (int i = 0; i < reminds.size(); i++) {
            if (reminds.get(i).getRemindType().equals(this.getResources().getString(R.string.water))) {
                llWater.setVisibility(View.GONE);
            } else if (reminds.get(i).getRemindType().equals(this.getResources().getString(R.string.fertilize))) {
                llFertilizer.setVisibility(View.GONE);
            } else if (reminds.get(i).getRemindType().equals(this.getResources().getString(R.string.spray))) {
                llSpray.setVisibility(View.GONE);
            } else {
                llPrune.setVisibility(View.GONE);
            }
        }

        llWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkRemind.remove(dialog.getContext().getResources().getString(R.string.water));
                if (checkRemind.size() == 0) {
                    addRemind.setVisibility(View.GONE);
                }
                reminds.add(new Remind("0", dialog.getContext().getResources().getString(R.string.water), getInstantDateTime(),"07:00","5"));
                remindAdapter.notifyDataSetChanged();
                dialog.dismiss();

            }
        });

        llFertilizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkRemind.remove(dialog.getContext().getResources().getString(R.string.fertilize));
                if (checkRemind.size() == 0) {
                    addRemind.setVisibility(View.GONE);
                }
                reminds.add(new Remind("1", dialog.getContext().getResources().getString(R.string.fertilize), getInstantDateTime(),"07:00", "5"));
                remindAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        llSpray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkRemind.remove(dialog.getContext().getResources().getString(R.string.spray));
                if (checkRemind.size() == 0) {
                    addRemind.setVisibility(View.GONE);
                }
                reminds.add(new Remind("2", dialog.getContext().getResources().getString(R.string.spray), getInstantDateTime(),"07:00", "5"));
                remindAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        llPrune.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkRemind.remove(dialog.getContext().getResources().getString(R.string.prune));
                if (checkRemind.size() == 0) {
                    addRemind.setVisibility(View.GONE);
                }
                reminds.add(new Remind("3", dialog.getContext().getResources().getString(R.string.prune), getInstantDateTime(),"07:00", "5"));
                remindAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void openEditRemindDialog(final Remind remind, final int position) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.edit_remind_dialog);

        final TextInputEditText edtRemindDay = dialog.findViewById(R.id.edtRemindDay);
        final TextInputEditText edtRemindTime = dialog.findViewById(R.id.edtRemindTime);
        final Button btnEdit = dialog.findViewById(R.id.btnEdit);
        final Button btnBack = dialog.findViewById(R.id.btnBack);

        edtRemindDay.setText(remind.getCareCycle());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(Constant.getTimeFormat());
        Date date;
        try {
            date = sdf.parse(remind.getRemindTime());
            assert date != null;
            edtRemindTime.setText(sdf.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        edtRemindTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDialog(edtRemindTime);
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Objects.requireNonNull(edtRemindDay.getText()).toString().isEmpty() || Objects.requireNonNull(edtRemindTime.getText()).toString().isEmpty()) {
                    Toast.makeText(AddPlanActivity.this, "Please enter all title!", Toast.LENGTH_SHORT).show();
                } else {
                    Remind remindEdited = new Remind(remind.getRemindID(), remind.getRemindType(), remind.getRemindCreateDT(), edtRemindTime.getText().toString(), edtRemindDay.getText().toString());
                    reminds.set(position, remindEdited);
                    remindAdapter.notifyItemChanged(position);
                    dialog.dismiss();
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showTimeDialog(final TextInputEditText edtRemindTime) {
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(AddPlanActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String hours = String.valueOf(hourOfDay);
                String minutes = String.valueOf(minute);
                if (hourOfDay < 10) {
                    hours = "0" + hours;
                }
                if (minute < 10) {
                    minutes = "0" + minutes;
                }
                edtRemindTime.setText(hours + ":" + minutes);
            }
        }, hour, minute, android.text.format.DateFormat.is24HourFormat(AddPlanActivity.this));
        timePickerDialog.show();
    }

    private String getInstantDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(Constant.getDateTimeFormat(), Locale.US);
        return sdf.format(calendar.getTime());
    }

    private void initView() {
        imgBack = findViewById(R.id.imgBack);
        imgDeletePlant = findViewById(R.id.imgDeletePlant);
        imgCamera = findViewById(R.id.imgCamera);
        imgPlantAvatar = findViewById(R.id.imgPlantAvatar);
        imgGallery = findViewById(R.id.imgGallery);
        txtSave = findViewById(R.id.txtSave);
        edtPlanName = findViewById(R.id.edtPlanName);
        edtPlanNote = findViewById(R.id.edtPlanNote);
        rcvListCareSchedule = findViewById(R.id.rcvListCareSchedule);
        addRemind = findViewById(R.id.addCareSchedule);
    }
}
