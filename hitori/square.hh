#ifndef SQUARE_HH
#define SQUARE_HH

#include <vector>
#include <iostream>

class Square;
using gameboard = vector< vector< Square>>;

class Square
{
public:
    Square(int x, int y, Gameboard* gameboard);
private:
    int x_;
    int y_;

    Gameboard* gameboard_;
};

#endif // SQUARE_HH

