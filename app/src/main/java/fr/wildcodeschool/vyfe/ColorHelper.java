package fr.wildcodeschool.vyfe;
/**
 * This class convert color (name Firebase) to Drawable (int)
 */

public class ColorHelper extends Exception {

    public static int convertColor(String nameColor) throws Exception {

        int colorFile = 0;
        switch (nameColor) {
            case "color_gradient_blue_dark":
                colorFile = R.drawable.color_gradient_blue_dark;
                break;
            case "color_gradient_blue_light":
                colorFile = R.drawable.color_gradient_blue_light;
                break;
            case "color_gradient_faded_orange":
                colorFile = R.drawable.color_gradient_faded_orange;
                break;
            case "color_gradient_green":
                colorFile = R.drawable.color_gradient_green;
                break;
            case "color_gradient_grey":
                colorFile = R.drawable.color_gradient_grey;
                break;
            case "color_gradient_rosy":
                colorFile = R.drawable.color_gradient_rosy;
                break;
            default:

                throw new Exception("Couleur non trouvée");

        }

        return colorFile;
    }


}
