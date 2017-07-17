package ai.thanasakis.uda.inventoryapp.inventoryapp;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by programbench on 7/4/2017.
 */

public class InventoryContract {
    public static final String CONTENT_AUTHORITY = "ai.thanasakis.uda.inventoryapp.inventoryapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_ITEMS = "items";

    //empty constructor private not to instantiate
    private InventoryContract() {
    }

    public static final class ProductItem implements BaseColumns {

        public final static String TABLE_NAME = "mainTable";

        /**
         * Name of the Product.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_NAME = "name";

        /**
         * Description of the product.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_DESCRIPTION = "description";

        /**
         * Remaining quantity of the product.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_QUANTITY = "quantity";

        /**
         * Price of the product.
         * <p>
         * Type: REAL
         */
        public final static String COLUMN_PRICE = "price";
        /**
         * Photo of the item.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_PHOTO = "photo";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ITEMS);
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;
    }

}
