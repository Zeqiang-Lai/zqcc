package main;

import ast.StmtNode;
import ast.XMLPrinter;
import error.ErrorCollector;
import lexer.Lexer;
import lexer.SourceBuffer;
import lexer.Token;
import lexer.TokenType;
import parser.Parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Vector;

public class ParserRunner {

    String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    Vector<Token> readTokensFromXML(String path) {
        Vector<Token> tokens = new Vector<>();

        String xml = null;
        try {
            xml = readFile(path, Charset.forName("utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] lines = xml.split("\n");
        for(int i=1; i<lines.length-1; i+=7) {
            tokens.add(Token.fromXML(Arrays.copyOfRange(lines, i, i+6)));
        }

        return tokens;
    }

    void run(String source_path, String out, boolean use_xml) {
        File f = new File(source_path);
        String file_name = f.getName();
        if(out == null) out = file_name;

        String source = null;
        try {
            source = readFile(source_path, Charset.forName("utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        ErrorCollector errorCollector = ErrorCollector.getInstance();
        errorCollector.setFile_name(file_name);

        Vector<Token> tokens = new Vector<>();
        if(use_xml) {
            tokens = readTokensFromXML(source_path);
        } else {
            SourceBuffer buff = new SourceBuffer(source);
            Lexer lexer = new Lexer(buff);
            Token token = lexer.scan();
            while (token.type != TokenType.EOF) {
                tokens.add(token);
                token = lexer.scan();
            }
        }

        Parser parser = new Parser(tokens);
        StmtNode.CompilationUnit tree = parser.parse();

        if(errorCollector.hasError()) {
            errorCollector.show();
            return;
        }

        System.out.println(file_name + " is successfully parsed!");
        XMLPrinter printer = new XMLPrinter();
        String xml = printer.print(tree);

        PrintWriter writer;
        try {
            writer = new PrintWriter(source_path+"_tree.xml");
            writer.println(xml);
            writer.close();
            System.out.println("AST in XML format is saved at " + source_path + "_tree.xml.");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    static void printUsage() {
        String usage = "OVERVIEW: ZQC parser\n\n" +
                "USAGE: parser [options] <inputs>\n\n" +
                "OPTIONS:\n" +
                "\t-xml    \tUse xml as input.\n" +
                "\t-o <file>\tWrite output to <file>.xml";
        System.out.println(usage);
    }

    public static void main(String[] args) {
        String source = null;
        String out = null;
        boolean use_xml = false;

        if(args.length == 0) {
            printUsage();
            return;
        } else {
            for(int i=0; i<args.length;) {
                if(args[i].equals("-o")) {
                    out = args[i+1];
                    i++;
                } else if(args[i].equals("-xml")) {
                    use_xml = true;
                    i++;
                } else {
                    if(source == null)
                        source = args[i];
                    i++;
                }
            }
        }

        ParserRunner runner = new ParserRunner();
        runner.run(source, out, use_xml);
    }
}
