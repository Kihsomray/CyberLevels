package net.zerotoil.dev.iridiumapi.patterns;

import net.zerotoil.dev.iridiumapi.IridiumAPI;

import java.awt.*;
import java.util.regex.*;

public class Gradient implements Patterns {

    Pattern pattern = Pattern.compile("<G:([0-9A-Fa-f]{6})>(.*?)</G:([0-9A-Fa-f]{6})>");

    public String process(String string) {
        Matcher matcher = pattern.matcher(string);
        while (matcher.find()) {
            String start = matcher.group(1);
            String end = matcher.group(3);
            String content = matcher.group(2);
            string = string.replace(matcher.group(),
                    IridiumAPI.color(content,
                            new Color(Integer.parseInt(start, 16)),
                            new Color(Integer.parseInt(end, 16))
                    )
            );
        }
        return string;
    }
}