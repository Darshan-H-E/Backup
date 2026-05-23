package main

import (
	"github.com/fatih/color"
	"time"
)

// variables

var seatingCapacity = 10
var arrivalRate = 100 // millisecond
var cutDuration = 1000 * time.Millisecond
var timeOpen = 10 * time.Second

func main() {
	// print welcome msg
	color.Yellow("The sleeping barber problem")
	color.Yellow("---------------------------")

	// create channels 
	clientChan := make(chan string, seatingCapacity)
	doneChan := make(chan bool)

	// create barbershop
	shop := BarberShop{
		ShopCapacity: seatingCapacity,
		hairCutDuration: cutDuration,
		NumberOfBarbers: 0,
		ClientsChan: clientChan,
		BarbersDoneChan: doneChan,
		Open: true,
	}

	color.Green("Shop is open for the day")

	// add barbers
	shop.addBarber("Frank")

	// start the barbershop as a goroutine

	// add clients

	// block until barbershop is closed

	time.Sleep(5 * time.Second)
}
