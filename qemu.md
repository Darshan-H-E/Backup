## Deps 

base-devel git python meson ninja glib2 pixman gtk3 libsdl sdl2 libpulse libslirp usbredir spice gtk-vnc libvirt alsa-lib jack libxkbcommon libepoxy

ndctl libcbor capstone spice spice-protocol spice-vdagent sdl2
virglrenderer libva vde2

seabios

## Qemu Configuration & Build

make distclean  # clean previous build completely

./configure --prefix=$HOME/qemu-install --target-list=x86_64-softmmu --enable-spice --enable-slirp --enable-virtio --enable-vnc --enable-gtk --enable-sdl --enable-kvm


make -j$(nproc)
make install

## health check

qemu-system-x86_64 -spice help

## Modprobe

sudo modprobe virtio
sudo modprobe virtio_pci
sudo modprobe virtio_gpu


lsmod | grep virtio

## Spice utils and copy/paste 

sudo systemctl enable --now spice-vdagent
sudo systemctl enable --now qemu-guest-agent

## Usb passthrough

sudo pacman -S usbutils
lsusb

cat /etc/udev/rules.d/99-qemu-usb.rules 
sudo vim 99-qemu-usb.rules

▶ ~ cat /etc/udev/rules.d/99-qemu-usb.rules 
───────┬────────────────────────────────────────────────────────────────────────────────────────────────
       │ File: /etc/udev/rules.d/99-qemu-usb.rules 
───────┼────────────────────────────────────────────────────────────────────────────────────────────────
   1   │ # /etc/udev/rules.d/99-qemu-usb.rules 
   2   │ SUBSYSTEM=="usb", ATTR{idVendor}=="046d", ATTR{idProduct}=="0825", MODE="0666" 
───────┼────────────────────────────────────────────────────────────────────────────────────────────────

## Bluetooth

sudo systemctl start bluetooth
sudo systemctl status bluetooth
bluetoothctl 

### Set default output

    pactl list short sinks

## libvirtd (Optional) [link](https://claude.ai/share/d5964c79-10d8-4893-9a84-4cf35c1d0f29)

sudo systemctl status libvirtd.service
sudo systemctl enable --now libvirtd.service

## Virtual disk create
qemu-img create -f qcow2 ubuntu_disk.qcow2 25G

## OpenGL (Future work)
sudo pacman -S mesa mesa-demos mesa-utils nvidia nvidia-utils linux-headers

glxinfo | grep "OpenGL renderer"

uname
