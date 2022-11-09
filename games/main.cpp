/*  COMP.CS.100 Project 2: GAME STATISTICS
 * ===============================
 * EXAMPLE SOLUTION
 * ===============================
 *
 *  Acts as a game statistics with n commands:
 * ALL_GAMES - Prints all known game names
 * GAME <game name> - Prints all players playing the given game
 * ALL_PLAYERS - Prints all known player names
 * PLAYER <player name> - Prints all games the given player plays
 * ADD_GAME <game name> - Adds a new game
 * ADD_PLAYER <game name> <player name> <score> - Adds a new player with the
 * given score for the given game, or updates the player's score if the player
 * already playes the game
 * REMOVE_PLAYER <player name> - Removes the player from all games
 *
 *  The data file's lines should be in format game_name;player_name;score
 * Otherwise the program execution terminates instantly (but still gracefully).
 *
 * This program is made to collect information about games, players and players
 * points on specific games. The data; gamenames, playernames and points, are
 * collected in data structure of a map inside of a map.
 *
 * The program starts with user giving the program a file of game and playerinfo,
 * which is then read and tranfered to the data structure. The user can give the
 * program commands as 'all_games', which prints all gamenames given to the
 * program, for the user to see, 'game' with gamename, which tells the user
 * single games information, such as playersnames and points, who are playing the
 * game. Then we have command on printing all players names ('all_players')
 * and command for printing all games a single user plays ('player' with
 * playername). The user can also remove players and give it new ones.
 *
 * The game end when user types 'quit' or 'QUIT'.
 *
 * Program author:
 * Name: Olivia Takkinen
 * Student Number: 39543194
 * UserID: gcolta
 * E-mail: olivia.takkinen@tuni.fi
 *
 * The full version of the program.
 * */

#include <iostream>
#include <fstream>
#include <string>
#include <vector>
#include <map>
#include <set>
#include <algorithm>

using namespace std;
using GAME_DATA = map<string, map<string, int>>;

const string GIVE_FILE_NAME = "Give a name for input file: ";
const string NEXT_ROUND = "games> ";

// error messages:
const string FILE_CAN_NOT_BE_READ = "Error: File could not be read.";
const string INVALID_FORMAT = "Error: Invalid format in file." ;
const string INVALID_INPUT = "Error: Invalid input.";
const string GAME_NOT_FOUND = "Error: Game could not be found.";
const string PLAYER_NOT_FOUND = "Error: Player could not be found.";
const string ALREADY_EXISTS = "Error: Already exists.";

// prints in alphabetical order
const string GAMES_IN_ALPH = "All games in alphabetical order:";
const string PLAYERS_IN_ALPH = "All players in alphabetical order:";

// item was added to the structure
const string ADDED_GAME = "Game was added.";
const string ADDED_PLAYER = "Player was added.";
const string PLAYER_REMOVED = "Player was removed from all games.";

//
const string POINTS_AND_PLAYERS = " has these scores and players, listed in ascending order: ";

// Casual split func, if delim char is between "'s, ignores it.
std::vector<std::string> split( const std::string& str, char delim = ';' )
{
    std::vector<std::string> result = {""};
    bool inside_quatation = false;
    for ( auto current_char : str )
    {
        if ( current_char == '"' )
        {
            inside_quatation = not inside_quatation;
        }
        else if ( current_char == delim and not inside_quatation )
        {
            result.push_back("");
        }
        else
        {
            result.back().push_back(current_char);
        }
    }
    if ( result.back() == "" )
    {
        result.pop_back();
    }
    return result;
}

// Function to add information to the data structure. Function is used when the
// program saves information from the original file to be used in this program
// and when user adds a new player
void add_player(GAME_DATA& gameinfo, vector<string>& playerinfo) {
    string game = playerinfo.at(0);

    if (playerinfo.size() <= 1) {
        map<string, int> empty;
        gameinfo.insert(pair<string, map<string, int>> ({game, empty}));
    } else {
        string player_name = playerinfo.at(1);
        string points = playerinfo.at(2);
        int points_int = stoi(points);
        map<string, int> player_info;
        player_info.insert({player_name, points_int});

        if (gameinfo.find(game) != gameinfo.end()) {
            for (auto& gamename : gameinfo) {
                if (gamename.first == game) {
                    gamename.second.insert({player_name, points_int});
                }
            }
        } else {
            gameinfo.insert(pair<string, map<string, int>> ({game, player_info}));
        }
    }
}

// helping function to print set. Used when printing playernames and
// gamenames.
void print_set(set<string> print_this) {

    set<string>::iterator iter = print_this.begin();
    while (iter != print_this.end()) {
        cout << (*iter) << endl;
        iter++;
    }
}

// Helping function to chech if a player already exists
bool player_exists(GAME_DATA& gameinfo, string players_name) {
    for (auto& games : gameinfo) {
        for (auto& player : games.second) {
            if (player.first == players_name) {
                return true;
            }
        }
    }
    return false;
}

// Function to print information about what games a single player plays.
// Function reads information from gameinfo structure and sorts it to another form.
void playerinfo(GAME_DATA& gameinfo, string players_name) {
    set<string> games_the_player_plays;
    if (player_exists(gameinfo, players_name)) {
        for (auto& games : gameinfo) {
            string game_name = games.first;
            for (auto& player : games.second) {
                if (player.first == players_name) {
                    games_the_player_plays.insert(game_name);
                }
            }
        }
        cout << "Player " << players_name << " playes the following games: " << endl;
        print_set(games_the_player_plays);
    } else {
        cout << PLAYER_NOT_FOUND << endl;
    }
}

// Helping function for game_info-function to get together what points are given
// to different games and what points do players have.
void gather_points_with_players(map<int, set<string>>& points_and_players,
                              string player, int points) {
    if (!(points_and_players.find(points) != points_and_players.end())) {
        set<string> players;
        players.insert(player);
        points_and_players.insert({points, players});
    } else {
        for (auto& players_info : points_and_players) {
            if (players_info.first == points) {
                set<string> playernames = players_info.second;
                playernames.insert(player);

                // the point is first deleted and then added back with
                // set of players who has these points
                points_and_players.erase(points);
                points_and_players.insert({points, playernames});
            }
        }
    }
}

//prints information that is gathered
void print_points(map<int, set<string>>& points_and_players) {
    for (auto const& points : points_and_players) {
        cout << points.first << " : ";
        set<string> players_names = points.second;
        int indecs = 1;

        set<string>::iterator iter;
        for (iter = players_names.begin(); iter != players_names.end(); ++iter) {
            if (indecs < (int)players_names.size()) {
                cout << *iter << ", ";
            } else {
                cout << *iter << endl;
            }
            indecs++;
        }
    }
}

// Function to print single games information from gameinfo structure.
void game_info(GAME_DATA& gameinfo, string gamename) {
    if (gameinfo.find(gamename) != gameinfo.end()) {
        cout << "Game " << gamename << POINTS_AND_PLAYERS << endl;
        map<int, set<string>> points_and_players;
        for (auto const& games : gameinfo) {
            if (games.first == gamename) {

                for (auto const& info : games.second) {
                    string player = info.first;
                    int points = info.second;

                    //inserts playerinfos to data structure in helping function
                    gather_points_with_players(points_and_players, player, points);
                }
            }
        }
        print_points(points_and_players);
    }
    // The given game does not exists
    else {
        cout << GAME_NOT_FOUND << endl;
    }
}

// Function assembles all players names that are given to gameinfo structure
void all_players(GAME_DATA& gameinfo) {
    set<string> gamers;

    for (auto& game : gameinfo) {
        for (auto& players : game.second) {
            gamers.insert(players.first);
        }
    }
    // The set is send to print_set which prints out the assembled information
    print_set(gamers);
}

// Function to assemble all games names. The function uses print_set function
// to print out the assembled information.
void all_games(GAME_DATA& gameinfo) {
    set<string> games;

    for (const auto& game : gameinfo) {
       string gamename = game.first;
       if (games.find(gamename) == games.end()) {
           games.insert(gamename);
       }
    }
    print_set(games);
}

// Function to add new game to data structure. Function checks if the game
// already exists, and if it does not, it is added to the structure by add_player
// function.
void add_game(GAME_DATA& gameinfo, string gamename) {
    if (!(gameinfo.find(gamename) != gameinfo.end())) {
        vector<string> game = {gamename};
        add_player(gameinfo, game);
        cout << ADDED_GAME << endl;
    } else {
        cout << ALREADY_EXISTS << endl;
    }
}

// Function to add players to a game, or to update points the player has got.
void new_player(GAME_DATA& gameinfo, vector<string> playerinformation) {
    string gamename = playerinformation.at(1);
    string playername = playerinformation.at(2);
    string points = playerinformation.at(3);
    int points_int = stoi(playerinformation.at(3));

    vector<string> singleinformation = {gamename, playername, points};

    if (gameinfo.find(gamename) != gameinfo.end()) {
        // a new player is added to the gamestats (data structure)
        if (!player_exists(gameinfo, playername)) {
            add_player(gameinfo, singleinformation);
        }
        // already added player is given new points in a game
        else {
            for (auto iter = gameinfo.begin();iter != gameinfo.end(); iter++) {
                for(const auto& iter2 : (*iter).second) {
                    if (playername != iter2.first && iter2.second < points_int) {
                        (*iter).second.erase(playername);
                        add_player(gameinfo, singleinformation);
                    }
                }
            }
        }
        cout << ADDED_PLAYER << endl;
    } else {
        cout << GAME_NOT_FOUND << endl;
    }
}

// function to remove player and their points from all games
void erase_player(GAME_DATA& gameinfo,
                  string removed_player) {
    if (player_exists(gameinfo, removed_player)) {
        for (auto iter = gameinfo.begin();iter != gameinfo.end(); iter++) {
            for(const auto& iter2 : (*iter).second) {
                string playername = iter2.first;

                if (playername != removed_player) {
                    (*iter).second.erase(removed_player);
                }
            }
        }
        cout << PLAYER_REMOVED << endl;

    } else {
        cout << PLAYER_NOT_FOUND << endl;
    }
}

// Function to read file, and send the information to other function that
// saves the information in the used data structure.
bool read_file(string filename, GAME_DATA& gameinfo) {
    ifstream file_object(filename);
    if ( not file_object ) {
        cout << INVALID_FORMAT << endl;
        return false;
    } else if (file_object.fail()) {
        cout << FILE_CAN_NOT_BE_READ  << endl;
        return false;
    } else {
        string line;

        while (getline(file_object, line)) {
            vector<string> filecontent = split(line);
            if (filecontent.size() == 3) {
                add_player(gameinfo, filecontent);
            } else {
                cout << INVALID_FORMAT << endl;
                return false;
            }
        }
        file_object.close();
    }
    return true;
}

int main()
{
    string inputFile = "";
    cout << GIVE_FILE_NAME ;
    getline(cin, inputFile);

    map<string, map<string, int>> gameinfo;

    bool file_is_read = read_file(inputFile, gameinfo);

    if (!file_is_read) {
        return EXIT_FAILURE;
    }

    // A loop to print out basic outputs while the game keeps going.
    // If the user gives 'quit' or 'QUIT', the loop stops and the program stops.
    while (true) {
        cout << NEXT_ROUND;
        string user_input;
        getline(cin, user_input);

        vector<string> input = split(user_input, ' ');

        if (input.size() > 0) {
            string command = input.at(0);

            if (command == "QUIT" || command == "quit") {
                return EXIT_SUCCESS;
            }
            else if (command == "ALL_GAMES" || command == "all_games") {
                cout << GAMES_IN_ALPH << endl;
                all_games(gameinfo);
            }
            else if (command == "GAME" || command == "game") {
                if (input.size() < 2) {
                    cout << INVALID_INPUT  << endl;
                } else {
                    game_info(gameinfo, input.at(1));
                }
            }
            else if (command == "ALL_PLAYERS" || command == "all_players") {
                    cout << PLAYERS_IN_ALPH << endl;
                    all_players(gameinfo);
            }
            else if (command == "PLAYER" || command == "player") {
                if (input.size() < 2) {
                    cout << INVALID_INPUT << endl;
                } else {
                    playerinfo(gameinfo, input.at(1));
                }
            }
            else if (command == "ADD_GAME" || command == "add_game") {
                if (input.size() < 2) {
                    cout << INVALID_INPUT << endl;
                } else {
                    add_game(gameinfo, input.at(1));
                }
            }
            else if (command == "ADD_PLAYER" || command == "add_player") {
                if (input.size() != 4) {
                    cout << INVALID_INPUT << endl;
                } else {
                    new_player(gameinfo, input);
                }
            }
            else if (command == "REMOVE" || command == "remove") {
                if (input.size() < 2) {
                    cout << INVALID_INPUT << endl;
                } else {
                    erase_player(gameinfo, input.at(1));
                }
            }
            else {
                cout << INVALID_INPUT << endl;
            }
        }
    }

    return EXIT_SUCCESS;
}

