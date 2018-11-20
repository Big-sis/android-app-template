package fr.wildcodeschool.vyfe;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import fr.wildcodeschool.vyfe.helper.ColorHelper;

import static org.junit.Assert.*;

public class ColorHelperTest {

    @Before
    public void setUp() throws Exception {
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();


    @Test
    public void convertColor() throws ColorNotFoundException {
        assertEquals(R.drawable.color_gradient_blue_dark, ColorHelper.convertColor("color_gradient_blue_dark"));
        assertEquals(R.drawable.color_gradient_blue_light, ColorHelper.convertColor("color_gradient_blue_light"));
        assertEquals(R.drawable.color_gradient_faded_orange, ColorHelper.convertColor("color_gradient_faded_orange"));
        assertEquals(R.drawable.color_gradient_green, ColorHelper.convertColor("color_gradient_green"));
        assertEquals(R.drawable.color_gradient_grey, ColorHelper.convertColor("color_gradient_grey"));
        assertEquals(R.drawable.color_gradient_rosy, ColorHelper.convertColor("color_gradient_rosy"));

        thrown.expect(ColorNotFoundException.class);
        ColorHelper.convertColor("not_a_color");
    }
}