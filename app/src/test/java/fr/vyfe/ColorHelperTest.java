package fr.vyfe;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import fr.vyfe.helper.ColorHelper;

import static org.junit.Assert.assertEquals;


public class ColorHelperTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void convertColor() {
        assertEquals(R.drawable.color_gradient_blue_dark, ColorHelper.getInstance().findColorById("color_gradient_blue_dark").getImage());
        assertEquals(R.drawable.color_gradient_blue_light, ColorHelper.getInstance().findColorById("color_gradient_blue_light").getImage());
        assertEquals(R.drawable.color_gradient_faded_orange, ColorHelper.getInstance().findColorById("color_gradient_faded_orange").getImage());
        assertEquals(R.drawable.color_gradient_green, ColorHelper.getInstance().findColorById("color_gradient_green").getImage());
        assertEquals(R.drawable.color_gradient_rosy, ColorHelper.getInstance().findColorById("color_gradient_rosy").getImage());
    }
}