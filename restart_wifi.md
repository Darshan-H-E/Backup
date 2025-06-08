## Commands

```bash
rfkill list
sudo rfkill unblock wifi
sudo systemctl restart NetworkManager
sudo ip link set wlan0 up
nmcli device wifi rescan
nmcli device wifi list
nmcli device status
journalctl -xeu NetworkManager

```
## Optional

```bash
sudo systemctl restart iwd
sudo systemctl status iwd

lspci -k | grep -A 3 -i network
lsmod | grep iwlwifi
sudo modprobe -r iwlwifi
sudo modprobe iwlwifi

```
