#!/usr/bin/perl
use strict;
use warnings;
$/ = \1;
open(FH, "<Packet1Login.class") || die "cannot open: $!";
my $i = 0;
while(<FH>) {
    print "(byte)" if ord($_) > 127;     # love you, Java
    printf "0x%.2x, ", ord($_);
    print "\n" if (++$i % 16) == 0;
}
print "\n";

