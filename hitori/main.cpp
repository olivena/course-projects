/**
 * Hitori
 *
 * Description:
 *     This program is a one kind Hitori game. The game consist of a gameboard of 5x5,
 * which is filled with numbers from 1 to 5. The purpose of this game is to get eact
 * vertical and horizontal line to not contain duplicate numbers. This can be accessed
 * by deleting squares from the gameboard by giving it coordinates for squares.
 *     The user loses the game if the delete squares next to each other or isolates a filled
 * square from others.
 *     The program begins with asking the user, if they want the gameboard filled with random
 * numbers from 1-5 or do they want to give the input themselves. In the second option
 * the user is asked to insert an input which contains 25 numbers.
 *     After this starts the game rounds where the user is asked to give coordinates for
 * a square to be deleted. The game checks if the coordinates match on the gameboard and
 * is the coordinate already used.
 *     In each round, the program checks if the user has won or lost, and if not either, the
 * game moves to a next round. If the user won or lost the game ends.
 *
 *
 * Program author:
 * Name: Olivia Takkinen
 * E-mail: olivia.takkinen@gmail.com
 *
 * Last change 14.2.2022
 *
 *
 * PROBLEMS:
 * #4 + #5 you lost not workingSR
*/

#include <iostream>
#include <vector>
#include <random>
#include <string>
#include <algorithm>

using namespace std;

const unsigned int BOARD_SIDE = 5;
const unsigned char EMPTY = ' ';

// Converts the given numeric string to the corresponding integer
// (by calling stoi).
// If the given string is not numeric, returns zero.
unsigned int stoi_with_check(const string& str)
{
    bool is_numeric = true;
    for(unsigned int i = 0; i < str.length(); ++i)
    {
        if(not isdigit(str.at(i)))
        {
            is_numeric = false;
            break;
        }
    }
    if(is_numeric)
    {
        return stoi(str);
    }
    else
    {
        return 0;
    }
}

// Prints the game board with row and column numbers.
void print(const vector<vector<int>>& gameboard)
{
    cout << "=================" << endl;
    cout << "|   | 1 2 3 4 5 |" << endl;
    cout << "-----------------" << endl;
    for(unsigned int i = 0; i < BOARD_SIDE; ++i)
    {
        cout << "| " << i + 1 << " | ";
        for(unsigned int j = 0; j < BOARD_SIDE; ++j)
        {
            if(gameboard.at(i).at(j) == 0)
            {
                cout << EMPTY << " ";
            }
            else
            {
                cout << gameboard.at(i).at(j) << " ";
            }
        }
        cout << "|" << endl;
    }
    cout << "=================" << endl;
}

/*
 * Method to construct the gameboard. It is used in the begin of the program
 * and its values depends on the seed which is given by the user.
 * The function creates a random gameboard of 5x5 inside a
 * vector<vector>, and the values range between 1 to 5.
 */
void create_random_gameboard(vector<vector<int>>& gameboard) {
    int seedValue;
    cout << "Enter seed value: ";
    cin >> seedValue;

    default_random_engine gen(seedValue);
    uniform_int_distribution<unsigned int> distr(1, 5);

    for( int unsigned i = 0; i < BOARD_SIDE; ++i ) {
        vector<int> row;

        for( int unsigned j = 0; j < BOARD_SIDE; ++j ) {
            int random = distr(gen);
            row.push_back(random);
        }
        gameboard.push_back(row);
    }
}

/*
 * Helping method to change user input of gameboard values to vector
*/
vector<unsigned int> split (string inputLine) {
    char splitter = ' ';
    vector<unsigned int> squareInput;

    for (char character : inputLine) {
        if (character != splitter) {
            string middlephase;
            middlephase.push_back(character);
            int charInt = stoi_with_check(middlephase);
            squareInput.push_back(charInt);
        }
    }
    return squareInput;
}

/*
 * A method to create a gameboard from users input. User is asked to give input (of 25 characters) which
 * are then used to fill the gameboard. The method uses split-method (above) to create correct gameboard.
*/
void create_input_gameboard(vector<vector<int>>& gameboard) {
    string input;
    cout << "Input: ";
    string inputString;
    for (int i = 0; i < 25 ; i++) {
        cin >> input;
        inputString += input;
    }

    vector<unsigned int> gameboardInside = split(inputString);

    int index = 0;
    for (int unsigned i = 0; i < BOARD_SIDE; i++) {
        vector<int> row;
        for (int unsigned j = 0; j < BOARD_SIDE; j++) {
            row.push_back(gameboardInside.at(index));
            index++;
        }
        gameboard.push_back(row);
    }
}

/*
 * Function tries to delete a square from the gameboard. The user gives coordinates
 * for the square to be deleted. The function contains terms to catch possible errors
 * such as values out of board or coordinates that match with already deleted square.
*/

bool delete_square(vector<vector<int>>& gameboard, int i, int j) {
    if (i > 5 || j > 5) {
         cout << "Out of board" << endl;
         return true;
    } else if (i < 0 || j < 0){
        cout << "Out of board" << endl;
        return true;
    } else if (gameboard.at(i).at(j) == 0) {
         cout << "Already removed" << endl;
         return true;
    } else {
        gameboard.at(i).at(j) = 0;
        print(gameboard);
    }
    return false;
}


/*
 * User loses game if they delete two squares next to each other or if user has deleted all squares
 * around a square.
*/
bool check_if_next_to_each_other(vector<vector<int>> gameBoard) {
    //Checking if two squares next to each other are empty, which causes the user to lose
    for (int x = 1; x < (int)BOARD_SIDE - 1; x ++) {
        for (int y = 1; y < (int)BOARD_SIDE - 1; y++) {
            if (gameBoard.at(x).at(y) == 0) {
                if (gameBoard.at(x + 1).at(y) == 0 || gameBoard.at(x).at(y + 1) == 0 || gameBoard.at(x - 1).at(y) == 0
                    || gameBoard.at(x).at(y - 1) == 0 ) {
                    return true;
                }
            }
        }
    }
    return false;
}

/*
 * Method to see if the user has isolated a square.
 */
bool check_if_isolated(vector<vector<int>> gameBoard) {
    //(x,y) is the square we are at right know
    for (int x = 0; x <= int(BOARD_SIDE); x++) {
        for (int y = 0; y <= int(BOARD_SIDE); y++) {

            //checking if corners are isolated
            if (x == 0 && y == 0) {
                if ((gameBoard.at(x).at(y + 1)) == 0 && (gameBoard.at(x + 1).at(y)) == 0) {
                    return true;
                }
            } else if (x == 4 && y == 4) {
                if ((gameBoard.at(x - 1).at(y)) == 0 && (gameBoard.at(x).at(y - 1)) == 0) {
                    return true;
                }
            } else if (x == 0 && y == 4) {
                if ((gameBoard.at(x).at(y - 1)) == 0 && (gameBoard.at(x + 1).at(y)) == 0) {
                    return true;
                }
            } else if (x == 4 && y == 0) {
                if ((gameBoard.at(x - 1).at(y)) == 0 && (gameBoard.at(x).at(y + 1)) == 0) {
                    return true;
                }
            }
             // checking if there is an island in the middle
            if ((x > 0 && x < (int)BOARD_SIDE - 2) && (y > 0 && y < (int)BOARD_SIDE - 2)) {
                if ((gameBoard.at(x).at(y - 1) == 0) && (gameBoard.at(x + 1).at(y) == 0)
                        && (gameBoard.at(x).at(y + 1) == 0) && (gameBoard.at(x - 1).at(y) == 0)) {
                    return true;
                }
            }
        }
    }

    return false;
}

/*(find(vertical.begin(), vertical.end(), gameBoard.at(j).at(i)) != vertical.end())
 * The user wins if they succeed at creating a gameboard where vertical lines and horizontal lines
 * all contain numbers from 1 to 5, and these numbers only exist one time in specific line.
*/
bool check_if_won(vector<vector<int>> gameBoard) {
    int noSame = 0;
    for (unsigned int i = 0; i < BOARD_SIDE; i++) {
        vector<int> vertical;
        vector<int> horizontal;
        for (unsigned int j = 0; j < BOARD_SIDE; j++ ) {

            if (gameBoard.at(j).at(i) != 0) {
                if (find(vertical.begin(), vertical.end(), gameBoard.at(j).at(i)) != vertical.end()) {
                    noSame++;
                }
            } else if (gameBoard.at(i).at(j) != 0) {
                if (find(horizontal.begin(), horizontal.end(), gameBoard.at(i).at(j)) != horizontal.end()) {
                    noSame++;
                }
            }
            vertical.push_back(gameBoard.at(j).at(i));
            horizontal.push_back(gameBoard.at(i).at(j));
        }
    }

    if (noSame == 0) {
        return true;
    } else {
        return false;
    }

}

int main()
{
    bool insertedInput = true;
    string input;

    vector<vector<int>> gameBoard;
    while (insertedInput) {
        cout << "Select start (R for random, I for input): ";
        getline(cin, input);

        if (input == "r" || input == "R" || input == "i" || input == "I") {
            insertedInput = false;
            if (input == "r"|| input == "R") {
                create_random_gameboard(gameBoard);
            } else if (input == "i" || input == "I"){
                create_input_gameboard(gameBoard);
            }
        }
    }

    print(gameBoard);

    while (true) {
        //asks user coordinates for a square to be deleted
        string x;
        string y;
        cout << "Enter removable element (x, y): ";
        cin >> x;

        if (x == "q") {
            cout << "Quitting" << endl;
            return EXIT_SUCCESS;
        }

        cin >> y;

        if (y == "q") {
            cout << "Quitting" << endl;
            return EXIT_SUCCESS;
        }

        unsigned int intX = stoi_with_check(x);
        intX -= 1;
        unsigned int intY = stoi_with_check(y);
        intY -= 1;

        //deletes the square and checks if the user lost or one the game, if not either
        //the game moves to the next round.
        bool outOfRange = delete_square(gameBoard, intY, intX);
        if (!outOfRange) {

            bool lost = check_if_next_to_each_other(gameBoard);

            bool isolated = check_if_isolated(gameBoard);

            if (lost || isolated) {
                cout << "You lost" << endl;
                return EXIT_SUCCESS;
            }

            bool won = check_if_won(gameBoard);
            if (won) {
                cout << "You won" << endl;
                return EXIT_SUCCESS;
            }
        }
    }
    return 0;
}
