package main;

import ast.StmtNode;
import ast.XMLPrinter;
import lexer.Lexer;
import lexer.SourceBuffer;
import lexer.Token;
import lexer.TokenType;
import parser.Parser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Vector;

public class ParserRunner {

    static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    static Vector<Token> readTokensFromXML(String path) {
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

    public static void main(String[] args) {
        String source_path = "test/test1.c";
        if(args.length == 0) {
            System.out.println("No specified source file. Use default: "+source_path);
        } else {
            source_path = args[0];
        }

        String source = null;
        try {
            source = readFile(source_path, Charset.forName("utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        SourceBuffer buff = new SourceBuffer(source);


        Lexer lexer = new Lexer(buff);
        Vector<Token> tokens = new Vector<>();
        Token token = lexer.scan();
//        tokens.add(token);
        while (token.type != TokenType.EOF) {
            tokens.add(token);
            token = lexer.scan();
        }

        tokens = readTokensFromXML("test/test1.c.xml");

        Parser parser = new Parser(tokens);
        StmtNode.CompilationUnit tree = parser.parse();

        XMLPrinter printer = new XMLPrinter();
        String xml = printer.print(tree);

        PrintWriter out;
        try {
            out = new PrintWriter("test/test1.c.tree.xml");
            out.println(xml);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
