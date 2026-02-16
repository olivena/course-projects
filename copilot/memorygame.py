import pygame
import random
import sys
from enum import Enum

# Initialize Pygame
pygame.init()

# Constants
GRID_SIZE = 8
CARD_SIZE = 60
MARGIN = 10
WINDOW_WIDTH = GRID_SIZE * (CARD_SIZE + MARGIN) + MARGIN
WINDOW_HEIGHT = GRID_SIZE * (CARD_SIZE + MARGIN) + MARGIN + 100
FPS = 60
FLIP_DELAY = 500  # milliseconds

# Colors
WHITE = (255, 255, 255)
BLACK = (0, 0, 0)
GRAY = (200, 200, 200)
DARK_GRAY = (100, 100, 100)
GREEN = (0, 200, 0)
RED = (200, 0, 0)
BLUE = (0, 0, 255)
LIGHT_BLUE = (100, 150, 255)

class CardState(Enum):
    FACE_DOWN = 0
    FACE_UP = 1
    MATCHED = 2

class Card:
    def __init__(self, x, y, identifier):
        self.x = x
        self.y = y
        self.identifier = identifier
        self.state = CardState.FACE_DOWN
        self.rect = pygame.Rect(x, y, CARD_SIZE, CARD_SIZE)
    
    def draw(self, screen, font):
        if self.state == CardState.MATCHED:
            pygame.draw.rect(screen, LIGHT_BLUE, self.rect)
            pygame.draw.rect(screen, DARK_GRAY, self.rect, 2)
        elif self.state == CardState.FACE_UP:
            pygame.draw.rect(screen, GREEN, self.rect)
            pygame.draw.rect(screen, BLACK, self.rect, 2)
            # Draw identifier
            text = font.render(str(self.identifier), True, BLACK)
            text_rect = text.get_rect(center=self.rect.center)
            screen.blit(text, text_rect)
        else:  # FACE_DOWN
            pygame.draw.rect(screen, GRAY, self.rect)
            pygame.draw.rect(screen, BLACK, self.rect, 2)
    
    def is_clicked(self, pos):
        return self.rect.collidepoint(pos)

class MemoryGame:
    def __init__(self):
        self.screen = pygame.display.set_mode((WINDOW_WIDTH, WINDOW_HEIGHT))
        pygame.display.set_caption("Memory Game")
        self.clock = pygame.time.Clock()
        self.font = pygame.font.Font(None, 36)
        self.small_font = pygame.font.Font(None, 24)
        
        self.cards = []
        self.selected_cards = []
        self.score = 0
        self.attempts = 0
        self.game_over = False
        self.flip_back_time = None
        
        self.initialize_game()
    
    def initialize_game(self):
        """Initialize the game board with shuffled pairs."""
        identifiers = []
        # Create pairs (8x8 = 64 cards = 32 pairs)
        for i in range(32):
            identifiers.append(i)
            identifiers.append(i)
        
        random.shuffle(identifiers)
        
        # Create cards in grid layout
        idx = 0
        for row in range(GRID_SIZE):
            for col in range(GRID_SIZE):
                x = MARGIN + col * (CARD_SIZE + MARGIN)
                y = MARGIN + row * (CARD_SIZE + MARGIN)
                card = Card(x, y, identifiers[idx])
                self.cards.append(card)
                idx += 1
    
    def handle_click(self, pos):
        """Handle card click."""
        if self.game_over or self.flip_back_time is not None:
            return
        
        for card in self.cards:
            if card.is_clicked(pos) and card.state == CardState.FACE_DOWN:
                card.state = CardState.FACE_UP
                self.selected_cards.append(card)
                
                if len(self.selected_cards) == 2:
                    self.attempts += 1
                    self.check_match()
                break
    
    def check_match(self):
        """Check if the two selected cards match."""
        card1, card2 = self.selected_cards
        
        if card1.identifier == card2.identifier:
            # Match found
            card1.state = CardState.MATCHED
            card2.state = CardState.MATCHED
            self.score += 10
            self.selected_cards = []
            
            # Check if game is over
            if all(card.state == CardState.MATCHED for card in self.cards):
                self.game_over = True
        else:
            # No match - flip back after delay
            self.flip_back_time = pygame.time.get_ticks()
    
    def update(self):
        """Update game state."""
        if self.flip_back_time is not None:
            current_time = pygame.time.get_ticks()
            if current_time - self.flip_back_time >= FLIP_DELAY:
                # Flip cards back
                for card in self.selected_cards:
                    card.state = CardState.FACE_DOWN
                self.selected_cards = []
                self.flip_back_time = None
    
    def draw(self):
        """Draw the game."""
        self.screen.fill(WHITE)
        
        # Draw cards
        for card in self.cards:
            card.draw(self.screen, self.font)
        
        # Draw UI at bottom
        ui_y = GRID_SIZE * (CARD_SIZE + MARGIN) + MARGIN
        pygame.draw.line(self.screen, BLACK, (0, ui_y), (WINDOW_WIDTH, ui_y), 2)
        
        score_text = self.small_font.render(f"Score: {self.score}", True, BLACK)
        self.screen.blit(score_text, (20, ui_y + 15))
        
        attempts_text = self.small_font.render(f"Attempts: {self.attempts}", True, BLACK)
        self.screen.blit(attempts_text, (WINDOW_WIDTH // 2 - 50, ui_y + 15))
        
        # Draw game over message
        if self.game_over:
            overlay = pygame.Surface((WINDOW_WIDTH, WINDOW_HEIGHT))
            overlay.set_alpha(200)
            overlay.fill(BLACK)
            self.screen.blit(overlay, (0, 0))
            
            game_over_text = self.font.render("Game Over!", True, GREEN)
            final_score_text = self.small_font.render(f"Final Score: {self.score}", True, WHITE)
            final_attempts_text = self.small_font.render(f"Attempts: {self.attempts}", True, WHITE)
            restart_text = self.small_font.render("Press SPACE to restart or ESC to quit", True, WHITE)
            
            self.screen.blit(game_over_text, (WINDOW_WIDTH // 2 - game_over_text.get_width() // 2, WINDOW_HEIGHT // 2 - 60))
            self.screen.blit(final_score_text, (WINDOW_WIDTH // 2 - final_score_text.get_width() // 2, WINDOW_HEIGHT // 2))
            self.screen.blit(final_attempts_text, (WINDOW_WIDTH // 2 - final_attempts_text.get_width() // 2, WINDOW_HEIGHT // 2 + 40))
            self.screen.blit(restart_text, (WINDOW_WIDTH // 2 - restart_text.get_width() // 2, WINDOW_HEIGHT // 2 + 100))
        
        pygame.display.flip()
    
    def restart(self):
        """Restart the game."""
        self.cards = []
        self.selected_cards = []
        self.score = 0
        self.attempts = 0
        self.game_over = False
        self.flip_back_time = None
        self.initialize_game()
    
    def run(self):
        """Main game loop."""
        running = True
        while running:
            self.clock.tick(FPS)
            
            for event in pygame.event.get():
                if event.type == pygame.QUIT:
                    running = False
                elif event.type == pygame.MOUSEBUTTONDOWN:
                    self.handle_click(event.pos)
                elif event.type == pygame.KEYDOWN:
                    if event.key == pygame.K_SPACE and self.game_over:
                        self.restart()
                    elif event.key == pygame.K_ESCAPE:
                        running = False
            
            self.update()
            self.draw()
        
        pygame.quit()
        sys.exit()

if __name__ == "__main__":
    game = MemoryGame()
    game.run()
 