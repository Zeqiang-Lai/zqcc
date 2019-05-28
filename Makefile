SRCDIR = src
BINDIR = bin

SRC += $(wildcard src/ast/*.java)
SRC += $(wildcard src/error/*.java)
SRC = $(wildcard src/lexer/*.java)
SRC += $(wildcard src/main/*.java)
SRC += $(wildcard src/parser/*.java)
SRC += $(wildcard src/utils/*.java)

TMP = $(patsubst %.java,%.class,$(SRC))
CLASS = $(patsubst $(SRCDIR)/%,$(BINDIR)/%,$(TMP))

$(BIN_DIR):
	mkdir -p $(BIN_DIR)

lexer: $(SRC) $(BIN_DIR)
	javac -d $(BINDIR) -sourcepath $(SRCDIR) $(SRC)
	cd bin && jar cfe lexer.jar main.LexerRunner . && cd ..

parser: $(SRC) $(BIN_DIR)
	javac -d $(BINDIR) -sourcepath $(SRCDIR) $(SRC)
	cd bin && jar cfe parser.jar main.ParserRunner . && cd ..

run-lexer: 
	java -jar bin/lexer.jar test/test1.c

run-parser: 
	java -jar bin/parser.jar test/test1.c

clean:
	$(RM) -rf bin

.PHONY: clean