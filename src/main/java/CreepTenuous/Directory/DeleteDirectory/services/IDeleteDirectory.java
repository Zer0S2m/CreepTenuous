package CreepTenuous.Directory.DeleteDirectory.services;

import CreepTenuous.Directory.BuilderDirectory.enums.Directory;

import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

public interface IDeleteDirectory {
    void delete(String[] parents, String name) throws NoSuchFileException;
}
