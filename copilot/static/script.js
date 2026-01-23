let gameState = {
    cards: [],
    score: 0,
    attempts: 0,
    gameOver: false
};

// Initialize the game
async function initGame() {
    const response = await fetch('/api/game/start', {
        method: 'POST'
    });
    const data = await response.json();
    gameState = { ...data, gameOver: false };
    renderBoard();
    updateStats();
}

// Render the game board
function renderBoard() {
    const board = document.getElementById('gameBoard');
    board.innerHTML = '';
    
    gameState.cards.forEach((card, index) => {
        const cardEl = document.createElement('div');
        cardEl.className = 'card';
        
        if (card.matched) {
            cardEl.classList.add('matched');
            cardEl.textContent = card.identifier;
        } else if (card.flipped) {
            cardEl.classList.add('flipped');
            cardEl.textContent = card.identifier;
        }
        
        cardEl.onclick = () => flipCard(index);
        board.appendChild(cardEl);
    });
}

// Flip a card
async function flipCard(cardId) {
    // Disable clicking during comparison
    if (gameState.cards[cardId].matched || gameState.cards[cardId].flipped) {
        return;
    }
    
    const response = await fetch('/api/game/flip', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ card_id: cardId })
    });
    const data = await response.json();
    
    if (data.error) {
        return;
    }
    
    gameState.cards = data.cards;
    gameState.score = data.score;
    gameState.attempts = data.attempts;
    gameState.gameOver = data.game_over;
    
    renderBoard();
    updateStats();
    
    if (data.action === 'compare') {
        // Disable board during flip back
        const board = document.getElementById('gameBoard');
        board.style.pointerEvents = 'none';
        
        setTimeout(() => {
            if (!data.matched) {
                // Flip cards back if not matched
                const firstCard = data.cards.find((c, idx) => {
                    return gameState.cards[idx].flipped && !c.matched;
                });
                renderBoard();
            }
            board.style.pointerEvents = 'auto';
            
            // Check if game over
            if (gameState.gameOver) {
                showGameOverModal();
            }
        }, 800);
    }
}

// Update stats display
function updateStats() {
    document.getElementById('score').textContent = gameState.score;
    document.getElementById('attempts').textContent = gameState.attempts;
}

// Show game over modal
function showGameOverModal() {
    document.getElementById('finalScore').textContent = gameState.score;
    document.getElementById('finalAttempts').textContent = gameState.attempts;
    document.getElementById('gameOverModal').classList.remove('hidden');
}

// Restart game
async function restartGame() {
    document.getElementById('gameOverModal').classList.add('hidden');
    await initGame();
}

// Start the game when page loads
window.addEventListener('DOMContentLoaded', initGame);
