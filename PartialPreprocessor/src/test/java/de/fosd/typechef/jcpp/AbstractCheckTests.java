package de.fosd.typechef.jcpp;

import de.fosd.typechef.featureexpr.FeatureExpr;
import de.fosd.typechef.featureexpr.MacroContext$;
import junit.framework.Assert;
import org.anarres.cpp.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AbstractCheckTests {

    private Preprocessor pp;

    public AbstractCheckTests() {
        super();
    }

    protected void testFile(String filename) throws LexerException, IOException {
        testFile(filename, false);
    }

    /**
     * parses a file and checks the result against the results specified in the
     * filename.check file
     *
     * @param filename
     * @throws LexerException
     * @throws IOException
     */
    private void testFile(String filename, boolean debug)
            throws LexerException, IOException {
        String folder = "tc_data/";

        InputStream inputStream = getClass().getResourceAsStream(
                "/" + folder + filename);

        List<Token> output = null;
        String error = null;
        LexerException ex = null;
        try {
            output = parse(new FileLexerSource(inputStream, folder + filename),
                    debug, getClass().getResource("/" + folder).getFile());
        } catch (LexerException e) {
            ex = e;
            error = "ERROR: " + e.toString();
        }
        if (!check(filename, folder, output, error))
            if (ex != null)
                throw ex;

    }

    protected String parseCodeFragment(String code) throws LexerException,
            IOException {
        return serialize(parse(new StringLexerSource(code, true), false, null));
    }

    private boolean check(String filename, String folder,
                          List<Token> tokenStream, String errorMsg)
            throws FileNotFoundException, IOException {
        boolean containsErrorCheck = false;
        InputStream inputStream = getClass().getResourceAsStream(
                "/" + folder + filename + ".check");
        BufferedReader checkFile = new BufferedReader(new InputStreamReader(
                inputStream));
        String line;
        String cleanedOutput = serialize(tokenStream).replace("definedEx(",
                "defined(");
        while ((line = checkFile.readLine()) != null) {
            if (line.startsWith("!")) {
                String substring = line.substring(2);
                if (cleanedOutput.toString().contains(substring)) {
                    System.err.println(cleanedOutput);
                    Assert.fail(substring
                            + " found but not expected in output\n"
                            + cleanedOutput.toString());
                }
            }
            if (line.startsWith("+")) {
                int expected = Integer.parseInt(line.substring(1, 2));
                int found = 0;
                String substring = line.substring(3);

                String content = cleanedOutput.toString();
                int idx = content.indexOf(substring);
                while (idx >= 0) {
                    found++;
                    content = content.substring(idx + substring.length());
                    idx = content.indexOf(substring);
                }

                if (expected != found) {
                    failOutput(cleanedOutput);
                    Assert.fail(substring + " found " + found
                            + " times, but expected " + expected + " times\n"
                            + content);
                }
            }
            if (line.startsWith("*")) {
                String substring = line.substring(2);

                String content = cleanedOutput.toString();
                int idx = content.indexOf(substring);
                if (idx < 0) {
                    failOutput(cleanedOutput);
                    Assert.fail(substring + " not found but expected\n"
                            + content);
                }
            }
            if (line.startsWith("T")) {
                // checks presence condition for token
                // Syntax: T <tokenText> with <presenceCondition>
                String expectedName = line.substring(2);
                String expectedFeature = expectedName.substring(expectedName
                        .indexOf(" with ") + 6);
                expectedName = expectedName.substring(0, expectedName
                        .indexOf(" with "));
                FeatureExpr expectedExpr = parseFeatureExpr(expectedFeature);
                boolean foundToken = false;

                for (Token t : tokenStream) {
                    if (t.getText().equals(expectedName)) {
                        foundToken = true;
                        // expect equivalent presence conditions
                        Assert.assertTrue("found token " + expectedName
                                + " with " + t.getFeature()
                                + " instead of expected " + expectedExpr,
                                FeatureExprLib.l().createEquiv(t.getFeature(),
                                        expectedExpr).isTautology());
                    }
                }
                Assert.assertTrue("token " + expectedName + " not found.",
                        foundToken);
            }
            if (line.trim().equals("error")) {
                containsErrorCheck = true;
                Assert.assertTrue(
                        "Expected error, but preprocessing succeeded",
                        errorMsg != null);
            }
            if (line.trim().equals("print")) {
                System.out.println(cleanedOutput.toString());
            }
            if (line.trim().equals("macrooutput")) {
                pp.debugWriteMacros();
            }
        }
        return containsErrorCheck;
    }

    private FeatureExpr parseFeatureExpr(String expectedFeature) {
        try {
            return new Preprocessor(new StringLexerSource(expectedFeature
                    + "\n")).parse_featureExpr();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LexerException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void failOutput(String output) {
        System.err.println(output);
        if (pp != null)
            pp.debugWriteMacros();
    }

    private List<Token> parse(Source source, boolean debug, String folder)
            throws LexerException, IOException {
        // XXX Why here? And isn't the whole thing duplicated from elsewhere?
        MacroContext$.MODULE$.setPrefixFilter("CONFIG_");

        pp = new Preprocessor();
        pp.addFeature(Feature.DIGRAPHS);
        pp.addFeature(Feature.TRIGRAPHS);
        pp.addFeature(Feature.LINEMARKERS);
        pp.addWarnings(Warning.allWarnings());
        pp.setListener(new PreprocessorListener(pp) {
            @Override
            public void handleWarning(Source source, int line, int column,
                                      String msg) throws LexerException {
                super.handleWarning(source, line, column, msg);
                throw new LexerException(msg + " " + source + ":" + line + ":"
                        + column);
            }
        });
        pp.addMacro("__JCPP__", FeatureExprLib.base());

        // include path
        if (folder != null)
            pp.getSystemIncludePath().add(folder);

        pp.addInput(source);

        List<Token> output = new ArrayList<Token>();
        for (; ;) {
            Token tok = pp.getNextToken();
            if (tok == null)
                break;
            if (tok.getType() == Token.EOF)
                break;

            output.add(tok);
            if (debug)
                System.out.print(tok.getText());
        }
        return output;
    }

    private String serialize(List<Token> tokenstream) {
        StringWriter strWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(strWriter);
        if (tokenstream != null)
            for (Token t : tokenstream)
                t.lazyPrint(writer);
        StringBuffer output = strWriter.getBuffer();
        return output.toString();
    }

}
