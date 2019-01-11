package fr.vyfe.helper;

public class TimeHelper {
    public static  String mllsConvert(int millSeconde) {
        millSeconde = millSeconde / 1000;
        int minutes = millSeconde / 60;
        int second = millSeconde % 60;
        return String.valueOf(minutes) + ":" + String.valueOf(second);
    }
}
