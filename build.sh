#!/usr/bin/env bash
set -e

javac Main.java

# Evita conflictos de GLIBC/Snap al cargar librerias nativas de AWT/Swing.
env -u LD_LIBRARY_PATH -u LD_PRELOAD -u GTK_PATH -u GDK_PIXBUF_MODULE_FILE /usr/bin/java Main
