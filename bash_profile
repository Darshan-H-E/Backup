#
# ~/.bash_profile
#

export EDITOR=nvim
export BROWSER=firefox
export ANDROID_HOME=$HOME/.config/android
export PATH=$ANDROID_HOME/cmdline-tools/tools/bin/:$PATH
export PATH=$ANDROID_HOME/platform-tools/:$PATH
export CHROME_EXECUTABLE=google-chrome-stable

[[ -f ~/.bashrc ]] && . ~/.bashrc

# startx
