package com.capgemini.cobigen.impl.util;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import com.capgemini.cobigen.api.exception.CobiGenRuntimeException;

/**
 *
 */
public class CobiGenPropertiesUtil {

    /** Filename of the {@link Properties} used to customize cobigen properties and template relocation. */
    public static final String COBIGEN_PROPERTIES = "cobigen.properties";

    /** The {@link Charset} used to read {@link #COBIGEN_PROPERTIES}. */
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    /**
     * @param folder
     *            the {@link Path} pointing to the folder that may contain a {@code cobigen.properties} file.
     * @return the new {@link Properties} containing the properties from a potential
     *         {@code cobigen.properties}. Will be empty if no such properties file exists.
     */
    public static final Properties load(Path folder) {

        return load(folder, null);
    }

    /**
     * @param folder
     *            the {@link Path} pointing to the folder that may contain a {@code cobigen.properties} file.
     * @param parent
     *            the parent {@link Properties} to inherit from and override with potentially read properties.
     * @return the new {@link Properties} containing the properties from a potential
     *         {@code cobigen.properties} merged with the given {@code parent} properties.
     */
    public static final Properties load(Path folder, Properties parent) {

        Path propertiesPath = folder.resolve(COBIGEN_PROPERTIES);
        if (!Files.exists(propertiesPath)) {
            if (parent == null) {
                return new Properties();
            }
            return parent;
        }

        Properties properties = new Properties();
        if (parent != null) {
            properties.putAll(parent);
        }
        try (Reader reader = Files.newBufferedReader(propertiesPath, UTF_8)) {
            properties.load(reader);
        } catch (IOException e) {
            throw new CobiGenRuntimeException("Failed to read " + COBIGEN_PROPERTIES + " from " + folder, e);
        }
        return properties;
    }

}
