#!/bin/bash

# Find current brightness
current=$(xrandr --verbose | grep -i brightness | head -n1 | awk '{print $2}')

# Calculate new brightness (+0.1)
new=$(echo "$current + 0.05" | bc)

# Cap to maximum 1.0
max=1.0
cmp=$(echo "$new > $max" | bc)
if [ "$cmp" -eq 1 ]; then
    new=$max
fi

# Apply new brightness
xrandr --output DP-0 --brightness "$new"
echo "Brightness increased to $new"
