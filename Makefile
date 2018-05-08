JAVAC=javac
CLASSPATH=.
FLAGS=#-Xlint:unchecked

SRCDIRS=com/cs262/dobj com/cs262/dobj/test com/cs262/dobj/consensus

SOURCES=$(foreach d, $(SRCDIRS), $(wildcard $(d)/*.java))
CLASSES=$(SOURCES:.java=.class)

all: $(CLASSES)

%.class: %.java
	$(JAVAC) -cp $(CLASSPATH) $(FLAGS) $<

run: all
	java com/cs262/dobj/test/Main

clean:
	rm -f $(foreach d, $(SRCDIRS), $(d)/*.class)
