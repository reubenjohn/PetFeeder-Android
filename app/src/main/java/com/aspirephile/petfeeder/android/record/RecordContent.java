package com.aspirephile.petfeeder.android.record;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Helper class for providing sample timestamp for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class RecordContent {
    /**
     * An array of sample (dummy) items.
     */
    public static final List<Item> ITEMS = new ArrayList<Item>();
    private static final int COUNT = 25;
    static Random random = new Random();
    private static android.text.format.DateFormat dateFormat = new android.text.format.DateFormat();

    static public List<Item> fetchRecords() {
        ITEMS.clear();
        int status = random.nextInt(COUNT / 2);
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i, i < status));
        }
        return ITEMS;
    }

    private static void addItem(Item item) {
        ITEMS.add(item);
    }

    private static Item createDummyItem(int position, boolean status) {
        return new Item(String.valueOf(position),
                dateFormat.format("dd/MM/yyyy hh:mm a", System.currentTimeMillis()).toString(),
                String.valueOf(random.nextInt(12)) + " servings",
                status ? "PENDING" : "COMPLETED");
    }

    /**
     * A dummy item representing a piece of timestamp.
     */
    public static class Item {
        public final String id, timestamp, quantity, status;

        public Item(String id, String timestamp, String quantity, String status) {
            this.id = id;
            this.timestamp = timestamp;
            this.quantity = quantity;
            this.status = status;
        }

        @Override
        public String toString() {
            return timestamp;
        }
    }
}
