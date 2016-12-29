package com.aspirephile.pedifeed.android.schedule;

import android.content.ContentValues;
import android.text.format.DateFormat;

import com.aspirephile.pedifeed.android.db.Contract;

import java.util.Calendar;
import java.util.Comparator;

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
        public final String timestamp, title, quantity, repeatMode;
        public int _id;

        RowItem(Content schedule) {
            this._id = schedule._id;
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR, schedule.getHour());
            cal.set(Calendar.MINUTE, schedule.getMinute());
            this.timestamp = DateFormat.format("hh:mm a", cal.getTime()).toString();
            this.title = schedule.name;
            this.quantity = String.valueOf(schedule.quantity);
            this.repeatMode = schedule.isWeekly() ? "WEEKLY" : "DAILY";
        }
    }

    public static class Content {
        public static Comparator<? super Content> timestampComparator = new Comparator<Content>() {
            @Override
            public int compare(Content o1, Content o2) {
                return (int) (o1.calendar - o2.calendar);
            }
        };
        public int _id;
        public String name;
        public int quantity;
        private long calendar;

        public Content() {
        }

        public Content(Content content) {
            this._id = content._id;
            name = content.name;
            quantity = content.quantity;
            calendar = content.calendar;
        }

        public short getMinute() {
            return (short) (calendar % 60);
        }

        public void setMinute(short minute) {
            if (minute < 0 || minute >= 60) throw new IllegalArgumentException();
            calendar -= getMinute();
            calendar += minute;
        }

        public short getHour() {
            return (short) ((short) (calendar / 60) % 24);
        }

        public void setHour(short hour) {
            if (hour < 0 || hour >= 24) throw new IllegalArgumentException();
            calendar -= getHour() * 60;
            calendar += hour * 60;

        }

        public int getDayOfWeek() {
            return (int) (calendar / 60 / 24);
        }

        public void setDayOfWeek(int dayOfWeek) {
            if (dayOfWeek < 1 || dayOfWeek >= 8)
                throw new IllegalArgumentException("Day of week cannot be: " + dayOfWeek);
            calendar -= getDayOfWeek() * 24 * 60;
            calendar += dayOfWeek * 24 * 60;
        }

        public boolean isWeekly() {
            return getDayOfWeek() < 8;
        }

        public void setIsDaily() {
            calendar -= getDayOfWeek() * 24 * 60;
            calendar += 8 * 24 * 60;
        }

        public ContentValues getContentValues() {
            ContentValues values = new ContentValues();
            values.put(Contract.Schedule.NAME, name);
            values.put(Contract.Schedule.QUANTITY, quantity);
            values.put(Contract.Schedule.DATETIME, getEncodedCalendar());
            return values;
        }

        public long getEncodedCalendar() {
            return calendar;

        }

        public void setEncodedCalendar(long encodedTimestamp) {
            calendar = 0;
            setMinute((short) (encodedTimestamp % 60));
            encodedTimestamp /= 60;
            setHour((short) (encodedTimestamp % 24));
            encodedTimestamp /= 24;
            if (encodedTimestamp == 8) {
                setIsDaily();
            } else {
                setDayOfWeek((int) (encodedTimestamp % 7));
            }
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public long getWeekRelativeMillis() {
            return (getEncodedCalendar() - 60L * 24) * 60L * 1000L;
        }
    }
}
