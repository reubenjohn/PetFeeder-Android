package com.aspirephile.petfeeder.android.schedule;

import android.content.ContentValues;
import android.text.format.DateFormat;

import com.aspirephile.petfeeder.android.db.Contract;

import java.util.Calendar;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class Schedule {

    /**
     * A dummy item representing a piece of content.
     */
    public static class RowItem {
        final String timestamp, title, quantity, repeatMode;
        int _id;

        RowItem(Content schedule) {
            this._id = schedule._id;
            this.timestamp = DateFormat.format("hh:mm a", schedule.calendar.getTime()).toString();
            this.title = schedule.name;
            this.quantity = String.valueOf(schedule.quantity);
            this.repeatMode = schedule.isWeekly() ? "WEEKLY" : "DAILY";
        }

        @Override
        public String toString() {
            return timestamp;
        }
    }

    public static class Content {
        public int _id;
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

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}
