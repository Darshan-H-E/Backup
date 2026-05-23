package main

import (
	"fmt"
	"strings"
	"sync"
	"time"
)

// Dining philosopher's problem

// store info about Philosopher
type Philosopher struct {
	name string
	rightfork int
	leftfork int
}

// list of all philosophers
var philosophers = []Philosopher{
	{name: "Socrates", leftfork: 0, rightfork: 1},
	{name: "Aristotle", leftfork: 1, rightfork: 2},
	{name: "Pascal", leftfork: 2, rightfork: 3},
	{name: "Locke", leftfork: 3, rightfork: 4},
	{name: "Plato", leftfork: 4, rightfork: 0},
}

var hunger = 3 // eat 3 times and you are done
var eatTime = 1 * time.Second
var thinkTime = 3 * time.Second
var sleepTime = 1 * time.Second // some delay in printing to console

var orderMutex sync.Mutex // a mutex for the slice orderFinished
var orderFinished []string

func main() {
	// print welcome msg
	fmt.Println("Dining Philosopher's problem")
	fmt.Println("____________________________")
	fmt.Println("The table is empty")

	time.Sleep(sleepTime)

	// start meal
	dine()

	// print out finished msg
	fmt.Println("____________________________")
	fmt.Println("The table is empty")
	fmt.Println("____________________________")
	fmt.Printf("orderFinished: %s.\n", strings.Join(orderFinished, ", "))
}

func dine() {
	// eatTime = 0 * time.Second
	// thinkTime = 0 * time.Second
	// sleepTime = 0 * time.Second // some delay in printing to console

	wg := &sync.WaitGroup{}
	wg.Add(len(philosophers)) // done when everyone's done eating

	seated := &sync.WaitGroup{}
	seated.Add(len(philosophers)) // done when everyone's seated at the table

	// forks is a map of all 5 forks
	var forks = make(map[int]*sync.Mutex)
	for i := 0; i < len(philosophers); i++ {
		forks[i] = &sync.Mutex{}
	}

	// start the meal
	for i := 0; i < len(philosophers); i++ {
		// fire off a goroutine for current philosopher 
		go diningProblem(philosophers[i], wg, forks, seated)
	}

	wg.Wait()
}

func diningProblem(philosopher Philosopher, wg *sync.WaitGroup, forks map[int]*sync.Mutex, seated *sync.WaitGroup) {
	defer wg.Done()

	// seat the philosopher
	fmt.Printf("%s is seated at the table.\n", philosopher.name)
	seated.Done()

	seated.Wait()

	// eat three times
	for i := hunger; i > 0; i-- {
		// get a lock on both forks

		if philosopher.leftfork > philosopher.rightfork {
			forks[philosopher.rightfork].Lock()
			fmt.Printf("\t%s takes the right fork.\n", philosopher.name)
			forks[philosopher.leftfork].Lock()
			fmt.Printf("\t%s takes the left fork.\n", philosopher.name)
		} else {
			forks[philosopher.leftfork].Lock()
			fmt.Printf("\t%s takes the left fork.\n", philosopher.name)
			forks[philosopher.rightfork].Lock()
			fmt.Printf("\t%s takes the right fork.\n", philosopher.name)
		}

		fmt.Printf("\t%s has both forks and is eating.\n", philosopher.name)
		time.Sleep(eatTime)


		fmt.Printf("\t%s is thinking.\n", philosopher.name)
		time.Sleep(thinkTime)

		forks[philosopher.leftfork].Unlock()
		forks[philosopher.rightfork].Unlock()

		fmt.Printf("\t%s put down the forks.\n", philosopher.name)
	}

	fmt.Println(philosopher.name, "is satisfied.")
	fmt.Println(philosopher.name, "left the table.")

	orderMutex.Lock()
	orderFinished = append(orderFinished, philosopher.name)
	orderMutex.Unlock()
}
