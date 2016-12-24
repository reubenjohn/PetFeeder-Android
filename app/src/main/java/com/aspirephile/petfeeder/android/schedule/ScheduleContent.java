package com.aspirephile.petfeeder.android.schedule;

import android.text.format.DateFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class ScheduleContent {

    private static Random random = new Random();

    /**
     * An array of sample (dummy) items.
     */
    public static final List<Item> ITEMS = new ArrayList<Item>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, Item> ITEM_MAP = new HashMap<String, Item>();
    private static final int COUNT = 25;
    private static DateFormat dateFormat = new DateFormat();

    static public List<Item> fetchSchedules() {
        ITEMS.clear();
        int status = random.nextInt(COUNT / 2);
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
        return ITEMS;
    }

    private static void addItem(Item item) {
        ITEMS.add(item);
    }

    private static Item createDummyItem(int position) {
        return new Item(String.valueOf(position),
                dateFormat.format("hh:mm a",System.currentTimeMillis()).toString(),
                random.nextBoolean() ? "Breakfast" : "Lunch",
                String.valueOf(random.nextInt(12)) + " servings",
                random.nextBoolean() ? "DAILY" : "WEEKLY");
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class Item {
        public final String id, timestamp, title, quantity, repeatMode;

        public Item(String id, String timestamp, String title, String quantity, String repeatMode) {
            this.id = id;
            this.timestamp = timestamp;
            this.title = title;
            this.quantity = quantity;
            this.repeatMode = repeatMode;
        }

        @Override
        public String toString() {
            return timestamp;
        }
    }
}
