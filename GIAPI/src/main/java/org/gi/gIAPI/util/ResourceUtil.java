package org.gi.gIAPI.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * JAR/클래스패스 리소스를 로컬 디렉토리로 배포하는 유틸리티.
 * - 단일 파일/폴더 전체 복사
 * - JAR 내부 폴더도 리스트업 가능 (jar:file:... FileSystem 활용)
 */
public final class ResourceUtil {

    public enum OverwritePolicy {
        ONLY_IF_MISSING, OVERWRITE
    }

    public static Builder of(Class<?> resourceBaseClass) {
        return new Builder(resourceBaseClass);
    }

    public static Builder of(ClassLoader classLoader) {
        return new Builder(classLoader);
    }

    public static final class Builder {
        private final Class<?> baseClass;
        private final ClassLoader classLoader;
        private String source;                      // "/defaults" 또는 "/config.yml"
        private Path targetDir;                     // 대상 디렉토리
        private OverwritePolicy overwrite = OverwritePolicy.ONLY_IF_MISSING;
        private Predicate<String> exclude = s -> false; // 내부 경로(리소스 상대경로) 기준

        private Builder(Class<?> baseClass) {
            this.baseClass = Objects.requireNonNull(baseClass);
            this.classLoader = null;
        }

        private Builder(ClassLoader classLoader) {
            this.baseClass = null;
            this.classLoader = Objects.requireNonNull(classLoader);
        }

        public Builder from(String classpathPath) {
            // 항상 슬래시로 시작하도록 정규화
            if (!classpathPath.startsWith("/")) classpathPath = "/" + classpathPath;
            this.source = classpathPath;
            return this;
        }

        public Builder to(Path targetDirectory) {
            this.targetDir = Objects.requireNonNull(targetDirectory);
            return this;
        }

        /** 없을 때만 복사 (기본값) */
        public Builder onlyIfMissing() {
            this.overwrite = OverwritePolicy.ONLY_IF_MISSING;
            return this;
        }

        /** 항상 덮어쓰기 */
        public Builder overwriteExisting() {
            this.overwrite = OverwritePolicy.OVERWRITE;
            return this;
        }

        /** 특정 리소스 제외 (상대 경로 기준, 예: p -> p.endsWith(".example.yml")) */
        public Builder exclude(Predicate<String> predicate) {
            this.exclude = Objects.requireNonNull(predicate);
            return this;
        }

        /** 단일 파일 배포 (source가 파일 경로여야 함) */
        public void deploy() throws IOException {
            ensureConfigured(true);
            Files.createDirectories(targetDir);
            String fileName = Paths.get(source).getFileName().toString();
            Path target = targetDir.resolve(fileName);
            copyOne(source, target, overwrite);
        }

        /** 폴더 전체 배포 (source가 폴더 경로여야 함) */
        public void deployAll() throws IOException {
            ensureConfigured(false);
            Files.createDirectories(targetDir);

            List<String> resourcePaths = listResources(source);
            // 자기자신(폴더 루트)는 제외하고, 디렉토리 엔트리 제외
            List<String> files = resourcePaths.stream()
                    .filter(p -> !p.endsWith("/"))
                    .filter(p -> !exclude.test(relativize(source, p)))
                    .collect(Collectors.toList());

            for (String fullPath : files) {
                String rel = relativize(source, fullPath);         // "a/b.yml"
                Path out = targetDir.resolve(rel);
                Files.createDirectories(out.getParent());
                copyOne(fullPath, out, overwrite);
            }
        }

        // ------- 내부 구현 -------

        private void ensureConfigured(boolean singleFile) {
            if (source == null) throw new IllegalStateException("from(\"/path\")를 먼저 호출하세요.");
            if (targetDir == null) throw new IllegalStateException("to(Path)를 먼저 호출하세요.");
            if (singleFile && source.endsWith("/")) {
                throw new IllegalStateException("deploy()는 파일 경로만 지원합니다. 폴더는 deployAll()을 사용하세요.");
            }
            if (!singleFile && !source.endsWith("/")) {
                // 폴더로 강제 정규화 ("/defaults" → "/defaults/")
                source = source + "/";
            }
        }

        /** classpath 상의 경로 전체를 나열 (JAR/파일 시스템 모두 지원) */
        private List<String> listResources(String folderPath) throws IOException {
            // 리소스 URL을 얻고, URI 스킴에 따라 분기
            URI uri = locateURI(folderPath).orElseThrow(() ->
                    new IllegalArgumentException("폴더 리소스를 찾을 수 없습니다: " + folderPath));

            if ("jar".equalsIgnoreCase(uri.getScheme())) {
                // jar:file:/...!/<path> 형태 → 해당 JAR FileSystem 열고 walk
                return listFromJarFS(uri, folderPath);
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                // 개발 환경(exploded classpath)
                return listFromFileFS(uri, folderPath);
            } else {
                // 일부 환경에서 "jrt" 등 특수 스킴은 지원하지 않을 수 있음
                throw new UnsupportedOperationException("지원하지 않는 URI 스킴: " + uri.getScheme());
            }
        }

        private Optional<URI> locateURI(String path) {
            try {
                if (baseClass != null) {
                    var url = baseClass.getResource(path);
                    return Optional.ofNullable(url).map(u -> {
                        try { return u.toURI(); } catch (URISyntaxException e) { return null; }
                    });
                } else {
                    var url = classLoader.getResource(path.startsWith("/") ? path.substring(1) : path);
                    return Optional.ofNullable(url).map(u -> {
                        try { return u.toURI(); } catch (URISyntaxException e) { return null; }
                    });
                }
            } catch (Exception e) {
                return Optional.empty();
            }
        }

        private List<String> listFromFileFS(URI uri, String folderPath) throws IOException {
            Path root = Paths.get(uri);
            List<String> results = new ArrayList<>();
            try (Stream<Path> walk = Files.walk(root)) {
                for (Path p : walk.collect(Collectors.toList())) {
                    if (Files.isDirectory(p)) continue;
                    Path rel = root.relativize(p);
                    // classpath 절대경로로 복원
                    String full = normalize(folderPath + rel.toString().replace('\\', '/'));
                    results.add(full);
                }
            }
            return results;
        }

        private List<String> listFromJarFS(URI uri, String folderPath) throws IOException {
            // 예: jar:file:/.../app.jar!/defaults/
            // URI에서 "!" 앞의 JAR 파일과 내부 경로를 분리하여 FileSystem으로 접근
            String s = uri.toString();
            int sep = s.indexOf("!/");
            if (sep < 0) throw new IOException("잘못된 JAR URI: " + s);

            URI jarUri = URI.create(s.substring(0, sep));
            String insidePath = s.substring(sep + 1); // "/defaults/"

            try (FileSystem fs = getOrCreateFileSystem(jarUri)) {
                Path root = fs.getPath(insidePath);
                if (!Files.exists(root)) return List.of();
                List<String> results = new ArrayList<>();
                try (Stream<Path> walk = Files.walk(root)) {
                    for (Path p : walk.collect(Collectors.toList())) {
                        if (Files.isDirectory(p)) continue;
                        Path rel = root.relativize(p);
                        String full = normalize(folderPath + rel.toString().replace('\\', '/'));
                        results.add(full);
                    }
                }
                return results;
            }
        }

        private static FileSystem getOrCreateFileSystem(URI jarUri) throws IOException {
            try {
                return FileSystems.getFileSystem(jarUri);
            } catch (FileSystemNotFoundException e) {
                return FileSystems.newFileSystem(jarUri, java.util.Map.of());
            }
        }

        private void copyOne(String resourcePath, Path target, OverwritePolicy policy) throws IOException {
            if (policy == OverwritePolicy.ONLY_IF_MISSING && Files.exists(target)) {
                return;
            }
            try (InputStream in = open(resourcePath)) {
                if (in == null) {
                    throw new IOException("리소스를 찾을 수 없습니다: " + resourcePath);
                }
                // 덮어쓰기 정책 반영
                CopyOption[] options = (policy == OverwritePolicy.OVERWRITE)
                        ? new CopyOption[]{StandardCopyOption.REPLACE_EXISTING}
                        : new CopyOption[]{};
                Files.copy(in, target, options);
            }
        }

        private InputStream open(String resourcePath) {
            if (baseClass != null) {
                return baseClass.getResourceAsStream(resourcePath);
            } else {
                String p = resourcePath.startsWith("/") ? resourcePath.substring(1) : resourcePath;
                return classLoader.getResourceAsStream(p);
            }
        }

        private static String relativize(String root, String full) {
            // root="/defaults/", full="/defaults/a/b.yml" → "a/b.yml"
            String normRoot = normalize(root);
            String normFull = normalize(full);
            if (normFull.startsWith(normRoot)) {
                return normFull.substring(normRoot.length());
            }
            return normFull;
        }

        private static String normalize(String p) {
            // 슬래시 정규화
            return p.replace("\\", "/");
        }
    }
}
