package com.funqtional.util;

import util.Base64;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;

/**
 * @author Eric Ballnath
 */
public class Captcha {

    private String phrase = "";
    private static final CharSequence charseq =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int LETTER_WIDTH = 25;
    private static final int NUM_LETTERS = 4;
    private static final int WIDTH = LETTER_WIDTH * NUM_LETTERS;
    private static final int HEIGHT = 40;
    private static final double SKEW = 0.8;

    /**
     * Returns a base64 encoded image as a string.
     * Make() must be called before phrase().
     *
     * @return base64 encoded image string
     */
    public String make() {
        StringBuilder sb = new StringBuilder();
        Random rand = new Random();
        for (int n = 0; n < NUM_LETTERS; n++) {
            sb.append(charseq.charAt(rand.nextInt(charseq.length())));
        }
        phrase = sb.toString();
        BufferedImage image = makeImage(phrase);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "JPEG", baos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Base64.encodeBytes(baos.toByteArray());
    }

    /**
     * Get the generated captcha phrase for assertion.
     * Make() must be called before phrase().
     *
     * @return captcha phrase string
     */
    public String phrase() {
        return phrase;
    }

    private BufferedImage makeImage(String phrase) {
        BufferedImage outImage = new BufferedImage(WIDTH, HEIGHT,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = outImage.createGraphics();
        g2d.setColor(java.awt.Color.WHITE);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(0, 0, (WIDTH) - 1, HEIGHT - 1);

        AffineTransform affineTransform = new AffineTransform();
        for (int i = 0; i < NUM_LETTERS; i++) {
            double angle;
            if (Math.random() * 2 > 1) {
                angle = Math.random() * SKEW;
            } else {
                angle = Math.random() * -SKEW;
            }
            affineTransform.rotate(angle, (LETTER_WIDTH * i) + (LETTER_WIDTH / 2), HEIGHT / 2);
            g2d.setTransform(affineTransform);
            setRandomFont(g2d);
            setRandomFGColor(g2d);
            g2d.drawString(phrase.substring(i, i + 1),
                    (i * LETTER_WIDTH) + 3, 28 + (int) (Math.random() * 6));

            affineTransform.rotate(-angle, (LETTER_WIDTH * i) + (LETTER_WIDTH / 2), HEIGHT / 2);
        }
        return outImage;
    }

    private static final Color[] RANDOM_FG_COLORS = {
            Color.BLACK, Color.BLUE, Color.DARK_GRAY};

    private void setRandomFont(Graphics2D g2d) {
        Font font = new Font("dialog", 1, 33);
        g2d.setFont(font);
    }

    private void setRandomFGColor(Graphics2D g2d) {
        int colorId = (int) (Math.random() * RANDOM_FG_COLORS.length);
        g2d.setColor(RANDOM_FG_COLORS[colorId]);
    }
}
