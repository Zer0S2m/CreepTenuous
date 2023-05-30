package com.zer0s2m.creeptenuous.api.controllers.system;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiCopyFileDoc;
import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.containers.ContainerDataCopyFile;
import com.zer0s2m.creeptenuous.common.data.DataCopyFileApi;
import com.zer0s2m.creeptenuous.common.http.ResponseCopyFileApi;
import com.zer0s2m.creeptenuous.common.utils.CloneList;
import com.zer0s2m.creeptenuous.core.handlers.AtomicSystemCallManager;
import com.zer0s2m.creeptenuous.services.redis.system.ServiceCopyFileRedisImpl;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceCopyFileImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@V1APIRestController
public class ControllerApiCopyFile implements ControllerApiCopyFileDoc {
    private final ServiceCopyFileImpl serviceCopyFile;

    private final ServiceCopyFileRedisImpl serviceCopyFileRedis;

    @Autowired
    public ControllerApiCopyFile(
            ServiceCopyFileImpl serviceCopyFile,
            ServiceCopyFileRedisImpl serviceCopyFileRedis
    ) {
        this.serviceCopyFile = serviceCopyFile;
        this.serviceCopyFileRedis = serviceCopyFileRedis;
    }

    /**
     * Copy file(s)
     * <p>Called method via {@link AtomicSystemCallManager} - {@link ServiceCopyFileImpl#copy(String, List, List)}
     * or {@link ServiceCopyFileImpl#copy(List, List, List)}</p>
     * @param file copy data file
     * @param accessToken raw JWT access token
     * @return result copy file(s)
     * @throws IOException if an I/O error occurs or the parent directory does not exist
     * @throws InvocationTargetException Exception thrown by an invoked method or constructor.
     * @throws NoSuchMethodException Thrown when a particular method cannot be found.
     * @throws InstantiationException Thrown when an application tries to create an instance of a class
     * using the newInstance method in class {@code Class}.
     * @throws IllegalAccessException An IllegalAccessException is thrown when an application
     * tries to reflectively create an instance
     */
    @Override
    @PostMapping("/file/copy")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseCopyFileApi copy(
            final @Valid @RequestBody DataCopyFileApi file,
            @RequestHeader(name = "Authorization") String accessToken
    ) throws IOException, InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException {
        serviceCopyFileRedis.setAccessToken(accessToken);
        List<String> mergeRealAndSystemParents = CloneList.cloneOneLevel(
                file.systemParents(),
                file.systemToParents()
        );
        serviceCopyFileRedis.checkRights(file.parents(), mergeRealAndSystemParents, null);
        if (file.systemFileName() != null) {
            serviceCopyFileRedis.checkRights(file.systemFileName());
            ContainerDataCopyFile containerData = AtomicSystemCallManager.call(
                    serviceCopyFile,
                    file.systemFileName(),
                    file.systemParents(),
                    file.systemToParents()
            );
            serviceCopyFileRedis.copy(containerData.target(), file.systemFileName(), containerData.systemFileName());
            return new ResponseCopyFileApi(List.of(containerData));
        } else {
            serviceCopyFileRedis.checkRights(file.systemNameFiles());
            List<ContainerDataCopyFile> containersData = AtomicSystemCallManager.call(
                    serviceCopyFile,
                    Objects.requireNonNull(file.systemNameFiles()),
                    file.systemParents(),
                    file.systemToParents()
            );
            serviceCopyFileRedis.copy(
                    containersData.stream().map(ContainerDataCopyFile::target).collect(Collectors.toList()),
                    Objects.requireNonNull(file.systemNameFiles()),
                    containersData.stream().map(ContainerDataCopyFile::systemFileName).collect(Collectors.toList())
            );
            return new ResponseCopyFileApi(containersData);
        }
    }
}
