package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public final class ResourceLoader {
    private ResourceLoader() {}

    public static URL getUrl(String path) {
        if (path == null || path.isBlank()) {
            return null;
        }

        String normalized = path.startsWith("/") ? path.substring(1) : path;
        ClassLoader loader = ResourceLoader.class.getClassLoader();

        URL url = loader.getResource(normalized);
        if (url != null) {
            return url;
        }

        url = ResourceLoader.class.getResource(path.startsWith("/") ? path : "/" + path);
        if (url != null) {
            return url;
        }

        String[] candidates = {
            normalized,
            "res/" + normalized,
            "../res/" + normalized,
            "Digimon_Digital_Monsters_AllOut_Attack/res/" + normalized,
            "../Digimon_Digital_Monsters_AllOut_Attack/res/" + normalized
        };

        for (String candidate : candidates) {
            File file = new File(candidate);
            if (file.exists()) {
                try {
                    return file.toURI().toURL();
                } catch (Exception ignored) {
                }
            }
        }
        return null;
    }

    public static InputStream open(String path) throws IOException {
        URL url = getUrl(path);
        if (url != null) {
            return url.openStream();
        }
        throw new IOException("No se encontró el recurso: " + path);
    }

    public static BufferedImage image(String path) throws IOException {
        try (InputStream input = open(path)) {
            return ImageIO.read(input);
        }
    }
}
