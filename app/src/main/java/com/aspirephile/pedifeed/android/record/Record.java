package com.aspirephile.pedifeed.android.record;

import android.content.ContentValues;
import android.text.format.DateFormat;
import android.util.Log;

import com.aspirephile.pedifeed.android.connection.BluetoothSerialService;
import com.aspirephile.pedifeed.android.db.Contract;
import com.aspirephile.pedifeed.android.schedule.Schedule;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Helper class for providing sample timestamp for user interfaces created by
 * Android template wizards.
 * <p>
 */
public class Record {
    /**
     * An array of sample (dummy) items.
     */
    public static final List<Item> ITEMS = new ArrayList<Item>();
    private static Random random = new Random();

    /**
     * A dummy item representing a piece of timestamp.
     */
    public static class Item {
        public final String id, name, timestamp, quantity;

        public Item(String id, String name, String timestamp, String quantity) {
            this.id = id;
            this.name = name;
            this.timestamp = timestamp;
            this.quantity = quantity;
        }

        public Item(DateFormat dateFormat, Content content) {
            this(String.valueOf(content._id), content.name, DateFormat.format("dd/MM/yyyy hh:mm a", System.currentTimeMillis()).toString(), String.valueOf(content.quantity));
        }
    }

    public static class Content implements Serializable {
        private static final SimpleDateFormat dateTimeFormat = (SimpleDateFormat) SimpleDateFormat.getInstance();
        private static final Date date = new Date();
        public int _id;
        public String name;
        public int quantity;
        public long timestamp;

        public Content() {
        }

        public Content(Schedule.Content content, long startOfWeekElapse) {
            _id = Math.abs(random.nextInt());
            name = content.name;
            quantity = content.quantity;
            long weekRelativeMillis = content.getWeekRelativeMillis();
            timestamp = (startOfWeekElapse + weekRelativeMillis);
        }

        public ContentValues getContentValues() {
            ContentValues values = new ContentValues();
            values.put(Contract.Schedule.NAME, name);
            values.put(Contract.Schedule.QUANTITY, quantity);
            values.put(Contract.Schedule.DATETIME, timestamp);
            return values;
        }

        @Override
        public String toString() {
            date.setTime(timestamp);
            return "{ _id: " + _id + ", name: " + name + ", quantity: " + quantity + ", timestamp: " + dateTimeFormat.format(date) + " (" + timestamp + ")" + " }";
        }

        public void readTransmittableObject(String s, long startTimestamp) {
            // default deserialization
            String[] parts = s.split(" ");
            _id = Integer.parseInt(parts[0]);
            timestamp = startTimestamp + (long) Integer.parseInt(parts[1]);
            quantity = Integer.parseInt(parts[2]);
        }

        public String getTransmittableLine(long startingTimestamp) {
            return String.valueOf(_id) + ' ' + (int) (timestamp - startingTimestamp) + ' ' + quantity + ' ';
        }
    }

    public static class WeeklyBuilder {

        private final List<Schedule.Content> weeklySchedule;
        private final Calendar startOfWeek;

        public WeeklyBuilder(List<Schedule.Content> list) {
            if (list == null || list.size() == 0)
                throw new IllegalArgumentException("Schedule cannot be empty");
            this.startOfWeek = Calendar.getInstance();

            ArrayList<Schedule.Content> dailySchedule = new ArrayList<>();

            weeklySchedule = new ArrayList<>();
            for (Schedule.Content content : list) {
                if (content.isWeekly()) {
                    weeklySchedule.add(content);
                } else {
                    dailySchedule.add(content);
                }
            }

            for (int dayOfWeek = 1; dayOfWeek <= 7; dayOfWeek++) {
                for (Schedule.Content content : dailySchedule) {
                    Schedule.Content dailyRepeatContent = new Schedule.Content(content);
                    dailyRepeatContent.setDayOfWeek(dayOfWeek);
                    weeklySchedule.add(dailyRepeatContent);
                }
            }

            Collections.sort(weeklySchedule, Schedule.Content.timestampComparator);

        }

        public void nextWeek() {
            startOfWeek.setTimeInMillis(startOfWeek.getTimeInMillis() + 604800000L);
        }

        public void setStartOfWeekFromAnyTimeInWeek(long anyTimeInWeek) {
            startOfWeek.setTimeInMillis(anyTimeInWeek);
            startOfWeek.set(Calendar.DAY_OF_WEEK, 1);
            startOfWeek.set(Calendar.HOUR, 0);
            startOfWeek.set(Calendar.AM_PM, Calendar.AM);
            startOfWeek.set(Calendar.MINUTE, 0);
            startOfWeek.set(Calendar.SECOND, 0);
            startOfWeek.set(Calendar.MILLISECOND, 0);
            startOfWeek.getTimeInMillis();
            //
        }

        public ArrayList<Content> getWeekRecords() {
            long startOfWeekElapse = startOfWeek.getTimeInMillis();

            ArrayList<Content> recordList = new ArrayList<>();
            for (Schedule.Content content : weeklySchedule) {
                Content record = new Content(content, startOfWeekElapse);
                recordList.add(record);
            }
            return recordList;
        }

        public ArrayList<Content> getRemainingWeekRecordsAfter(long timestamp) {
            setStartOfWeekFromAnyTimeInWeek(timestamp);
            long startOfWeekElapse = startOfWeek.getTimeInMillis();

            ArrayList<Content> recordList = new ArrayList<>();
            for (Schedule.Content content : weeklySchedule) {
                //TODO Optimize code statement to avoid perform check check when the first content after the timestamp is found
                if (startOfWeekElapse + content.getWeekRelativeMillis() >= timestamp) {
                    Content record = new Content(content, startOfWeekElapse);
                    recordList.add(record);
                }
            }
            return recordList;
        }
    }

    public static class SyncManager {
        private final BluetoothSerialService mSerialService;
        private int syncLatency;
        private int recordsRemaining, recordsToWrite;
        private WeeklyBuilder builder;
        private ByteBuffer inputStream;
        private boolean isListening;

        public SyncManager(BluetoothSerialService mSerialService) {
            this.mSerialService = mSerialService;
            inputStream = ByteBuffer.allocate(1024);
            isListening = false;
        }

        public SyncManager setSyncLatency(int syncLatency) {
            this.syncLatency = syncLatency;
            return this;
        }

        public SyncManager setRecordsToWrite(int recordsToWrite) {
            if (recordsToWrite <= 0)
                throw new IllegalArgumentException("Number of records to write must be a natural number");
            this.recordsRemaining = this.recordsToWrite = recordsToWrite;
            return this;
        }

        public SyncManager setScheduleList(List<Schedule.Content> list) {
            this.builder = new WeeklyBuilder(list);
            return this;
        }

        private void writeRecords(long startingTimestamp) throws IOException {
            mSerialService.write("r".getBytes());
            mSerialService.write((String.valueOf(recordsRemaining) + " ").getBytes());
            ArrayList<Content> recordList = builder.getRemainingWeekRecordsAfter(startingTimestamp);
            transmitWhileWriteRemaining(recordList, startingTimestamp);

            while (!isTransmissionComplete()) {
                builder.nextWeek();
                recordList = builder.getWeekRecords();
                transmitWhileWriteRemaining(recordList, startingTimestamp);
                recordList.clear();
            }
        }

        private void transmitWhileWriteRemaining(ArrayList<Content> list, long startingTimestamp) throws IOException {
            for (Record.Content record : list) {
                String recordLine = record.getTransmittableLine(startingTimestamp);
                Log.d("TAG", recordLine + "\n" + record.toString());
                mSerialService.write(recordLine.getBytes());
                try {
                    Thread.sleep(syncLatency);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                recordsRemaining--;
                if (recordsRemaining == 0)
                    return;
            }
        }

        public boolean isTransmissionComplete() {
            return recordsRemaining == 0;
        }

        public void sync() {
            isListening = true;
            recordsRemaining = recordsToWrite;
            mSerialService.write("c".getBytes());

            try {
                writeRecords(System.currentTimeMillis());
                readRecords();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            isListening = false;
        }

        private void readRecords() {
            mSerialService.write("w".getBytes());
        }

        public void onRead(byte[] buf) {
            if (isListening && buf.length < inputStream.limit()) {
                while (buf.length >= inputStream.capacity()) ;
                inputStream.put(buf);
            }
        }
    }

}
