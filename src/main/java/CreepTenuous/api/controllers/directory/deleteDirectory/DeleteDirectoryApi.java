package CreepTenuous.api.controllers.directory.deleteDirectory;

import CreepTenuous.api.controllers.directory.deleteDirectory.forms.FormDeleteDirectoryApi;
import CreepTenuous.api.core.version.v1.V1APIController;
import CreepTenuous.services.directory.DeleteDirectory.services.impl.DeleteDirectory;
import CreepTenuous.services.directory.utils.check.CheckIsExistsDirectoryApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.nio.file.NoSuchFileException;

@V1APIController
public class DeleteDirectoryApi implements CheckIsExistsDirectoryApi {
    @Autowired
    private DeleteDirectory deleteDirectory;

    @DeleteMapping("/directory/delete")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public final void deleteDirectory(
            @RequestBody FormDeleteDirectoryApi directoryForm
    ) throws NoSuchFileException {
        deleteDirectory.delete(
                directoryForm.getParents(),
                directoryForm.getName()
        );
    }
}
