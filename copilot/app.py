from flask import Flask, render_template, jsonify, request
import random
import json
import os
from datetime import datetime

app = Flask(__name__)

SCORES_FILE = 'scores.json'

def load_scores():
    """Load scores from JSON file."""
    if os.path.exists(SCORES_FILE):
        with open(SCORES_FILE, 'r') as f:
            return json.load(f)
    return []

def save_scores(scores):
    """Save scores to JSON file."""
    with open(SCORES_FILE, 'w') as f:
        json.dump(scores, f, indent=2)

def add_score(name, score, attempts):
    """Add a new score to the list."""
    scores = load_scores()
    scores.append({
        'name': name,
        'score': score,
        'attempts': attempts,
        'date': datetime.now().strftime('%Y-%m-%d %H:%M:%S')
    })
    # Sort by score descending, then by attempts ascending
    scores.sort(key=lambda x: (-x['score'], x['attempts']))
    # Keep only top 100 scores
    scores = scores[:100]
    save_scores(scores)
    return scores

class MemoryGame:
    def __init__(self):
        self.cards = []
        self.score = 0
        self.attempts = 0
        self.matched_pairs = 0
        self.first_card = None
        self.game_over = False
        self.cards_to_flip_back = []
        # Clown and circus emojis - all unique
        self.emojis = [
            '🤡', '🎪', '🎨', '🎭', '🎺', '🎸', '🎻', '🥁',
            '🎯', '🎲', '🎳', '🎮', '🎰', '🧩', '🎀', '🎁',
            '🤹', '💃', '🕺', '🎤', '🎬', '🎢', '🎡', '🎠',
            '🪀', '🎈', '🎩', '🎧', '🎓', '🎉', '🎱', '🎷'
        ]
        self.initialize_game()
    
    def initialize_game(self):
        """Initialize the game board with shuffled pairs."""
        identifiers = []
        # Create pairs (8x8 = 64 cards = 32 pairs)
        # Each pair gets the same emoji
        for i in range(32):
            identifiers.append(i)
            identifiers.append(i)
        
        random.shuffle(identifiers)
        
        # Create cards
        self.cards = [
            {
                'id': idx,
                'identifier': identifiers[idx],
                'emoji': self.emojis[identifiers[idx]],
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
        
        # If there are pending cards to flip back, flip them back now (new turn started)
        if self.cards_to_flip_back:
            for cid in self.cards_to_flip_back:
                self.cards[cid]['flipped'] = False
            self.cards_to_flip_back = []
        
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
                # No match - deduct 1 point and cards stay visible
                result['matched'] = False
                self.score = max(0, self.score - 1)  # Don't let score go below 0
                self.cards_to_flip_back = [self.first_card, second_card_id]
            
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

@app.route('/api/scores/save', methods=['POST'])
def save_score():
    """Save a score to the leaderboard."""
    data = request.json
    name = data.get('name', 'Anonymous').strip()
    score = data.get('score', 0)
    attempts = data.get('attempts', 0)
    
    if not name:
        name = 'Anonymous'
    
    scores = add_score(name, score, attempts)
    return jsonify({'success': True, 'scores': scores})

@app.route('/api/scores/get', methods=['GET'])
def get_scores():
    """Get the leaderboard."""
    scores = load_scores()
    return jsonify({'scores': scores})

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)
