package com.way_ne.utils;

import com.way_ne.pojo.CaptchaInfo;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginCaptchaStore {
    private static final long EXPIRE_MS = 5 * 60 * 1000L;

    private final Map<String, CaptchaEntry> store = new ConcurrentHashMap<>();

    public CaptchaInfo create() throws IOException {
        clearExpired();
        String code = randomCode();
        String captchaId = UUID.randomUUID().toString();
        store.put(captchaId, new CaptchaEntry(code, System.currentTimeMillis() + EXPIRE_MS));
        return new CaptchaInfo(captchaId, "data:image/png;base64," + render(code));
    }

    public boolean validate(String captchaId, String captchaCode) {
        clearExpired();
        if (captchaId == null || captchaCode == null) {
            return false;
        }
        CaptchaEntry entry = store.remove(captchaId);
        if (entry == null || entry.expireAt < System.currentTimeMillis()) {
            return false;
        }
        return entry.code.equalsIgnoreCase(captchaCode.trim());
    }

    private void clearExpired() {
        long now = System.currentTimeMillis();
        store.entrySet().removeIf(entry -> entry.getValue().expireAt < now);
    }

    private String randomCode() {
        String source = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            int index = (int) (Math.random() * source.length());
            builder.append(source.charAt(index));
        }
        return builder.toString();
    }

    private String render(String code) throws IOException {
        BufferedImage image = new BufferedImage(120, 40, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, 120, 40);
        graphics.setColor(new Color(220, 220, 220));
        for (int i = 0; i < 6; i++) {
            graphics.drawLine((int) (Math.random() * 120), (int) (Math.random() * 40),
                    (int) (Math.random() * 120), (int) (Math.random() * 40));
        }
        graphics.setFont(new Font("Arial", Font.BOLD, 24));
        graphics.setColor(new Color(60, 60, 60));
        graphics.drawString(code, 18, 28);
        graphics.dispose();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outputStream);
        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }

    private record CaptchaEntry(String code, long expireAt) {
    }
}
