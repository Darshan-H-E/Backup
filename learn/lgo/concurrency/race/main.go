// Problem:
// package main
//
// import (
// 	"fmt"
// 	"sync"
// )
//
// var msg string
//
// func updateMessage(s string, w *sync.WaitGroup) {
// 	defer w.Done()
// 	msg = s
// }
//
// func main() {
// 	msg = "Star wars"
// 	var wg sync.WaitGroup
//
// 	wg.Add(2)
// 	go updateMessage("Obi", &wg)
// 	go updateMessage("Ani", &wg)
// 	wg.Wait()
//
// 	fmt.Println(msg)
// }


// Solution:
package main

import (
	"fmt"
	"sync"
)

var msg string

func updateMessage(s string, w *sync.WaitGroup, m *sync.Mutex) {
	defer w.Done()
	m.Lock()
	msg = s
	m.Unlock()
}

func main() {
	msg = "Star wars"
	var wg sync.WaitGroup
	var mut sync.Mutex

	wg.Add(2)
	go updateMessage("Obi", &wg, &mut)
	go updateMessage("Ani", &wg, &mut)
	wg.Wait()

	fmt.Println(msg)
}
