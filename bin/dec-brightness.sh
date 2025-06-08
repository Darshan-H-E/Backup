#!/bin/bash

# Find current brightness
current=$(xrandr --verbose | grep -i brightness | head -n1 | awk '{print $2}')

# Calculate new brightness (-0.1)
new=$(echo "$current - 0.05" | bc)

# Set minimum brightness
min=0.1
cmp=$(echo "$new < $min" | bc)
if [ "$cmp" -eq 1 ]; then
    new=$min
fi

# Apply new brightness
xrandr --output DP-0 --brightness "$new"
echo "Brightness decreased to $new"
