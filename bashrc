set -o vi
bind '"jk":vi-movement-mode'
bind '"\C-l":clear-screen'

[[ $- != *i* ]] && return
alias ls='ls --color=auto'

PS1="\[\033[32m\]▶ \[\033[37m\]\[\033[34m\]\W \[\033[0m\]"

# bash history unlimited 
HISTSIZE= 
HISTFILESIZE=

# Pomodoro options as associative array
declare -A pomo_options=(
  ["work"]="45"
  ["break"]="10"
)

# Pomodoro function
pomodoro () {
  local val="$1"
  if [[ -n "$val" && -n "${pomo_options[$val]}" ]]; then
    echo "$val" | lolcat
    timer "${pomo_options[$val]}m"
    # spd-say "'$val' session done"
  else
    echo "Usage: pomodoro [work|break]"
  fi
}

alias no='newsboat'
alias nn='nvim'
alias rn='ranger'
alias gst='git status'
alias lg='lazygit'
alias lad='lazydocker'
alias pm='pulsemixer'
alias cat="bat"
alias f='fortune'
alias fb='fortune | boxes -d parchment | lolcat'
alias fc='fortune | cowsay | lolcat'
alias a='aichat'
alias wo="pomodoro work"
alias br="pomodoro break"

alias pg='ping -c1 www.google.com'
alias so='source ~/.bashrc'
alias up='sudo pacman -Syu'
alias clean='sudo pacman -Rns $(pacman -Qdtq)'
alias ssd='systemctl start docker'
alias die='shutdown now'
alias logout='pkill dwm'
alias r='ffmpeg -f x11grab -s 2560x1440 -i :0.0 -f pulse -i bluez_input.8C:64:A2:8A:F9:41 -c:v libx264 -preset fast -crf 22 -c:a aac output.mp4'
alias r6='ffmpeg -framerate 60 -f x11grab -s 2560x1440 -i :0.0 -f pulse -i bluez_input.8C:64:A2:8A:F9:41 -c:v libx264 -preset fast -crf 22 -c:a aac output_60fps.mp4'
alias vm='/home/d14/dev/qemu/build/qemu-system-x86_64 -L /usr/share/qemu -enable-kvm -m 8192 -cpu host -smp 8 -device virtio-vga -display gtk -device virtio-serial-pci -chardev spicevmc,id=spicechannel0,name=vdagent -device virtserialport,chardev=spicechannel0,name=com.redhat.spice.0 -device ich9-intel-hda -device hda-duplex -drive file=/home/d14/vmIso/ubuntu_disk.qcow2,format=qcow2'

alias vmc='vm -device usb-ehci,id=ehci -device usb-host,hostbus=3,hostaddr=3'

# source venv
alias va='source ./venv/bin/activate'
alias iv='virtualenv3 venv'
alias cod='conda deactivate'

# Avante Gemini

# xorg brightness
alias cb="xrandr --verbose | grep -i brightness | head -n1 | awk '{print $2}'"
sb() {
    xrandr --output DP-0 --brightness "$1"
}

# fnm
FNM_PATH="/home/d14/.local/share/fnm"
if [ -d "$FNM_PATH" ]; then
  export PATH="$FNM_PATH:$PATH"
  eval "`fnm env`"
fi

[ -s "${HOME}/.g/env" ] && \. "${HOME}/.g/env"  # g shell setup
export PATH=$PATH:$(go env GOPATH)/bin

#zoxide
eval "$(zoxide init bash)"

# dotnet
export DOTNET_ROOT=$HOME/apps/dotnet
export PATH=$DOTNET_ROOT:$PATH

# Exports
export EDITOR=nvim
export BROWSER=firefox
export COLORTERM=truecolor

[ -f ~/.fzf.bash ] && source ~/.fzf.bash
