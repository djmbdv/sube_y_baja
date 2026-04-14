import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

class Carta {
    private static final String[] SHAPE_NAMES = {"Oro", "Copas", "Espada", "Basto"};
    private static final int SPRITE_COLUMNS = 5;
    private static final int SPRITE_CELL_WIDTH = 203;
    private static final int SPRITE_CELL_HEIGHT = 328;
    private static final int SPRITE_HORIZONTAL_GAP = 2;
    private static final int TRIM_LEFT = 5;
    private static final int TRIM_TOP = 4;
    private static final int TRIM_RIGHT = 5;
    private static final int TRIM_BOTTOM = 5;

    public static final int ANCHO_CARTA = SPRITE_CELL_WIDTH - TRIM_LEFT - TRIM_RIGHT;
    public static final int ALTO_CARTA = SPRITE_CELL_HEIGHT - TRIM_TOP - TRIM_BOTTOM;
    private static final File[] SPRITE_FILES = new File[SHAPE_NAMES.length];

    public final int number;
    public final int shape;
    public final Image image;

    public Carta(int number, int shape) throws IOException {
        this.number = number > 7 ? number + 2 : number;
        this.shape = shape;
        this.image = loadCardImage(number, shape);
    }

    public String getShapeString() {
        if (shape < 0 || shape >= SHAPE_NAMES.length) {
            return "";
        }
        return SHAPE_NAMES[shape];
    }

    @Override
    public String toString() {
        return "Carta{numero=" + number + ", palo=" + getShapeString() + "}";
    }

    private static Image loadCardImage(int number, int shape) throws IOException {
        BufferedImage spriteSheet = ImageIO.read(getSpriteFile(shape));
        int spriteIndex = number - 1;
        int column = spriteIndex % SPRITE_COLUMNS;
        int row = spriteIndex / SPRITE_COLUMNS;
        int baseX = column * (SPRITE_CELL_WIDTH + SPRITE_HORIZONTAL_GAP);
        int baseY = row * SPRITE_CELL_HEIGHT;
        int x = baseX + TRIM_LEFT;
        int y = baseY + TRIM_TOP;
        int availableWidth = Math.min(SPRITE_CELL_WIDTH, spriteSheet.getWidth() - baseX);
        int availableHeight = Math.min(SPRITE_CELL_HEIGHT, spriteSheet.getHeight() - baseY);
        int width = Math.max(1, availableWidth - TRIM_LEFT - TRIM_RIGHT);
        int height = Math.max(1, availableHeight - TRIM_TOP - TRIM_BOTTOM);

        return spriteSheet.getSubimage(
                x,
                y,
                width,
                height
        );
    }

    private static File getSpriteFile(int shape) {
        if (SPRITE_FILES[shape] == null) {
            SPRITE_FILES[shape] = new File(SHAPE_NAMES[shape].toLowerCase() + ".jpg");
        }
        return SPRITE_FILES[shape];
    }
}