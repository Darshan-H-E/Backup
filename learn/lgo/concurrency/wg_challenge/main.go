package main

import (
	"fmt"
	"sync"
)

var msg string

func updateMessage(s string, w *sync.WaitGroup) {
	defer w.Done()
	msg = s
	printMessage()
}

func printMessage() {
	fmt.Println(msg)
}

func main() {

	// challenge: modify this code so that the calls to updateMessage() on lines
	// 28, 30, and 33 run as goroutines, and implement wait groups so that
	// the program runs properly, and prints out three different messages.
	// Then, write a test for all three functions in this program: updateMessage(),
	// printMessage(), and main().

	msg = "Hello, world!"
	var wg sync.WaitGroup

	updates := []string{
		"Hello, universe!",
		"Hello, cosmos!",
		"Hello, world!",
	}

	wg.Add(len(updates))
	for _, i := range updates {
		go updateMessage(i, &wg)
	}
	wg.Wait()
}
