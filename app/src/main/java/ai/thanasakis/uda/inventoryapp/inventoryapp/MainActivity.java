package ai.thanasakis.uda.inventoryapp.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.Random;

import ai.thanasakis.uda.inventoryapp.inventoryapp.InventoryContract.ProductItem;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 1;
    LoaderManager loaderManager;
    @BindView(R.id.products_list)
    ListView inventoryListView;
    @BindView(R.id.empty_text)
    TextView emptyList;
    @BindView(R.id.material_design_android_floating_action_menu)
    FloatingActionMenu materialDesignFAM;
    @BindString(R.string.delete_sll)
    String str_delete_all;
    @BindString(R.string.delete_sll_confirm)
    String str_delete_all_confirm;
    @BindString(R.string.warning)
    String str_warning;
    @BindView(R.id.material_design_floating_action_menu_item1)
    FloatingActionButton fabButton1;
    @BindView(R.id.material_design_floating_action_menu_item2)
    FloatingActionButton fabButton2;
    @BindView(R.id.material_design_floating_action_menu_item3)
    FloatingActionButton fabButton3;
    private InventoryCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mCursorAdapter = new InventoryCursorAdapter(this, null);
        fabButton1.setVisibility(View.GONE);
        fabButton2.setVisibility(View.GONE);
        fabButton3.setVisibility(View.GONE);
        inventoryListView.setEmptyView(emptyList);
        inventoryListView.setAdapter(mCursorAdapter);
        inventoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ProductActivity.class);
                Uri currentItemUri = ContentUris.withAppendedId(ProductItem.CONTENT_URI, id);
                intent.setData(currentItemUri);
                startActivity(intent);
            }
        });
        materialDesignFAM.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                if (opened) {
                    fabButton1.setVisibility(View.VISIBLE);
                    fabButton2.setVisibility(View.VISIBLE);
                    fabButton3.setVisibility(View.VISIBLE);
                } else {
                    fabButton1.setVisibility(View.GONE);
                    fabButton2.setVisibility(View.GONE);
                    fabButton3.setVisibility(View.GONE);

                }
            }
        });
        loaderManager = getLoaderManager();
        loaderManager.initLoader(LOADER_ID, null, MainActivity.this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ProductItem._ID,
                ProductItem.COLUMN_NAME,
                ProductItem.COLUMN_QUANTITY,
                ProductItem.COLUMN_PRICE,
                ProductItem.COLUMN_PHOTO};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                ProductItem.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order*/
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    @OnClick({R.id.material_design_floating_action_menu_item1, R.id.material_design_floating_action_menu_item2, R.id.material_design_floating_action_menu_item3, R.id.material_design_android_floating_action_menu})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.material_design_floating_action_menu_item1:
                Intent intent = new Intent(MainActivity.this, ProductActivity.class);
                startActivity(intent);
                materialDesignFAM.close(true);
                break;
            case R.id.material_design_floating_action_menu_item2:
                ContentValues values = new ContentValues();
                Random r = new Random();
                values.put(ProductItem.COLUMN_NAME, "Test Item");
                values.put(ProductItem.COLUMN_QUANTITY, r.nextInt((100 - 10) + 1) + 10);
                values.put(ProductItem.COLUMN_PRICE, r.nextInt((100 - 10) + 1) + 10);
                values.put(ProductItem.COLUMN_PHOTO, "");
                // Insert a new row into the provider using the ContentResolver.
                Uri nUri = getContentResolver().insert(ProductItem.CONTENT_URI, values);
                materialDesignFAM.close(true);
                break;
            case R.id.material_design_floating_action_menu_item3:
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(str_warning)
                        .setMessage(str_delete_all_confirm)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                int rows = getContentResolver().delete(ProductItem.CONTENT_URI, null, null);
                                Toast.makeText(getApplicationContext(), str_delete_all, Toast.LENGTH_SHORT).show();
                                materialDesignFAM.close(true);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
                break;
        }
    }
}