package fr.vyfe.helper;

import fr.vyfe.ColorNotFoundException;
import fr.vyfe.R;

/**
 * This class converts color (name Firebase) to Drawable (int)
 */
public class ColorHelper {

    public static int convertColor(String nameColor) throws ColorNotFoundException {

        int colorFile;
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
            case "color_gradient_red":
                colorFile = R.drawable.color_gradient_red;
                break;
            case "color_gradient_blue":
                colorFile = R.drawable.color_gradient_blue;
                break;
            case "color_gradient_yellow":
                colorFile = R.drawable.color_gradient_yellow;
                break;
            case "color_gradient_magenta_light":
                colorFile = R.drawable.color_gradient_magenta_light;
                break;
            case "color_gradient_green_lightgreen":
                colorFile = R.drawable.color_gradient_green_lightgreen;
                break;
            default:
                throw new ColorNotFoundException(nameColor);
        }

        return colorFile;
    }
}
