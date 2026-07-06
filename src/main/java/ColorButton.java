import java.awt.*;

public class ColorButton {
    SnakeGame game;
    String name;
    int x;
    int y;
    int size = 40;
    Color head;
    Color body;

    public ColorButton(SnakeGame game, String name, int x, int y, Color head, Color body) {
        this.game = game;
        this.name = name;
        this.x = x;
        this.y = y;
        this.head = head;
        this.body = body;
    }
}