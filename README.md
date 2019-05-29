# ZQCC

The compiler for zqc(a subset of C).

## Getting started

### Compile

```
// compile lexer
make lexer
// compile parser
make parser
```

### Run

Parser:

```shell
OVERVIEW: ZQC parser

USAGE: parser [options] <inputs>

OPTIONS:
	-xml    	Use xml as input.
	-o <file>	Write output to <file>.xml
```

## TODO
- [ ] Optimize the structure of the AST. There are too much redundant code right now.

- [ ] Improve error recovery
  - [ ] skip definiete error symbol
- [ ] support comment