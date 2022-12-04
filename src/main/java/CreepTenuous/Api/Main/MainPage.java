package CreepTenuous.Api.Main;

import CreepTenuous.Directory.BuilderDirectory.BuilderDirectory;
import CreepTenuous.Directory.BuilderDirectory.ExceptionBadLevelDirectory;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

@RestController
public class MainPage {
    @GetMapping("/main")
    @ResponseStatus(code = HttpStatus.OK)
    public DataMainPage main(
            @RequestParam(value = "level", defaultValue = "1") Integer level,
            @RequestParam(value = "parents", defaultValue = "[]") String[] parents
    ) {
        if (level != parents.length) {
            throw new HttpMessageNotReadableException("Не верно указан уровень вложенности");
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