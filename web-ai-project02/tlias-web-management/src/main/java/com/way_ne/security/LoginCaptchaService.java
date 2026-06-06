package com.way_ne.security;

import com.way_ne.pojo.CaptchaInfo;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginCaptchaService {
    private static final String CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final long EXPIRE_MS = 5 * 60 * 1000L;

    private final Map<String, CaptchaEntry> captchaStore = new ConcurrentHashMap<>();
    private final Random random = new Random();

    public CaptchaInfo generate() {
        cleanup();
        String captchaId = UUID.randomUUID().toString().replace("-", "");
        String code = randomCode(4);
        captchaStore.put(captchaId, new CaptchaEntry(code, System.currentTimeMillis() + EXPIRE_MS));
        return new CaptchaInfo(captchaId, "data:image/png;base64," + toBase64Image(code));
    }

    public boolean validate(String captchaId, String captchaCode) {
        cleanup();
        if (captchaId == null || captchaCode == null) {
            return false;
        }
        CaptchaEntry entry = captchaStore.remove(captchaId);
        if (entry == null || entry.expireAt < System.currentTimeMillis()) {
            return false;
        }
        return entry.code.equalsIgnoreCase(captchaCode.trim());
    }

    private String randomCode(int length) {
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            builder.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return builder.toString();
    }

    private String toBase64Image(String code) {
        BufferedImage image = new BufferedImage(120, 40, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, 120, 40);

        graphics.setFont(new Font("Arial", Font.BOLD, 24));
        for (int i = 0; i < 6; i++) {
            graphics.setColor(randomColor(160, 220));
            int x1 = random.nextInt(120);
            int y1 = random.nextInt(40);
            int x2 = random.nextInt(120);
            int y2 = random.nextInt(40);
            graphics.drawLine(x1, y1, x2, y2);
        }

        for (int i = 0; i < code.length(); i++) {
            graphics.setColor(randomColor(20, 130));
            graphics.drawString(String.valueOf(code.charAt(i)), 18 + i * 22, 28 + random.nextInt(5));
        }
        graphics.dispose();

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", outputStream);
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("生成验证码失败", e);
        }
    }

    private Color randomColor(int min, int max) {
        int red = min + random.nextInt(max - min);
        int green = min + random.nextInt(max - min);
        int blue = min + random.nextInt(max - min);
        return new Color(red, green, blue);
    }

    private void cleanup() {
        long now = System.currentTimeMillis();
        captchaStore.entrySet().removeIf(entry -> entry.getValue().expireAt < now);
    }

    private record CaptchaEntry(String code, long expireAt) {
    }
}
