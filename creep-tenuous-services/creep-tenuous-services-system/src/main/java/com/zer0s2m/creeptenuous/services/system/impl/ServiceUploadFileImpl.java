package com.zer0s2m.creeptenuous.services.system.impl;

import com.zer0s2m.creeptenuous.common.annotations.ServiceFileSystem;
import com.zer0s2m.creeptenuous.common.containers.ContainerDataUploadFileFragment;
import com.zer0s2m.creeptenuous.common.http.ResponseObjectUploadFileApi;
import com.zer0s2m.creeptenuous.core.atomic.AtomicFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.AtomicFileSystemExceptionHandler;
import com.zer0s2m.creeptenuous.core.atomic.CoreServiceFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.ContextAtomicFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.FilesContextAtomic;
import com.zer0s2m.creeptenuous.core.atomic.ServiceFileSystemExceptionHandlerOperationUpload;
import com.zer0s2m.creeptenuous.core.atomic.AtomicServiceFileSystem;
import com.zer0s2m.creeptenuous.core.balancer.FileIsDirectoryException;
import com.zer0s2m.creeptenuous.services.system.ServiceUploadFile;
import com.zer0s2m.creeptenuous.services.system.core.ServiceBuildDirectoryPath;
import com.zer0s2m.creeptenuous.services.system.utils.UtilsFiles;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A service for working with downloading files, as well as their fragmentation and working
 * in atomic mode {@link AtomicServiceFileSystem}.
 */
@ServiceFileSystem("service-upload-file")
@CoreServiceFileSystem(method = "upload")
public class ServiceUploadFileImpl implements ServiceUploadFile {

    private final Logger logger = LogManager.getLogger(ServiceUploadFileImpl.class);

    private final ServiceBuildDirectoryPath buildDirectoryPath = new ServiceBuildDirectoryPath();

    /**
     * Upload files.
     * @param files Files.
     * @param systemParents System names of directories from which the directory path will be collected
     *                      where the file will be downloaded.
     * @return Info is upload and info system file object.
     * @throws IOException If an I/O error occurs or the parent directory does not exist.
     */
    @Override
    @AtomicFileSystem(
            name = "upload-file",
            handlers = {
                    @AtomicFileSystemExceptionHandler(
                            handler = ServiceFileSystemExceptionHandlerOperationUpload.class,
                            isExceptionMulti = true,
                            operation = ContextAtomicFileSystem.Operations.UPLOAD
                    )
            }
    )
    public List<ResponseObjectUploadFileApi> upload(@NotNull Map<Path, String> files, List<String> systemParents)
            throws IOException {
        Path sourceDir = Paths.get(buildDirectoryPath.build(systemParents));
        return files
                .entrySet()
                .stream()
                .map((file) -> {
                    try {
                        ResponseObjectUploadFileApi data = copyFileAndPushContext(
                                new FileInputStream(file.getKey().toFile()),
                                file.getValue(), sourceDir, false);

                        writeLogUploadFile(data);

                        return data;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * Upload files and fragment them into parts.
     * @param inputStreams File streams.
     * @param systemParents System names of directories from which the directory path will be collected
     *                      where the file will be downloaded.
     * @param originFileNames Original file names.
     * @return Execution result.
     * @throws IOException If an I/O error occurs or the parent directory does not exist.
     */
    @Override
    public List<ContainerDataUploadFileFragment> uploadFragment(
            @NotNull List<InputStream> inputStreams, List<String> originFileNames, List<String> systemParents)
            throws IOException {
        Path sourceDir = Paths.get(buildDirectoryPath.build(systemParents));
        List<ContainerDataUploadFileFragment> dataUploadFileFragments = new ArrayList<>();

        IntStream.range(0, inputStreams.size())
                .forEach(idx -> {
                    final String originalName = originFileNames.get(idx);

                    ResponseObjectUploadFileApi uploadFileApi = copyFileAndPushContext(
                            inputStreams.get(idx), originalName, sourceDir, true);

                    writeLogUploadFile(uploadFileApi);

                    try {
                        Collection<Path> fragmentedPartsFile = FilesContextAtomic.fragment(uploadFileApi.systemPath());

                        dataUploadFileFragments.add(new ContainerDataUploadFileFragment(
                                originalName,
                                uploadFileApi.systemFileName(),
                                fragmentedPartsFile,
                                uploadFileApi.systemPath()));
                    } catch (IOException | FileIsDirectoryException e) {
                        throw new RuntimeException(e);
                    }
                });

        return dataUploadFileFragments;
    }

    /**
     * Copy a file with a new system name and write it to the context for atomic mode.
     * @param inputStream Stream file.
     * @param originFileName Original file name.
     * @param sourceDir Directory path where the file will be copied.
     * @return Execution result.
     */
    @Contract("_, _, _, _ -> new")
    private @NotNull ResponseObjectUploadFileApi copyFileAndPushContext(
            final InputStream inputStream, final String originFileName, final @NotNull Path sourceDir,
            final boolean isFragment) {
        String newFileName = UtilsFiles.getNewFileName(originFileName);
        Path targetLocation = sourceDir.resolve(originFileName);
        Path newTargetLocation = sourceDir.resolve(newFileName);

        try {
            if (!isFragment) {
                FilesContextAtomic.copy(
                        inputStream,
                        newTargetLocation,
                        StandardCopyOption.REPLACE_EXISTING
                );
            } else {
                Files.copy(inputStream, newTargetLocation);
            }
        } catch (IOException e) {
            logger.error(e);
            return new ResponseObjectUploadFileApi(
                    originFileName,
                    newFileName,
                    false,
                    targetLocation,
                    newTargetLocation
            );
        }
        return new ResponseObjectUploadFileApi(
                originFileName,
                newFileName,
                true,
                targetLocation,
                newTargetLocation
        );
    }

    /**
     * Log file uploaded data.
     * @param data Data.
     */
    private void writeLogUploadFile(final @NotNull ResponseObjectUploadFileApi data) {
        logger.info("Upload file: path ["
                + data.systemPath() + "] real name [" + data.realFileName() + "]");
    }

}
