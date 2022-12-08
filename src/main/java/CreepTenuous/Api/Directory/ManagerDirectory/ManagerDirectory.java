package CreepTenuous.Api.Directory.ManagerDirectory;

import CreepTenuous.Api.Directory.ManagerDirectory.data.DataManagerDirectory;
import CreepTenuous.Directory.BuilderDirectory.services.impl.BuilderDirectory;
import CreepTenuous.Directory.BuilderDirectory.exceptions.ExceptionBadLevelDirectory;
import CreepTenuous.Directory.check.CheckIsExistsDirectory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/directory")
public class ManagerDirectory implements CheckIsExistsDirectory {
    @Autowired
    private BuilderDirectory builderDirectory;

    @GetMapping("")
    @ResponseStatus(code = HttpStatus.OK)
    public DataManagerDirectory main(
            @RequestParam(value = "level", defaultValue = "1") Integer level,
            @RequestParam(value = "parents", defaultValue = "[]") String[] parents
    ) throws IOException {
        return builderDirectory.build(parents, level);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ExceptionBadLevelDirectory handleExceptionBadLevel(HttpMessageNotReadableException error) {
        return new ExceptionBadLevelDirectory(error.getMessage());
    }
}