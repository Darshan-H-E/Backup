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

(
  while pgrep -x dwm >/dev/null; do
    TIME="$(date '+%d/%m %a %H:%M')"
    SSID="$(iwgetid -r)"
    VOLUME="$(pamixer --get-volume)"
    PLAYER_URL="$(playerctl metadata xesam:url 2>/dev/null || true)"

    if echo "$PLAYER_URL" | grep -qF "music.youtube.com" ||
      echo "$PLAYER_URL" | grep -qF "open.spotify.com"; then
      SONG="$(playerctl metadata --format '{{artist}} • {{title}}' 2>/dev/null || true)"
      [ -z "$SONG" ] && SONG="Music"
    else
      SONG="Music"
    fi

    [ -z "$SSID" ] && SSID=""
    xprop -root -f WM_NAME 8s -set WM_NAME "$SONG | $SSID | Vol: $VOLUME | $TIME"
    sleep 1
  done
) &

dwm
