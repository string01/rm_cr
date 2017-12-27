
The cash-register project is an implementation of the RM 'cash register' programming problem.

This implementation is based on an OOA/D of the components used to create a cash register.

While admittedly this implementation is overkill for the given problem, I had an idea of how to model the parts to make
a cash register. The core domain model classes are CashRegister, CashDrawer, Accumulator and Denomination.

The CashRegister has-a CashDrawer which, in turn has-a collection of Accumulator instances. There is one Accumulator for
each Denomination that is defined. In this case, $20, 10, 5, 2, and 1. The design should be easily extensible to
allow for fractional denominations $0.50, 0.25, etc. without too much trouble. I only implemented the denominations
given in the problem description at this time.

System Behavior.
When a transaction (put, take, change) is run on the CashRegister, a virtual CashDrawer instance is created either from
the given String input format with the number of each denomination (e.g. 1 2 3 4 5 is 1 $20, 2 $10's 3 $5's, 4 $2's and
5 $1's for a total of 68. This virtual CashDrawer is then added to or subtracted from the CashRegister's CashDrawer
(with exceptions for bad input format or insufficient funds).

When the 'change' command is given (e.g. 'change 11') the Integer '11' is turned into a CashDrawer instance using a
simple algo. which starts with the highest denomination and works it's way down to the lowest to make $11. An attempt
is then made to create a CashDrawer that would make up the required amount of change given the contents of the
CashRegister's existing CashDrawer. If a valid amount of change can be made, the 'change' CashDrawer is taken from the
existing CashRegister's drawer and the CashRegister is left with the remainder. If, on the other hand, the correct chang
e cannot be created, the Partition class is used to create a Map of possible combinations from the given denominations
to make up the requested amount. (Full disclosure: I finally went to The Google to find the math on this one as
I had myself thinking in circles. Although I got the basic recursive algo I was looking for, I had to engineer it to
produce just a set of valid combinations w.r.t. our given set of denominations and filter the rest). Once a set of possible and valid
combinations are obtained, they are tried against the existing CashDrawer to see if any of them form an intersection with
the existing CashDrawer. If so, the requested amount is taken from the CashRegister's CashDrawer.

Building:
$> mvn clean install
Can be used to build and run tests.

This is not a spring-boot application. In this is used by the cash-register-shell project as a dependency.
