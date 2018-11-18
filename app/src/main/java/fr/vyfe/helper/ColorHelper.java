package fr.vyfe.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import fr.vyfe.R;
import fr.vyfe.model.ColorModel;

/**
 * This class converts color (name Firebase) to Drawable (int)
 */
public class ColorHelper {

    private static ColorHelper instance;
    private ArrayList<ColorModel> colors;


    private ColorHelper(){
        colors = new ArrayList<>();
        colors.add(new ColorModel("color_gradient_blue_dark", R.string.color_gradient_blue_dark, R.drawable.color_gradient_blue_dark));
        colors.add(new ColorModel("color_gradient_blue_light", R.string.color_gradient_blue_light , R.drawable.color_gradient_blue_light));
        colors.add(new ColorModel("color_gradient_faded_orange", R.string.color_gradient_faded_orange , R.drawable.color_gradient_faded_orange));
        colors.add(new ColorModel("color_gradient_green", R.string.color_gradient_green , R.drawable.color_gradient_green));
        colors.add(new ColorModel("color_gradient_grey", R.string.color_gradient_grey , R.drawable.color_gradient_grey));
        colors.add(new ColorModel("color_gradient_rosy", R.string.color_gradient_rosy , R.drawable.color_gradient_rosy));
        colors.add(new ColorModel("color_gradient_red", R.string.color_gradient_red , R.drawable.color_gradient_red));
        colors.add(new ColorModel("color_gradient_blue", R.string.color_gradient_blue , R.drawable.color_gradient_blue));
        colors.add(new ColorModel("color_gradient_yellow", R.string.color_gradient_yellow , R.drawable.color_gradient_yellow));
        colors.add(new ColorModel("color_gradient_magenta_light", R.string.color_gradient_magenta_light , R.drawable.color_gradient_magenta_light));
        colors.add(new ColorModel("color_gradient_green_lightgreen", R.string.color_gradient_green_lightgreen , R.drawable.color_gradient_green_lightgreen));
    }


    public static ColorHelper getInstance(){
        if (instance == null)
            instance = new ColorHelper();
        return instance;
    }

    public ArrayList<ColorModel> getColors() {
        return this.colors;
    }

    public ColorModel findColorById(String name){
        for (ColorModel color: colors) {
            if (color.getId().equals(name))
                return color;
        }
        return null;
    }
}
