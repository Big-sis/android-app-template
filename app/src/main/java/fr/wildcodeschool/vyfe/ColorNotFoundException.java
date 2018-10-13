package fr.wildcodeschool.vyfe;

public class ColorNotFoundException extends Exception {
    public ColorNotFoundException(String color) {
        super("Color " + color + " is not defined as a Vyfe standard color ");
    }
}
