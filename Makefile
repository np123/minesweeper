JC = @javac
SRC = $(wildcard src/**/*.java) # enumerate all java files in the path
BIN = ./bin
SP = ./src # sourcepath variable
CP = ./bin # classpath variable

.SUFFIXES:			# delete all suffixes
.SUFFIXES: .java .class		# define our suffix list

.java.class:
	mkdir -p bin
	@echo "Compiling $< to $@ ..."
	$(JC) -cp $(CP) -sourcepath $(SP) -d $(BIN) $< 

.PHONY: default classes clean

default: classes

classes: $(SRC:.java=.class)

clean:
	@echo "Remove all class file ..."
	@rm -rf $(BIN)/$(PKG)/*.class
	@echo "done"
