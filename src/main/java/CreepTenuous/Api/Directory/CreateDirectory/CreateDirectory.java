package CreepTenuous.Api.Directory.CreateDirectory;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/directory")
public class CreateDirectory {
    @GetMapping("/create")
    @ResponseStatus(code = HttpStatus.CREATED)

    public final void deleteDirectory() {

    }
}
