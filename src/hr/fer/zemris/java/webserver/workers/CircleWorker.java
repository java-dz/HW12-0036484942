package hr.fer.zemris.java.webserver.workers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import hr.fer.zemris.java.webserver.RequestContext;

/**
 * Produces a PNG image with dimensions <tt>200x200</tt> containing a single
 * circle filled with a random color. The PNG image is stored in 4 bytes (3
 * bytes for RBG and 1 byte for alpha), in order for the image background to
 * be transparent.
 *
 * @author Mario Bobic
 */
public class CircleWorker implements IWebWorker {

	@Override
	public void processRequest(RequestContext context) {
		context.setMimeType("image/png");
		
		BufferedImage bim = new BufferedImage(200, 200, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2d = bim.createGraphics();
		
		Random rnd = new Random();
		g2d.setColor(new Color(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)));
		g2d.fillOval(0, 0, 200, 200);
		
		g2d.dispose();

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			ImageIO.write(bim, "png", bos);
			context.write(bos.toByteArray());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
