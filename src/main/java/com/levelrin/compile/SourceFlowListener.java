package com.levelrin.compile;

import com.levelrin.antlr.generated.OurGrammarBaseListener;
import com.levelrin.antlr.generated.OurGrammarParser;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * It parses the source and finds dependencies (other source files that are used by that source) of that source.
 */
public final class SourceFlowListener extends OurGrammarBaseListener {

    /**
     * See {@link SdkObjects#sourceMap()}.
     */
    private final Map<String, Path> sdkObjectsMap;

    /**
     * We will store source paths into this as they are used.
     */
    private final Set<Path> lazyUsedSources = new HashSet<>();

    /**
     * Constructor.
     *
     * @param sdkObjectsMap See {@link SourceFlowListener#sdkObjectsMap}.
     */
    public SourceFlowListener(final Map<String, Path> sdkObjectsMap) {
        this.sdkObjectsMap = sdkObjectsMap;
    }

    @Override
    public void enterObjectFromSdk(final OurGrammarParser.ObjectFromSdkContext context) {
        final String nameText = context.NAME().getText();
        this.lazyUsedSources.add(this.sdkObjectsMap.get(nameText));
    }

    /**
     * As is.
     *
     * @return As is.
     */
    public Set<Path> usedSources() {
        return new HashSet<>(this.lazyUsedSources);
    }

}
