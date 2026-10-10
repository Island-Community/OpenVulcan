package me.frep.vulcan.spigot.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public final class VulcanConfig extends YamlConfiguration {

    public VulcanConfig() {
        options().parseComments(true);
    }

    public void mergeBundledDefaults(File file, InputStream resource, String[] ignoredSections) {
        VulcanConfig defaults = loadConfiguration(resource);
        if (mergeDefaults(defaults, defaults, ignoredSections) && file != null)
            save(file);
    }

    private boolean mergeDefaults(VulcanConfig defaults, ConfigurationSection section, String[] ignoredSections) {
        boolean changed = false;
        for (String key : section.getKeys(false)) {
            String path = section.getCurrentPath().isEmpty() ? key : section.getCurrentPath() + "." + key;
            if (section.isConfigurationSection(key)) {
                boolean ignored = contains(path) && Arrays.stream(ignoredSections).anyMatch(path::contains);
                if (!ignored)
                    changed = mergeDefaults(defaults, section.getConfigurationSection(key), ignoredSections) || changed;
            } else if (!contains(path)) {
                set(path, section.get(key));
                changed = true;
            }
            if (!getComments(path).equals(defaults.getComments(path))) {
                setComments(path, defaults.getComments(path));
                changed = true;
            }
            if (!getInlineComments(path).equals(defaults.getInlineComments(path))) {
                setInlineComments(path, defaults.getInlineComments(path));
                changed = true;
            }
        }
        return changed;
    }

    @Override
    public void save(File file) {
        try {
            super.save(file);
        } catch (IOException exception) {
            throw new UncheckedIOException("Cannot save configuration " + file, exception);
        }
    }

    @Override
    public void load(File file) {
        try {
            super.load(file);
        } catch (IOException exception) {
            throw new UncheckedIOException("Cannot read configuration " + file, exception);
        } catch (InvalidConfigurationException exception) {
            throw new IllegalArgumentException("Invalid YAML in " + file, exception);
        }
    }

    public static VulcanConfig loadConfiguration(File file) {
        VulcanConfig configuration = new VulcanConfig();
        configuration.load(file);
        return configuration;
    }

    public static VulcanConfig loadConfiguration(InputStream resource) {
        return loadConfiguration(new InputStreamReader(Objects.requireNonNull(resource, "Missing bundled configuration"), StandardCharsets.UTF_8));
    }

    public static VulcanConfig loadConfiguration(Reader reader) {
        try (Reader input = reader) {
            StringBuilder contents = new StringBuilder();
            char[] buffer = new char[4096];
            int length;
            while ((length = input.read(buffer)) != -1) contents.append(buffer, 0, length);
            VulcanConfig configuration = new VulcanConfig();
            configuration.loadFromString(contents.toString());
            return configuration;
        } catch (IOException exception) {
            throw new UncheckedIOException("Cannot read bundled configuration", exception);
        } catch (InvalidConfigurationException exception) {
            throw new IllegalArgumentException("Invalid bundled YAML", exception);
        }
    }
}
