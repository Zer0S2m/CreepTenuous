package com.zer0s2m.creeptenuous.services.system.utils;

import com.zer0s2m.creeptenuous.common.containers.ContainerDataUploadFileSystemObject;
import com.zer0s2m.creeptenuous.common.enums.Directory;
import com.zer0s2m.creeptenuous.common.utils.TreeNode;
import com.zer0s2m.creeptenuous.core.context.ContextAtomicFileSystem;
import com.zer0s2m.creeptenuous.core.services.Distribution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ThreadUnpackingDirectory extends Thread {

    private final Logger logger = LogManager.getLogger(ThreadUnpackingDirectory.class);

    private final List<Path> files;

    private final Path outputDirectory;

    private final byte[] buffer = new byte[(int) Directory.BYTES_UNPACKING_ZIP.getInt()];

    private final List<ContainerDataUploadFileSystemObject> finalData = new ArrayList<>();

    private final TreeNode<String> map = new TreeNode<>();

    private String callClassName;

    /**
     * Context for working with the file system in <b>atomic mode</b>
     */
    ContextAtomicFileSystem contextAtomicFileSystem = ContextAtomicFileSystem.getInstance();

    public ThreadUnpackingDirectory(String name, List<Path> files, Path outputDirectory) {
        this.setName(name);
        this.files = files;
        this.outputDirectory = outputDirectory;
    }

    @Override
    public void run() {
        for (Path file : files) {
            File outputDirectoryFile = new File(outputDirectory.toString());
            File realFile = file.toFile();
            try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(realFile))) {
                unpacking(zipInputStream, outputDirectoryFile);
                logger.info(String.format("Is part file(s) unpacking: %s", realFile.delete()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Unpacking archive zip
     * @param zipInputStream stream
     * @param outputDirectoryFile output
     * @throws IOException system error
     */
    private void unpacking(
            ZipInputStream zipInputStream,
            File outputDirectoryFile
    ) throws IOException {
        ZipEntry zipEntryDir = zipInputStream.getNextEntry();
        while (zipEntryDir != null) {
            File newFile = newFile(outputDirectoryFile, zipEntryDir);
            if (zipEntryDir.isDirectory()) {
                if (!newFile.isDirectory() && !newFile.mkdirs()) {
                    throw new IOException("Failed to create directory " + newFile);
                }
            } else {
                File parent = newFile.getParentFile();
                if (!parent.isDirectory() && !parent.mkdirs()) {
                    throw new IOException("Failed to create directory " + parent);
                }

                try (FileOutputStream fos = new FileOutputStream(newFile)) {
                    int len;
                    while ((len = zipInputStream.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                }
            }
            ContainerDataUploadFileSystemObject finalData = buildFinalData(
                    newFile,
                    UtilsFiles.getNameFileRawStr(zipEntryDir.getName())
            );
            addFinalData(finalData);
            zipEntryDir = zipInputStream.getNextEntry();
        }
    }

    /**
     * Get file from zip archive
     * @param destinationDir zip archive
     * @param zipEntryDir object from zip archive
     * @return ready file for file system
     * @throws IOException system error
     */
    private File newFile(File destinationDir, ZipEntry zipEntryDir) throws IOException {
        List<String> systemPartPaths = buildHashMapPathFiles(zipEntryDir);
        String newFileName = String.join(File.separator, systemPartPaths);
        if (zipEntryDir.isDirectory()) {
            newFileName += Directory.SEPARATOR.get();
        }

        File destFile = new File(destinationDir, newFileName);

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + newFileName);
        }

        addOperationDataUpload(destFile);

        return destFile;
    }

    /**
     * Write operation data to atomic mode context
     * @param file dest file
     */
    private void addOperationDataUpload(File file) {
        HashMap<String, Object> operationData = new HashMap<>();

        Path target = file.toPath();
        String systemNameFile = target.getFileName().toString();

        operationData.put("_class", getCallClassName());
        operationData.put("operation", ContextAtomicFileSystem.Operations.UPLOAD);
        operationData.put("targetPath", target);

        contextAtomicFileSystem.addOperationData(systemNameFile, operationData);
    }

    /**
     * Get data after create file system object
     * @param file output file
     * @param realName real name file system object
     * @return info file system object
     */
    private ContainerDataUploadFileSystemObject buildFinalData(File file, String realName) {
        return new ContainerDataUploadFileSystemObject(
                realName,
                file.getName(),
                Path.of(file.getPath()),
                file.isFile(),
                file.isDirectory()
        );
    }

    /**
     * Building hash map for <b>redis</b> and system paths
     * @param zipEntryDir object in <b>zip</b>
     * @return prepared parts of the <b>system path</b>
     */
    private List<String> buildHashMapPathFiles(ZipEntry zipEntryDir) {
        List<String> parts = Arrays.asList(zipEntryDir.getName().split(Directory.SEPARATOR.get()));
        List<String> buildParts = new ArrayList<>();

        final Optional<TreeNode<String>>[] previewNode = new Optional[]{Optional.of(this.map)};

        parts.forEach((part) -> {
            Optional<TreeNode<String>> node = previewNode[0].get().findChildren(part);

            if (node.isPresent()) {
                TreeNode<String> readyNode = node.get();
                buildParts.add(readyNode.getMetaValue("systemName"));
                previewNode[0] = previewNode[0].get().findChildren(part);
            } else {
                TreeNode<String> newNode = previewNode[0].get().addChild(part);
                String systemName;
                if (zipEntryDir.isDirectory()) {
                    systemName = Distribution.getUUID();
                } else {
                    systemName = UtilsFiles.getNewFileName(parts.get(parts.size() - 1));
                }
                newNode.putMeta("systemName", systemName);
                buildParts.add(systemName);
            }
        });

        return buildParts;
    }

    /**
     * Add final data
     * @param partFinalData data info file system object
     */
    private void addFinalData(ContainerDataUploadFileSystemObject partFinalData) {
        this.finalData.add(partFinalData);
    }

    public List<ContainerDataUploadFileSystemObject> getFinalData() {
        return this.finalData;
    }

    /**
     * Set caller class from method
     * @param callClassName caller class from method
     */
    public void setCallClassName(String callClassName) {
        this.callClassName = callClassName;
    }

    /**
     * Get caller class from method
     * @return caller class from method
     */
    public String getCallClassName() {
        return callClassName;
    }

}