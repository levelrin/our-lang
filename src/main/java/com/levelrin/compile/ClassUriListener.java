package com.levelrin.compile;

import com.levelrin.antlr.generated.OurGrammarBaseListener;
import com.levelrin.antlr.generated.OurGrammarParser;
import java.util.List;
import org.antlr.v4.runtime.misc.ParseCancellationException;

/**
 * It's for parsing the `class-uri` in the metadata.
 * It will throw the {@link ParseCancellationException} for optimization.
 * So, please catch that exception.
 */
public final class ClassUriListener extends OurGrammarBaseListener {

    /**
     * It will be the value of the `class-uri` in the metadata.
     */
    private String lazyClassUri;

    @Override
    public void enterMetadataBody(final OurGrammarParser.MetadataBodyContext context) {
        final OurGrammarParser.PairsContext pairsContext = context.pairs();
        final List<OurGrammarParser.PairContext> pairContexts = pairsContext.pair();
        for (final OurGrammarParser.PairContext pairContext : pairContexts) {
            final String name = pairContext.NAME().getText();
            final OurGrammarParser.ValueContext valueContext = pairContext.value();
            if ("class-uri".equals(name)) {
                this.lazyClassUri = valueContext.getText();
                throw new ParseCancellationException("Found the `class-uri` in the metadata. Cancel the parse flow.");
            }
        }
    }

    /**
     * Please make sure you start the listener before using this method.
     *
     * @return As is.
     */
    public OurString classUriInOurString() {
        return new OurString(this.lazyClassUri);
    }

}
