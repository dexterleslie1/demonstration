#!/usr/bin/python

import argparse

# prefix_chars
parser = argparse.ArgumentParser(prog='PROG', prefix_chars='-+')
parser.add_argument('+f')
parser.add_argument('++bar')
args = parser.parse_args('+f X ++bar Y'.split())
print("prefix_chars demo parse args '+f X ++bar Y' result is " + str(args))

# add_argument name/flags
parser = argparse.ArgumentParser(prog='PROG')
parser.add_argument('-f', '--foo')
parser.add_argument('bar')

args = parser.parse_args(['BAR'])
print("add_argument name/flags demo parse args ['BAR'] result is " + str(args))

args = parser.parse_args(['BAR', '--foo', 'FOO'])
print("add_argument name/flags demo parse args ['BAR', '--foo', 'FOO'] result is " + str(args))

args = parser.parse_args(['-f', 'FOO1', 'BAR'])
print("add_argument name/flags demo parse args ['-f', 'FOO1', 'BAR'] result is " + str(args))

# add_argument action=store_const
parser = argparse.ArgumentParser()
parser.add_argument('--foo', action='store_const', const=42)
args = parser.parse_args('--foo'.split())
print("add_argument action='store_const',const=42 result is " + str(args))

# add_argument action=store_true,action=store_false
parser = argparse.ArgumentParser()
parser.add_argument('--foo', action='store_true')
parser.add_argument('--bar', action='store_false')
parser.add_argument('--baz', action='store_false')
args = parser.parse_args('--foo --bar'.split())
print("add_argument action='store_true',action='store_false' parse args '--foo --bar' result is " + str(args))
args = parser.parse_args('--bar --baz'.split())
print("add_argument action='store_true',action='store_false' parse args '--bar --baz' result is " + str(args))

# add_argument action=append
parser = argparse.ArgumentParser()
parser.add_argument('--foo', action='append')
args = parser.parse_args('--foo 1 --foo 2'.split())
print("add_argument action=append' parser args '--foo 1 --foo 2' result is " + str(args))

# add_argument action=count
parser = argparse.ArgumentParser()
parser.add_argument('--verbose', '-v', action='count')
args = parser.parse_args('-vvv'.split())
print("add_argument action=count parser args '-vvv' result is " + str(args))

# add_argument action=version
parser = argparse.ArgumentParser(prog='PROG')
parser.add_argument('--version', action='version', version='%(prog)s 2.0')
print("add_argument action=version parser args '--version' result is: ")
parser.parse_args('--version'.split())


