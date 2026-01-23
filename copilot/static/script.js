let gameState = {
    cards: [],
    score: 0,
    attempts: 0,
    gameOver: false,
    flipBackTimer: null
};

// Tab switching
function switchTab(tab) {
    // Hide all tabs
    document.querySelectorAll('.tab-content').forEach(el => el.classList.remove('active'));
    document.querySelectorAll('.tab-btn').forEach(el => el.classList.remove('active'));
    
    // Show selected tab
    document.getElementById(tab + 'Tab').classList.add('active');
    event.target.classList.add('active');
    
    // Load leaderboard if switching to it
    if (tab === 'leaderboard') {
        loadLeaderboard();
    }
}

// Initialize the game
async function initGame() {
    const response = await fetch('/api/game/start', {
        method: 'POST'
    });
    const data = await response.json();
    gameState = { ...data, gameOver: false };
    renderBoard();
    updateStats();
    loadLeaderboard();
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
            cardEl.textContent = card.emoji;
        } else if (card.flipped) {
            cardEl.classList.add('flipped');
            cardEl.textContent = card.emoji;
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
    
    // Clear any pending flip-back timer (new turn started)
    if (gameState.flipBackTimer) {
        clearTimeout(gameState.flipBackTimer);
        gameState.flipBackTimer = null;
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
        if (!data.matched) {
            // No match - set 30 second timer to flip back
            gameState.flipBackTimer = setTimeout(() => {
                // Flip cards back after 30 seconds
                gameState.cards = gameState.cards.map(card => {
                    if (!card.matched) {
                        card.flipped = false;
                    }
                    return card;
                });
                renderBoard();
                gameState.flipBackTimer = null;
            }, 15000); // 15 seconds
        } else {
            // Match found - check if game over
            if (gameState.gameOver) {
                showGameOverModal();
            }
        }
    }
}

// Load leaderboard
async function loadLeaderboard() {
    const response = await fetch('/api/scores/get');
    const data = await response.json();
    const scores = data.scores.slice(0, 10); // Show only top 10
    
    const tbody = document.getElementById('scoresBody');
    tbody.innerHTML = '';
    
    if (scores.length === 0) {
        tbody.innerHTML = '<tr><td colspan="3" class="empty-leaderboard">No scores yet. Play to be on the leaderboard!</td></tr>';
        return;
    }
    
    scores.forEach((score, index) => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${index + 1}</td>
            <td>${score.name}</td>
            <td>${score.score}</td>
        `;
        tbody.appendChild(row);
    });
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
    document.getElementById('playerName').value = '';
    document.getElementById('gameOverModal').classList.remove('hidden');
}

// Submit score to leaderboard
async function submitScore() {
    const name = document.getElementById('playerName').value || 'Anonymous';
    
    const response = await fetch('/api/scores/save', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            name: name,
            score: gameState.score,
            attempts: gameState.attempts
        })
    });
    const data = await response.json();
    
    if (data.success) {
        // Show success and play again
        alert('Score saved!');
        playAgain();
    }
}

// Play again
async function playAgain() {
    document.getElementById('gameOverModal').classList.add('hidden');
    await initGame();
}

// Start the game when page loads
window.addEventListener('DOMContentLoaded', initGame);
