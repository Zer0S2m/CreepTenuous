package CreepTenuous.Api.Directory.ManagerDirectory;

import CreepTenuous.Api.Directory.ManagerDirectory.data.DataMainPage;
import CreepTenuous.Directory.BuilderDirectory.services.BuilderDirectory;
import CreepTenuous.Directory.BuilderDirectory.ExceptionBadLevelDirectory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/directory")
public class ManagerDirectory {
    @Autowired
    private BuilderDirectory builderDirectory;

    @GetMapping("")
    @ResponseStatus(code = HttpStatus.OK)
    public DataMainPage main(
            @RequestParam(value = "level", defaultValue = "1") Integer level,
            @RequestParam(value = "parents", defaultValue = "[]") String[] parents
    ) {
        return builderDirectory.build(parents, level);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ExceptionBadLevelDirectory handleException(HttpMessageNotReadableException error) {
        return new ExceptionBadLevelDirectory(error.getMessage());
    }
}