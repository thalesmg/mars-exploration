# mars-exploration

This is my solution to [Xerpa's code chalenge](https://gist.github.com/nirev/c42c35eb9a839f7756558519f361bc06). 

The challenge consists in writing a program that will receive the size of a plateau
in Mars and a sequence of positions and movements that several probes should
perform. The output is the final position of each probe and its facing direction.

## Assumptions

It is not defined in the challenge what happens when a probe walks off the plateau.
I assumed that, once it walks off, it falls and crashes, and will be disabled
for further commands. In the output, its coordinates will be beyond the plateau
bounds and its direction will be the last it was facing before falling.

## Installation

1. Install `openjdk-8-jdk`. On Ubuntu/Debian:

       $ sudo apt install openjdk-8-jdk

2. (Recommended) Install [Leiningen](http://leiningen.org/).
3. Clone this repository:

        $ git clone https://github.com/thalesmg/mars-exploration.git

## Usage

The program can be run with [Leiningen](http://leiningen.org/):

    $ lein run [--plot]

The program expects input in the format defined by the exercise:

1. The first line must contain two integers representing the upper-right corner
of the plateau.
2. Then, for each probe that one wants to move:
    1. Enter a line that contains the initial coordinates and direction
    of the probe. E.g.: `1 1 N`.
    2. Enter a line that contains the sequence of movements the probe
    shall execute. E.g.: `MMMRRMMLLMMR`.
    3. Repeat these two steps for each probe.
3. To end the processing and see the output, enter an empty line.

A standalone `jar` file is also provided and can be run with `java`:

    $ java -jar target/uberjar/mars-exploration-0.1.0-SNAPSHOT-standalone.jar [--plot]

The program reads input from `stdin`.

```bash
$ lein run
5 5
1 2 N
LMLMLMLMM
3 3 E
MMRMMRMRRM

Output
> 1 3 N
> 5 1 E
```

```bash
$ cat input.txt | lein run

Output
> 1 3 N
> 5 1 E
```

### Options

If run with `--plot`, an ASCII depiction of the plateau with the probes over it
is shown.

```bash
$ cat input.txt | lein run --plot

Output
> 1 3 N
> 5 1 E
>
> ...... 
> ...... 
> .^.... 
> ...... 
> .....> 
> ...... 
>        
```

### Possible improvements

* Improve the parsing of user input to guard against malformed inputs
(empty lines when informing the plateau size and probe information, unexpected 
characters...).

## License
 
Copyright © 2016 Thales Macedo Garitezi

Distributed under the Eclipse Public License either version 1.0 or any later version.
