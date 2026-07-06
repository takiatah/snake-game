import javax.swing.JFrame;

public class StartGame {
    public static void main(String[] args) {

        // create frame
        JFrame frame = new JFrame("Snake Game");
        SnakeGame panel = new SnakeGame();

        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }

}
