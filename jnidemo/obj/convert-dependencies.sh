#!/bin/sh
# AUTO-GENERATED FILE, DO NOT EDIT!
if [ -f $1.org ]; then
  sed -e 's!^D:/tools/cygwin/lib!/usr/lib!ig;s! D:/tools/cygwin/lib! /usr/lib!ig;s!^D:/tools/cygwin/bin!/usr/bin!ig;s! D:/tools/cygwin/bin! /usr/bin!ig;s!^D:/tools/cygwin/!/!ig;s! D:/tools/cygwin/! /!ig;s!^F:!/cygdrive/f!ig;s! F:! /cygdrive/f!ig;s!^E:!/cygdrive/e!ig;s! E:! /cygdrive/e!ig;s!^D:!/cygdrive/d!ig;s! D:! /cygdrive/d!ig;s!^C:!/cygdrive/c!ig;s! C:! /cygdrive/c!ig;' $1.org > $1 && rm -f $1.org
fi