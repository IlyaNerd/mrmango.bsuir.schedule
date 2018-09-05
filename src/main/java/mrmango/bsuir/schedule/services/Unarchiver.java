package mrmango.bsuir.schedule.services;

import de.innosystec.unrar.Archive;
import de.innosystec.unrar.rarfile.FileHeader;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;

/**
 * created by Ilya Aleksandrovich
 * on 27-May-2018
 */
@Service
@Slf4j
public class Unarchiver {

    @SneakyThrows
    public void unrar(File archiveFile) {
        log.info("Unraring file [" + archiveFile.getPath() + "]");
        File archiveDir = new File(archiveFile.getParent() + "/");
        try (Archive archive = new Archive(archiveFile)) {
            for (FileHeader fileHeader : archive.getFileHeaders()) {
                File unraredFile = new File(archiveDir, fileHeader.getFileNameString());
                try (FileOutputStream os = new FileOutputStream(unraredFile)) {
                    archive.extractFile(fileHeader, os);
                }
            }
        }
    }
}
