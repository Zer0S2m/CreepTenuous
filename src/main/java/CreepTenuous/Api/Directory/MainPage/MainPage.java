package CreepTenuous.Api.Directory.MainPage;

import CreepTenuous.Api.Directory.MainPage.data.DataMainPage;
import CreepTenuous.Api.enums.EDirectory;
import CreepTenuous.Directory.BuilderDirectory.BuilderDirectory;
import CreepTenuous.Directory.BuilderDirectory.ExceptionBadLevelDirectory;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/directory")
public class MainPage {
    @GetMapping("")
    @ResponseStatus(code = HttpStatus.OK)
    public DataMainPage main(
            @RequestParam(value = "level", defaultValue = "1") Integer level,
            @RequestParam(value = "parents", defaultValue = "[]") String[] parents
    ) {
        if (level != parents.length) {
            throw new HttpMessageNotReadableException(EDirectory.NOT_VALID_LEVEL.get());
        }
        BuilderDirectory builderDirectory = new BuilderDirectory(parents);

        return new DataMainPage(
                builderDirectory.getArrPartsDirectory(),
                builderDirectory.getDirectory(),
                level
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ExceptionBadLevelDirectory handleException(HttpMessageNotReadableException error) {
        return new ExceptionBadLevelDirectory(error.getMessage());
    }
}