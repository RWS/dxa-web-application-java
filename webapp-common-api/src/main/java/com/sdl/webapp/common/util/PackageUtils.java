package com.sdl.webapp.common.util;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

import java.io.IOException;

/**
 * Utilities for working with Java packages.
 */
public final class PackageUtils {

    private static final String WITH_SUBPACKAGES_PATTERN = "/**/*.class";
    private static final String WITHOUT_SUBPACKAGES_PATTERN = "/*.class";

    /**
     * Callback interface.
     */
    public static interface ClassCallback {

        /**
         * Handle the class that was found.
         *
         * @param metadataReader Metadata reader from which information about the class that was found can be read,
         *                       without the need for the class being loaded by a JVM classloader.
         */
        void doWith(MetadataReader metadataReader);
    }

    private PackageUtils() {
    }

    /**
     * Calls the specified callback for all classes that are found in the specified package or any subpackage of the
     * specified package.
     *
     * @param basePackage The package in which to search for classes.
     * @param callback The callback to be called for each class that is found.
     * @throws IOException When an I/O error occurs.
     */
    public static void doWithClasses(String basePackage, ClassCallback callback) throws IOException {
        doWithClasses(basePackage, true, callback);
    }

    /**
     * Calls the specified callback for all classes that are found in the specified package and optionally all the
     * subpackages of the specified package.
     *
     * @param basePackage The package in which to search for classes.
     * @param includeSubPackages {@code true} if subpackages should be included, {@code false} otherwise.
     * @param callback The callback to be called for each class that is found.
     * @throws IOException When an I/O error occurs.
     */
    public static void doWithClasses(String basePackage, boolean includeSubPackages, ClassCallback callback)
            throws IOException {
        // Inspired by Spring's ClassPathScanningCandidateComponentProvider.findCandidateComponents(String basePackage)
        // and Spring's ReflectionUtils

        final String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                ClassUtils.convertClassNameToResourcePath(basePackage) +
                (includeSubPackages ? WITH_SUBPACKAGES_PATTERN : WITHOUT_SUBPACKAGES_PATTERN);

        final ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        final MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);

        for (Resource resource : resourcePatternResolver.getResources(packageSearchPath)) {
            if (resource.isReadable()) {
                callback.doWith(metadataReaderFactory.getMetadataReader(resource));
            }
        }
    }
}
