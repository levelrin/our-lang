package com.levelrin.ourlang;

import com.levelrin.antlr.generated.OurGrammarBaseListener;
import com.levelrin.antlr.generated.OurGrammarParser;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

public final class OurGrammarListener extends OurGrammarBaseListener {

    private final StringBuilder js;

    private final List<Path> loadedMethods;

    public OurGrammarListener(final StringBuilder js, final List<Path> loadedMethods) {
        this.js = js;
        this.loadedMethods = loadedMethods;
    }

    @Override
    public void enterHeader(final OurGrammarParser.HeaderContext context) {
        if ("native-logic".equals(context.NAME().getText())) {
            String nativeMethodContent = "not initialized yet.";
            if (context.content().string().STRING() != null) {
                nativeMethodContent = context.content().string().STRING().getText().substring(1, context.content().string().STRING().getText().length() - 1);
            } else if (context.content().string().COMPLEX_STRING() != null) {
                nativeMethodContent = context.content().string().COMPLEX_STRING().getText().substring(
                    "-- our-string-start --".length(),
                    context.content().string().COMPLEX_STRING().getText().length() - "-- our-string-end --".length()
                );
            }
            this.js.append(nativeMethodContent);
        }
    }

    @Override
    public void enterMethodCall(final OurGrammarParser.MethodCallContext context) {
        final OurGrammarParser.ValueContext valueContext = context.value();
        if (valueContext.STRING() != null) {
            // A string object is calling a method.
            final URL methodUrl = this.getClass().getClassLoader().getResource("our-lang/sdk/string/" + context.NAME().getText() + ".ours");
            if (methodUrl != null) {
                try {
                    final Path methodPath = Paths.get(Objects.requireNonNull(methodUrl).toURI());
                    if (!loadedMethods.contains(methodPath)) {
                        final String methodContent = Files.readString(methodPath, StandardCharsets.UTF_8);
                        final OurGrammarListener methodListener = new OurGrammarListener(this.js, this.loadedMethods);
                        new OurGrammarWalker(methodContent, methodListener).walk();
                        this.loadedMethods.add(methodPath);
                    }
                } catch (final URISyntaxException ex) {
                    throw new IllegalStateException("Failed to get the path of the method.", ex);
                } catch (final IOException ex) {
                    throw new IllegalStateException("Failed to read the method content.", ex);
                }
            }
            this.js.append(valueContext.STRING().getText()).append('.').append(context.NAME().getText()).append('(');
        }
    }

    @Override
    public void enterParameters(final OurGrammarParser.ParametersContext context) {
        for (int index = 0; index < context.value().size(); index++) {
            final OurGrammarParser.ValueContext valueContext = context.value(index);
            if (valueContext.STRING() != null) {
                this.js.append(valueContext.STRING().getText());
            } else if (valueContext.NAME() != null) {
                this.js.append(valueContext.NAME().getText());
            }
            if (index < context.value().size() - 1) {
                this.js.append(',');
            }
        }
    }

    @Override
    public void exitMethodCall(final OurGrammarParser.MethodCallContext context) {
        this.js.append(");");
    }

}
