vizeplot
========

Vizeplot is a tool for multidimensional exploration of datasets. You can load
a dataset, and then visualize the data on multiple scatterplots, choosing a
subset of dimensions to visualize in each scatterplot. You can then interact
with the data using brushing and linking to select subsets of data points. You
can also edit the dataset to correct or remove bad rows. A log of your actions
is stored and can be written out to provide provenance for analytical results.

Additional description of the program features can be found in the file 
writeup.pdf that is included in this repository. The included screenshot,
screenshot.png, shows an example dataset (iris.csv, also included) visualized
in two scatterplots with a subset of points highlighted in both.


build and run
=============

This project requires java development kit, which you should be able to install
using a package manager with a command like:
sudo apt-get install default-jdk

The project has one external dependency, opencsv, which is copied in this 
repository. The included make.sh and run.sh scripts should work to correctly
set the classpath for running and building with this dependency. To build the
project, run:
./make.sh

To start the program, run:
./run.sh

