package com.hhd.utils;

import org.apache.tika.metadata.HttpHeaders;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaMetadataKeys;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.helpers.DefaultHandler;

import java.io.InputStream;
import java.util.HashMap;

/**
 * @author -无心
 * @date 2023/2/22 1:47:40
 */
public class MimeTypeUtils {
    private static final String[] TYPE = {"audio", "video", "image"};

    public static int getMimeType(MultipartFile file) {
        AutoDetectParser parser = new AutoDetectParser();
        parser.setParsers(new HashMap<>(8));
        Metadata metadata = new Metadata();
        metadata.add(TikaMetadataKeys.RESOURCE_NAME_KEY, file.getName());
        try (InputStream stream = file.getInputStream()) {
            parser.parse(stream, new DefaultHandler(), metadata, new ParseContext());
        } catch (Exception e) {
            throw new RuntimeException();
        }
        String s = metadata.get(HttpHeaders.CONTENT_TYPE);
        for (int i = 0; i < TYPE.length; i++) {
            if (StringUtils.startsWithIgnoreCase(s, TYPE[i])) {
                return i;
            }
        }

        return 5;
    }


}
