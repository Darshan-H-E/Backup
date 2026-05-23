package main

import (
	"io"
	"os"
	"strings"
	"sync"
	"testing"
)

func Test_UpdateMsg(t *testing.T) {
	stdOut := os.Stdout
	r, w, _ := os.Pipe()
	os.Stdout = w

	var wg sync.WaitGroup
	wg.Add(1)
	go updateMessage("z", &wg)
	wg.Wait()

	_ = w.Close()
	result, _ := io.ReadAll(r)
	output := string(result)

	if !strings.Contains(output, "z") {
		t.Errorf("'z' not there")
	}

	os.Stdout = stdOut
}

func Test_PrintMsg(t *testing.T) {
	stdOut := os.Stdout
	r, w, _ := os.Pipe()
	os.Stdout = w

	msg = "hello"
	printMessage()

	_ = w.Close()
	result, _ := io.ReadAll(r)
	output := string(result)

	if !strings.Contains(output, "hello") {
		t.Errorf("'hello' not there")
	}

	os.Stdout = stdOut
}

func Test_Main(t *testing.T) {
	stdOut := os.Stdout
	r, w, _ := os.Pipe()
	os.Stdout = w

	main()

	_ = w.Close()
	result, _ := io.ReadAll(r)
	output := string(result)
	pattern := `Hello, world!
Hello, universe!
Hello, cosmos!`

	if !strings.Contains(output, pattern) {
		t.Errorf("pattern not there")
	}

	os.Stdout = stdOut
}
