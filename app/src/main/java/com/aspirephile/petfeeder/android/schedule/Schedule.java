package com.aspirephile.petfeeder.android.schedule;

import android.content.ContentValues;
import android.text.format.DateFormat;

import com.aspirephile.petfeeder.android.db.Contract;

import java.util.ArrayList;
import java.util.Calendar;
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
public class Schedule {

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, RowItem> ITEM_MAP = new HashMap<String, RowItem>();
    /**
     * An array of sample (dummy) items.
     */
    private static final List<RowItem> ITEMS = new ArrayList<RowItem>();
    private static final int COUNT = 25;
    private static Random random = new Random();
    private static DateFormat dateFormat = new DateFormat();

    static List<RowItem> fetchSchedules() {
        ITEMS.clear();
        int status = random.nextInt(COUNT / 2);
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
        return ITEMS;
    }

    private static void addItem(RowItem item) {
        ITEMS.add(item);
    }

    private static RowItem createDummyItem(int position) {
        return new RowItem(String.valueOf(position),
                DateFormat.format("hh:mm a", System.currentTimeMillis()).toString(),
                random.nextBoolean() ? "Breakfast" : "Lunch",
                String.valueOf(random.nextInt(12)) + " servings",
                random.nextBoolean() ? "DAILY" : "WEEKLY");
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about RowItem: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class RowItem {
        final String id, timestamp, title, quantity, repeatMode;

        RowItem(String id, String timestamp, String title, String quantity, String repeatMode) {
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

    public static class Content {
        public long _id;
        public String name;
        public int quantity;
        private Calendar calendar;

        public Content() {
            calendar = Calendar.getInstance();
        }

        public short getHour() {
            return (short) calendar.get(Calendar.HOUR);
        }

        public void setHour(short hour) {
            calendar.set(Calendar.HOUR, hour);
        }

        public short getMinute() {
            return (short) calendar.get(Calendar.MINUTE);
        }

        public void setMinute(short minute) {
            calendar.set(Calendar.MINUTE, minute);
        }

        public boolean isWeekly() {
            return getDayOfWeek() == 0;
        }

        public void setIsDaily() {
            calendar.set(Calendar.DAY_OF_WEEK, 0);
        }

        public int getDayOfWeek() {
            return calendar.get(Calendar.DAY_OF_WEEK);
        }

        public void setDayOfWeek(int dayOfWeek) {
            calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        }

        public ContentValues getContentValues() {
            ContentValues values = new ContentValues();
            values.put(Contract.Schedule.NAME, name);
            values.put(Contract.Schedule.QUANTITY, quantity);
            values.put(Contract.Schedule.DATETIME, getEncodedCalendar());
            return values;
        }

        public long getEncodedCalendar() {
            return getMinute() + 60 * (getHour() + 24 * getDayOfWeek());

        }

        public void setEncodedCalendar(long encodedCalendar) {
            setMinute((short) (encodedCalendar % 60));
            encodedCalendar /= 60;
            setHour((short) (encodedCalendar % 24));
            encodedCalendar /= 24;
            setDayOfWeek((int) (encodedCalendar % 7));
        }
    }
}
