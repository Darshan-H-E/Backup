#!/bin/sh

xrdb merge ~/.Xresources &
xmodmap ~/.Xmodmap

xset s off
xset -dpms
xset s noblank
xset r rate 200 30

picom -f & 
nitrogen --restore &

# Start polkit-gnome agent
# eval $(/usr/lib/polkit-gnome/polkit-gnome-authentication-agent-1 &)

# Update xsetroot with date, Wi-Fi SSID, and volume every minute
while true; do
    TIME="$(date '+%d/%m %a %H:%M')"
    SSID="$(iwgetid -r)"
    VOLUME="$(pamixer --get-volume)"  # Get the current volume level

    # If connected to Wi-Fi, show SSID, otherwise show "No Wi-Fi"
    if [ -n "$SSID" ]; then
        xsetroot -name "$SSID | Vol: $VOLUME | $TIME"
    else
        xsetroot -name "No Wi-Fi | Vol: $VOLUME | $TIME"
    fi
    sleep 60
done &

dwm
