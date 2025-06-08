# VI mode
Set-PSReadLineOption -EditMode Vi
Set-PSReadLineKeyHandler -Chord Tab -Function Complete
Set-PSReadLineKeyHandler -Chord Ctrl-r -Function ReverseSearchHistory -ViMode Insert
Set-PSReadLineKeyHandler -Chord Ctrl-r -Function ReverseSearchHistory -ViMode Command

# FNM
fnm env --use-on-cd --shell powershell | Out-String | Invoke-Expression

# Zoxide
Invoke-Expression (& { (zoxide init powershell | Out-String) })

# Alias
Set-Alias nn nvim
Set-Alias lg lazygit

# --------------------- Rebind jk to esc -----------------------------------------
Set-PSReadLineKeyHandler -vimode insert -Chord "k" -ScriptBlock { mapTwoLetterNormal 'k' 'j' }
Set-PSReadLineKeyHandler -vimode insert -Chord "j" -ScriptBlock { mapTwoLetterNormal 'j' 'k' }
function mapTwoLetterNormal($a, $b){
  mapTwoLetterFunc $a $b -func $function:setViCommandMode
}
function setViCommandMode{
    [Microsoft.PowerShell.PSConsoleReadLine]::ViCommandMode()
}

function mapTwoLetterFunc($a,$b,$func) {
  if ([Microsoft.PowerShell.PSConsoleReadLine]::InViInsertMode()) {
    $key = $host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
    if ($key.Character -eq $b) {
        &$func
    } else {
      [Microsoft.Powershell.PSConsoleReadLine]::Insert("$a")
      if ($key.Character -eq 0x00) {
        return
      } else {
        $wshell = New-Object -ComObject wscript.shell
        $wshell.SendKeys("{$($key.Character)}")
      }
    }
  }
}

function replaceWithExit {
    [Microsoft.PowerShell.PSConsoleReadLine]::BackwardKillLine()
    [Microsoft.PowerShell.PSConsoleReadLine]::KillLine()
    [Microsoft.PowerShell.PSConsoleReadLine]::Insert('exit')
}
Set-PSReadLineKeyHandler -Chord ";" -ScriptBlock { mapTwoLetterFunc ';' 'q' -func $function:replaceWithExit }
# ------------------------------------------------------------------------------
