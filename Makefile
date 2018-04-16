JAVAC=javac
CLASSPATH=.

SOURCES=$(wildcard com/cs262/dobj/*.java)
CLASSES=$(SOURCES:.java=.class)

all: $(CLASSES)

%.class: %.java
	$(JAVAC) -cp $(CLASSPATH) $<

clean:
	rm $(CLASSES)
