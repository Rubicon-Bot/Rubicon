package fun.rubicon.core;

import fun.rubicon.util.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class ImageEditor {

    private BufferedImage image;
    private Graphics2D g;

    public ImageEditor(BufferedImage image) {
        this.image = image;
        this.g = image.createGraphics();
    }

    public ImageEditor drawText(int x, int y, Font font, String text) {
        g.setFont(font);
        g.drawString(text, x, y);
        return this;
    }

    public ImageEditor drawTextCentered(int y, Font font, String text) {
        FontMetrics metrics = g.getFontMetrics(font);
        drawText((image.getWidth() - metrics.stringWidth(text)) / 2, y, font, text);
        return this;
    }

    public ImageEditor drawImage(int x, int y, int width, int height, BufferedImage img) {
        g.drawImage(img, x, y, width, height, null);
        return this;
    }

    public ImageEditor drawImageCentered(int y, int width, int height, BufferedImage img) {
        drawImage((image.getWidth() - width) / 2, y, width, height, img);
        return this;
    }

    public ImageEditor drawRoundImageCentered(int y, int width, int height, BufferedImage img) {
        drawRoundImage((image.getWidth() - width) / 2, y, width, height, img);
        return this;
    }

    public ImageEditor drawRoundImage(int x, int y, int width, int height, BufferedImage img) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Shape clipShape = new Ellipse2D.Float(0, 0, width, height);
        BufferedImage transparent = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D bg = transparent.createGraphics();

        bg.setComposite(AlphaComposite.Clear);
        bg.fillRect(0, 0, width, height);
        bg.setComposite(AlphaComposite.SrcOver);
        bg.drawImage(img, 0, 0, width, height, null);

        g.setPaint(new TexturePaint(transparent, new Rectangle2D.Float(0, 0, width, height)));
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g.translate(x, y);
        g.fill(clipShape);
        g.setPaint(null);
        return this;
    }

    public BufferedImage getImage() {
        return image;
    }

    public InputStream getInputStream() {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", os);
        } catch (IOException e) {
            Logger.error(e);
        }
        return new ByteArrayInputStream(os.toByteArray());
    }

    public void close() {
        g.dispose();
    }
}
