package com.levelrin.compile;

import java.nio.file.Path;
import java.util.Map;

/**
 * It represents the `.ours` source files.
 */
public interface Sources {

    /**
     * Read all `.ours` files, store them into a map, and return it.
     *
     * @return Key - the path to the source file.
     *         Value - the content of the file.
     */
    Map<Path, String> sourceMap();

}
