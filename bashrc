set -o vi
bind '"jk":vi-movement-mode'
bind '"\C-l":clear-screen'

[[ $- != *i* ]] && return
alias ls='ls --color=auto'

#ping www.google.com
alias pg='ping -c1 www.google.com'

# fzf
source /usr/share/fzf/key-bindings.bash

alias up='sudo pacman -Syyu'

PS1="\[\033[32m\]  \[\033[37m\]\[\033[34m\]\W \[\033[0m\]"

# bash history unlimited 
HISTSIZE= 
HISTFILESIZE=

# yay
alias yp="yay -Slq | fzf -m --preview 'cat <(yay -Si {1}) <(yay -Fl {1} | awk \"{print \$2}\")' | xargs -ro  yay -S"

#logo-ls
alias la='logo-ls -A'
alias ll='logo-ls -al'

alias v='vim'
alias nn='nvim'
alias lv='lvim'
alias rn='ranger'

#mpd
alias kek=" killall mpd ncmpcpp ncmpcpp_cover_art.sh"
alias mpd=" mpd && ~/.ncmpcpp/ncmpcpp-ueberzug/ncmpcpp-ueberzug"
alias np="~/.ncmpcpp/ncmpcpp-ueberzug/ncmpcpp-ueberzug"

# mpv
alias au='mpv --no-video'
# youtube-dl to download stuffs
alias yt='youtube-dl --extract-audio --add-metadata --xattrs --embed-thumbnail --audio-quality 0 --audio-format mp3'
alias ytv='youtube-dl --merge-output-format mp4 -f "bestvideo+bestaudio[ext=m4a]/best" --embed-thumbnail --add-metadata'

alias die='systemctl poweroff'
alias pm='pulsemixer'
alias cb='pipes.sh'
alias gg='supergfxctl -g'
alias com='supergfxctl -m compute'

#CPU
alias perf="sudo cpupower -c all frequency-set -g performance"
alias pow="sudo cpupower -c all frequency-set -g powersave"
alias nor="sudo cpupower -c all frequency-set -g schedutil"

b (){
  xrandr --output eDP --brightness 0."$1"
}

# less color support
export LESS_TERMCAP_mb=$'\e[1;32m'
export LESS_TERMCAP_md=$'\e[1;32m'
export LESS_TERMCAP_me=$'\e[0m'
export LESS_TERMCAP_se=$'\e[0m'
export LESS_TERMCAP_so=$'\e[01;33m'
export LESS_TERMCAP_ue=$'\e[0m'
export LESS_TERMCAP_us=$'\e[1;4;31m'

# source venv
alias va='source ./venv/bin/activate'
alias iv='python ../virtualenv.pyz venv'

ch (){
  curl cht.sh/"$1"/"$2"
}

# fnm
export PATH=/home/volt/.fnm:$PATH
eval "`fnm env`"
