import javafx.scene.image.Image;

class Bird {
        int x, y, width, height;
        Image img;

        Bird(int x, int y, Image img) {
            this.x = x;
            this.y = y;
            this.width = 34;
            this.height = 24;
            this.img = img;
        }
}