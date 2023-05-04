package com.zer0s2m.CreepTenuous.services.directory.upload.threads;

import com.zer0s2m.CreepTenuous.providers.build.os.core.Distribution;
import com.zer0s2m.CreepTenuous.services.core.Directory;
import com.zer0s2m.CreepTenuous.services.directory.upload.containers.ContainerDataUploadFile;
import com.zer0s2m.CreepTenuous.utils.UtilsFiles;
import com.zer0s2m.CreepTenuous.utils.TreeNode;

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

    private final List<ContainerDataUploadFile> finalData = new ArrayList<>();

    private final TreeNode<String> map = new TreeNode<>("root");

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
            ContainerDataUploadFile finalData = buildFinalData(
                    newFile,
                    UtilsFiles.getNameFileRawStr(zipEntryDir.getName())
            );
            addFinalData(finalData);
            zipEntryDir = zipInputStream.getNextEntry();
        }
    }

    private File newFile(File destinationDir, ZipEntry zipEntryDir) throws IOException {
        List<String> systemPartPaths = buildHashMapPathFiles(zipEntryDir);
        String newFileName = String.join(File.separator, systemPartPaths);
        if (destinationDir.isDirectory()) {
            newFileName = newFileName + Directory.SEPARATOR.get();
        }

        File destFile = new File(destinationDir, newFileName);

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + newFileName);
        }

        return destFile;
    }

    private ContainerDataUploadFile buildFinalData(File file, String realName) {
        return new ContainerDataUploadFile(
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
        List<String> parts = Arrays.asList(zipEntryDir.getName().split("/"));
        List<String> buildParts = new ArrayList<>();

        parts.forEach((part) -> {
            Optional<TreeNode<String>> node = map.findTreeNode(part);
            if (node.isPresent()) {
                TreeNode<String> readyNode = node.get();
                buildParts.add(readyNode.getMetaValue("systemName"));
            } else {
                TreeNode<String> newNode = map.addChild(part);
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

    private void addFinalData(ContainerDataUploadFile partFinalData) {
        this.finalData.add(partFinalData);
    }

    public List<ContainerDataUploadFile> getFinalData() {
        return this.finalData;
    }
}
