package CreepTenuous.api.controllers.files.deleteFile;

import CreepTenuous.api.controllers.files.deleteFile.data.DataDeleteFile;
import CreepTenuous.api.core.version.v1.V1APIController;
import CreepTenuous.services.directory.utils.check.CheckIsExistsDirectoryApi;
import CreepTenuous.api.controllers.common.exceptions.NoSuchFileExistsException;
import CreepTenuous.services.files.deleteFile.service.impl.DeleteFile;
import CreepTenuous.services.files.utils.check.CheckIsExistsFileApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@V1APIController
public class DeleteFileApi implements CheckIsExistsDirectoryApi, CheckIsExistsFileApi {
    @Autowired
    private DeleteFile deleteFile;

    @DeleteMapping("/file/delete")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteFile(@RequestBody DataDeleteFile file) throws IOException, NoSuchFileExistsException {
        deleteFile.delete(
                file.getNameFile(),
                file.getParents()
        );
    }
}
