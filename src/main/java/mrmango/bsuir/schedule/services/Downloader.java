package mrmango.bsuir.schedule.services;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * created by Ilya Aleksandrovich
 * on 27-May-2018
 */
@Service
public class Downloader {

    private final String dir;

    public Downloader(@Value("${download.dir}") String dir) {
        this.dir = dir;
    }

    @SneakyThrows
    public File download(String from) {
        URL url = new URL(from);
        File archive = new File(dir + "schedule.rar");
        archive.getParentFile().mkdirs();
        try (ReadableByteChannel rbc = Channels.newChannel(url.openStream());
             FileOutputStream fos = new FileOutputStream(archive)) {
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }
        return archive;
    }
}
