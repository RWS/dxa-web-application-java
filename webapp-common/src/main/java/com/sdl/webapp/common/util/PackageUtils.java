package com.sdl.webapp.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

import java.io.IOException;

public class PackageUtils {
    private static final Logger LOG = LoggerFactory.getLogger(PackageUtils.class);

    public static interface ClassCallback {
        void doWith(MetadataReader metadataReader);
    }

    private PackageUtils() {
    }

    public static void doWithClasses(String basePackage, ClassCallback callback) throws IOException {
        // Inspired by Spring's ClassPathScanningCandidateComponentProvider.findCandidateComponents(String basePackage)
        // and Spring's ReflectionUtils

        final String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                ClassUtils.convertClassNameToResourcePath(basePackage) + "/**/*.class";
        LOG.debug("packageSearchPath: {}", packageSearchPath);

        final ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        final MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);

        for (Resource resource : resourcePatternResolver.getResources(packageSearchPath)) {
            if (resource.isReadable()) {
                LOG.trace("Resource: {}", resource);
                callback.doWith(metadataReaderFactory.getMetadataReader(resource));
            } else {
                LOG.debug("Skipping resource because it is not readable: {}", resource);
            }
        }
    }
}
