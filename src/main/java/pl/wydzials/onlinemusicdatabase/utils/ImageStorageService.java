package pl.wydzials.onlinemusicdatabase.utils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageStorageService {

  private final Path root = Paths.get("images");

  public String save(final MultipartFile file) throws IOException {
    Validation.notNull(file);

    final String id = UUID.randomUUID().toString();

    Thumbnails.of(file.getInputStream())
        .size(600, 600)
        .crop(Positions.CENTER)
        .outputFormat("jpg")
        .toFile(root.resolve(id + ".jpg").toFile());
    return id;
  }
}
