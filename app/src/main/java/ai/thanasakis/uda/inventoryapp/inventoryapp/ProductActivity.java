package ai.thanasakis.uda.inventoryapp.inventoryapp;

import android.Manifest;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import ai.thanasakis.uda.inventoryapp.inventoryapp.InventoryContract.ProductItem;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

import static ai.thanasakis.uda.inventoryapp.inventoryapp.InventoryContentProvider.LOG_TAG;
import static android.R.attr.path;

/**
 * Created by programbench on 7/9/2017.
 */

public class ProductActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // protected static final String PHOTO_TAKEN = "photo_taken";
    private static final String STATE_CURRENT_URI = "CURRENT_URI";
    private static final int EXISTING_LOADER_ID = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int PERMISSIONS_REQUEST = 3;
    //protected boolean _taken = true;
    //File sdImageMainDirectory;
    @BindView(R.id.image_product)
    ImageView mImage;
    @BindView(R.id.edit_name)
    EditText mName;
    @BindView(R.id.text_quantity)
    TextView mQuantity;
    @BindView(R.id.edit_price)
    EditText mPrice;
    @BindView(R.id.button_plus)
    Button mButtonPlus;
    @BindView(R.id.button_minus)
    Button mButtonMinus;
    @BindView(R.id.button_order)
    Button mButtonOrder;
    @BindView(R.id.button_select_photo)
    Button mButtonSelectImage;
    @BindView(R.id.button_take_photo)
    Button mButtonTakePhoto;
    @BindView(R.id.button_delete)
    Button buttonDelete;
    @BindView(R.id.button_save)
    Button buttonSaveChanges;
    @BindString(R.string.denied)
    String str_denied;
    @BindString(R.string.load_failed)
    String str_load_failed;
    @BindString(R.string.delete_info)
    String str_delete_info;
    @BindString(R.string.discard_info)
    String str_discard_info;
    @BindString(R.string.discard_positive)
    String str_discard_positive;
    @BindString(R.string.discard_negative)
    String str_discard_negative;
    @BindString(R.string.delete_negative)
    String str_delete_negative;
    @BindString(R.string.delete_positive)
    String str_delete_positive;
    @BindString(R.string.save_success)
    String str_save_success;
    @BindString(R.string.save_fail)
    String str_save_fail;
    @BindString(R.string.update_success)
    String str_update_success;
    @BindString(R.string.update_fail)
    String str_update_fail;
    @BindString(R.string.delete_success)
    String str_delete_success;
    @BindString(R.string.delete_fail)
    String str_delete_fail;
    @BindString(R.string.no_email_app)
    String str_no_email;
    @BindString(R.string.info_needed)
    String str_info_needed;
    @BindString(R.string.info_needed)
    String str_image_needed;
    private boolean mChanges = false;
    private Uri mCurrentProductUri;
    private int PICK_IMAGE_REQUEST = 1;
    private String mCurrentPhotoPath;
    private Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        ButterKnife.bind(this);
        Intent intent = getIntent();

        if (savedInstanceState != null)
            mCurrentProductUri = savedInstanceState.getParcelable(STATE_CURRENT_URI);
        else
            mCurrentProductUri = intent.getData();
        if (mCurrentProductUri == null) {
            setTitle("New Product");
        } else {
            setTitle("Change Product");
            getLoaderManager().initLoader(EXISTING_LOADER_ID, null, this);
        }
        mButtonTakePhoto.setEnabled(false);
        requestPermissions();
    }

    public void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        PERMISSIONS_REQUEST);
            }
        } else {
            mButtonTakePhoto.setEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    mButtonTakePhoto.setEnabled(true);
                } else {
                    Toast.makeText(getApplicationContext(), str_denied, Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @OnClick(R.id.button_select_photo)
    public void Select(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "Choose an image"), PICK_IMAGE_REQUEST);
    }

    @OnClick(R.id.button_save)
    public void SaveItem(View v) {
        saveProduct();
    }

    @OnClick(R.id.button_plus)
    public void Increase(View view) {
        String value = mQuantity.getText().toString();
        value = value.substring(10, value.length());
        int current = 0;
        if (!value.isEmpty())
            current = Integer.parseInt(value) + 1;
        mQuantity.setText("Quantity: " + String.valueOf(current));
    }

    @OnClick(R.id.button_minus)
    public void Decrease(View view) {
        String value = mQuantity.getText().toString();
        value = value.substring(10, value.length());
        int current = 1;
        if (!value.isEmpty())
            current = Integer.parseInt(value) - 1;
        if (current >= 0)
            mQuantity.setText("Quantity: " + String.valueOf(current));
    }

    @OnTouch({R.id.edit_name, R.id.text_quantity, R.id.edit_price, R.id.button_plus, R.id.button_minus})
    public boolean Changed(View view, MotionEvent motionEvent) {
        mChanges = true;
        return false;
    }

    @OnClick(R.id.button_take_photo)
    public void onButtonTakePhotoClicked() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            File file = createImageFile();

            mImageUri = FileProvider.getUriForFile(getApplication().getApplicationContext(),
                    "ai.thanasakis.uda.inventoryapp.inventoryapp.fileprovider", file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = "PHOTO_OF_" + mName.getText().toString();
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    @OnClick(R.id.button_delete)
    public void onButtonDeleteClicked() {
        showDeleteDialog();
    }

    @OnClick(R.id.button_order)
    public void onButtonEmailClicked() {
        emailForOrder(new String[]{"test@test.com"}, "New Order: " + mName.getText().toString(), "We would like to order some more of this product");
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                ProductItem._ID,
                ProductItem.COLUMN_NAME,
                ProductItem.COLUMN_QUANTITY,
                ProductItem.COLUMN_PRICE,
                ProductItem.COLUMN_PHOTO
        };

        return new CursorLoader(this, mCurrentProductUri, projection, null, null, null);
    }

    public Bitmap getBitmapFromUri(Uri uri) {

        if (uri == null || uri.toString().isEmpty())
            return null;

        InputStream input = null;
        try {
            input = this.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, null);
            input.close();
            return bitmap;

        } catch (FileNotFoundException fne) {
            Log.e(LOG_TAG, str_load_failed, fne);
            return null;
        } catch (Exception e) {
            Log.e(LOG_TAG, str_load_failed, e);
            return null;
        } finally {
            try {
                input.close();
            } catch (IOException ioe) {
                //
            }
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(ProductItem.COLUMN_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(ProductItem.COLUMN_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(ProductItem.COLUMN_PRICE);
            int pictureColumnIndex = cursor.getColumnIndex(ProductItem.COLUMN_PHOTO);

            final String name = cursor.getString(nameColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            int price = cursor.getInt(priceColumnIndex);

            mImageUri = Uri.parse(cursor.getString(pictureColumnIndex));
            mName.setText(name);
            mQuantity.setText("Quantity: " + Integer.toString(quantity));
            mPrice.setText(Integer.toString(price));
            mImage.setImageURI(mImageUri);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mName.setText("");
        mQuantity.setText("");
        mPrice.setText("");
    }

    private void showChangedDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(str_discard_info);
        builder.setPositiveButton(str_discard_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    finish();
                }
            }
        });
        builder.setNegativeButton(str_discard_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(str_delete_info);
        builder.setPositiveButton(str_delete_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(str_delete_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void emailForOrder(String[] recipient, String subject, String body) {

        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setData(Uri.parse("mailto:"));
            intent.setType("text/html");
            intent.putExtra(Intent.EXTRA_EMAIL, recipient);
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(Intent.EXTRA_TEXT, body);
            intent.putExtra(Intent.EXTRA_STREAM, mImageUri);
            startActivity(Intent.createChooser(intent, "Send mail..."));
        } catch (Throwable t) {
            Toast.makeText(ProductActivity.this, str_no_email + t.toString(), Toast.LENGTH_LONG).show();

        }
    }

    @Override
    public void onBackPressed() {
        if (!mChanges) {
            super.onBackPressed();
            return;
        }
        showChangedDialog();
    }

    private void saveProduct() {

        String name = mName.getText().toString().trim();
        String quantityText = mQuantity.getText().toString();
        quantityText = quantityText.substring(10, quantityText.length());
        String priceText = mPrice.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(quantityText) || TextUtils.isEmpty(priceText)) {
            Toast.makeText(this, str_info_needed, Toast.LENGTH_SHORT).show();
            return;
        }

        if (mImage.getDrawable() == null) {
            Toast.makeText(this, str_image_needed, Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(ProductItem.COLUMN_NAME, name);
        int quantity = 0;
        if (!TextUtils.isEmpty(quantityText)) {
            quantity = Integer.parseInt(quantityText);
        }
        values.put(ProductItem.COLUMN_QUANTITY, quantity);

        int price = 0;
        if (!TextUtils.isEmpty(priceText)) {
            price = Integer.parseInt(priceText);
        }
        values.put(ProductItem.COLUMN_PRICE, price);
        values.put(ProductItem.COLUMN_PHOTO, mImageUri.toString());

        if (mCurrentProductUri == null) {

            Uri newUri = getContentResolver().insert(ProductItem.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, str_save_fail, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, str_save_success, Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentProductUri, values,
                    null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, str_update_fail, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, str_update_success, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void deleteProduct() {

        if (mCurrentProductUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, str_delete_fail, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, str_delete_success, Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) try {
                mImageUri = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
                mImage.setImageBitmap(bitmap);
                mImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bitmap mBitmap = getBitmapFromUri(mImageUri);
            mImage.setImageBitmap(mBitmap);
            mImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
        if (mCurrentProductUri != null)
            savedInstanceState.putParcelable(STATE_CURRENT_URI, mCurrentProductUri);

    }

}


