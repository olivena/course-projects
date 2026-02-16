---
agent: agent
model: GPT-4o
description: Create a memory game where players match pairs of cards with specific features and rules.

---
Define the task to achieve, including specific requirements, constraints, and success criteria.
You are to create a memory game where players must match pairs of cards. The game should include the following features:
1. A grid layout in size 8x8
2. Each card should have an identifier (e.g., numbers, letters, or images), two cards have the same identifier, forming a pair
3. Cards should be initially face down and flip to reveal their identifier when clicked
4. Players can flip two cards at a time; if they match, they remain face up, otherwise, they flip back down after a short delay
5. A scoring system that awards points for each matched pair
6. A turn tracker to monitor the number of attempts made by the player
7. Program is made with python and uses a simple GUI library Pygame
8. The game ends when all pairs are matched, displaying the final score and number of attempts
Please provide the complete code implementation for this memory game, ensuring it meets all the specified requirements. 