# onelogin-fraction
# Audition code for OneLogin 2/2020 #
### Fraction Math App ###
This is a console application to evaluate simple expressions comprised of fractions
(integers, improper and mixed fractions allowed).

The supported operations are addition (+), subtraction (-), multiplication (*) and
division (/).  Parenthesization is not yet supported ... sorry.

Expressions are made up of strictly alternating numeric values and operators with
spaces between them, and must start and end with a value.  A value may be one of
the following:

value type | examples |   |   |
:--------- | :------- | - | - |
an integer value | 21 | -377 | +610
a proper fraction | 0/2 | -3/5 | +13/21
an improper fraction | 8/5 | -377/3 | +987/144
a mixed fraction | 1_3/5 | -125_2/3 | +6_123/144

An example expression:
```
  ? 1_3/5 + +987/144 * -377 / 21/5
  = -9276033/15120
```

Expressions may be specified two ways:
  1) Running the app from the command line will result in a "? " prompt.  An expression
     may be entered there, terminated by an Enter.  The result will be shown on the
     next line, preceded by "= ".  Prompts will continue until either an empty line or
     an EOF (^Z on Windows, ^D on Linux) is entered.

  2) An expression may also be provided as a command line argument.  The expression 
     will be written out (preceded by "? ") with the result following with the
     preceding "= ".

Note: 
  Fractions are modeled using a pair of 32-bit signed integers for numerator and
  denominator respectively.  Values that would require use of integers outside of this
  32-bit range (for either the numerator, denominator or both), either as an input 
  value, output value or as an intermediate value during expression operation, may 
  yield an ±Infinity or NaN result.
