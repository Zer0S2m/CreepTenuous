package CreepTenuous.Api.Directory.DeleteDirectory;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/directory")
public class DeleteDirectory {
    @GetMapping("/delete")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)

    public final void deleteDirectory() {

    }
}
