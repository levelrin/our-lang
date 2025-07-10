package com.levelrin.compile;

import com.levelrin.antlr.generated.OurGrammarBaseListener;
import com.levelrin.antlr.generated.OurGrammarParser;
import java.util.List;
import org.antlr.v4.runtime.misc.ParseCancellationException;

/**
 * It's for parsing `settings.ours` file.
 */
public final class SettingsListener extends OurGrammarBaseListener {

    /**
     * It will be the value of the `package-name` in the metadata.
     */
    private String lazyPackageName;

    @Override
    public void enterMetadataBody(final OurGrammarParser.MetadataBodyContext context) {
        final OurGrammarParser.PairsContext pairsContext = context.pairs();
        final List<OurGrammarParser.PairContext> pairContexts = pairsContext.pair();
        for (final OurGrammarParser.PairContext pairContext : pairContexts) {
            final String name = pairContext.NAME().getText();
            final OurGrammarParser.ValueContext valueContext = pairContext.value();
            if ("package-name".equals(name)) {
                this.lazyPackageName = valueContext.getText();
                throw new ParseCancellationException("Found the `package-name` in the metadata. Cancel the parse flow.");
            }
        }
    }

    /**
     * Please make sure you start the listener before using this method.
     *
     * @return As is.
     */
    public String packageName() {
        return this.lazyPackageName;
    }

}
