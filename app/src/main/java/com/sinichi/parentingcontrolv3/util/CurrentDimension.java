package com.sinichi.parentingcontrolv3.util;

public class CurrentDimension {

    public static String defineMonth(int month) {
        String[] months = {"Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli",
                "Agustus", "September", "Oktober", "November", "Desember"};
        return months[month];
    }

    public static String defineDays(int day) {
        String[] days = {"Minggu", "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu"};
        return days[day - 1];
    }
}
