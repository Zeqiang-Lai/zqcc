package main;

import lexer.Lexer;
import lexer.SourceBuffer;
import lexer.Token;
import lexer.TokenType;
import utils.XMLBuilder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class LexerRunner {

    static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
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

        XMLBuilder xmlHelper = new XMLBuilder();
        xmlHelper.append("<project>");
        Lexer lexer = new Lexer(buff);
        Token token = lexer.scan();
        while (token.type != TokenType.EOF) {
//            System.out.println(token.type + "| " + token.value + "| " + token.line);
            xmlHelper.append(token);
            token = lexer.scan();
        }
        xmlHelper.append("</project>");
        
        System.out.println("Finish!");
        System.out.println("Generate xml...");
        try {
            xmlHelper.generate(source_path+".xml");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("Generate xml success!");
    }
}
