package main

import (
	"io"
	"os"
	"strings"
	"sync"
	"testing"
)

func Test_PrintSom(t *testing.T) {
	stdOut := os.Stdout
	r, w, _ := os.Pipe()
	os.Stdout = w

	var wg sync.WaitGroup
	
	wg.Add(1)
	go printSom("z", &wg)
	wg.Wait()
	_ = w.Close()

	result, _ := io.ReadAll(r)
	output := string(result)

	if !strings.Contains(output, "z") {
		t.Errorf("'z' not there")
	}

	os.Stdout = stdOut
}
