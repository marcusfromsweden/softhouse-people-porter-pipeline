# Softhouse People Porter Pipeline

A file processing pipeline built with Apache Camel to transform structured flat file input into XML. The application
ingests text files containing person-related data, parses them, constructs domain objects, and outputs them as XML using
JAXB.

## Overview

The application reads `.txt` files from a designated input folder, processes each entry into a `Person`
object (including their addresses, phone numbers, and family members), and aggregates the results into a `People`
container, which is marshalled to XML.

## Pipeline steps

1. **Read & Split**  
   Input files are split into blocks where each block represents one person (starting with a `P|` line).

2. **Parse Lines**  
   Each block is parsed into a list of `InputLine` objects (structured representation of each line).

3. **Build Domain Objects**  
   A `Person` object is created for each block, including nested `Phone`, `Address`, and `FamilyMember` data.

4. **Aggregate to People**  
   All `Person` objects are aggregated into a single `People` object.

5. **Marshal to XML**  
   The aggregated data is transformed into an XML document.

## Error handling

- Things like invalid lines and lines out-of-order result in exceptions.
- The input file is moved to `camel/error/`.
- Logging provides detailed exception information.

## Running the application

### Prerequisites

- Java 21 or newer
- Apache Maven 3.8 or newer

### Build and Run

To compile and run the application using Maven:

```bash
mvn clean package
mvn exec:java -Dexec.mainClass=com.softhouse.technicaltests.peopleporterpipeline.MainApp
```

### Folders

- **Input:** `camel/input` — Place `.txt` files here to trigger processing.
- **Output:** `camel/output` — Produced XML files will be written here, using the same filename with a `.xml` extension.
- **Processed:** `camel/processed/` — Processed input files are moved here.
- **Error:** `camel/error` — Files that fail during processing are moved here for inspection.

### Input file format

The application expects each file to contain blocks of lines describing one or more people. Each block must begin with a `P|` line. Supported line types include:

- `P|firstName|lastName` — Defines a person
- `T|mobile|landLine` — Phone details (landLine is optional)
- `A|street|city|postalCode` — Address details (postalCode is optional)
- `F|name|birthYear` — Family member (can also be followed by T or A lines)

### Handling multiple phones or addresses

Each `Person` and `FamilyMember` can have **only one** `T` (phone) and **only one** `A` (address) line.  
If multiple lines of the same type are encountered:

- The **first one** is used.
- Any **subsequent** lines of the same type are **skipped**.
- A warning is logged indicating the skipped line.

### Example input file

```plaintext
P|Carl Gustaf|Bernadotte
T|0768-101801|08-101801
A|Drottningholms slott|Stockholm|10001
F|Victoria|1977
A|Haga Slott|Stockholm|10002
F|Carl Philip|1979
T|0768-101802|08-101802
P|Barack|Obama
A|1600 Pennsylvania Avenue|Washington, D.C
```

#### Resulting XML

```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<people>
    <person>
        <firstname>Carl Gustaf</firstname>
        <lastname>Bernadotte</lastname>
        <address>
            <street>Drottningholms slott</street>
            <city>Stockholm</city>
            <postal-code>10001</postal-code>
        </address>
        <phone>
            <mobile>0768-101801</mobile>
            <land-line>08-101801</land-line>
        </phone>
        <family>
            <name>Victoria</name>
            <born>1977</born>
            <address>
                <street>Haga Slott</street>
                <city>Stockholm</city>
                <postal-code>10002</postal-code>
            </address>
        </family>
        <family>
            <name>Carl Philip</name>
            <born>1979</born>
            <phone>
                <mobile>0768-101802</mobile>
                <land-line>08-101802</land-line>
            </phone>
        </family>
    </person>
    <person>
        <firstname>Barack</firstname>
        <lastname>Obama</lastname>
        <address>
            <street>1600 Pennsylvania Avenue</street>
            <city>Washington, D.C</city>
        </address>
    </person>
</people>
```




