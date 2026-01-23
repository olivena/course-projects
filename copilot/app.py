from flask import Flask, render_template, jsonify, request
import random

app = Flask(__name__)

class MemoryGame:
    def __init__(self):
        self.cards = []
        self.score = 0
        self.attempts = 0
        self.matched_pairs = 0
        self.first_card = None
        self.game_over = False
        self.initialize_game()
    
    def initialize_game(self):
        """Initialize the game board with shuffled pairs."""
        identifiers = []
        # Create pairs (8x8 = 64 cards = 32 pairs)
        for i in range(32):
            identifiers.append(i)
            identifiers.append(i)
        
        random.shuffle(identifiers)
        
        # Create cards
        self.cards = [
            {
                'id': idx,
                'identifier': identifiers[idx],
                'flipped': False,
                'matched': False
            }
            for idx in range(64)
        ]
        self.score = 0
        self.attempts = 0
        self.matched_pairs = 0
        self.first_card = None
        self.game_over = False
    
    def flip_card(self, card_id):
        """Flip a card and check for matches."""
        if card_id < 0 or card_id >= len(self.cards):
            return {'error': 'Invalid card'}
        
        card = self.cards[card_id]
        
        if card['matched'] or card['flipped']:
            return {'error': 'Card already flipped or matched'}
        
        card['flipped'] = True
        
        if self.first_card is None:
            self.first_card = card_id
            return {
                'action': 'waiting',
                'cards': self.cards,
                'score': self.score,
                'attempts': self.attempts,
                'game_over': self.game_over
            }
        else:
            # Second card flipped
            self.attempts += 1
            second_card_id = card_id
            first_card = self.cards[self.first_card]
            
            result = {
                'action': 'compare',
                'cards': self.cards,
                'score': self.score,
                'attempts': self.attempts,
                'game_over': self.game_over
            }
            
            if first_card['identifier'] == card['identifier']:
                # Match found
                first_card['matched'] = True
                card['matched'] = True
                self.score += 10
                self.matched_pairs += 1
                result['matched'] = True
                
                # Check if game is over
                if self.matched_pairs == 32:
                    self.game_over = True
                    result['game_over'] = True
            else:
                # No match - flip back
                result['matched'] = False
            
            result['cards'] = self.cards
            result['score'] = self.score
            self.first_card = None
            return result
    
    def restart(self):
        """Restart the game."""
        self.initialize_game()

# Global game instance
game = MemoryGame()

@app.route('/')
def index():
    return render_template('index.html')

@app.route('/api/game/start', methods=['POST'])
def start_game():
    """Start a new game."""
    global game
    game = MemoryGame()
    return jsonify({
        'cards': game.cards,
        'score': game.score,
        'attempts': game.attempts
    })

@app.route('/api/game/flip', methods=['POST'])
def flip():
    """Flip a card."""
    data = request.json
    card_id = data.get('card_id')
    result = game.flip_card(card_id)
    return jsonify(result)

@app.route('/api/game/restart', methods=['POST'])
def restart():
    """Restart the game."""
    global game
    game.restart()
    return jsonify({
        'cards': game.cards,
        'score': game.score,
        'attempts': game.attempts
    })

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)
