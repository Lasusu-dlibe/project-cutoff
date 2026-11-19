#!/usr/bin/env bash
set -euo pipefail

mkdir -p out results
javac -d out src/*.java
java -cp out Benchmark
java -cp out ChartGenerator

echo "Done. Open results/results.csv and results/random-cutoff-chart.svg"
