import javafx.scene.image.Image;

class Pipe {
        int x, y, width, height;
        Image img;
        boolean passed = false;

        Pipe(int x, int y, int width, int height, Image img) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.img = img;
        }
    }