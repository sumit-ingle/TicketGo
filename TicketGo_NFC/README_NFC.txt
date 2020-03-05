This folder two files that must be executed:
	1. nfc-emulate-forum-tag4_v2.c - emulates NFC terminal for station named "GHATKOPAR". Run this for
									 destination terminal
	2. nfc-emulate-forum-tag4_v3.c - emulates NFC terminal for station named "AIRPORTRD". Run this for
									 source terminal

Following libraries should be installed first for running the programs:
	- https://github.com/nfc-tools/libnfc
	- http://wiringpi.com/download-and-install/
									 
To compile them on terminal, use:
	1. gcc -o nfc-emulate-forum-tag4_v2 nfc-emulate-forum-tag4_v2.c nfc-utils.c -lnfc
	2. gcc -o nfc-emulate-forum-tag4_v3 nfc-emulate-forum-tag4_v2.c nfc-utils.c -lnfc -lWiringPi
	
To run them on terminal, use:
	1. ./nfc-emulate-forum-tag4_v2
	2. ./nfc-emulate-forum-tag4_v3

Pin numbers:
PIR sensor pin - GPIO 8
LED pin - GPIO 16
