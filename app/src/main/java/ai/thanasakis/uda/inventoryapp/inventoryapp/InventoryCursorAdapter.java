package ai.thanasakis.uda.inventoryapp.inventoryapp;

/**
 * Created by programbench on 7/6/2017.
 */

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import ai.thanasakis.uda.inventoryapp.inventoryapp.InventoryContract.ProductItem;

import static ai.thanasakis.uda.inventoryapp.inventoryapp.InventoryContract.ProductItem.COLUMN_QUANTITY;

public class InventoryCursorAdapter extends CursorAdapter {

    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        TextView textViewName = (TextView) view.findViewById(R.id.text_name);
        TextView textViewQuantity = (TextView) view.findViewById(R.id.text_quantity);
        TextView textViewPrice = (TextView) view.findViewById(R.id.text_price);
        Button saleButton = (Button) view.findViewById(R.id.button_sall);
        final int position = cursor.getPosition();

        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cursor.moveToPosition(position);
                int itemIdColumnIndex = cursor.getColumnIndex(ProductItem._ID);
                final long itemId = cursor.getLong(itemIdColumnIndex);
                Uri mCurrentItemUri = ContentUris.withAppendedId(ProductItem.CONTENT_URI, itemId);
                int quantityColumnIndex = cursor.getColumnIndex(COLUMN_QUANTITY);
                String itemQuantity = cursor.getString(quantityColumnIndex);
                int updateQuantity = Integer.parseInt(itemQuantity);

                if (updateQuantity > 0) {
                    Toast.makeText(context, context.getResources().getString(R.string.quantity_reduced), Toast.LENGTH_SHORT).show();
                    updateQuantity--;
                    ContentValues updateValues = new ContentValues();
                    updateValues.put(ProductItem.COLUMN_QUANTITY, updateQuantity);
                    int rows = context.getContentResolver().update(mCurrentItemUri, updateValues, null, null);
                } else {
                    Toast.makeText(context, context.getResources().getString(R.string.quantity_zero), Toast.LENGTH_SHORT).show();
                }
            }
        });

        int nameColumnIndex = cursor.getColumnIndex(ProductItem.COLUMN_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(COLUMN_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(ProductItem.COLUMN_PRICE);

        String itemName = cursor.getString(nameColumnIndex);
        int itemQuantity = cursor.getInt(quantityColumnIndex);
        int itemPrice = cursor.getInt(priceColumnIndex);

        String quantityField = "Quantity: " + Integer.toString(itemQuantity);
        String priceField = "Price: " + Integer.toString(itemPrice);

        textViewName.setText(itemName);
        textViewQuantity.setText(quantityField);
        textViewPrice.setText(priceField);


    }

}
