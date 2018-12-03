package fr.vyfe;

public class RestartSession {

    public static String implementTitleGrid(String titleName) {
        if (titleName.matches("^.*[_0-9]")) {
            String[] parts = titleName.split("_");
            String versionNum = parts[parts.length - 1];

            StringBuilder name = new StringBuilder();
            for (int i = 0; i < parts.length - 1; i++){
                if (i>0) name.append("_");
                name.append(parts[i]);
            }

            int number = Integer.parseInt(versionNum);
            return name.toString() + "_"+String.valueOf(number + 1);

        } else
            return titleName + "_2";
    }

}
