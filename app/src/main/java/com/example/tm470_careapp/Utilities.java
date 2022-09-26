package com.example.tm470_careapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class Utilities {

    private static MainActivity activity;

    public static void setImageViewWithByteArray(ImageView view, byte[] data) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        view.setImageBitmap(bitmap);
    }

    public static Bitmap getImageBitmap(byte[] data) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        return bitmap;
    }

    public static boolean isValidDate(String inDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(inDate.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }

    public static LocalDate convertStringToDate(String inDate) {
       // SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        LocalDate date = null;
        if (inDate.length() > 1 && inDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            System.out.println("ID2");
            formatter = formatter.withLocale(Locale.UK);  // Locale specifies human language for translating, and cultural norms for lowercase/uppercase and abbreviations and such. Example: Locale.US or Locale.CANADA_FRENCH
            date = LocalDate.parse(inDate, formatter);
        }
        return date;
    }

    public static String formatDateStringToString(String date) {
        String parsedDate = null;
        Date initDate = null;
        try {
            initDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            parsedDate = formatter.format(initDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parsedDate;
    }

    public static Integer getUserAccessLevel() {
        System.out.println("Start");
        Integer staffAccessLevel = 0;
        Database dbConnection = Database.getInstance();
        staffAccessLevel = dbConnection.getStaffAccessLevel();
        // Default to level 3 for minimal perms.
        if (staffAccessLevel == 0) {
            staffAccessLevel = 99;
        }

        System.out.println(staffAccessLevel);
        System.out.println("End");
        return staffAccessLevel;
    }
}