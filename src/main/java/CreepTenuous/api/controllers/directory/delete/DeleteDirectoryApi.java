package CreepTenuous.api.controllers.directory.delete;

import CreepTenuous.api.controllers.directory.delete.data.FormDeleteDirectoryApi;
import CreepTenuous.api.core.version.v1.V1APIController;
import CreepTenuous.services.directory.delete.services.impl.DeleteDirectory;
import CreepTenuous.providers.build.os.services.CheckIsExistsDirectoryApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.nio.file.NoSuchFileException;

@V1APIController
public class DeleteDirectoryApi implements CheckIsExistsDirectoryApi {
    private final DeleteDirectory deleteDirectory;

    @Autowired
    public DeleteDirectoryApi(DeleteDirectory deleteDirectory) {
        this.deleteDirectory = deleteDirectory;
    }

    @DeleteMapping("/directory/delete")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public final void deleteDirectory(
            final @RequestBody FormDeleteDirectoryApi directoryForm
    ) throws NoSuchFileException {
        deleteDirectory.delete(
                directoryForm.getParents(),
                directoryForm.getName()
        );
    }
}
