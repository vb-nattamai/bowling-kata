// Package main — Bowling Kata Go CLI scorer.
// Usage: echo "10 10 10 10 10 10 10 10 10 10 10 10" | go run cmd/scorer/main.go
package main

import (
	"fmt"
	"os"
	"strconv"
	"strings"

	"github.com/vb-nattamai/bowling-kata/pkg/bowling"
)

func main() {
	if len(os.Args) < 2 {
		fmt.Fprintln(os.Stderr, "Usage: scorer <roll1> <roll2> ... <rollN>")
		os.Exit(1)
	}

	rolls := make([]int, 0, len(os.Args)-1)
	for _, arg := range os.Args[1:] {
		n, err := strconv.Atoi(strings.TrimSpace(arg))
		if err != nil || n < 0 || n > 10 {
			fmt.Fprintf(os.Stderr, "invalid roll: %q\n", arg)
			os.Exit(1)
		}
		rolls = append(rolls, n)
	}

	game := bowling.NewGame()
	for _, r := range rolls {
		if err := game.Roll(r); err != nil {
			fmt.Fprintf(os.Stderr, "error: %v\n", err)
			os.Exit(1)
		}
	}

	score, err := game.Score()
	if err != nil {
		fmt.Fprintf(os.Stderr, "error scoring: %v\n", err)
		os.Exit(1)
	}

	fmt.Printf("Score: %d\n", score)
	if score == 300 {
		fmt.Println("🎳 Perfect game!")
	}
}
