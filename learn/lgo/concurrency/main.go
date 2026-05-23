package main

import (
	"fmt"
	"sync"
)

func printSom(s string, w *sync.WaitGroup) {
	defer w.Done()
	fmt.Println(s)
}

func main() {
	var wg sync.WaitGroup

	words := []string{
		"a", "b", "c", "d", "e",
	}

	wg.Add(len(words))
	for i, w := range words {
		go printSom(fmt.Sprintf("%d: %s", i, w), &wg)
	}
	wg.Wait()

	wg.Add(1)
	printSom("baka", &wg)
}
