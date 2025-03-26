package com.yaofangwang.ofd2jpg;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.ofdrw.converter.GeneralConvertException;
import org.ofdrw.converter.export.ImageExporter;
import org.ofdrw.converter.utils.CommonUtil;

public class Converter {
    public static void main(String[] args) {
        CommandArgs commandArgs = new CommandArgs(args);
        if (commandArgs.showError) {
            System.exit(1);
        }
        if (commandArgs.showHelp) {
            System.out.println("Usage: ofd2jpg [options] <OFD file>");
            System.out.println("Options:");
            System.out.println("  -h, --help      Show this help message and exit");
            System.out.println("  -v, --version   Show version information and exit");
            System.out.println("  -d, --dpi=DPI   Set the DPI for the output images (default: 300)");
            System.out.println("  -o, --output=DIR   Set the output directory for the converted images (default: current directory)");
            System.exit(0);
        }
        if (commandArgs.showVersion) {
            System.out.println("ofd2jpg 1.0.0");
            System.exit(0);
        }
        convert(commandArgs.ofdFilePath, commandArgs.outDirPath, commandArgs.dpi);
    }

    static void convert(String ofdFilePath, String outDirPath, int dpi) {
        Path ofdPath = Paths.get(ofdFilePath).toAbsolutePath();
        

        // 处理输出目录逻辑
        Path finalOutDir = resolveOutputDirectory(ofdPath, outDirPath);

        // 创建临时目录
        Path tempDir;
        try {
            tempDir = Files.createTempDirectory(UUID.randomUUID().toString());
        } catch (IOException e) {
            throw new GeneralConvertException("can't create tmp dir: " + e.getMessage(), e);
        }

        double ppm = CommonUtil.dpiToPpm(dpi);
        try (ImageExporter exporter = new ImageExporter(ofdPath, tempDir, "JPG", ppm)) {
            exporter.export();

            // 处理生成的文件
            processGeneratedFiles(ofdPath, tempDir, finalOutDir);

            System.out.println("convert success!");
        } catch (GeneralConvertException e) {
            handleError("convert failed: ", e, 2);
        } catch (Exception e) {
            handleError("unknown error: ", e, 3);
        } finally {
            cleanupTempDirectory(tempDir);
        }
    }

    private static Path resolveOutputDirectory(Path ofdPath, String outDirPath) {
        if (outDirPath == null || outDirPath.trim().isEmpty()) {
            return ofdPath.getParent();
        }

        Path specifiedPath = Paths.get(outDirPath).toAbsolutePath();
        if (Files.isDirectory(specifiedPath)) {
            return specifiedPath;
        }

        return ofdPath.getParent();
    }

    private static void processGeneratedFiles(Path ofdPath, Path tempDir, Path finalOutDir) throws GeneralConvertException {
        try (Stream<Path> it = Files.list(tempDir)) {
            List<Path> jpgFiles = it.filter(p -> p.getFileName().toString().endsWith(".jpg"))
                    .sorted(Comparator.comparing(p -> {
                        String name = p.getFileName().toString();
                        return Integer.parseInt(name.substring(0, name.lastIndexOf('.')));
                    }))
                    .toList();

            if (jpgFiles.isEmpty()) {
                throw new GeneralConvertException("no jpg files generated");
            }

            String baseName = ofdPath.getFileName().toString();
            boolean singleFile = jpgFiles.size() == 1;

            for (int i = 0; i < jpgFiles.size(); i++) {
                Path source = jpgFiles.get(i);
                String newName = singleFile ?
                        baseName + ".jpg" :
                        String.format("%s_%d.jpg", baseName, i);

                Files.move(source, finalOutDir.resolve(newName),
                        StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new GeneralConvertException("process file failed: " + e.getMessage(), e);
        }
    }

    private static void cleanupTempDirectory(Path tempDir) {
        if (tempDir == null) return;
        try (Stream<Path> it = Files.walk(tempDir)){
            it.sorted(Comparator.reverseOrder()).forEach(p -> {
                try {
                    Files.deleteIfExists(p);
                } catch (IOException e) {
                    System.err.println("warning: can't delete file: " + e.getMessage());
                }
            });
        } catch (IOException e) {
            System.err.println("warning: can't close temp dir: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void handleError(String message, Exception e, int exitCode) {
        System.err.println(message + e.getMessage());
        e.printStackTrace();
        System.exit(exitCode);
    }
}