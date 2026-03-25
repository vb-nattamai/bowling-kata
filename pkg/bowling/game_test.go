package bowling_test

import (
	"testing"

	"github.com/vb-nattamai/bowling-kata/pkg/bowling"
)

func rollN(g *bowling.Game, n, pins int) {
	for i := 0; i < n; i++ {
		_ = g.Roll(pins)
	}
}

func TestGutterGame(t *testing.T) {
	g := bowling.NewGame()
	rollN(g, 20, 0)
	score, _ := g.Score()
	if score != 0 {
		t.Errorf("gutter game: want 0, got %d", score)
	}
}

func TestAllOnes(t *testing.T) {
	g := bowling.NewGame()
	rollN(g, 20, 1)
	score, _ := g.Score()
	if score != 20 {
		t.Errorf("all ones: want 20, got %d", score)
	}
}

func TestPerfectGame(t *testing.T) {
	g := bowling.NewGame()
	rollN(g, 12, 10)
	score, _ := g.Score()
	if score != 300 {
		t.Errorf("perfect game: want 300, got %d", score)
	}
}

func TestSpare(t *testing.T) {
	g := bowling.NewGame()
	_ = g.Roll(5)
	_ = g.Roll(5)
	_ = g.Roll(3)
	rollN(g, 17, 0)
	score, _ := g.Score()
	if score != 16 {
		t.Errorf("spare: want 16, got %d", score)
	}
}
