package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.util.Base64;

public class utils {
    public static Image decodeBase64ToImage(String base64String) {
        if (base64String == null || base64String.trim().isEmpty()) {
            return new Image("file:default_image.png");
        }
        try {
            byte[] imageBytes = Base64.getDecoder().decode(base64String);
            return new Image(new ByteArrayInputStream(imageBytes));
        } catch (Exception e) {
            return new Image("file:default_image.png");
        }
    }
}
