import Exceptions.FailedCheckException;

/**
 * Класс - поле класса Route
 */

public class Coordinates {
    private int x; //Поле может быть null
    private Long y; //Значение поля должно быть больше -765, Поле не может быть null

    public Coordinates(int x, Long y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Проверка для x Integer
     */
    public static Checker<Integer> xCheck = (Integer I) -> {
        if (I != null) return I;
        else throw new FailedCheckException();
    };

    /**
     * Проверка для y Long
     */
    public static Checker<Long> yCheck = (Long L) -> {
        if (L != null && L > -765) return L;
        else throw new FailedCheckException();
    };

    public int getX() {
        return x;
    }

    public Long getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Coordinates{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}