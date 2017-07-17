package ai.thanasakis.uda.inventoryapp.inventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ai.thanasakis.uda.inventoryapp.inventoryapp.InventoryContract.ProductItem;

/**
 * Created by programbench on 7/4/2017.
 */

public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Inventory.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_INVENTORY_TABLE = "CREATE TABLE " + ProductItem.TABLE_NAME + " ("
                + ProductItem._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductItem.COLUMN_NAME + " TEXT,"
                + ProductItem.COLUMN_DESCRIPTION + " TEXT,"
                + ProductItem.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + ProductItem.COLUMN_PRICE + " REAL NOT NULL DEFAULT 0, "
                + ProductItem.COLUMN_PHOTO + " TEXT" + ")";

        db.execSQL(CREATE_INVENTORY_TABLE);
        Log.d("DB Created succesfully", "Success");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //No need for an upgrade at this stage of the app
        Log.d("DB Updated succesfully", "Success");
    }
}
