package com.levelrin.ourlang;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class Output {

    private final StringBuilder defaultObjects = new StringBuilder();

    private final StringBuilder classDefinitions = new StringBuilder();

    private final StringBuilder methodDefinitions = new StringBuilder();

    private final StringBuilder mainLogic = new StringBuilder();

    private final StringBuilder testLogic = new StringBuilder();

    private final List<Path> loadedClasses = new ArrayList<>();

    private final List<Path> loadedMethods = new ArrayList<>();

    public boolean hasClassLoaded(final Path path) {
        return this.loadedClasses.contains(path);
    }

    public void markLoadedClass(final Path path) {
        this.loadedClasses.add(path);
    }

    public void appendToClassDefinitions(final String text) {
        this.classDefinitions.append(text);
    }

    public boolean hasMethodLoaded(final Path path) {
        return this.loadedMethods.contains(path);
    }

    public void markLoadedMethod(final Path path) {
        this.loadedMethods.add(path);
    }

    public void appendToMethodDefinitions(final String text) {
        this.methodDefinitions.append(text);
    }

    public void appendToDefaultObjects(final String text) {
        this.defaultObjects.append(text);
    }

    public void appendToMainLogic(final String text) {
        this.mainLogic.append(text);
    }

    public void appendToTestLogic(final String text) {
        this.testLogic.append(text);
    }

    @Override
    public String toString() {
        return String.valueOf(this.classDefinitions) +
            this.defaultObjects +
            this.methodDefinitions +
            this.testLogic +
            this.mainLogic;
    }

}
