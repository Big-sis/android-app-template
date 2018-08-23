package fr.wildcodeschool.vyfe;

class ColorNotFoundException extends Throwable {
    ColorNotFoundException(String color) {
        super("Color " + color + " is not defined as a Vyfe standard color ");
    }
}
