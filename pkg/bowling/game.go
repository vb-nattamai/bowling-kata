// Package bowling implements the core bowling scoring logic in Go.
package bowling

import "errors"

// Game holds the state of a single bowling game.
type Game struct {
	rolls     [21]int
	rollIndex int
}

// NewGame creates a ready-to-use Game.
func NewGame() *Game {
	return &Game{}
}

// Roll records a single roll. Returns an error if the game is over or roll is invalid.
func (g *Game) Roll(pins int) error {
	if pins < 0 || pins > 10 {
		return errors.New("pins must be between 0 and 10")
	}
	if g.rollIndex >= 21 {
		return errors.New("game is already complete")
	}
	g.rolls[g.rollIndex] = pins
	g.rollIndex++
	return nil
}

// Score calculates the total game score. Returns an error if the game has no rolls.
func (g *Game) Score() (int, error) {
	if g.rollIndex == 0 {
		return 0, errors.New("no rolls recorded")
	}
	score := 0
	idx := 0
	for frame := 0; frame < 10; frame++ {
		if idx >= g.rollIndex {
			break
		}
		if g.rolls[idx] == 10 { // strike
			score += 10 + g.rolls[idx+1] + g.rolls[idx+2]
			idx++
		} else if g.rolls[idx]+g.rolls[idx+1] == 10 { // spare
			score += 10 + g.rolls[idx+2]
			idx += 2
		} else {
			score += g.rolls[idx] + g.rolls[idx+1]
			idx += 2
		}
	}
	return score, nil
}
