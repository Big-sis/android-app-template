package fr.vyfe;

public class RestartSession {

    public static String implementTitleGrid(String titleName) {

        if (titleName.contains("_")) {
            String[] parts = titleName.split("_");
            String part2 = parts[parts.length - 1];

            String[] first = titleName.split(part2);
            String part1 = first[0];

            try {
                int number = Integer.parseInt(part2);
                part2 = String.valueOf(number += 1);
                titleName = part1 + part2;

            } catch (NumberFormatException nfe) {
                titleName += "_2";
            }
        } else titleName += "_2";

        return titleName;
    }

}
