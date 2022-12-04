package CreepTenuous.Api.Main;

import CreepTenuous.Files.CreateDirectory.BuilderDirectory;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class MainPage {
    @GetMapping("/main")
    public DataMainPage main(
            @RequestParam(value = "level", defaultValue = "1") Integer level,
            @RequestParam(value = "parents", defaultValue = "[]") String[] parents
    ) {
        BuilderDirectory builderDirectory = new BuilderDirectory(parents);

        return new DataMainPage(
                builderDirectory.getDirectory(),
                level
        );
    }
}