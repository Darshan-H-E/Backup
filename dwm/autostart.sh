#!/bin/sh

xrdb merge ~/.Xresources &
xmodmap ~/.Xmodmap

xset s off
xset -dpms
xset s noblank
xset r rate 200 30

picom -f & 
nitrogen --restore &
lxpolkit &

# Start polkit-gnome agent
# eval $(/usr/lib/polkit-gnome/polkit-gnome-authentication-agent-1 &)

# Update xsetroot with date, Wi-Fi SSID, and volume every minute
while true; do
    TIME="$(date '+%d/%m %a %H:%M')"
    SSID="$(iwgetid -r)"
    VOLUME="$(pamixer --get-volume)"
    SONG="$(playerctl metadata --format '{{artist}} • {{title}}' 2>/dev/null)"

    if [ -z "$SONG" ]; then
        SONG="Music"
    fi

    if [ -z "$SSID" ]; then
        SSID="$(No Wi-Fi)"
    fi

    xsetroot -name "$SONG | $SSID | Vol: $VOLUME | $TIME"
    sleep 1
done &

dwm
