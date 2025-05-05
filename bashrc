#
# ~/.bashrc
#

set -o vi
bind '"jk":vi-movement-mode'
bind '"\C-l":clear-screen'

[[ $- != *i* ]] && return
alias ls='ls --color=auto'

#ping www.google.com
alias pg='ping -c1 www.google.com'

#PS1="\[\033[32m\]  \[\033[37m\]\[\033[34m\]\W \[\033[0m\]"
PS1="\[\033[32m\]▶ \[\033[37m\]\[\033[34m\]\W \[\033[0m\]"

# bash history unlimited 
HISTSIZE= 
HISTFILESIZE=

alias v='vim'
alias nn='nvim'
alias lv='~/.local/bin/lvim'
alias rn='ranger'
alias gst='git status'
alias lg='lazygit'
alias lad='lazydocker'

# source venv
alias va='source ./venv/bin/activate'
alias iv='python ../virtualenv.pyz venv'
alias cod='conda deactivate'
alias so='source ~/.bashrc'
alias up='sudo pacman -Syu'
alias die='shutdown now'
alias logout='pkill dwm'

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
