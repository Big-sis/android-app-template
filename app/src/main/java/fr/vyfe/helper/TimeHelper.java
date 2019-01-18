package fr.vyfe.helper;

public class TimeHelper {
    public static  String formatMillisecTime(int millSeconde) {
        millSeconde = millSeconde / 1000;
        int minutes = millSeconde / 60;
        int second = millSeconde % 60;
        String stringMinutes = String.valueOf(minutes);
        String stringSecond =String.valueOf(second);

        if(minutes<10)stringMinutes="0"+stringMinutes;
        if(second<10)stringSecond="0"+stringSecond;

        return stringMinutes + ":" + stringSecond;
    }
}
