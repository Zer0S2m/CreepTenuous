package CreepTenuous.Api.Directory.DeleteDirectory;

import CreepTenuous.Api.Directory.DeleteDirectory.forms.FormDeleteDirectoryApi;
import CreepTenuous.Api.core.version.v1.V1APIController;
import CreepTenuous.Directory.DeleteDirectory.services.impl.DeleteDirectory;
import CreepTenuous.Directory.utils.check.CheckIsExistsDirectoryApi;

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
