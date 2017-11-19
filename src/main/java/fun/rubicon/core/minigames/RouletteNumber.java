package fun.rubicon.core.minigames;

/**
 * Rubicon Discord bot
 *
 * @author xEiisKeksx
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.core.minigames
 */
public class RouletteNumber {

    private int number;

    public RouletteNumber(int numb, RouletteColor color, RouletteEvenOdd evenOdd, RouletteDozen FirstSecondThird, RouletteEighteen FirstSecond, RouletteColumn UpMidDown) {
        this.number = numb;
    }
}

enum RouletteColor {
    BLACK,
    RED
}

enum RouletteEvenOdd {
    EVEN,
    ODD
}

enum RouletteDozen{
    FIRST,
    SECOND,
    THIRD
}

enum RouletteEighteen{
    FIRST,
    SECOND
}

enum RouletteColumn{
    UP,
    MID,
    DOWN
}
