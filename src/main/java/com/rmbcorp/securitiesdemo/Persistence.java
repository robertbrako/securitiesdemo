package com.rmbcorp.securitiesdemo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Persistence {

    private static final Logger logger = Logger.getLogger(Persistence.class.getName());
    private static final String BASE_DIR = "tmp";
    private File tmp = new File(BASE_DIR);

    public void save(String filename, String content) {
        try {
            if (tmp.exists() || tmp.mkdir()) {
                File file = new File(tmp, filename);
                if (file.exists() || file.createNewFile()) {
                    Files.write(file.toPath(), content.getBytes());
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
    }

    public List<String> load(String filename) {
        List<String> result = new ArrayList<>();
        if (new File(tmp, filename).exists()) {
            try {
                result = Files.readAllLines(Paths.get(BASE_DIR, filename));
            } catch (IOException e) {
                logger.log(Level.SEVERE, e.getLocalizedMessage(), e);
            }
        }
        return result;
    }
}
