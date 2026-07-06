import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class SnakeKeyAdapter extends KeyAdapter {
    SnakeGame game;

    public SnakeKeyAdapter(SnakeGame game) {
        this.game = game;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        switch(keyCode) {
            case KeyEvent.VK_LEFT:
                if (game.direction != game.RIGHT) {
                    game.direction = game.LEFT;
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (game.direction != game.LEFT) {
                    game.direction = game.RIGHT;
                }
                break;
            case KeyEvent.VK_UP:
                if (game.direction != game.DOWN) {
                    game.direction = game.UP;
                }
                break;
            case KeyEvent.VK_DOWN:
                if (game.direction != game.UP) {
                    game.direction = game.DOWN;
                }
                break;
        }
    }
}