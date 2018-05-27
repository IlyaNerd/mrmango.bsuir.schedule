package mrmango.bsuir.schedule.services;

import de.innosystec.unrar.Archive;
import de.innosystec.unrar.rarfile.FileHeader;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;

/**
 * created by Ilya Aleksandrovich
 * on 27-May-2018
 */
@Service
public class Unarchiver {

    @SneakyThrows
    public void unrar(File file) {
        File archiveDir = new File(file.getParent() + "/" + file.getName().replace(".rar", ""));
        if (!archiveDir.mkdir()) {
            throw new RuntimeException("Unable to create dir " + archiveDir);
        }
        try (Archive archive = new Archive(file)) {
            for (FileHeader fileHeader : archive.getFileHeaders()) {
                try (FileOutputStream os = new FileOutputStream(new File(archiveDir, fileHeader.getFileNameString()))) {
                    archive.extractFile(fileHeader, os);
                }
            }
        }
    }
}
