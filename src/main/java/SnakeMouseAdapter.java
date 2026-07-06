import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SnakeMouseAdapter extends MouseAdapter {
    SnakeGame game;

    public SnakeMouseAdapter(SnakeGame game) {
        this.game = game;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        this.game = game;
        if (game.state == game.START) {
            checkColorButtons(e.getX(), e.getY());
        }
    }

    public void checkColorButtons(int mouseX, int mouseY) {
        for (ColorButton button : game.colorButtons) {
            if ((mouseX >= button.x) &&
                    (mouseX <= button.x + button.size) &&
                    (mouseY >= button.y) &&
                    (mouseY <= button.y + button.size)) {

                game.colorChosen = button;
                game.repaint();
            }
        }
    }
}

