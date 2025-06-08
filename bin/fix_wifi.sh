#!/bin/bash

# Check Wi-Fi block status
echo "Checking rfkill status..."
rfkill list

# Unblock Wi-Fi
echo "Unblocking Wi-Fi..."
sudo rfkill unblock wifi

# Restart NetworkManager
echo "Restarting NetworkManager..."
sudo systemctl restart NetworkManager

# Bring up wlan0 interface
echo "Bringing up wlan0..."
sudo ip link set wlan0 up

# Rescan Wi-Fi networks
echo "Rescanning Wi-Fi networks..."
nmcli device wifi rescan

# List available Wi-Fi networks
echo "Available Wi-Fi networks:"
nmcli device wifi list

# Show device status
echo "Device status:"
nmcli device status

# Show NetworkManager logs
echo "Recent NetworkManager logs:"
journalctl -xeu NetworkManager | tail -n 50
